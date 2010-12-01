package org.renci.gate.engine;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.renci.common.exec.Input;
import org.renci.common.exec.Output;
import org.renci.gate.ClassAd;
import org.renci.gate.GATESite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteSelectionCallable implements Callable<GATESite> {

    private final Logger logger = LoggerFactory.getLogger(SiteSelectionCallable.class);

    private final Executor exec = Executor.getInstance();

    private String portalUser;

    private String requirements;

    private List<GATESite> siteList;
    
    public SiteSelectionCallable(List<GATESite> siteList) {
        super();
        this.siteList = siteList;
        this.portalUser = "";
    }

    public SiteSelectionCallable(List<GATESite> siteList, String portalUser, String requirements) {
        super();
        this.siteList = siteList;
        this.portalUser = portalUser;
        this.requirements = requirements;
    }

    public GATESite call() {
        
        // get a list of sites which passed the test jobs, and matches the job requirements
        List<String> matchedSites = null;
        try {
            matchedSites = loadAdvertisedSitesMatchingReqs();
        } catch (Exception e) {
            logger.error("Unable to retrive matching sites from Condor: " + e.getMessage());
            return null;
        }

        if (matchedSites == null || matchedSites.isEmpty()) {
            logger.error("No matching sites passed the maintenance jobs");
            return null;
        }

        // create a list of PGFSites by merging the db list and the matched site list
        Hashtable<String, GATESite> sites = new Hashtable<String, GATESite>();
        if (siteList != null) {
            for (GATESite site : siteList) {
                if (matchedSites.contains(site.getName())) {
                    GATESite gateSite = new GATESite();
                    gateSite.setName(site.getName());
                    sites.put(site.getName(), gateSite);
                } else {
                    logger.info("Site did not meet job requirements: " + site.getName());
                }
            }
        }
               
        if (sites == null || sites.isEmpty()) {
            logger.error("No matching sites after merging sites from database and from maintenance jobs");
            return null;
        }

        invokeAndUpdateSites(portalUser, sites);

        // find the highest site score
        int highScore = -100;
        String highSiteName = null;
        Enumeration<GATESite> e = sites.elements();
        while (e.hasMoreElements()) {
            GATESite s = (GATESite) e.nextElement();

            String info = "     Score for " + s.getName() + ": " + s.getScore();
            if (s.getScoreComment() != null) {
                info += " (" + s.getScoreComment() + ")";
            }
            logger.info(info);

            info = "                Glideins: " + s.getIdleCount() + " idle, " + s.getRunningCount() + " running, "
                    + s.getHeldCount() + " held";
            logger.info(info);

            if (s.getScore() > highScore) {
                highScore = s.getScore();
                highSiteName = s.getName();
            }
        }
        if (highSiteName != null && highScore > 0) {
            return sites.get(highSiteName);
        }

        // no site found
        return null;
    }

    /**
     * Runs condor_q and update the supplied sites
     */
    private void invokeAndUpdateSites(String portalUser, Hashtable<String, GATESite> sites) {
        // run condor_q
        StringBuilder sb = new StringBuilder();
        sb.append("(condor_q -l -constraint '(IsPilotGlideIn == 1 && PortalUser == \"").append(portalUser)
                .append("\")'; echo)");
        Input shellInput = new Input();
        shellInput.setCommand(sb.toString());
        logger.info(sb.toString());
        try {
            Output shellOutput = exec.run(shellInput);
            logger.info(shellOutput.getStdout().toString());

            if (shellOutput.getStderr() != null && shellOutput.getStderr().length() > 2) {
                logger.error("Shell error: " + shellOutput.getStderr());
                return;
            }

            String[] lines = shellOutput.getStdout().toString().split("\n");

            for (String currentLine : lines) {
                ClassAd ad = new ClassAd();

                ad.parseLine(currentLine);

                if (ad == null) {
                    ad = new ClassAd();
                }

                if (currentLine.equals("")) {

                    // we have an add, process it
                    String clusterId = ad.getReallyTrimmed("ClusterId");

                    if (StringUtils.isNotEmpty(clusterId)) {

                        String siteName = ad.getReallyTrimmed("GLIDEIN_SITE_NAME");
                        int jobCount = ad.getAsInt("GLIDEIN_COUNT");
                        if (jobCount == 0) {
                            jobCount = 1;
                        }

                        if (siteName != null) {

                            GATESite s = sites.get(siteName);

                            if (s != null) {

                                String stateString = stateId2String(ad.getReallyTrimmed("JobStatus"));

                                // update site with status of the glidein
                                JobStateType state = JobStateType.valueOf(stateString);

                                switch (state) {

                                    case HELD:
                                        s.setHeldCount(s.getHeldCount() + jobCount);
                                        break;

                                    case IDLE:
                                        s.setIdleCount(s.getIdleCount() + jobCount);
                                        break;

                                    case RUNNING:
                                        s.setRunningCount(s.getRunningCount() + jobCount);
                                        break;

                                    case COMPLETED:
                                        // ignore
                                        break;

                                    case REMOVED:
                                        // ignore
                                        break;

                                    default:
                                        logger.warn("Unknown state: " + state);
                                        break;

                                }
                            }
                        }
                    }

                    // we are done with this ad
                    ad.clear();
                    ad = null;
                } else {
                    ad.setRawLine(currentLine);
                }
            }

        } catch (ExecutorException e) {
            logger.error("Shell exception: " + e.getMessage());
            return;
        }

    }

    private String stateId2String(String id) {

        /*
         * From http://pages.cs.wisc.edu/~adesmet/status.html 0 Unexpanded U 1 Idle I 2 Running R 3 Removed X 4
         * Completed C 5 Held H 6 Submission_err E
         */

        if (id.equals("1")) {
            return "IDLE";
        }
        if (id.equals("2")) {
            return "RUNNING";
        }
        if (id.equals("3")) {
            return "REMOVED";
        }
        if (id.equals("4")) {
            return "COMPLETED";
        }
        if (id.equals("5")) {
            return "HELD";
        }

        return "UNEXPANDED";
    }

    private List<String> loadAdvertisedSitesMatchingReqs() throws Exception {

        List<String> sites = new ArrayList<String>();

        StringBuilder sb = new StringBuilder();
        sb.append("condor_status -any -format '%s\\n' SiteName -constraint '").append(requirements)
                .append("' -constraint 'GATESite == True'");
        Input shellInput = new Input();
        shellInput.setCommand(sb.toString());
        logger.info(sb.toString());
        try {
            Output shellOutput = exec.run(shellInput);
            logger.info(shellOutput.getStdout().toString());

            if (shellOutput.getStderr() != null && shellOutput.getStderr().length() > 2) {
                throw new Exception("Shell error: " + shellOutput.getStderr());
            }

            String[] lines = shellOutput.toString().split("\n");
            for (String currentLine : lines) {
                sites.add(currentLine);
            }
        } catch (ExecutorException e) {
            logger.error("Failed to submit jm job: " + e.getMessage());
        }
        return sites;
    }

}

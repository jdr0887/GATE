package org.renci.gate.engine;

import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.renci.common.exec.Input;
import org.renci.common.exec.Output;
import org.renci.gate.GATESite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlideinEngineRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(GlideinEngineRunnable.class);

    private final Executor exec = Executor.getInstance();

    // total number of alloted glideins
    private int totalAllotedGlidein = 100;

    private List<PortalUserJobInfo> portalUserJobInfoList;

    private List<GATESite> siteList;

    private BundleContext context;

    public GlideinEngineRunnable(BundleContext context, List<GATESite> siteList,
            List<PortalUserJobInfo> portalUserJobInfoList) {
        super();
        this.context = context;
        this.portalUserJobInfoList = portalUserJobInfoList;
        this.siteList = siteList;
    }

    public void run() {

        int numPortalUser = portalUserJobInfoList.size();
        int numGlideinPerUser = 100;

        logger.info("Number of portal users " + numPortalUser);

        // For every portal user generate glideins; currently generate one glidein for every user
        Iterator<PortalUserJobInfo> userIter = portalUserJobInfoList.iterator();
        while (userIter.hasNext()) { // For all portal users

            PortalUserJobInfo currUserJobInfo = userIter.next();

            String currUser = currUserJobInfo.getPortalUserName();
            currUserJobInfo.setAllotedGlidein(numGlideinPerUser);

            int numActiveGlidein = currUserJobInfo.getNumActiveGlidein();
            int numIdleGlidein = currUserJobInfo.getNumPendingGlidein();
            int numActiveJob = currUserJobInfo.getNumActiveJobs();
            int numIdleJob = currUserJobInfo.getNumIdleJobs();
            int numTotalJob = numActiveJob + numIdleJob;

            logger.info("User: " + currUser + " | numActiveGlidein: " + numActiveGlidein + " | numIdleGlidein: "
                    + numIdleGlidein + " | numActiveJob: " + numActiveJob + " | numIdleJob: " + numIdleJob);

            // submit glideins
            if (numTotalJob > 0) {

                // assume we need new glideins, and then run some tests to negate the assumptions
                boolean needGlidein = true;

                if (numTotalJob > 100 && (numActiveGlidein > (numTotalJob * 0.85))) {
                    logger.info("      Number of running glideins is probably enough for the workload. No glideins needed.");
                    needGlidein = false;
                } else if (numActiveJob > (numTotalJob * 0.9)) {
                    logger.info("      Number of running jobs is high compared to idle jobs. No glideins needed.");
                    needGlidein = false;
                } else if (numIdleGlidein >= 150) {
                    logger.info("      High number of idle glideins. No glideins needed.");
                    needGlidein = false;
                }

                if (needGlidein) {
                    // Submit glideins

                    // how many glideins we need is determined by how many idle jobs the user has
                    int numToSubmit = 1;
                    if (numIdleJob > 30 && numIdleJob * 10 > numActiveJob) {
                        numToSubmit = (int) Math.round(0.3 * numIdleJob);
                        numToSubmit = Math.min(numToSubmit, 200);
                        logger.info("Planning on submitting " + numToSubmit + " glideins in this iteration");
                    }

                    int remainingToSubmit = numToSubmit;
                    int submittedLastIteration = 1;

                    while ((remainingToSubmit > 0) && (submittedLastIteration > 0)) {
                        logger.info("Submitting glidein for " + currUser + " with needs: " + currUserJobInfo.getNeeds());

                        CreateGlideInCallable glidein = new CreateGlideInCallable(context, siteList, remainingToSubmit,
                                currUser, currUserJobInfo.getUserEmail(), currUserJobInfo.getAuthTimestamp(),
                                currUserJobInfo.getAuthIP(), currUserJobInfo.getRequirements(),
                                currUserJobInfo.getNeeds());
                        submittedLastIteration = glidein.call();
                        remainingToSubmit = remainingToSubmit - submittedLastIteration;
                    }
                }
            }

            // remove pending glideins
            if (numTotalJob == 0 && numIdleGlidein > 0) {
                removePendingGlideins(currUser);
            }

        }

    }

    private void removePendingGlideins(String user) {
        logger.info("Removing pending glide-ins for user " + user);
        StringBuilder sb = new StringBuilder();
        sb.append("condor_rm -constraint 'JobStatus==1 && PortalUser==\"").append(user).append("\"'");
        Input shellInput = new Input();
        shellInput.setCommand(sb.toString());
        try {
            // TODO handle shell output better
            Output shellOutput = exec.run(shellInput);
        } catch (ExecutorException e) {
            logger.error("Unable to remove pending glideins: " + e.getMessage());
        }
    }

    public int getTotalAllotedGlidein() {
        return totalAllotedGlidein;
    }

    public void setTotalAllotedGlidein(int totalAllotedGlidein) {
        this.totalAllotedGlidein = totalAllotedGlidein;
    }

}

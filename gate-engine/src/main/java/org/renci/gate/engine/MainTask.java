package org.renci.gate.engine;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;

import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetrics;
import org.renci.gate.SiteScoreInfo;
import org.renci.jlrm.LRMException;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.ClassAdvertisementFactory;
import org.renci.jlrm.condor.CondorJobStatusType;
import org.renci.jlrm.condor.cli.CondorCLIFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(MainTask.class);

    private Map<String, GATEService> gateServiceMap;

    private String condorHome;

    public MainTask(Map<String, GATEService> gateServiceMap, String condorHome) {
        super();
        this.gateServiceMap = gateServiceMap;
        this.condorHome = condorHome;
    }

    @Override
    public void run() {

        // get a snapshot of jobs across sites
        Map<String, GlideinMetrics> siteMetricsMap = new HashMap<String, GlideinMetrics>();
        for (String siteName : this.gateServiceMap.keySet()) {
            GATEService gateService = this.gateServiceMap.get(siteName);
            GlideinMetrics glideinMetrics = gateService.lookupMetrics();
            siteMetricsMap.put(gateService.getSiteInfo().getName(), glideinMetrics);
        }

        // go get a snapshot of local jobs
        CondorCLIFactory condorCLIFactory = CondorCLIFactory.getInstance(new File(condorHome));
        Map<String, List<ClassAdvertisement>> jobMap = null;
        try {
            jobMap = condorCLIFactory.lookupJobsByOwner(System.getProperty("user.name"));
        } catch (LRMException e) {
            e.printStackTrace();
        }

        if (jobMap != null) {

            int totalJobs = jobMap.size();

            if (totalJobs > 0) {

                int idleJobs = 0;
                int runningJobs = 0;
                int heldJobs = 0;

                for (String job : jobMap.keySet()) {
                    List<ClassAdvertisement> classAdList = jobMap.get(job);
                    for (ClassAdvertisement classAd : classAdList) {
                        if (ClassAdvertisementFactory.CLASS_AD_KEY_JOB_STATUS.equals(classAd.getKey())) {
                            int statusCode = Integer.valueOf(classAd.getValue().trim());
                            if (statusCode == CondorJobStatusType.HELD.getCode()) {
                                ++heldJobs;
                            }
                            if (statusCode == CondorJobStatusType.IDLE.getCode()) {
                                ++idleJobs;
                            }
                            if (statusCode == CondorJobStatusType.RUNNING.getCode()) {
                                ++runningJobs;
                            }
                        }
                    }
                }

                // assume we need new glideins, and then run some tests to negate the assumptions
                boolean needGlidein = true;
                for (String siteName : this.gateServiceMap.keySet()) {
                    GATEService gateService = this.gateServiceMap.get(siteName);
                    GlideinMetrics glideinMetrics = siteMetricsMap.get(gateService.getSiteInfo().getName());
                    if (totalJobs > 0) {

                        if (totalJobs > 100 && (glideinMetrics.getRunning() > (totalJobs * 0.2))) {
                            logger.info("Number of running glideins is probably enough for the workload. No glideins needed.");
                            needGlidein = false;
                        } else if (runningJobs > (totalJobs * 0.9)) {
                            logger.info("Number of running jobs is high compared to idle jobs. No glideins needed.");
                            needGlidein = false;
                        } else if (glideinMetrics.getPending() >= gateService.getSiteInfo().getMaxIdleCount()) {
                            logger.info("High number of idle glideins. No glideins needed.");
                            needGlidein = false;
                        }

                    }

                }

                // calculate number of glideins to submit
                if (needGlidein) {

                    int numToSubmit = 1;
                    for (String siteName : this.gateServiceMap.keySet()) {
                        GATEService gateService = this.gateServiceMap.get(siteName);
                        GlideinMetrics glideinMetrics = siteMetricsMap.get(gateService.getSiteInfo().getName());
                        if (totalJobs > 0) {

                            // how many glideins we need is determined by how many idle jobs the user has
                            if (idleJobs > 30 && idleJobs * 10 > runningJobs) {
                                numToSubmit = (int) Math.round(0.1 * idleJobs);
                                numToSubmit = Math.min(numToSubmit, 10);
                                logger.info("Planning on submitting " + numToSubmit + " glideins in this iteration");
                            }

                            // how many glideins to submit
                            numToSubmit = Math.min(numToSubmit, gateService.getSiteInfo().getMaxMultipleJobs());

                            // will we exceed the max number of glideins to the site?
                            numToSubmit = Math.min(numToSubmit, gateService.getSiteInfo().getMaxTotalCount()
                                    - glideinMetrics.getTotal());

                            logger.info("Will try to submit " + numToSubmit + " glideins");

                        }

                    }

                    // find the highest site score
                    Map<String, SiteScoreInfo> siteScoreMap = new HashMap<String, SiteScoreInfo>();
                    for (String siteName : this.gateServiceMap.keySet()) {
                        GATEService gateService = this.gateServiceMap.get(siteName);
                        GlideinMetrics glideinMetrics = siteMetricsMap.get(gateService.getSiteInfo().getName());
                        SiteScoreInfo siteScoreInfo = calculateScore(gateService, glideinMetrics);
                        if (siteScoreInfo != null) {
                            siteScoreMap.put(gateService.getSiteInfo().getName(), siteScoreInfo);
                        }
                    }

                    List<Map.Entry<String, SiteScoreInfo>> list = new LinkedList<Map.Entry<String, SiteScoreInfo>>(
                            siteScoreMap.entrySet());

                    // sort list based on comparator...descending score
                    Collections.sort(list, new Comparator<Map.Entry<String, SiteScoreInfo>>() {
                        @Override
                        public int compare(Entry<String, SiteScoreInfo> o1, Entry<String, SiteScoreInfo> o2) {
                            return o2.getValue().getScore().compareTo(o1.getValue().getScore());
                        }
                    });

                    Map.Entry<String, SiteScoreInfo> winner = list.get(0);

                    for (int i = numToSubmit; i <= 0; --i) {
                        logger.info("Submitting glidein for {} to {}", System.getProperty("user.name"), winner.getKey());
                        GATEService gateService = this.gateServiceMap.get(winner.getKey());
                        gateService.postGlidein();
                    }

                }

            } else {

                // remove pending glideins
                for (String siteName : this.gateServiceMap.keySet()) {
                    GATEService gateService = this.gateServiceMap.get(siteName);
                    GlideinMetrics glideinMetrics = siteMetricsMap.get(gateService.getSiteInfo().getName());
                    if (glideinMetrics.getPending() > 0) {
                        gateService.deleteGlidein();
                    }
                }

            }
        }

    }

    private SiteScoreInfo calculateScore(GATEService gateService, GlideinMetrics glideinMetrics) {
        SiteScoreInfo siteScoreInfo = new SiteScoreInfo();
        // first, check if some limits have been reached
        if (glideinMetrics.getTotal() >= gateService.getSiteInfo().getMaxTotalCount()) {
            siteScoreInfo.setMessage("Total number of glideins has reached the limit of "
                    + gateService.getSiteInfo().getMaxTotalCount());
            siteScoreInfo.setScore(0);
            return siteScoreInfo;
        }

        if (glideinMetrics.getPending() >= gateService.getSiteInfo().getMaxIdleCount()) {
            siteScoreInfo.setMessage("Number of idle/pending glideins has reached the limit of "
                    + gateService.getSiteInfo().getMaxIdleCount());
            siteScoreInfo.setScore(0);
            return siteScoreInfo;
        }

        // we want to start all sites at the same score so that we can give
        // all sites a chance - this helps initial startup when a user submits
        // one or a few jobs
        if (glideinMetrics.getTotal() == 0) {
            siteScoreInfo.setMessage("Total number of glides is 0");
            siteScoreInfo.setScore(500);
            return siteScoreInfo;
        }

        // basic score, the maximum number of idle jobs
        int score = gateService.getSiteInfo().getMaxIdleCount();

        // idle jobs are not so good
        score = score - glideinMetrics.getPending() * 3;

        // running jobs are good
        score = score + glideinMetrics.getPending();

        // if we have a positive number, use the multiplication factor
        if (score > 0) {
            score = score * gateService.getSiteInfo().getMultiplier();
        }

        // when a lot of glideins are running, lower the score to spread the
        // jobs out between the sites
        if (score > 15 && glideinMetrics.getRunning() > 10 && glideinMetrics.getPending() < 5) {
            siteScoreInfo.setMessage("Score lowered to spread jobs out on available sites");
            siteScoreInfo.setScore(1);
            return siteScoreInfo;
        }
        return null;
    }

}

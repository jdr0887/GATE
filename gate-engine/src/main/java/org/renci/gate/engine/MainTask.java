package org.renci.gate.engine;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimerTask;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.gate.SiteInfo;
import org.renci.gate.SiteScoreInfo;
import org.renci.gate.config.GATEConfigurationService;
import org.renci.jlrm.LRMException;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.ClassAdvertisementFactory;
import org.renci.jlrm.condor.CondorJobStatusType;
import org.renci.jlrm.condor.cli.CondorCLIFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(MainTask.class);

    private ServiceTracker tracker;

    private GATEConfigurationService configService;

    private final Random random = new Random();

    public MainTask(ServiceTracker tracker, GATEConfigurationService configService) {
        super();
        this.tracker = tracker;
        this.configService = configService;
    }

    @Override
    public void run() {

        String condorHome = configService.getCoreProperties().getProperty(GATEConfigurationService.CONDOR_HOME);

        Map<String, GATEService> gateServiceMap = new HashMap<String, GATEService>();

        ServiceReference[] siteSelectorServiceRefArray = tracker.getServiceReferences();

        if (siteSelectorServiceRefArray != null) {
            for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                logger.info(serviceRef.toString());
                GATEService gateService = (GATEService) tracker.getService(serviceRef);
                if (gateService != null) {
                    gateServiceMap.put(gateService.getSiteInfo().getName(), gateService);
                }
            }
        }

        // get a snapshot of jobs across sites
        Map<String, List<GlideinMetric>> siteMetricsMap = new HashMap<String, List<GlideinMetric>>();
        for (String siteName : gateServiceMap.keySet()) {
            GATEService gateService = gateServiceMap.get(siteName);
            List<GlideinMetric> glideinMetricList;
            try {
                glideinMetricList = gateService.lookupMetrics();
            } catch (Exception e) {
                logger.error("There was a problem looking up metrics...doing nothing", e);
                return;
            }
            siteMetricsMap.put(gateService.getSiteInfo().getName(), glideinMetricList);
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

            int totalCondorJobs = jobMap.size();

            if (totalCondorJobs > 0) {

                int idleCondorJobs = 0;
                int runningCondorJobs = 0;

                for (String job : jobMap.keySet()) {
                    logger.debug("job: {}", job);
                    List<ClassAdvertisement> classAdList = jobMap.get(job);
                    for (ClassAdvertisement classAd : classAdList) {
                        logger.debug("classAd: {}", classAd);
                        if (ClassAdvertisementFactory.CLASS_AD_KEY_JOB_STATUS.equals(classAd.getKey())) {
                            int statusCode = Integer.valueOf(classAd.getValue().trim());
                            if (statusCode == CondorJobStatusType.IDLE.getCode()) {
                                ++idleCondorJobs;
                            }
                            if (statusCode == CondorJobStatusType.RUNNING.getCode()) {
                                ++runningCondorJobs;
                            }
                        }
                    }
                }

                logger.info("runningCondorJobs: {}", runningCondorJobs);
                logger.info("totalCondorJobs: {}", totalCondorJobs);

                // assume we need new glideins, and then run some tests to negate the assumptions
                boolean needGlidein = true;

                for (String siteName : gateServiceMap.keySet()) {

                    GATEService gateService = gateServiceMap.get(siteName);
                    SiteInfo siteInfo = gateService.getSiteInfo();
                    logger.info(siteInfo.toString());
                    Map<String, GlideinMetric> metricsMap = siteMetricsMap.get(siteInfo.getName());

                    for (String queue : metricsMap.keySet()) {
                        GlideinMetric metrics = metricsMap.get(queue);
                        logger.info(metrics.toString());

                        if (totalCondorJobs > 100 && (metrics.getRunning() > (totalCondorJobs * 0.5))) {
                            logger.info("Number of running glideins is probably enough for the workload.");
                            needGlidein = false;
                        } else if (runningCondorJobs > (totalCondorJobs * 0.75)) {
                            logger.info("Number of running jobs is high compared to idle jobs.");
                            needGlidein = false;
                        }
                    }

                }

                if (!needGlidein) {
                    logger.info("No glideins needed.");
                    return;
                }

                Map<String, SiteScoreInfo> siteScoreMap = new HashMap<String, SiteScoreInfo>();

                for (String siteName : gateServiceMap.keySet()) {

                    GATEService gateService = gateServiceMap.get(siteName);
                    SiteInfo siteInfo = gateService.getSiteInfo();
                    GlideinMetric metrics = siteMetricsMap.get(siteInfo.getName());

                    SiteScoreInfo siteScoreInfo = calculateScore(siteInfo, metrics);
                    if (siteScoreInfo != null && siteScoreInfo.getScore() > 0) {
                        siteScoreMap.put(gateService.getSiteInfo().getName(), siteScoreInfo);
                    }

                }

                List<Map.Entry<String, SiteScoreInfo>> list = new LinkedList<Map.Entry<String, SiteScoreInfo>>(
                        siteScoreMap.entrySet());

                logger.info("list.size(): {}", list.size());
                if (list.size() > 0) {

                    // sort list based on comparator...descending score
                    Collections.sort(list, new Comparator<Map.Entry<String, SiteScoreInfo>>() {
                        @Override
                        public int compare(Entry<String, SiteScoreInfo> o1, Entry<String, SiteScoreInfo> o2) {
                            return o2.getValue().getScore().compareTo(o1.getValue().getScore());
                        }
                    });

                    Map.Entry<String, SiteScoreInfo> winner = null;
                    if (list.size() <= 3) {
                        winner = list.get(0);
                    } else {
                        winner = list.get(random.nextInt(3));
                    }

                    GATEService gateService = gateServiceMap.get(winner.getKey());
                    SiteInfo siteInfo = gateService.getSiteInfo();
                    GlideinMetric metrics = siteMetricsMap.get(siteInfo.getName());

                    logger.info(winner.getValue().toString());

                    // calculate number of glideins to submit
                    double numToSubmit = 1;

                    numToSubmit = siteInfo.getMaxMultipleJobs();
                    numToSubmit -= metrics.getPending() * 0.4;
                    numToSubmit -= metrics.getRunning() * 0.4;
                    numToSubmit -= runningCondorJobs * 0.005;
                    numToSubmit += idleCondorJobs * 0.005;

                    if (numToSubmit <= 1 && metrics.getRunning() <= siteInfo.getMaxRunningCount()) {
                        numToSubmit = 1;
                    }

                    numToSubmit = Math.round(numToSubmit);
                    numToSubmit = Math.min(numToSubmit, siteInfo.getMaxMultipleJobs());
                    logger.info("Planning on submitting {} glideins in this iteration", numToSubmit);

                    for (int i = 0; i < numToSubmit; ++i) {
                        logger.info("Submitting glidein for {} to {}", System.getProperty("user.name"), winner.getKey());
                        gateService.postGlidein();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {

                // remove pending glideins
                for (String siteName : gateServiceMap.keySet()) {
                    GATEService gateService = gateServiceMap.get(siteName);
                    GlideinMetric glideinMetrics = siteMetricsMap.get(gateService.getSiteInfo().getName());
                    if (glideinMetrics.getTotal() > 0) {
                        gateService.deleteGlidein();
                    }
                }

            }
        }

    }

    private SiteScoreInfo calculateScore(SiteInfo siteInfo, GlideinMetric metrics) {
        SiteScoreInfo siteScoreInfo = new SiteScoreInfo();

        int maxTotal = siteInfo.getMaxIdleCount() + siteInfo.getMaxRunningCount();
        // first, check if some limits have been reached

        if (metrics.getTotal() >= maxTotal) {
            siteScoreInfo.setMessage("Total number of glideins has reached the limit of " + maxTotal);
            siteScoreInfo.setScore(0D);
            return siteScoreInfo;
        }

        if (metrics.getPending() >= siteInfo.getMaxIdleCount()) {
            siteScoreInfo.setMessage("Number of idle/pending glideins has reached the limit of "
                    + siteInfo.getMaxIdleCount());
            siteScoreInfo.setScore(0D);
            return siteScoreInfo;
        }

        // we want to start all sites at the same score so that we can give
        // all sites a chance - this helps initial startup when a user submits
        // one or a few jobs
        if (metrics.getTotal() == 0) {
            siteScoreInfo.setMessage("Total number of glideins: " + metrics.getTotal());
            siteScoreInfo.setScore(100D);
            return siteScoreInfo;
        }

        double score = 100;
        double pendingWeight = 9;
        // penalize for pending jobs
        for (int i = 1; i < metrics.getPending() + 1; ++i) {
            score -= i * pendingWeight;
        }
        logger.info("score = {}", score);

        double runningWeight = 4.5;
        // reward for pending jobs
        for (int i = 1; i < metrics.getRunning() + 1; ++i) {
            score += i * runningWeight;
        }
        logger.info("score = {}", score);

        // when a lot of glideins are running, lower the score to spread the
        // jobs out between the sites
        if (score > 100) {
            siteScoreInfo.setMessage("Score lowered to spread jobs out on available sites");
            siteScoreInfo.setScore(100D);
            return siteScoreInfo;
        }

        siteScoreInfo.setMessage("Total number of glideins: " + metrics.getTotal());
        siteScoreInfo.setScore(score);
        return siteScoreInfo;
    }

}

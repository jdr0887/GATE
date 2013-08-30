package org.renci.gate.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.ServiceReference;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.gate.SiteQueueScore;
import org.renci.jlrm.JLRMException;
import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.ClassAdvertisementFactory;
import org.renci.jlrm.condor.CondorJobStatusType;
import org.renci.jlrm.condor.cli.CondorLookupJobsByOwnerCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GATEEngineRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(GATEEngineRunnable.class);

    private final Random random = new Random();

    private GATEServiceTracker serviceTracker;

    public GATEEngineRunnable(GATEServiceTracker serviceTracker) {
        super();
        this.serviceTracker = serviceTracker;
    }

    @Override
    public void run() {
        logger.info("ENTERING run()");

        Map<String, GATEService> gateServiceMap = new HashMap<String, GATEService>();

        ServiceReference[] siteSelectorServiceRefArray = serviceTracker.getServiceReferences();

        if (siteSelectorServiceRefArray != null) {
            for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                Object service = serviceTracker.getService(serviceRef);
                logger.info("service.getClass().getName() = {}", service.getClass().getName());
                if (service instanceof GATEService) {
                    GATEService gateService = (GATEService) service;
                    Site site = gateService.getSite();
                    logger.info(site.toString());
                    gateServiceMap.put(site.getName(), gateService);
                }
            }
        }

        logger.info("gateServiceMap.size() == {}", gateServiceMap.size());

        String username = System.getProperty("user.name");

        // go get a snapshot of local jobs
        Map<String, List<ClassAdvertisement>> jobMap = new HashMap<String, List<ClassAdvertisement>>();
        try {
            CondorLookupJobsByOwnerCallable callable = new CondorLookupJobsByOwnerCallable(username);
            jobMap.putAll(callable.call());
        } catch (JLRMException e) {
            e.printStackTrace();
        }

        if (jobMap.size() == 0) {
            logger.warn("no local condor jobs");
            return;
        }

        int totalCondorJobs = jobMap.size();
        logger.info("totalCondorJobs: {}", totalCondorJobs);

        int idleCondorJobs = calculateJobCount(jobMap, CondorJobStatusType.IDLE);
        logger.info("idleCondorJobs: {}", idleCondorJobs);

        int runningCondorJobs = calculateJobCount(jobMap, CondorJobStatusType.RUNNING);
        logger.info("runningCondorJobs: {}", runningCondorJobs);

        Map<String, Integer> requiredSiteMetricsMap = calculateRequiredSiteCount(jobMap);

        // get a snapshot of jobs across sites & queues
        Map<String, Map<String, GlideinMetric>> siteQueueGlideinMetricsMap = globalMetricsLookup(gateServiceMap);

        boolean needGlidein = isGlideinNeeded(gateServiceMap, siteQueueGlideinMetricsMap, idleCondorJobs,
                runningCondorJobs, totalCondorJobs);

        if (!needGlidein) {
            logger.warn("No glideins needed.");
            return;
        }

        Map<String, Double> percentSiteRequiredJobOccuranceMap = new HashMap<String, Double>();
        Double percentSiteRequiredJobOccuranceScore = 0.0;
        for (String siteName : gateServiceMap.keySet()) {
            logger.info("siteName = {}", siteName);
            if (requiredSiteMetricsMap.containsKey(siteName)) {
                percentSiteRequiredJobOccuranceScore = (double) (requiredSiteMetricsMap.get(siteName) / requiredSiteMetricsMap
                        .size());
                logger.info("percentSiteRequiredJobOccuranceScore = {}", percentSiteRequiredJobOccuranceScore);
                percentSiteRequiredJobOccuranceMap.put(siteName, percentSiteRequiredJobOccuranceScore);
            }
        }

        List<SiteQueueScore> siteQueueScoreInfoList = new ArrayList<SiteQueueScore>();

        for (String siteName : gateServiceMap.keySet()) {
            GATEService gateService = gateServiceMap.get(siteName);
            Site siteInfo = gateService.getSite();
            Map<String, GlideinMetric> metricsMap = siteQueueGlideinMetricsMap.get(siteInfo.getName());

            Double percentSiteRequiredJobOccurance = 0D;
            if (percentSiteRequiredJobOccuranceMap.get(siteName) != null) {
                percentSiteRequiredJobOccurance = percentSiteRequiredJobOccuranceMap.get(siteName);
            }
            LocalCondorMetric localCondorMetrics = new LocalCondorMetric(siteName, idleCondorJobs, runningCondorJobs,
                    percentSiteRequiredJobOccurance);
            logger.info(localCondorMetrics.toString());

            if (localCondorMetrics.getSiteRequiredJobOccurance() == 1.0) {
                siteQueueScoreInfoList.clear();
                siteQueueScoreInfoList.addAll(calculate(gateService, siteInfo, metricsMap, localCondorMetrics));
                break;
            }

            siteQueueScoreInfoList.addAll(calculate(gateService, siteInfo, metricsMap, localCondorMetrics));
        }

        logger.info("siteQueueScoreInfoList.size(): {}", siteQueueScoreInfoList.size());
        if (siteQueueScoreInfoList.size() > 0) {

            // sort list based on comparator...descending score
            Collections.sort(siteQueueScoreInfoList, new Comparator<SiteQueueScore>() {
                @Override
                public int compare(SiteQueueScore o1, SiteQueueScore o2) {
                    return o2.getScore().compareTo(o1.getScore());
                }
            });

            SiteQueueScore winner = null;
            if (siteQueueScoreInfoList.size() <= 3) {
                winner = siteQueueScoreInfoList.get(0);
            } else {
                winner = siteQueueScoreInfoList.get(random.nextInt(3));
            }

            logger.info(winner.toString());

            if (winner.getScore() > 0) {
                Executors.newSingleThreadExecutor().execute(new SubmitGlideinRunnable(gateServiceMap, winner));
            }

        }

        if (jobMap != null && jobMap.size() == 0) {
            Executors.newSingleThreadExecutor().execute(
                    new KillGlideinRunnable(jobMap, gateServiceMap, siteQueueGlideinMetricsMap));
        }

    }

    private Map<String, Integer> calculateRequiredSiteCount(Map<String, List<ClassAdvertisement>> jobMap) {
        Map<String, Integer> requiredSiteScoreMap = new HashMap<String, Integer>();
        for (String job : jobMap.keySet()) {
            List<ClassAdvertisement> classAdList = jobMap.get(job);
            for (ClassAdvertisement classAd : classAdList) {
                if (ClassAdvertisementFactory.CLASS_AD_KEY_REQUIREMENTS.equalsIgnoreCase(classAd.getKey())) {
                    String requirements = classAd.getValue();
                    if (requirements.contains("TARGET.JLRM_SITE_NAME")) {
                        Pattern pattern = Pattern.compile("^.+JLRM_SITE_NAME == \"([\\S]+)\".+$");
                        Matcher matcher = pattern.matcher(requirements);
                        if (matcher.matches()) {
                            String requiredSiteName = matcher.group(1);
                            logger.debug("requiredSiteName = {}", requiredSiteName);
                            if (!requiredSiteScoreMap.containsKey(requiredSiteName)) {
                                requiredSiteScoreMap.put(requiredSiteName, 0);
                            } else {
                                requiredSiteScoreMap.put(requiredSiteName,
                                        requiredSiteScoreMap.get(requiredSiteName) + 1);
                            }
                        }
                    }
                }
            }
        }
        return requiredSiteScoreMap;
    }

    private int calculateJobCount(Map<String, List<ClassAdvertisement>> jobMap, CondorJobStatusType status) {
        int condorJobs = 0;
        for (String job : jobMap.keySet()) {
            List<ClassAdvertisement> classAdList = jobMap.get(job);
            for (ClassAdvertisement classAd : classAdList) {
                if (ClassAdvertisementFactory.CLASS_AD_KEY_JOB_STATUS.equalsIgnoreCase(classAd.getKey())) {
                    int statusCode = Integer.valueOf(classAd.getValue().trim());
                    if (statusCode == status.getCode()) {
                        ++condorJobs;
                    }
                }
            }
        }
        return condorJobs;
    }

    private Map<String, Map<String, GlideinMetric>> globalMetricsLookup(Map<String, GATEService> gateServiceMap) {
        logger.info("ENTERING globalMetricsLookup(Map<String, GATEService>)");

        Map<String, Map<String, GlideinMetric>> siteQueueGlideinMetricsMap = new HashMap<String, Map<String, GlideinMetric>>();

        for (String siteName : gateServiceMap.keySet()) {

            GATEService gateService = gateServiceMap.get(siteName);
            Map<String, GlideinMetric> glideinMetricMap = null;

            try {
                if (!gateService.isValid()) {
                    logger.warn("isValid() failure: {}", siteName);
                    continue;
                }
                glideinMetricMap = gateService.lookupMetrics();
                if (glideinMetricMap == null) {
                    logger.warn("null glideinMetricMap: {}", siteName);
                    continue;
                }
                Thread.sleep(3000);
            } catch (Exception e) {
                logger.error("There was a problem looking up metrics", e);
            }

            siteQueueGlideinMetricsMap.put(gateService.getSite().getName(), glideinMetricMap);
        }
        return siteQueueGlideinMetricsMap;
    }

    private boolean isGlideinNeeded(Map<String, GATEService> gateServiceMap,
            Map<String, Map<String, GlideinMetric>> siteQueueGlideinMetricsMap, int idleCondorJobs,
            int runningCondorJobs, int totalCondorJobs) {
        logger.info("ENTERING isGlideinNeeded(Map<String, GATEService>, Map<String, Map<String, GlideinMetric>>, int, int, int)");

        // assume we need new glideins, and then run some tests to negate the assumptions
        boolean needGlidein = true;

        int totalRunningGlideinJobs = 0;
        int totalPendingGlideinJobs = 0;
        int maxAllowableJobs = 0;

        try {

            for (String siteName : gateServiceMap.keySet()) {

                GATEService gateService = gateServiceMap.get(siteName);
                Site siteInfo = gateService.getSite();
                maxAllowableJobs += siteInfo.getMaxTotalPending() + siteInfo.getMaxTotalRunning();

                logger.info(siteInfo.toString());
                Map<String, GlideinMetric> metricsMap = siteQueueGlideinMetricsMap.get(siteInfo.getName());

                if (metricsMap == null) {
                    logger.warn("metricsMap is null for {}", siteName);
                    continue;
                }

                for (String queue : metricsMap.keySet()) {
                    logger.debug("queue: {}", queue);
                    GlideinMetric metrics = metricsMap.get(queue);
                    logger.info(metrics.toString());
                    totalRunningGlideinJobs += metrics.getRunning();
                    totalPendingGlideinJobs += metrics.getPending();
                }

            }

            logger.info("totalRunningGlideinJobs: {}", totalRunningGlideinJobs);
            logger.info("totalPendingGlideinJobs: {}", totalPendingGlideinJobs);
            logger.info("maxAllowableJobs: {}", maxAllowableJobs);

            int totalCurrentlySubmitted = totalRunningGlideinJobs + totalPendingGlideinJobs;
            logger.info("totalCurrentlySubmitted: {}", totalCurrentlySubmitted);

            if (idleCondorJobs == 0) {
                logger.info("No more idle local Condor jobs");
                needGlidein = false;
            }

            if (totalCurrentlySubmitted >= maxAllowableJobs) {
                logger.info("Total number of glideins has reached the limit of " + maxAllowableJobs);
                needGlidein = false;
            }

            if (totalRunningGlideinJobs > (totalCondorJobs * 0.4)) {
                logger.info("Number of running glideins is enough for the workload.");
                needGlidein = false;
            }

            if (runningCondorJobs > (totalCondorJobs * 0.8) && totalCurrentlySubmitted > 0) {
                logger.info("Number of running jobs is high compared to idle jobs.");
                needGlidein = false;
            }

        } catch (Exception e1) {
            logger.error("Error", e1);
        }
        return needGlidein;
    }

    private List<SiteQueueScore> calculate(GATEService gateService, Site siteInfo,
            Map<String, GlideinMetric> metricsMap, LocalCondorMetric localCondorMetrics) {
        logger.info("ENTERING calculate(GATEService, Site, Map<String, GlideinMetric>, LocalCondorMetrics)");
        List<SiteQueueScore> ret = new ArrayList<SiteQueueScore>();

        Map<String, Queue> siteQueueInfoMap = siteInfo.getQueueInfoMap();

        for (String queueName : siteQueueInfoMap.keySet()) {

            Queue queueInfo = siteQueueInfoMap.get(queueName);

            if (StringUtils.isNotEmpty(gateService.getActiveQueues())) {
                List<String> activeQueueList = Arrays.asList(gateService.getActiveQueues().split(","));
                if (!activeQueueList.contains(queueName)) {
                    logger.info("excluding \"{}\" queue due to not being active", queueName);
                    continue;
                }
            }

            SiteQueueScore siteScoreInfo = new SiteQueueScore();
            siteScoreInfo.setSiteName(siteInfo.getName());
            siteScoreInfo.setQueueName(queueName);

            GlideinMetric metrics = metricsMap.get(queueName);

            Integer numberToSubmit = calculateNumberToSubmit(siteInfo, queueInfo, metrics,
                    localCondorMetrics.getRunning(), localCondorMetrics.getIdle());
            siteScoreInfo.setNumberToSubmit(numberToSubmit);

            if (metrics.getTotal() == 0) {
                siteScoreInfo.setMessage("No glideins have been submitted yet");
                siteScoreInfo.setScore(200);
                ret.add(siteScoreInfo);
                continue;
            }

            logger.info(metrics.toString());

            if (metrics.getPending() >= siteInfo.getMaxTotalPending()) {
                logger.info("Pending job threshold has been met: {} of {}", metrics.getPending(),
                        siteInfo.getMaxTotalPending());
                siteScoreInfo.setMessage("No glideins needed...pending job threshold has been met");
                siteScoreInfo.setScore(0);
                ret.add(siteScoreInfo);
                continue;
            }

            int totalJobs = metrics.getRunning() + metrics.getPending();

            if (totalJobs == 0) {
                // we want to start all sites at the same score so that we can give
                // all sites a chance - this helps initial startup when a user submits
                // one or a few jobs
                siteScoreInfo.setMessage("Total number of glideins: " + totalJobs);
                siteScoreInfo.setScore(200);
                ret.add(siteScoreInfo);
                continue;
            }

            if (totalJobs >= queueInfo.getMaxJobLimit()) {
                // don't use this queue
                siteScoreInfo.setMessage("Queue is maxed out");
                siteScoreInfo.setScore(0);
                ret.add(siteScoreInfo);
                continue;
            }

            double score = 100;
            double pendingWeight = 6.5;
            for (int i = 1; i < metrics.getPending() + 1; ++i) {
                score -= i * pendingWeight;
            }
            logger.info("penalized score = {}", score);
            double runningWeight = 4.5;
            for (int i = 1; i < metrics.getRunning() + 1; ++i) {
                score += i * runningWeight;
            }
            logger.info("rewarded score = {}", score);
            score *= queueInfo.getWeight();
            logger.info("adjusted by queue weight = {}", score);
            if (localCondorMetrics.getSiteRequiredJobOccurance() > 0) {
                score += localCondorMetrics.getSiteRequiredJobOccurance() * 100;
                logger.info("adjusted by siteRequiredJobOccurance = {}", score);
            }

            // when a lot of glideins are running, lower the score to spread the
            // jobs out between the sites
            if (score > 200) {
                siteScoreInfo.setMessage("Score lowered to spread jobs out on available sites");
                siteScoreInfo.setScore(200);
                ret.add(siteScoreInfo);
                continue;
            }

            siteScoreInfo.setMessage("Total number of glideins: " + metrics.getTotal());
            siteScoreInfo.setScore(Long.valueOf(Math.round(score)).intValue());
            logger.info(siteScoreInfo.toString());
            ret.add(siteScoreInfo);

        }

        return ret;
    }

    private Integer calculateNumberToSubmit(Site siteInfo, Queue queueInfo, GlideinMetric metrics,
            int runningCondorJobs, int idleCondorJobs) {
        double numToSubmit = 1;
        numToSubmit = queueInfo.getMaxMultipleJobsToSubmit();
        if (metrics != null) {
            numToSubmit -= metrics.getPending() * 0.4;
            numToSubmit -= metrics.getRunning() * 0.4;
        }
        numToSubmit -= runningCondorJobs * 0.005;
        numToSubmit += idleCondorJobs * 0.005;

        if (numToSubmit <= 1 && (metrics != null && metrics.getRunning() <= siteInfo.getMaxTotalRunning())) {
            numToSubmit = 1;
        }

        numToSubmit = Math.round(numToSubmit);
        int ret = Double.valueOf(Math.min(numToSubmit, queueInfo.getMaxMultipleJobsToSubmit())).intValue();
        // return ret;
        return 1;
    }

}

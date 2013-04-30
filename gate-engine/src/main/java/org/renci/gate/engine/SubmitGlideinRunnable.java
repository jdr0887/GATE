package org.renci.gate.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.gate.SiteQueueScore;
import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.ClassAdvertisementFactory;
import org.renci.jlrm.condor.CondorJobStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmitGlideinRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(SubmitGlideinRunnable.class);

    private final Random random = new Random();

    private Map<String, List<ClassAdvertisement>> jobMap;

    private Map<String, GATEService> gateServiceMap;

    private Map<String, Map<String, GlideinMetric>> siteQueueGlideinMetricsMap;

    public SubmitGlideinRunnable(Map<String, List<ClassAdvertisement>> jobMap, Map<String, GATEService> gateServiceMap,
            Map<String, Map<String, GlideinMetric>> siteQueueGlideinMetricsMap) {
        super();
        this.jobMap = jobMap;
        this.gateServiceMap = gateServiceMap;
        this.siteQueueGlideinMetricsMap = siteQueueGlideinMetricsMap;
    }

    @Override
    public void run() {

        int totalCondorJobs = jobMap.size();

        int idleCondorJobs = 0;
        int runningCondorJobs = 0;

        Map<String, Integer> requiredSiteMetrics = new HashMap<String, Integer>();

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
                if (ClassAdvertisementFactory.CLASS_AD_KEY_REQUIREMENTS.equals(classAd.getKey())) {

                    String requirements = classAd.getValue();
                    if (requirements.contains("TARGET.JLRM_SITE_NAME")) {
                        Pattern pattern = Pattern.compile("^.+JLRM_SITE_NAME == \"([\\S]+)\".+$");
                        Matcher matcher = pattern.matcher(requirements);
                        if (matcher.matches()) {
                            String requiredSiteName = matcher.group(1);
                            if (!requiredSiteMetrics.containsKey(requiredSiteName)) {
                                requiredSiteMetrics.put(requiredSiteName, 0);
                            } else {
                                requiredSiteMetrics
                                        .put(requiredSiteName, requiredSiteMetrics.get(requiredSiteName) + 1);
                            }
                        }
                    }

                }
            }
        }

        logger.info("runningCondorJobs: {}", runningCondorJobs);
        logger.info("idleCondorJobs: {}", idleCondorJobs);

        // assume we need new glideins, and then run some tests to negate the assumptions
        boolean needGlidein = true;

        try {
            for (String siteName : gateServiceMap.keySet()) {

                if (!needGlidein) {
                    // some other site already established that we don't need more glideins
                    break;
                }

                GATEService gateService = gateServiceMap.get(siteName);
                Site siteInfo = gateService.getSite();
                logger.info(siteInfo.toString());
                Map<String, GlideinMetric> metricsMap = siteQueueGlideinMetricsMap.get(siteInfo.getName());

                int totalRunningGlideinJobs = 0;
                int totalPendingGlideinJobs = 0;

                for (String queue : metricsMap.keySet()) {
                    GlideinMetric metrics = metricsMap.get(queue);
                    logger.info(metrics.toString());
                    totalRunningGlideinJobs += metrics.getRunning();
                    totalPendingGlideinJobs += metrics.getPending();
                }

                logger.info("totalRunningGlideinJobs: {}", totalRunningGlideinJobs);
                logger.info("totalPendingGlideinJobs: {}", totalPendingGlideinJobs);

                int maxAllowableJobs = siteInfo.getMaxTotalPending() + siteInfo.getMaxTotalRunning();
                int totalCurrentlySubmitted = totalRunningGlideinJobs + totalPendingGlideinJobs;

                logger.info("maxAllowableJobs: {}", maxAllowableJobs);
                logger.info("totalSiteJobs: {}", totalCurrentlySubmitted);

                if (totalCurrentlySubmitted >= maxAllowableJobs) {
                    logger.info("Total number of glideins has reached the limit of " + maxAllowableJobs);
                    needGlidein = false;
                    continue;
                }

                if (totalRunningGlideinJobs > (totalCondorJobs * 0.6)) {
                    logger.info("Number of running glideins is probably enough for the workload.");
                    needGlidein = false;
                    continue;
                }

                if (runningCondorJobs > (totalCondorJobs * 0.6) && totalCurrentlySubmitted > 0) {
                    logger.info("Number of running jobs is high compared to idle jobs.");
                    needGlidein = false;
                    continue;
                }

                if (totalPendingGlideinJobs >= siteInfo.getMaxTotalPending()) {
                    logger.info("Pending job threshold has been met: {} of {}", totalPendingGlideinJobs,
                            siteInfo.getMaxTotalPending());
                    needGlidein = false;
                    continue;
                }

            }
        } catch (Exception e1) {
            logger.error("Error", e1);
        }

        if (!needGlidein) {
            logger.info("No glideins needed.");
            return;
        }

        List<SiteQueueScore> siteQueueScoreInfoList = new ArrayList<SiteQueueScore>();

        Map<String, Double> percentSiteRequiredJobOccuranceMap = new HashMap<String, Double>();
        Double percentSiteRequiredJobOccuranceScore = 0.0;
        for (String siteName : gateServiceMap.keySet()) {
            if (requiredSiteMetrics.containsKey(siteName)) {
                percentSiteRequiredJobOccuranceScore = (double) (requiredSiteMetrics.get(siteName) / requiredSiteMetrics
                        .size());
                percentSiteRequiredJobOccuranceMap.put(siteName, percentSiteRequiredJobOccuranceScore);
            }
        }

        logger.info("gateServiceMap.size(): {}", gateServiceMap.size());
        for (String siteName : gateServiceMap.keySet()) {
            GATEService gateService = gateServiceMap.get(siteName);
            Site siteInfo = gateService.getSite();
            Map<String, GlideinMetric> metricsMap = siteQueueGlideinMetricsMap.get(siteInfo.getName());
            LocalCondorMetric localCondorMetrics = new LocalCondorMetric(siteName, idleCondorJobs, runningCondorJobs,
                    percentSiteRequiredJobOccuranceMap.get(siteName));
            logger.info(localCondorMetrics.toString());
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

                GATEService gateService = gateServiceMap.get(winner.getSiteName());
                Site siteInfo = gateService.getSite();
                logger.debug(siteInfo.toString());
                Queue queueInfo = siteInfo.getQueueInfoMap().get(winner.getQueueName());
                logger.debug(queueInfo.toString());
                for (int i = 0; i < winner.getNumberToSubmit(); ++i) {
                    logger.info(String.format("Submitting %d of %d glideins for %s to %s:%s", i + 1,
                            winner.getNumberToSubmit(), siteInfo.getUsername(), winner.getSiteName(),
                            winner.getQueueName()));
                    gateService.createGlidein(queueInfo);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

    }

    private List<SiteQueueScore> calculate(GATEService gateService, Site siteInfo,
            Map<String, GlideinMetric> metricsMap, LocalCondorMetric localCondorMetrics) {
        logger.info("ENTERING calculate(GATEService, Site, Map<String, GlideinMetric>, LocalCondorMetrics)");
        List<SiteQueueScore> ret = new ArrayList<SiteQueueScore>();

        for (String queueName : siteInfo.getQueueInfoMap().keySet()) {

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

            Queue queueInfo = siteInfo.getQueueInfoMap().get(queueName);
            GlideinMetric metrics = metricsMap.get(queueName);

            if (metrics == null) {
                siteScoreInfo.setMessage("GlideinMetric is null...meaning no jobs have been submitted");
                siteScoreInfo.setScore(100);
                siteScoreInfo.setNumberToSubmit(queueInfo.getMaxMultipleJobsToSubmit());
                ret.add(siteScoreInfo);
                continue;
            }
            logger.info(metrics.toString());

            Integer numberToSubmit = calculateNumberToSubmit(siteInfo, queueInfo, metrics,
                    localCondorMetrics.getRunning(), localCondorMetrics.getIdle());
            siteScoreInfo.setNumberToSubmit(numberToSubmit);

            int totalJobs = metrics.getRunning() + metrics.getPending();

            if (totalJobs == 0) {
                // we want to start all sites at the same score so that we can give
                // all sites a chance - this helps initial startup when a user submits
                // one or a few jobs
                siteScoreInfo.setMessage("Total number of glideins: " + totalJobs);
                siteScoreInfo.setScore(100);
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

            Integer score = calculateScore(metrics, localCondorMetrics, queueInfo.getWeight());

            // when a lot of glideins are running, lower the score to spread the
            // jobs out between the sites
            if (score > 100) {
                siteScoreInfo.setMessage("Score lowered to spread jobs out on available sites");
                siteScoreInfo.setScore(100);
                ret.add(siteScoreInfo);
                continue;
            }

            siteScoreInfo.setMessage("Total number of glideins: " + metrics.getTotal());
            siteScoreInfo.setScore(score);
            logger.info(siteScoreInfo.toString());
            ret.add(siteScoreInfo);

        }

        return ret;
    }

    private Integer calculateScore(GlideinMetric metrics, LocalCondorMetric localCondorMetric, Double queueWeight) {
        double score = 100;
        double pendingWeight = 6.5;
        // penalize pending jobs
        for (int i = 1; i < metrics.getPending() + 1; ++i) {
            score -= i * pendingWeight;
        }
        logger.info("penalized = {}", score);

        double runningWeight = 4.5;
        // reward for pending jobs
        for (int i = 1; i < metrics.getRunning() + 1; ++i) {
            score += i * runningWeight;
        }
        logger.info("rewarded = {}", score);
        score *= queueWeight;
        logger.info("adjusted by queue weight = {}", score);
        score *= Math.max(localCondorMetric.getSiteRequiredJobOccurancePercentile(), 0.1) * 2;
        logger.info("adjusted by siteRequiredJobOccurancePercentile = {}", score);

        return Long.valueOf(Math.round(score)).intValue();
    }

    private Integer calculateNumberToSubmit(Site siteInfo, Queue queueInfo, GlideinMetric metrics,
            int runningCondorJobs, int idleCondorJobs) {

        double numToSubmit = 1;

        numToSubmit = queueInfo.getMaxMultipleJobsToSubmit();
        numToSubmit -= metrics.getPending() * 0.4;
        numToSubmit -= metrics.getRunning() * 0.4;
        numToSubmit -= runningCondorJobs * 0.005;
        numToSubmit += idleCondorJobs * 0.005;

        if (numToSubmit <= 1 && metrics.getRunning() <= siteInfo.getMaxTotalRunning()) {
            numToSubmit = 1;
        }

        numToSubmit = Math.round(numToSubmit);
        return Double.valueOf(Math.min(numToSubmit, queueInfo.getMaxMultipleJobsToSubmit())).intValue();
    }

}

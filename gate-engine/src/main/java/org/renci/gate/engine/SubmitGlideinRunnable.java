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

        Map<String, Integer> requiredSiteMetricsMap = new HashMap<String, Integer>();

        for (String job : jobMap.keySet()) {
            logger.info("job: {}", job);
            List<ClassAdvertisement> classAdList = jobMap.get(job);
            for (ClassAdvertisement classAd : classAdList) {
                logger.info("classAd.getKey() = {}", classAd.getKey());
                if (ClassAdvertisementFactory.CLASS_AD_KEY_JOB_STATUS.equalsIgnoreCase(classAd.getKey())) {
                    int statusCode = Integer.valueOf(classAd.getValue().trim());
                    if (statusCode == CondorJobStatusType.IDLE.getCode()) {
                        ++idleCondorJobs;
                    }
                    if (statusCode == CondorJobStatusType.RUNNING.getCode()) {
                        ++runningCondorJobs;
                    }
                }
                if (ClassAdvertisementFactory.CLASS_AD_KEY_REQUIREMENTS.equalsIgnoreCase(classAd.getKey())) {

                    String requirements = classAd.getValue();
                    logger.info("requirements = {}", requirements);
                    if (requirements.contains("TARGET.JLRM_SITE_NAME")) {
                        Pattern pattern = Pattern.compile("^.+JLRM_SITE_NAME == \"([\\S]+)\".+$");
                        Matcher matcher = pattern.matcher(requirements);
                        if (matcher.matches()) {
                            String requiredSiteName = matcher.group(1);
                            logger.info("requiredSiteName = {}", requiredSiteName);
                            if (!requiredSiteMetricsMap.containsKey(requiredSiteName)) {
                                requiredSiteMetricsMap.put(requiredSiteName, 0);
                            } else {
                                requiredSiteMetricsMap.put(requiredSiteName,
                                        requiredSiteMetricsMap.get(requiredSiteName) + 1);
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

                for (String queue : metricsMap.keySet()) {
                    GlideinMetric metrics = metricsMap.get(queue);
                    if (metrics != null) {
                        logger.info(metrics.toString());
                        totalRunningGlideinJobs += metrics.getRunning();
                        totalPendingGlideinJobs += metrics.getPending();
                    }
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

        if (!needGlidein) {
            logger.info("No glideins needed.");
            return;
        }

        List<SiteQueueScore> siteQueueScoreInfoList = new ArrayList<SiteQueueScore>();

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

        logger.info("gateServiceMap.size(): {}", gateServiceMap.size());
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

            Integer numberToSubmit = calculateNumberToSubmit(siteInfo, queueInfo, metrics,
                    localCondorMetrics.getRunning(), localCondorMetrics.getIdle());
            siteScoreInfo.setNumberToSubmit(numberToSubmit);

            if (metrics == null) {
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
        return Double.valueOf(Math.min(numToSubmit, queueInfo.getMaxMultipleJobsToSubmit())).intValue();
    }

}

package org.renci.gate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.renci.gate.AbstractGlideinSubmissionStrategy;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.gate.GlideinSubmissionBean;
import org.renci.gate.LocalCondorMetric;
import org.renci.gate.SiteQueueScore;
import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LargeGlideinSubmissionStrategy extends AbstractGlideinSubmissionStrategy {

    private final Logger logger = LoggerFactory.getLogger(LargeGlideinSubmissionStrategy.class);

    public LargeGlideinSubmissionStrategy() {
        super();
    }

    @Override
    public List<SiteQueueScore> calculateSiteQueueScores(GlideinSubmissionBean glideinSubmissionBean) {
        logger.debug("ENTERING calculateSiteQueueScores(LoadSubmissionStrategyBean)");

        Map<String, GATEService> gateServiceMap = glideinSubmissionBean.getGateServiceMap();
        Map<String, Integer> requiredSiteMetricsMap = glideinSubmissionBean.getRequiredSiteMetricsMap();

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

        List<GlideinMetric> siteQueueGlideinMetricList = glideinSubmissionBean.getSiteQueueGlideinMetricList();

        filter(gateServiceMap, siteQueueGlideinMetricList);

        List<SiteQueueScore> siteQueueScoreInfoList = new ArrayList<SiteQueueScore>();

        for (String siteName : gateServiceMap.keySet()) {
            GATEService gateService = gateServiceMap.get(siteName);
            Site site = gateService.getSite();

            List<Queue> siteQueueList = site.getQueueList();

            for (Queue queue : siteQueueList) {

                for (GlideinMetric glideinMetric : siteQueueGlideinMetricList) {

                    if (site.getName().equals(glideinMetric.getSiteName())
                            && queue.getName().equals(glideinMetric.getQueueName())) {

                        logger.info(glideinMetric.toString());

                        Double percentSiteRequiredJobOccurance = 0D;
                        if (percentSiteRequiredJobOccuranceMap.get(siteName) != null) {
                            percentSiteRequiredJobOccurance = percentSiteRequiredJobOccuranceMap.get(siteName);
                        }
                        LocalCondorMetric localCondorMetrics = new LocalCondorMetric(siteName,
                                glideinSubmissionBean.getIdleCondorJobs(),
                                glideinSubmissionBean.getRunningCondorJobs(), percentSiteRequiredJobOccurance);
                        logger.info(localCondorMetrics.toString());

                        if (localCondorMetrics.getSiteRequiredJobOccurance() == 1.0) {
                            siteQueueScoreInfoList.clear();
                            siteQueueScoreInfoList.add(calculate(site, queue, glideinMetric, localCondorMetrics));
                            break;
                        }

                        siteQueueScoreInfoList.add(calculate(site, queue, glideinMetric, localCondorMetrics));

                    }
                }

            }

        }
        // sort list based on comparator...descending score
        siteQueueScoreInfoList.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        return siteQueueScoreInfoList;
    }

    @Override
    public int calculateNumberToSubmit(GlideinSubmissionBean glideinSubmissionBean) {
        logger.debug("ENTERING calculateNumberToSubmit(LoadSubmissionStrategyBean)");
        return 1;
    }

    private SiteQueueScore calculate(Site site, Queue queue, GlideinMetric glideinMetric,
            LocalCondorMetric localCondorMetrics) {
        logger.debug("ENTERING calculate(Site, Queue, GlideinMetric, LocalCondorMetric)");

        SiteQueueScore siteScoreInfo = new SiteQueueScore();
        siteScoreInfo.setSiteName(glideinMetric.getSiteName());
        siteScoreInfo.setQueueName(glideinMetric.getQueueName());

        if (glideinMetric.getTotal() == 0) {
            siteScoreInfo.setMessage("No glideins have been submitted yet");
            siteScoreInfo.setScore(200);
            return siteScoreInfo;
        }

        double score = 100;
        double pendingWeight = 6.5;
        for (int i = 1; i < glideinMetric.getPending() + 1; ++i) {
            score -= i * pendingWeight;
        }
        logger.info("penalized score = {}", score);
        double runningWeight = 4.5;
        for (int i = 1; i < glideinMetric.getRunning() + 1; ++i) {
            score += i * runningWeight;
        }
        logger.info("rewarded score = {}", score);
        score *= queue.getWeight();
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
            return siteScoreInfo;
        }

        siteScoreInfo.setMessage("Total number of glideins: " + glideinMetric.getTotal());
        siteScoreInfo.setScore(Long.valueOf(Math.round(score)).intValue());
        logger.info(siteScoreInfo.toString());
        return siteScoreInfo;

    }

}

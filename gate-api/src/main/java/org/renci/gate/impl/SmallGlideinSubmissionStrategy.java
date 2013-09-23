package org.renci.gate.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

public class SmallGlideinSubmissionStrategy extends AbstractGlideinSubmissionStrategy {

    private final Logger logger = LoggerFactory.getLogger(SmallGlideinSubmissionStrategy.class);

    public SmallGlideinSubmissionStrategy() {
        super();
    }

    @Override
    public List<SiteQueueScore> calculateSiteQueueScores(GlideinSubmissionBean glideinSubmissionBean) {
        logger.info("ENTERING calculateSiteQueueScores(LoadSubmissionStrategyBean)");

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
            Site siteInfo = gateService.getSite();

            for (GlideinMetric glideinMetric : siteQueueGlideinMetricList) {
                logger.info(glideinMetric.toString());

                if (siteName.equals(glideinMetric.getSiteName())) {

                    Double percentSiteRequiredJobOccurance = 0D;
                    if (percentSiteRequiredJobOccuranceMap.get(siteName) != null) {
                        percentSiteRequiredJobOccurance = percentSiteRequiredJobOccuranceMap.get(siteName);
                    }
                    LocalCondorMetric localCondorMetrics = new LocalCondorMetric(siteName,
                            glideinSubmissionBean.getIdleCondorJobs(), glideinSubmissionBean.getRunningCondorJobs(),
                            percentSiteRequiredJobOccurance);
                    logger.info(localCondorMetrics.toString());

                    if (localCondorMetrics.getSiteRequiredJobOccurance() == 1.0) {
                        siteQueueScoreInfoList.clear();
                        siteQueueScoreInfoList.addAll(calculate(gateService, siteInfo, glideinMetric,
                                localCondorMetrics));
                        break;
                    }

                    siteQueueScoreInfoList.addAll(calculate(gateService, siteInfo, glideinMetric, localCondorMetrics));

                }
            }

        }

        // sort list based on comparator...descending score
        Collections.sort(siteQueueScoreInfoList, new Comparator<SiteQueueScore>() {
            @Override
            public int compare(SiteQueueScore o1, SiteQueueScore o2) {
                return o2.getScore().compareTo(o1.getScore());
            }
        });

        SiteQueueScore winner = null;
        if (siteQueueScoreInfoList.size() > 0) {
            winner = siteQueueScoreInfoList.get(0);
        }

        siteQueueScoreInfoList.clear();

        if (winner == null) {
            logger.warn("no winner!!!");
            return siteQueueScoreInfoList;
        }

        siteQueueScoreInfoList.add(winner);

        return siteQueueScoreInfoList;
    }

    @Override
    public int calculateNumberToSubmit(GlideinSubmissionBean glideinSubmissionBean) {
        logger.info("ENTERING calculateNumberToSubmit(LoadSubmissionStrategyBean)");
        return 1;
    }

    private List<SiteQueueScore> calculate(GATEService gateService, Site site, GlideinMetric glideinMetric,
            LocalCondorMetric localCondorMetrics) {
        logger.info("ENTERING calculate(GATEService, Site, Map<String, GlideinMetric>, LocalCondorMetrics)");
        List<SiteQueueScore> ret = new ArrayList<SiteQueueScore>();

        List<Queue> siteQueueList = site.getQueueList();

        for (Queue queue : siteQueueList) {

            if (!site.getName().equals(glideinMetric.getSiteName())
                    && !queue.getName().equals(glideinMetric.getQueueName())) {
                continue;
            }

            SiteQueueScore siteScoreInfo = new SiteQueueScore();
            siteScoreInfo.setSiteName(site.getName());
            siteScoreInfo.setQueueName(queue.getName());

            if (glideinMetric.getTotal() == 0) {
                siteScoreInfo.setMessage("No glideins have been submitted yet");
                siteScoreInfo.setScore(200);
                ret.add(siteScoreInfo);
                continue;
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
                ret.add(siteScoreInfo);
                continue;
            }

            siteScoreInfo.setMessage("Total number of glideins: " + glideinMetric.getTotal());
            siteScoreInfo.setScore(Long.valueOf(Math.round(score)).intValue());
            logger.info(siteScoreInfo.toString());
            ret.add(siteScoreInfo);

        }

        return ret;
    }

}

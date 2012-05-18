package org.renci.gate.engine;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.renci.gate.GlideinMetrics;
import org.renci.gate.SiteInfo;
import org.renci.gate.SiteScoreInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scratch {

    private final Logger logger = LoggerFactory.getLogger(Scratch.class);

    public Scratch() {
        super();
    }

    @Test
    public void testMapSort() {

        Map<String, SiteScoreInfo> siteScoreMap = new HashMap<String, SiteScoreInfo>();
        siteScoreMap.put("qwer", new SiteScoreInfo(5D, "qwer"));
        siteScoreMap.put("asdf", new SiteScoreInfo(1D, "asdf"));
        siteScoreMap.put("zxcv", new SiteScoreInfo(3D, "zxcv"));
        siteScoreMap.put("qwerasdfzxcv", new SiteScoreInfo(7D, "qwerasdfzxcv"));

        List<Map.Entry<String, SiteScoreInfo>> list = new LinkedList<Map.Entry<String, SiteScoreInfo>>(
                siteScoreMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, SiteScoreInfo>>() {
            @Override
            public int compare(Entry<String, SiteScoreInfo> o1, Entry<String, SiteScoreInfo> o2) {
                return o2.getValue().getScore().compareTo(o1.getValue().getScore());
            }
        });

        Map.Entry<String, SiteScoreInfo> winner = list.get(0);
        assertTrue(winner.getKey().equals("qwerasdfzxcv"));

    }

    @Test
    public void testNumberToSubmit() {
        SiteInfo siteInfo = new SiteInfo();
        siteInfo.setMaxIdleCount(9);
        siteInfo.setMaxMultipleJobs(3);
        siteInfo.setMaxNoClaimTime(60);
        siteInfo.setMaxQueueTime(1440);
        siteInfo.setMaxRunningCount(40);
        siteInfo.setMaxRunTime(2880);

        logger.info("Number To submit: {}", calculateNumberToSubmit(20, 0, siteInfo, new GlideinMetrics(0, 0)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(100, 0, siteInfo, new GlideinMetrics(0, 0)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(100, 10, siteInfo, new GlideinMetrics(0, 0)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(100, 10, siteInfo, new GlideinMetrics(1, 2)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(100, 20, siteInfo, new GlideinMetrics(2, 4)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(100, 30, siteInfo, new GlideinMetrics(4, 4)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(100, 30, siteInfo, new GlideinMetrics(6, 4)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(100, 40, siteInfo, new GlideinMetrics(8, 4)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(30, 5, siteInfo, new GlideinMetrics(1, 2)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(50, 10, siteInfo, new GlideinMetrics(2, 5)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(80, 12, siteInfo, new GlideinMetrics(4, 6)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(80, 12, siteInfo, new GlideinMetrics(10, 2)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(80, 12, siteInfo, new GlideinMetrics(30, 2)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(120, 12, siteInfo, new GlideinMetrics(40, 10)));
        logger.info("Number To submit: {}", calculateNumberToSubmit(2, 50, siteInfo, new GlideinMetrics(40, 10)));

    }

    private Long calculateNumberToSubmit(int idleCondorJobs, int runningCondorJobs, SiteInfo siteInfo,
            GlideinMetrics metrics) {
        logger.info(metrics.toString());
        double numToSubmit = siteInfo.getMaxMultipleJobs();
        numToSubmit -= metrics.getPending() * 0.4;
        numToSubmit -= metrics.getRunning() * 0.4;
        numToSubmit -= runningCondorJobs * 0.005;
        numToSubmit += idleCondorJobs * 0.005;

        if (numToSubmit <= 1 && metrics.getRunning() <= siteInfo.getMaxRunningCount()) {
            numToSubmit = 1;
        }

        // numToSubmit = Math.round(0.1 * idleCondorJobs);
        numToSubmit = Math.min(numToSubmit, siteInfo.getMaxMultipleJobs());
        // int totalGlideinsAllowed = siteInfo.getMaxRunningCount() + siteInfo.getMaxIdleCount();
        // numToSubmit = Math.min(numToSubmit, totalGlideinsAllowed - metrics.getTotal());
        return Math.round(numToSubmit);
    }

    @Test
    public void testScoring() {

        SiteInfo siteInfo = new SiteInfo();
        siteInfo.setMaxIdleCount(9);
        siteInfo.setMaxMultipleJobs(3);
        siteInfo.setMaxNoClaimTime(60);
        siteInfo.setMaxQueueTime(1440);
        siteInfo.setMaxRunningCount(40);
        siteInfo.setMaxRunTime(2880);

        SiteScoreInfo siteScoreInfo = calculateScore(siteInfo, new GlideinMetrics(0, 0));
        logger.info(siteScoreInfo.toString());

        siteScoreInfo = calculateScore(siteInfo, new GlideinMetrics(0, 2));
        logger.info(siteScoreInfo.toString());

        siteScoreInfo = calculateScore(siteInfo, new GlideinMetrics(1, 2));
        logger.info(siteScoreInfo.toString());

        siteScoreInfo = calculateScore(siteInfo, new GlideinMetrics(2, 2));
        logger.info(siteScoreInfo.toString());

        siteScoreInfo = calculateScore(siteInfo, new GlideinMetrics(4, 2));
        logger.info(siteScoreInfo.toString());

        siteScoreInfo = calculateScore(siteInfo, new GlideinMetrics(12, 8));
        logger.info(siteScoreInfo.toString());

        siteScoreInfo = calculateScore(siteInfo, new GlideinMetrics(12, 0));
        logger.info(siteScoreInfo.toString());

    }

    private SiteScoreInfo calculateScore(SiteInfo siteInfo, GlideinMetrics metrics) {
        logger.info(metrics.toString());
        SiteScoreInfo siteScoreInfo = new SiteScoreInfo();

        int maxTotal = siteInfo.getMaxRunningCount() + siteInfo.getMaxIdleCount();
        // first, check if some limits have been reached
        if (metrics.getTotal() >= maxTotal) {
            siteScoreInfo.setMessage("Total number of glideins has reached the limit of " + maxTotal);
            siteScoreInfo.setScore(0D);
            return siteScoreInfo;
        }

        int remainingPending = siteInfo.getMaxIdleCount() - metrics.getPending();
        logger.info("remainingPending = {}", remainingPending);

        if (remainingPending <= 0) {
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

        int remainingRunning = siteInfo.getMaxRunningCount() - metrics.getRunning();
        logger.info("remainingRunning = {}", remainingRunning);

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

        // double proportionPending = remainingPending / siteInfo.getMaxIdleCount();
        // double proportionRunning = metrics.getRunning() / siteInfo.getMaxRunningCount();
        // score = (proportionPending + proportionRunning) * 100;

        if (score > 100) {
            siteScoreInfo.setMessage("Score lowered to spread jobs out on available sites");
            siteScoreInfo.setScore(100D);
            return siteScoreInfo;
        }

        siteScoreInfo.setMessage("Total number of glideins: " + metrics.getTotal());
        siteScoreInfo.setScore(score);

        logger.info("---------------");
        return siteScoreInfo;
    }

}

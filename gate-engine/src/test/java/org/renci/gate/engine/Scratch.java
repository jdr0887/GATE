package org.renci.gate.engine;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.renci.gate.GlideinMetric;
import org.renci.gate.SiteQueueScore;
import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scratch {

    private final Logger logger = LoggerFactory.getLogger(Scratch.class);

    public Scratch() {
        super();
    }

    @Test
    public void testMapSort() {

        List<SiteQueueScore> siteScoreList = new ArrayList<SiteQueueScore>();
        siteScoreList.add(new SiteQueueScore("kure", "week", 5, "asdf", 1));
        siteScoreList.add(new SiteQueueScore("kure", "pseq_prod", 7, "qwer", 1));
        siteScoreList.add(new SiteQueueScore("kure", "pseq_tcga", 6, "qwer", 1));
        siteScoreList.add(new SiteQueueScore("blueridge", "serial", 5, "asdf", 1));

        Collections.sort(siteScoreList, new Comparator<SiteQueueScore>() {
            @Override
            public int compare(SiteQueueScore o1, SiteQueueScore o2) {
                return o2.getScore().compareTo(o1.getScore());
            }
        });

        SiteQueueScore winner = siteScoreList.get(0);
        assertTrue(winner.getQueueName().equals("pseq_prod"));

    }

    @Test
    public void testNumberToDelete() {

        Map<String, Queue> queueMap = new HashMap<String, Queue>();

        Queue pseqProdQueueInfo = new Queue();
        pseqProdQueueInfo.setName("pseq_prod");
        pseqProdQueueInfo.setWeight(1D);
        pseqProdQueueInfo.setMaxJobLimit(30);
        pseqProdQueueInfo.setMaxMultipleJobsToSubmit(4);
        pseqProdQueueInfo.setPendingTime(1440);
        pseqProdQueueInfo.setRunTime(2880);
        queueMap.put("pseq_prod", pseqProdQueueInfo);

        Site siteInfo = new Site();
        siteInfo.setMaxTotalPending(6);
        siteInfo.setMaxTotalRunning(40);
        siteInfo.setQueueInfoMap(queueMap);

        logger.info("Number To delete: {}",
                calculateNumberToDelete(80, 100, siteInfo, pseqProdQueueInfo, new GlideinMetric(10, 0, "pseq_prod")));
        logger.info("Number To delete: {}",
                calculateNumberToDelete(20, 100, siteInfo, pseqProdQueueInfo, new GlideinMetric(9, 0, "pseq_prod")));
        logger.info("Number To delete: {}",
                calculateNumberToDelete(0, 80, siteInfo, pseqProdQueueInfo, new GlideinMetric(5, 0, "pseq_prod")));

        logger.info("Number To delete: {}",
                calculateNumberToDelete(1000, 400, siteInfo, pseqProdQueueInfo, new GlideinMetric(40, 0, "pseq_prod")));
        logger.info("Number To delete: {}",
                calculateNumberToDelete(100, 400, siteInfo, pseqProdQueueInfo, new GlideinMetric(40, 0, "pseq_prod")));

    }

    private Integer calculateNumberToDelete(int idleCondorJobs, int runningCondorJobs, Site siteInfo, Queue queueInfo,
            GlideinMetric metrics) {
        logger.info("idleCondorJobs: {}, runningCondorJobs: {}", idleCondorJobs, runningCondorJobs);
        logger.info("metrics.getPending(): {}, metrics.getRunning(): {}", metrics.getPending(), metrics.getRunning());
        double numToDelete = 1;
        numToDelete += metrics.getRunning() * 0.35;
        numToDelete += runningCondorJobs * 0.003;
        numToDelete -= idleCondorJobs * 0.05;
        numToDelete = Math.round(numToDelete);
        if (Math.abs(numToDelete) != numToDelete && numToDelete != 0) {
            return 0;
        }
        return Double.valueOf(numToDelete).intValue();
    }

    @Test
    public void testNumberToSubmit() {

        Map<String, Queue> queueMap = new HashMap<String, Queue>();

        Queue pseqProdQueueInfo = new Queue();
        pseqProdQueueInfo.setName("pseq_prod");
        pseqProdQueueInfo.setWeight(1D);
        pseqProdQueueInfo.setMaxJobLimit(30);
        pseqProdQueueInfo.setMaxMultipleJobsToSubmit(4);
        pseqProdQueueInfo.setPendingTime(1440);
        pseqProdQueueInfo.setRunTime(2880);
        queueMap.put("pseq_prod", pseqProdQueueInfo);

        Queue weekQueueInfo = new Queue();
        weekQueueInfo.setName("week");
        weekQueueInfo.setWeight(0.8D);
        weekQueueInfo.setMaxJobLimit(30);
        weekQueueInfo.setMaxMultipleJobsToSubmit(2);
        weekQueueInfo.setPendingTime(1440);
        weekQueueInfo.setRunTime(2880);
        queueMap.put("week", weekQueueInfo);

        Site siteInfo = new Site();
        siteInfo.setMaxTotalPending(6);
        siteInfo.setMaxTotalRunning(40);
        siteInfo.setQueueInfoMap(queueMap);

        logger.info("Number To submit: {}", calculateNumberToSubmit(2, 0, siteInfo, pseqProdQueueInfo, null));
        logger.info("Number To submit: {}", calculateNumberToSubmit(20, 0, siteInfo, pseqProdQueueInfo, null));
        logger.info("Number To submit: {}",
                calculateNumberToSubmit(20, 0, siteInfo, pseqProdQueueInfo, new GlideinMetric(0, 0, "pseq_prod")));
        logger.info("Number To submit: {}",
                calculateNumberToSubmit(100, 0, siteInfo, pseqProdQueueInfo, new GlideinMetric(0, 0, "pseq_prod")));
        logger.info("Number To submit: {}",
                calculateNumberToSubmit(100, 0, siteInfo, pseqProdQueueInfo, new GlideinMetric(0, 4, "pseq_prod")));
        logger.info("Number To submit: {}",
                calculateNumberToSubmit(100, 0, siteInfo, pseqProdQueueInfo, new GlideinMetric(4, 0, "pseq_prod")));
        logger.info("Number To submit: {}",
                calculateNumberToSubmit(100, 4, siteInfo, pseqProdQueueInfo, new GlideinMetric(4, 0, "pseq_prod")));
        logger.info("Number To submit: {}",
                calculateNumberToSubmit(100, 4, siteInfo, pseqProdQueueInfo, new GlideinMetric(4, 3, "pseq_prod")));
        logger.info("Number To submit: {}",
                calculateNumberToSubmit(100, 20, siteInfo, pseqProdQueueInfo, new GlideinMetric(4, 5, "pseq_prod")));
        logger.info("Number To submit: {}",
                calculateNumberToSubmit(100, 20, siteInfo, pseqProdQueueInfo, new GlideinMetric(4, 6, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(100, 20, siteInfo, pseqProdQueueInfo, new GlideinMetric(4, 2, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(100, 20, siteInfo, pseqProdQueueInfo, new GlideinMetric(4, 2, "pseq_prod")));

        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(100, 10, siteInfo, pseqProdQueueInfo, new GlideinMetric(1, 2, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(100, 20, siteInfo, pseqProdQueueInfo, new GlideinMetric(2, 4, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(100, 30, siteInfo, pseqProdQueueInfo, new GlideinMetric(4, 4, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(100, 30, siteInfo, pseqProdQueueInfo, new GlideinMetric(6, 4, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(100, 40, siteInfo, pseqProdQueueInfo, new GlideinMetric(8, 4, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(30, 5, siteInfo, pseqProdQueueInfo, new GlideinMetric(1, 2, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(50, 10, siteInfo, pseqProdQueueInfo, new GlideinMetric(2, 5, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(80, 12, siteInfo, pseqProdQueueInfo, new GlideinMetric(4, 6, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(80, 12, siteInfo, pseqProdQueueInfo, new GlideinMetric(10, 2, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(80, 12, siteInfo, pseqProdQueueInfo, new GlideinMetric(30, 2, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(120, 12, siteInfo, pseqProdQueueInfo, new GlideinMetric(40, 10, "pseq_prod")));
        // logger.info("Number To submit: {}",
        // calculateNumberToSubmit(2, 50, siteInfo, pseqProdQueueInfo, new GlideinMetric(40, 10, "pseq_prod")));

    }

    private Integer calculateNumberToSubmit(int idleCondorJobs, int runningCondorJobs, Site siteInfo, Queue queueInfo,
            GlideinMetric metrics) {
        logger.info("idleCondorJobs: {}, runningCondorJobs: {}", idleCondorJobs, runningCondorJobs);
        double numToSubmit = 1;
        numToSubmit = queueInfo.getMaxMultipleJobsToSubmit();
        if (metrics != null) {
            logger.info("metrics.getPending(): {}, metrics.getRunning(): {}", metrics.getPending(),
                    metrics.getRunning());
            numToSubmit -= metrics.getPending() * 0.4;
            numToSubmit -= metrics.getRunning() * 0.4;
        }
        numToSubmit -= runningCondorJobs * 0.005;
        numToSubmit += idleCondorJobs * 0.005;

        if (numToSubmit <= 1 && (metrics != null && metrics.getRunning() <= siteInfo.getMaxTotalRunning())) {
            numToSubmit = 1;
        }

        numToSubmit = Math.round(numToSubmit);

        Integer ret = Double.valueOf(Math.min(numToSubmit, queueInfo.getMaxMultipleJobsToSubmit())).intValue();
        return ret;
    }

    @Test
    public void testScoring() {

        Site kureSiteInfo = new Site();
        kureSiteInfo.setName("Kure");
        kureSiteInfo.setMaxNoClaimTime(60);
        Map<String, Queue> queueMap = new HashMap<String, Queue>();

        Queue pseqProdQueueInfo = new Queue();
        pseqProdQueueInfo.setName("pseq_prod");
        pseqProdQueueInfo.setWeight(1D);
        pseqProdQueueInfo.setMaxJobLimit(30);
        pseqProdQueueInfo.setMaxMultipleJobsToSubmit(2);
        pseqProdQueueInfo.setPendingTime(1440);
        pseqProdQueueInfo.setRunTime(2880);
        queueMap.put("pseq_prod", pseqProdQueueInfo);

        Queue weekQueueInfo = new Queue();
        weekQueueInfo.setName("week");
        weekQueueInfo.setWeight(0.8D);
        weekQueueInfo.setMaxJobLimit(30);
        weekQueueInfo.setMaxMultipleJobsToSubmit(4);
        weekQueueInfo.setPendingTime(1440);
        weekQueueInfo.setRunTime(2880);
        queueMap.put("week", weekQueueInfo);
        kureSiteInfo.setQueueInfoMap(queueMap);

        Site topsailSiteInfo = new Site();
        topsailSiteInfo.setName("Topsail");
        topsailSiteInfo.setMaxNoClaimTime(60);
        queueMap = new HashMap<String, Queue>();

        Queue queue16QueueInfo = new Queue();
        queue16QueueInfo.setName("queue16");
        queue16QueueInfo.setWeight(1D);
        queue16QueueInfo.setMaxJobLimit(30);
        queue16QueueInfo.setMaxMultipleJobsToSubmit(2);
        queue16QueueInfo.setPendingTime(1440);
        queue16QueueInfo.setRunTime(2880);
        queueMap.put("queue16", queue16QueueInfo);
        topsailSiteInfo.setQueueInfoMap(queueMap);

        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(0, 2, pseqProdQueueInfo.getName()), 1.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(0, 2, queue16QueueInfo.getName()), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(2, 2, queue16QueueInfo.getName()), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(2, 0, queue16QueueInfo.getName()), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(4, 0, queue16QueueInfo.getName()), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(6, 0, queue16QueueInfo.getName()), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(0, 2, pseqProdQueueInfo.getName()), 0.2);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(0, 2, queue16QueueInfo.getName()), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(2, 0, pseqProdQueueInfo.getName()), 0.25);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(2, 0, queue16QueueInfo.getName()), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(2, 2, pseqProdQueueInfo.getName()), 0.3);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(2, 2, queue16QueueInfo.getName()), 0.0);
        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(4, 0, pseqProdQueueInfo.getName()), 0.35);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(4, 0, queue16QueueInfo.getName()), 0.0);
        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(4, 2, pseqProdQueueInfo.getName()), 0.4);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(4, 2, queue16QueueInfo.getName()), 0.0);
        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(4, 4, pseqProdQueueInfo.getName()), 0.45);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(4, 4, queue16QueueInfo.getName()), 0.0);
        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(4, 5, pseqProdQueueInfo.getName()), 0.5);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(4, 5, queue16QueueInfo.getName()), 0.0);
        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(4, 6, pseqProdQueueInfo.getName()), 0.55);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(4, 6, queue16QueueInfo.getName()), 0.0);
        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(4, 6, pseqProdQueueInfo.getName()), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(4, 6, queue16QueueInfo.getName()), 0.0);
        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(12, 8, pseqProdQueueInfo.getName()), 0.6);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(12, 8, queue16QueueInfo.getName()), 0.0);
        calculateScore(kureSiteInfo, pseqProdQueueInfo, new GlideinMetric(12, 0, pseqProdQueueInfo.getName()), 0.65);
        calculateScore(topsailSiteInfo, queue16QueueInfo, new GlideinMetric(12, 0, queue16QueueInfo.getName()), 0.0);

    }

    private SiteQueueScore calculateScore(Site siteInfo, Queue queueInfo, GlideinMetric metrics,
            Double siteRequiredJobOccurance) {
        logger.info("---------------");
        SiteQueueScore siteScoreInfo = new SiteQueueScore();
        siteScoreInfo.setSiteName(siteInfo.getName());
        siteScoreInfo.setQueueName(queueInfo.getName());

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
        score *= queueInfo.getWeight();
        logger.info("adjusted by queue weight = {}", score);
        if (siteRequiredJobOccurance > 0) {
            score += siteRequiredJobOccurance * 140;
            logger.info("adjusted by siteRequiredJobOccurancePercentile = {}", score);
        }

        // double proportionPending = remainingPending / siteInfo.getMaxIdleCount();
        // double proportionRunning = metrics.getRunning() / siteInfo.getMaxRunningCount();
        // score = (proportionPending + proportionRunning) * 100;

        if (score > 200) {
            siteScoreInfo.setMessage("Score lowered to spread jobs out on available sites");
            siteScoreInfo.setScore(200);
            logger.info(metrics.toString());
            logger.info(siteScoreInfo.toString());
            return siteScoreInfo;
        }

        siteScoreInfo.setMessage("Total number of glideins: " + metrics.getTotal());
        siteScoreInfo.setScore(Long.valueOf(Math.round(score)).intValue());
        logger.info(metrics.toString());
        logger.info(siteScoreInfo.toString());

        return siteScoreInfo;
    }

    @Test
    public void testParseRequirements() {
        String line = "ClusterId=397494,JobStatus=2,Requirements=( ( Arch == \"X86_64\" ) && ( OpSys == \"LINUX\" ) && ( Memory >= 500 ) && ( Disk >= 0 ) && ( TARGET.JLRM_SITE_NAME == \"Kure\" ) && ( TARGET.JLRM_USER == \"rc_renci.svc\" ) && ( TARGET.IS_GLIDEIN == true ) ) && ( TARGET.Disk >= RequestDisk ) && ( TARGET.Memory >= RequestMemory ) && ( TARGET.HasFileTransfer )";
        Pattern pattern = Pattern.compile("^.+JLRM_SITE_NAME == \"([\\S]+)\".+$");
        Matcher matcher = pattern.matcher(line);
        if (!matcher.matches()) {
            System.out.println("No match");
        } else {
            System.out.println(matcher.group(1));
        }

    }

}

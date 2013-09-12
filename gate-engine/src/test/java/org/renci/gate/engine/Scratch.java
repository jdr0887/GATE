package org.renci.gate.engine;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
        siteScoreList.add(new SiteQueueScore("Kure", "week", 5, "asdf"));
        siteScoreList.add(new SiteQueueScore("Kure", "pseq_prod", 7, "qwer"));
        siteScoreList.add(new SiteQueueScore("Kure", "pseq_tcga", 6, "qwer"));
        siteScoreList.add(new SiteQueueScore("Blueridge", "serial", 5, "asdf"));

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

        Site siteInfo = new Site();
        siteInfo.setName("Kure");

        List<Queue> queueList = new ArrayList<Queue>();

        Queue pseqProdQueueInfo = new Queue();
        pseqProdQueueInfo.setName("pseq_prod");
        pseqProdQueueInfo.setWeight(1D);
        pseqProdQueueInfo.setMaxPending(4);
        pseqProdQueueInfo.setMaxRunning(30);
        pseqProdQueueInfo.setRunTime(2880L);
        queueList.add(pseqProdQueueInfo);

        siteInfo.setQueueList(queueList);

        logger.info(
                "Number To delete: {}",
                calculateNumberToDelete(80, 100, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 10, 0)));
        logger.info(
                "Number To delete: {}",
                calculateNumberToDelete(20, 100, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 9, 0)));
        logger.info(
                "Number To delete: {}",
                calculateNumberToDelete(0, 80, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 5, 0)));

        logger.info(
                "Number To delete: {}",
                calculateNumberToDelete(1000, 400, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 40, 0)));
        logger.info(
                "Number To delete: {}",
                calculateNumberToDelete(100, 400, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 40, 0)));

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

        Site siteInfo = new Site();

        List<Queue> queueList = new ArrayList<Queue>();

        Queue pseqProdQueueInfo = new Queue();
        pseqProdQueueInfo.setName("pseq_prod");
        pseqProdQueueInfo.setWeight(1D);
        pseqProdQueueInfo.setMaxPending(2);
        pseqProdQueueInfo.setMaxRunning(12);
        pseqProdQueueInfo.setRunTime(5760L);
        queueList.add(pseqProdQueueInfo);

        Queue weekQueueInfo = new Queue();
        weekQueueInfo.setName("week");
        weekQueueInfo.setWeight(0.8D);
        weekQueueInfo.setMaxPending(2);
        weekQueueInfo.setMaxRunning(12);
        weekQueueInfo.setRunTime(2880L);
        queueList.add(weekQueueInfo);

        siteInfo.setQueueList(queueList);

        logger.info("Number To submit: {}", calculateNumberToSubmit(2, 0, siteInfo, pseqProdQueueInfo, null));
        logger.info("Number To submit: {}", calculateNumberToSubmit(20, 0, siteInfo, pseqProdQueueInfo, null));
        logger.info(
                "Number To submit: {}",
                calculateNumberToSubmit(20, 0, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 0, 0)));
        logger.info(
                "Number To submit: {}",
                calculateNumberToSubmit(100, 0, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 0, 0)));
        logger.info(
                "Number To submit: {}",
                calculateNumberToSubmit(100, 0, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 0, 4)));
        logger.info(
                "Number To submit: {}",
                calculateNumberToSubmit(100, 0, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 4, 0)));
        logger.info(
                "Number To submit: {}",
                calculateNumberToSubmit(100, 4, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 4, 0)));
        logger.info(
                "Number To submit: {}",
                calculateNumberToSubmit(100, 4, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 4, 3)));
        logger.info(
                "Number To submit: {}",
                calculateNumberToSubmit(100, 20, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 4, 5)));
        logger.info(
                "Number To submit: {}",
                calculateNumberToSubmit(51, 120, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 17, 0)));
        logger.info(
                "Number To submit: {}",
                calculateNumberToSubmit(51, 120, siteInfo, pseqProdQueueInfo, new GlideinMetric(siteInfo.getName(),
                        pseqProdQueueInfo.getName(), 14, 0)));

    }

    private Integer calculateNumberToSubmit(int idleCondorJobs, int runningCondorJobs, Site siteInfo, Queue queueInfo,
            GlideinMetric metrics) {
        logger.info("idleCondorJobs: {}, runningCondorJobs: {}", idleCondorJobs, runningCondorJobs);
        double numToSubmit = 1;
        if (metrics != null) {
            logger.info("metrics.getPending(): {}, metrics.getRunning(): {}", metrics.getPending(),
                    metrics.getRunning());
            numToSubmit -= metrics.getPending() * 0.4;
            numToSubmit -= metrics.getRunning() * 0.4;
        }
        numToSubmit -= runningCondorJobs * 0.005;
        numToSubmit += idleCondorJobs * 0.005;

        if (numToSubmit <= 1 && (metrics != null && metrics.getRunning() <= queueInfo.getMaxRunning())) {
            numToSubmit = 1;
        }

        numToSubmit = Math.round(numToSubmit);

        // Integer ret = Double.valueOf(Math.min(numToSubmit, queueInfo.getMaxMultipleJobsToSubmit())).intValue();
        return Double.valueOf(numToSubmit).intValue();
    }

    @Test
    public void testScoring() {

        List<Queue> queueList = new ArrayList<Queue>();

        Site kureSiteInfo = new Site();
        kureSiteInfo.setName("Kure");

        Queue pseqProdQueueInfo = new Queue();
        pseqProdQueueInfo.setName("pseq_prod");
        pseqProdQueueInfo.setWeight(1D);
        pseqProdQueueInfo.setMaxPending(2);
        pseqProdQueueInfo.setMaxRunning(30);
        pseqProdQueueInfo.setRunTime(2880L);
        queueList.add(pseqProdQueueInfo);

        Queue weekQueueInfo = new Queue();
        weekQueueInfo.setName("week");
        weekQueueInfo.setWeight(0.8D);
        weekQueueInfo.setMaxPending(2);
        weekQueueInfo.setMaxRunning(30);
        weekQueueInfo.setRunTime(2880L);
        queueList.add(weekQueueInfo);
        kureSiteInfo.setQueueList(queueList);

        queueList = new ArrayList<Queue>();

        Site topsailSiteInfo = new Site();
        topsailSiteInfo.setName("Topsail");

        Queue queue16QueueInfo = new Queue();
        queue16QueueInfo.setName("queue16");
        queue16QueueInfo.setWeight(1D);
        queue16QueueInfo.setMaxPending(2);
        queue16QueueInfo.setMaxRunning(30);
        queue16QueueInfo.setRunTime(2880L);
        queueList.add(queue16QueueInfo);
        topsailSiteInfo.setQueueList(queueList);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 0, 2), 1.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 0, 2), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 2, 2), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 2, 0), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 4, 0), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 6, 0), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 0, 2), 0.2);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 0, 2), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 2, 0), 0.25);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 2, 0), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 2, 2), 0.3);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 2, 2), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 4, 0), 0.35);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 4, 0), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 4, 2), 0.4);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 4, 2), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 4, 4), 0.45);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 4, 4), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 4, 5), 0.5);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 4, 5), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 4, 6), 0.55);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 4, 6), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 4, 6), 0.0);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 4, 6), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 12, 8), 0.6);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 12, 8), 0.0);

        calculateScore(kureSiteInfo, pseqProdQueueInfo,
                new GlideinMetric(kureSiteInfo.getName(), pseqProdQueueInfo.getName(), 12, 0), 0.65);
        calculateScore(topsailSiteInfo, queue16QueueInfo,
                new GlideinMetric(topsailSiteInfo.getName(), queue16QueueInfo.getName(), 12, 0), 0.0);

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

package org.renci.gate.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.ServiceReference;
import org.renci.gate.GATEException;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.gate.GlideinSubmissionBean;
import org.renci.gate.GlideinSubmissionContext;
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

    private GATEServiceTracker serviceTracker;

    private GATEEngineBeanService beanService;

    public GATEEngineRunnable(GATEServiceTracker serviceTracker, GATEEngineBeanService beanService) {
        super();
        this.serviceTracker = serviceTracker;
        this.beanService = beanService;
    }

    @Override
    public void run() {
        logger.debug("ENTERING run()");

        Map<String, GATEService> gateServiceMap = new HashMap<String, GATEService>();

        ServiceReference[] siteSelectorServiceRefArray = serviceTracker.getServiceReferences();

        if (siteSelectorServiceRefArray != null) {
            for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                Object service = serviceTracker.getService(serviceRef);
                logger.debug("service.getClass().getName() = {}", service.getClass().getName());
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
            logger.error("JLRMException", e);
        }

        int totalCondorJobCount = jobMap.size();
        logger.info("totalCondorJobCount: {}", totalCondorJobCount);

        int heldCondorJobCount = calculateJobCount(jobMap, CondorJobStatusType.HELD);
        logger.info("heldCondorJobCount: {}", heldCondorJobCount);

        int idleCondorJobCount = calculateJobCount(jobMap, CondorJobStatusType.IDLE);
        logger.info("idleCondorJobCount: {}", idleCondorJobCount);

        int runningCondorJobCount = calculateJobCount(jobMap, CondorJobStatusType.RUNNING);
        logger.info("runningCondorJobCount: {}", runningCondorJobCount);

        Map<String, Integer> requiredSiteMetricsMap = calculateRequiredSiteCount(jobMap);

        // get a snapshot of jobs across sites & queues
        List<GlideinMetric> siteQueueGlideinMetricList = globalMetricsLookup(gateServiceMap);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (jobMap.size() == 0 || heldCondorJobCount == totalCondorJobCount) {
            executorService.execute(new KillGlideinRunnable(jobMap, gateServiceMap, siteQueueGlideinMetricList));
            return;
        }

        boolean needGlidein = isGlideinNeeded(gateServiceMap, siteQueueGlideinMetricList, idleCondorJobCount,
                runningCondorJobCount, totalCondorJobCount);

        if (!needGlidein) {
            logger.warn("No glideins needed.");
            return;
        }

        GlideinSubmissionBean glideinSubmissionBean = new GlideinSubmissionBean();
        glideinSubmissionBean.setGateServiceMap(gateServiceMap);
        glideinSubmissionBean.setIdleCondorJobs(idleCondorJobCount);
        glideinSubmissionBean.setRequiredSiteMetricsMap(requiredSiteMetricsMap);
        glideinSubmissionBean.setRunningCondorJobs(runningCondorJobCount);
        glideinSubmissionBean.setSiteQueueGlideinMetricList(siteQueueGlideinMetricList);

        GlideinSubmissionContext context = new GlideinSubmissionContext(glideinSubmissionBean);
        List<SiteQueueScore> siteQueueScoreList = context.calculateSiteQueueScores();

        logger.info("siteQueueScoreList.size(): {}", siteQueueScoreList.size());

        for (SiteQueueScore siteQueueScore : siteQueueScoreList) {

            int numberToSubmit = context.calculateNumberToSubmit();
            logger.info("numberToSubmit: {}", numberToSubmit);

            for (int i = 0; i < numberToSubmit; ++i) {
                GATEService gateService = gateServiceMap.get(siteQueueScore.getSiteName());
                Site siteInfo = gateService.getSite();
                logger.info(String.format("Submitting %d of %d glideins for %s to %s:%s", i + 1, numberToSubmit,
                        siteInfo.getUsername(), siteQueueScore.getSiteName(), siteQueueScore.getQueueName()));
                executorService.execute(new SubmitGlideinRunnable(gateService, siteQueueScore));
            }

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

    private List<GlideinMetric> globalMetricsLookup(Map<String, GATEService> gateServiceMap) {
        logger.debug("ENTERING globalMetricsLookup(Map<String, GATEService>)");

        List<GlideinMetric> siteQueueGlideinMetricList = new ArrayList<GlideinMetric>();

        for (String siteName : gateServiceMap.keySet()) {

            GATEService gateService = gateServiceMap.get(siteName);
            List<GlideinMetric> glideinMetricList = null;

            try {
                if (!gateService.isValid()) {
                    logger.warn("isValid() failure: {}", siteName);
                    continue;
                }
            } catch (GATEException e) {
                logger.warn("isValid error", e);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                glideinMetricList = gateService.lookupMetrics();
            } catch (Exception e) {
                logger.warn("There was a problem looking up metrics", e);
            }

            if (glideinMetricList == null) {
                logger.warn("null glideinMetricList: {}", siteName);
                continue;
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            siteQueueGlideinMetricList.addAll(glideinMetricList);
        }
        return siteQueueGlideinMetricList;
    }

    private boolean isGlideinNeeded(Map<String, GATEService> gateServiceMap,
            List<GlideinMetric> siteQueueGlideinMetricList, int idleCondorJobs, int runningCondorJobs,
            int totalCondorJobs) {
        logger.debug("ENTERING isGlideinNeeded(Map<String, GATEService>, List<GlideinMetric>, int, int, int)");

        // assume we need new glideins, and then run some tests to negate the assumptions
        boolean needGlidein = true;

        int totalRunningGlideinJobs = 0;
        int totalPendingGlideinJobs = 0;

        try {

            for (String siteName : gateServiceMap.keySet()) {

                GATEService gateService = gateServiceMap.get(siteName);
                Site site = gateService.getSite();

                logger.info(site.toString());

                for (Queue queue : site.getQueueList()) {

                    for (GlideinMetric glideinMetric : siteQueueGlideinMetricList) {
                        logger.info(glideinMetric.toString());
                        if (siteName.equals(glideinMetric.getSiteName())
                                && queue.getName().equals(glideinMetric.getQueueName())) {
                            totalRunningGlideinJobs += glideinMetric.getRunning();
                            totalPendingGlideinJobs += glideinMetric.getPending();
                        }
                    }

                }

            }

            logger.info("totalRunningGlideinJobs: {}", totalRunningGlideinJobs);
            logger.info("totalPendingGlideinJobs: {}", totalPendingGlideinJobs);

            int totalCurrentlySubmitted = totalRunningGlideinJobs + totalPendingGlideinJobs;
            logger.info("totalCurrentlySubmitted: {}", totalCurrentlySubmitted);

            if (idleCondorJobs == 0) {
                logger.info("No more idle local Condor jobs");
                needGlidein = false;
            }

            if (totalCurrentlySubmitted >= this.beanService.getMaxTotalGlideins()) {
                logger.info("Total number of glideins has reached the limit of {}",
                        this.beanService.getMaxTotalGlideins());
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

}

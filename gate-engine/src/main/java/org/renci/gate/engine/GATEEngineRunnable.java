package org.renci.gate.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            logger.error("JLRMException", e);
        }

        int totalCondorJobs = jobMap.size();
        logger.info("totalCondorJobs: {}", totalCondorJobs);

        int idleCondorJobs = calculateJobCount(jobMap, CondorJobStatusType.IDLE);
        logger.info("idleCondorJobs: {}", idleCondorJobs);

        int runningCondorJobs = calculateJobCount(jobMap, CondorJobStatusType.RUNNING);
        logger.info("runningCondorJobs: {}", runningCondorJobs);

        Map<String, Integer> requiredSiteMetricsMap = calculateRequiredSiteCount(jobMap);

        // get a snapshot of jobs across sites & queues
        List<GlideinMetric> siteQueueGlideinMetricList = globalMetricsLookup(gateServiceMap);

        if (jobMap.size() == 0) {
            Executors.newSingleThreadExecutor().execute(
                    new KillGlideinRunnable(jobMap, gateServiceMap, siteQueueGlideinMetricList));
            return;
        }

        boolean needGlidein = isGlideinNeeded(gateServiceMap, siteQueueGlideinMetricList, idleCondorJobs,
                runningCondorJobs, totalCondorJobs);

        if (!needGlidein) {
            logger.warn("No glideins needed.");
            return;
        }

        GlideinSubmissionBean glideinSubmissionBean = new GlideinSubmissionBean();
        glideinSubmissionBean.setGateServiceMap(gateServiceMap);
        glideinSubmissionBean.setIdleCondorJobs(idleCondorJobs);
        glideinSubmissionBean.setRequiredSiteMetricsMap(requiredSiteMetricsMap);
        glideinSubmissionBean.setRunningCondorJobs(runningCondorJobs);
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
                Executors.newSingleThreadExecutor().execute(new SubmitGlideinRunnable(gateService, siteQueueScore));
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
        logger.info("ENTERING globalMetricsLookup(Map<String, GATEService>)");

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
        logger.info("ENTERING isGlideinNeeded(Map<String, GATEService>, List<GlideinMetric>, int, int, int)");

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

                for (GlideinMetric glideinMetric : siteQueueGlideinMetricList) {
                    logger.info(glideinMetric.toString());
                    if (siteName.equals(glideinMetric.getSiteName())) {
                        totalRunningGlideinJobs += glideinMetric.getRunning();
                        totalPendingGlideinJobs += glideinMetric.getPending();
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
        return needGlidein;
    }

}

package org.renci.gate.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.jlrm.JLRMException;
import org.renci.jlrm.Site;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.cli.CondorLookupJobsByOwnerCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(MainTask.class);

    private ServiceTracker tracker;

    public MainTask(ServiceTracker tracker) {
        super();
        this.tracker = tracker;
    }

    @Override
    public void run() {
        logger.info("ENTERING run()");
        Map<String, GATEService> gateServiceMap = new HashMap<String, GATEService>();

        ServiceReference[] siteSelectorServiceRefArray = tracker.getServiceReferences();

        if (siteSelectorServiceRefArray != null) {
            for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                if (serviceRef instanceof GATEService) {
                    GATEService gateService = (GATEService) tracker.getService(serviceRef);
                    if (gateService != null) {
                        Site site = gateService.getSite();
                        logger.info(site.toString());
                        gateServiceMap.put(site.getName(), gateService);
                    }
                }
            }
        }

        // get a snapshot of jobs across sites across queues
        Map<String, Map<String, GlideinMetric>> siteQueueGlideinMetricsMap = new HashMap<String, Map<String, GlideinMetric>>();
        logger.info("gateServiceMap.size() == {}", gateServiceMap.size());
        for (String siteName : gateServiceMap.keySet()) {
            try {
                GATEService gateService = gateServiceMap.get(siteName);
                Map<String, GlideinMetric> glideinMetricMap = gateService.lookupMetrics();
                siteQueueGlideinMetricsMap.put(gateService.getSite().getName(), glideinMetricMap);
            } catch (Exception e) {
                logger.error("There was a problem looking up metrics...doing nothing", e);
                return;
            }
        }

        // go get a snapshot of local jobs
        Map<String, List<ClassAdvertisement>> jobMap = null;
        try {
            CondorLookupJobsByOwnerCallable callable = new CondorLookupJobsByOwnerCallable(
                    System.getProperty("user.name"));
            jobMap = callable.call();
        } catch (JLRMException e) {
            e.printStackTrace();
        }

        if (jobMap != null) {
            Runnable runnable = null;
            if (jobMap.size() > 0) {
                runnable = new SubmitGlideinRunnable(jobMap, gateServiceMap, siteQueueGlideinMetricsMap);
            } else {
                runnable = new KillGlideinRunnable(jobMap, gateServiceMap, siteQueueGlideinMetricsMap);
            }
            Executors.newSingleThreadExecutor().execute(runnable);
        }

    }

}

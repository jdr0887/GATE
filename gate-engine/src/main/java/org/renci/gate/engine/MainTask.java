package org.renci.gate.engine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.jlrm.JLRMException;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.renci.jlrm.condor.cli.CondorCLIFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(MainTask.class);

    private ServiceTracker tracker;

    private String condorHome;

    public MainTask(ServiceTracker tracker, String condorHome) {
        super();
        this.tracker = tracker;
        this.condorHome = condorHome;
    }

    @Override
    public void run() {
        logger.info("ENTERING run()");
        logger.info("condorHome: {}", condorHome);
        Map<String, GATEService> gateServiceMap = new HashMap<String, GATEService>();

        ServiceReference[] siteSelectorServiceRefArray = tracker.getServiceReferences();

        if (siteSelectorServiceRefArray != null) {
            for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                logger.info(serviceRef.toString());
                GATEService gateService = (GATEService) tracker.getService(serviceRef);
                if (gateService != null) {
                    gateServiceMap.put(gateService.getSite().getName(), gateService);
                }
            }
        }

        // get a snapshot of jobs across sites across queues
        Map<String, Map<String, GlideinMetric>> siteQueueGlideinMetricsMap = new HashMap<String, Map<String, GlideinMetric>>();
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
        CondorCLIFactory condorCLIFactory = CondorCLIFactory.getInstance(new File(condorHome));
        Map<String, List<ClassAdvertisement>> jobMap = null;
        try {
            jobMap = condorCLIFactory.lookupJobsByOwner(System.getProperty("user.name"));
        } catch (JLRMException e) {
            e.printStackTrace();
        }

        if (jobMap != null) {

            if (jobMap.size() > 0) {

                SubmitGlideinRunnable runnable = new SubmitGlideinRunnable(jobMap, gateServiceMap,
                        siteQueueGlideinMetricsMap);
                runnable.run();

            } else {

                KillGlideinRunnable runnable = new KillGlideinRunnable(jobMap, gateServiceMap,
                        siteQueueGlideinMetricsMap);
                runnable.run();

            }
        }

    }

}

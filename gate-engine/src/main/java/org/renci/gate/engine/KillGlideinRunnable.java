package org.renci.gate.engine;

import java.util.List;
import java.util.Map;

import org.renci.gate.GATEException;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.jlrm.Queue;
import org.renci.jlrm.condor.ClassAdvertisement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KillGlideinRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(KillGlideinRunnable.class);

    private Map<String, List<ClassAdvertisement>> jobMap;

    private Map<String, GATEService> gateServiceMap;

    private Map<String, Map<String, GlideinMetric>> siteQueueGlideinMetricsMap;

    public KillGlideinRunnable() {
        super();
    }

    public KillGlideinRunnable(Map<String, List<ClassAdvertisement>> jobMap, Map<String, GATEService> gateServiceMap,
            Map<String, Map<String, GlideinMetric>> siteQueueGlideinMetricsMap) {
        super();
        this.jobMap = jobMap;
        this.gateServiceMap = gateServiceMap;
        this.siteQueueGlideinMetricsMap = siteQueueGlideinMetricsMap;
    }

    @Override
    public void run() {
        logger.info("ENTERING run()");

        for (String siteName : gateServiceMap.keySet()) {
            GATEService gateService = gateServiceMap.get(siteName);
            Map<String, GlideinMetric> glideinMetricMap = siteQueueGlideinMetricsMap.get(gateService.getSite()
                    .getName());
            for (String queue : glideinMetricMap.keySet()) {
                GlideinMetric metric = glideinMetricMap.get(queue);
                logger.debug("metric: {}", metric.toString());
                try {
                    if (metric.getPending() > 0) {
                        // remove all pending glideins
                        gateService.deletePendingGlideins();
                    } else if (metric.getPending() == 0 && metric.getRunning() > 0) {
                        // remove one running glidein
                        Map<String, Queue> queueInfoMap = gateService.getSite().getQueueInfoMap();
                        if (queueInfoMap.containsKey(metric.getQueue())) {
                            gateService.deleteGlidein(queueInfoMap.get(metric.getQueue()));
                        }
                    }
                } catch (GATEException e) {
                    logger.error("GATEException", e);
                }

            }
        }

    }

}

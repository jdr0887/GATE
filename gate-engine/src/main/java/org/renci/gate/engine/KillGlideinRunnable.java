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

    private List<GlideinMetric> siteQueueGlideinMetricList;

    public KillGlideinRunnable() {
        super();
    }

    public KillGlideinRunnable(Map<String, List<ClassAdvertisement>> jobMap, Map<String, GATEService> gateServiceMap,
            List<GlideinMetric> siteQueueGlideinMetricList) {
        super();
        this.jobMap = jobMap;
        this.gateServiceMap = gateServiceMap;
        this.siteQueueGlideinMetricList = siteQueueGlideinMetricList;
    }

    @Override
    public void run() {
        logger.info("ENTERING run()");

        for (String siteName : gateServiceMap.keySet()) {
            GATEService gateService = gateServiceMap.get(siteName);

            for (GlideinMetric glideinMetric : siteQueueGlideinMetricList) {
                logger.debug("glideinMetric: {}", glideinMetric.toString());
                if (glideinMetric.getSiteName().equals(gateService.getSite().getName())) {
                    try {
                        if (glideinMetric.getPending() > 0) {
                            // remove all pending glideins
                            gateService.deletePendingGlideins();
                        } else if (glideinMetric.getPending() == 0 && glideinMetric.getRunning() > 0) {
                            // remove one running glidein
                            List<Queue> queueList = gateService.getSite().getQueueList();
                            for (Queue queue : queueList) {
                                if (queue.getName().equals(glideinMetric.getQueueName())) {
                                    gateService.deleteGlidein(queue);
                                }
                            }
                        }
                    } catch (GATEException e) {
                        logger.error("GATEException", e);
                    }
                }
            }
        }

    }
}

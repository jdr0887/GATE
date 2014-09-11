package org.renci.gate.engine;

import java.util.List;

import org.renci.gate.GATEException;
import org.renci.gate.GATEService;
import org.renci.gate.SiteQueueScore;
import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmitGlideinRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(SubmitGlideinRunnable.class);

    private GATEService gateService;

    private SiteQueueScore siteQueueScore;

    public SubmitGlideinRunnable(GATEService gateService, SiteQueueScore siteQueueScore) {
        super();
        this.gateService = gateService;
        this.siteQueueScore = siteQueueScore;
    }

    @Override
    public void run() {
        logger.debug("ENTERING run()");
        Site siteInfo = gateService.getSite();
        logger.debug(siteInfo.toString());
        List<Queue> queueList = siteInfo.getQueueList();
        for (Queue queue : queueList) {
            if (queue.getName().equals(siteQueueScore.getQueueName())) {
                logger.debug(queue.toString());
                try {
                    gateService.createGlidein(queue);
                } catch (GATEException e) {
                    logger.error("GATEException", e);
                }
            }
        }
    }

}

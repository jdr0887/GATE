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

    private Integer numberToSubmit;

    public SubmitGlideinRunnable(GATEService gateService, SiteQueueScore siteQueueScore, Integer numberToSubmit) {
        super();
        this.gateService = gateService;
        this.siteQueueScore = siteQueueScore;
        this.numberToSubmit = numberToSubmit;
    }

    @Override
    public void run() {
        logger.debug("ENTERING run()");
        Site siteInfo = gateService.getSite();
        List<Queue> queueList = siteInfo.getQueueList();
        Queue queue = null;
        for (Queue q : queueList) {
            if (q.getName().equals(siteQueueScore.getQueueName())) {
                queue = q;
                break;
            }
        }
        if (queue == null) {
            logger.warn("Queue is null");
            return;
        }
        for (int i = 0; i < numberToSubmit; ++i) {
            try {
                logger.info(String.format("Submitting %d of %d glideins for %s to %s:%s", i + 1, numberToSubmit,
                        siteInfo.getUsername(), siteQueueScore.getSiteName(), siteQueueScore.getQueueName()));
                gateService.createGlidein(queue);
            } catch (GATEException e) {
                logger.error("GATEException", e);
            }
        }
    }

}

package org.renci.gate.engine;

import java.util.Map;

import org.renci.gate.GATEService;
import org.renci.gate.SiteQueueScore;
import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmitGlideinRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(SubmitGlideinRunnable.class);

    private Map<String, GATEService> gateServiceMap;

    private SiteQueueScore winner;

    public SubmitGlideinRunnable(Map<String, GATEService> gateServiceMap, SiteQueueScore winner) {
        super();
        this.gateServiceMap = gateServiceMap;
        this.winner = winner;
    }

    @Override
    public void run() {
        logger.info("ENTERING run()");

        GATEService gateService = gateServiceMap.get(winner.getSiteName());
        Site siteInfo = gateService.getSite();
        logger.debug(siteInfo.toString());
        Queue queueInfo = siteInfo.getQueueInfoMap().get(winner.getQueueName());
        logger.debug(queueInfo.toString());
        for (int i = 0; i < winner.getNumberToSubmit(); ++i) {
            logger.info(String.format("Submitting %d of %d glideins for %s to %s:%s", i + 1,
                    winner.getNumberToSubmit(), siteInfo.getUsername(), winner.getSiteName(), winner.getQueueName()));
            gateService.createGlidein(queueInfo);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}

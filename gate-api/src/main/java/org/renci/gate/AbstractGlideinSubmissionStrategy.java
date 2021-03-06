package org.renci.gate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGlideinSubmissionStrategy implements GlideinSubmissionStrategy {

    private final Logger logger = LoggerFactory.getLogger(AbstractGlideinSubmissionStrategy.class);

    public AbstractGlideinSubmissionStrategy() {
        super();
    }

    protected void filter(Map<String, GATEService> gateServiceMap, List<GlideinMetric> siteQueueGlideinMetricList) {
        logger.debug("ENTERING filter(List<GlideinMetric>)");

        Iterator<GlideinMetric> siteQueueScoreIter = siteQueueGlideinMetricList.iterator();
        while (siteQueueScoreIter.hasNext()) {
            GlideinMetric glideinMetric = siteQueueScoreIter.next();

            if (gateServiceMap.containsKey(glideinMetric.getSiteName())) {
                GATEService gateService = gateServiceMap.get(glideinMetric.getSiteName());
                Site site = gateService.getSite();

                List<Queue> siteQueueList = site.getQueueList();

                for (Queue queue : siteQueueList) {

                    if (queue.getName().equals(glideinMetric.getQueueName())) {

                        String activeQueues = gateService.getActiveQueues();
                        logger.info("activeQueues: {}", activeQueues);
                        if (StringUtils.isNotEmpty(activeQueues) && !activeQueues.contains(queue.getName())) {
                            logger.info("excluding \"{}\" queue due to not being active", queue.getName());
                            siteQueueScoreIter.remove();
                            continue;
                        }

                        if (glideinMetric.getPending() >= queue.getMaxPending()) {
                            logger.info("Pending job threshold has been met: {} of {}", glideinMetric.getPending(),
                                    queue.getMaxPending());
                            siteQueueScoreIter.remove();
                            continue;
                        }

                        if (glideinMetric.getRunning() >= queue.getMaxRunning()) {
                            logger.info("totalJobs is greater that queue max job limit");
                            siteQueueScoreIter.remove();
                            continue;
                        }
                    }
                }

            }

        }

    }
}

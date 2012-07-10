package org.renci.gate.plugin.blueridge;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class BlueRidgeGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(BlueRidgeGATEService.class);

    private Site site;

    private String collectorHost;

    private Map<String, Object> properties;

    public BlueRidgeGATEService() {
        super();
    }

    @Override
    public Map<String, GlideinMetric> lookupMetrics() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createGlidein(Queue queue) {
        logger.info("ENTERING createGlidein(Queue)");
        if (properties != null && properties.containsKey("active_queues")) {
            String[] activeQueues = StringUtils.split(properties.get("active_queues").toString(), ',');
            List<String> activeQueueList = Arrays.asList(activeQueues);
            if (!activeQueueList.contains(queue.getName())) {
                logger.warn("queue name is not in active queue list...see etc/org.renci.gate.plugin.blueridge.cfg");
                return;
            }
        }

    }

    @Override
    public void deleteGlidein(Queue queue) {
        logger.info("ENTERING deleteGlidein(Queue)");

    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getCollectorHost() {
        return collectorHost;
    }

    public void setCollectorHost(String collectorHost) {
        this.collectorHost = collectorHost;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}

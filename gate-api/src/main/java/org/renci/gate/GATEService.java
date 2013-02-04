package org.renci.gate;

import java.util.Map;

import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;

/**
 * 
 * @author jdr0887
 */
public interface GATEService {

    public Map<String, GlideinMetric> lookupMetrics();

    public void createGlidein(Queue queue);

    public void deleteGlidein(Queue queue);

    public void deletePendingGlideins();

    public Site getSite();

    public String getActiveQueues();

    public String getCollectorHost();

    public String getUsername();

}

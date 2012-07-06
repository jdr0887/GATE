package org.renci.gate;

import java.util.Map;

import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;



/**
 * 
 * @author jdr0887
 */
public interface GATEService {

    public Site getSite();

    public Map<String, GlideinMetric> lookupMetrics();

    public String getCollectorHost();
    
    public void createGlidein(Queue queue);

    public void deleteGlidein(Queue queue);

}

package org.renci.gate;

import java.util.List;

import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;

/**
 * 
 * @author jdr0887
 */
public interface GATEService {

    public void init() throws GATEException;

    public List<GlideinMetric> getMetrics() throws GATEException;

    public Boolean isValid() throws GATEException;

    public void createGlidein(Queue queue) throws GATEException;

    public void deleteGlidein(Queue queue) throws GATEException;

    public void deletePendingGlideins() throws GATEException;

    public Site getSite();

    public String getActiveQueues();

    public String getCollectorHost();

}

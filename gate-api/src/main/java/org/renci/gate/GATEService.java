package org.renci.gate;

import java.util.List;



/**
 * 
 * @author jdr0887
 */
public interface GATEService {

    public SiteInfo getSiteInfo();

    public List<GlideinMetric> lookupMetrics();

    public void postGlidein(SiteInfo site, QueueInfo queue);

    public void deleteGlidein(SiteInfo site, QueueInfo queue);

}

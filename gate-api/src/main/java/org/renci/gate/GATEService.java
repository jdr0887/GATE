package org.renci.gate;

import java.util.Map;


/**
 * 
 * @author jdr0887
 */
public interface GATEService {

    public SiteInfo getSiteInfo();

    public Map<String, GlideinMetrics> lookupMetrics();

    public void postGlidein(String queue);

    public void deleteGlidein();

}

package org.renci.gate;


/**
 * 
 * @author jdr0887
 */
public interface GATEService {

    public SiteInfo getSiteInfo();

    public GlideinMetrics lookupMetrics();

    public void postGlidein();

    public void deleteGlidein();

}

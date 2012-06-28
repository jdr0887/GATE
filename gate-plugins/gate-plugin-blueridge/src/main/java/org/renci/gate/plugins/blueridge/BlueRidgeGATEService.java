package org.renci.gate.plugins.blueridge;

import java.util.List;
import java.util.Map;

import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetrics;
import org.renci.gate.SiteInfo;
import org.renci.jlrm.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class BlueRidgeGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(BlueRidgeGATEService.class);

    private SiteInfo siteInfo;

    public BlueRidgeGATEService() {
        super();
    }

    @Override
    public SiteInfo getSiteInfo() {
        return siteInfo;
    }

    public void setSiteInfo(SiteInfo siteInfo) {
        this.siteInfo = siteInfo;
    }

    @Override
    public Map<String, GlideinMetrics> lookupMetrics() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void postGlidein(String queue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteGlidein() {
        // TODO Auto-generated method stub

    }

}

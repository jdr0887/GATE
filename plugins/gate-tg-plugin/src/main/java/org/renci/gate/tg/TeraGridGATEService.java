package org.renci.gate.tg;

import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBase;
import org.osgi.framework.BundleContext;
import org.renci.cm.RSPCredential;
import org.renci.gate.GATESite;
import org.renci.gate.services.GATEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class TeraGridGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BundleContext context;

    public TeraGridGATEService(BundleContext context) {
        super();
        this.context = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.osgmm.SiteSelectorService#getSiteList()
     */
    @Override
    public List<GATESite> getSiteList() {

        List<GATESite> siteList = new ArrayList<GATESite>();

        // main site list
        siteList.addAll(new ArrayList<GATESite>());
        logger.info("siteList.size() = " + siteList.size());

        return siteList;
    }

    @Override
    public List<RuleBase> getFilterSiteRules() {
        return null;
    }

//    @Override
//    public RSPCredential getCredential() {
//        return new TeraGridCredential(context);
//    }

}

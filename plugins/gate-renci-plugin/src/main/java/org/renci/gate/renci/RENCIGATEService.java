package org.renci.gate.renci;

import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBase;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.renci.cm.RSPCredential;
import org.renci.gate.GATESite;
import org.renci.gate.api.persistence.PersistenceException;
import org.renci.gate.api.persistence.SitePersistenceService;
import org.renci.gate.services.GATEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class RENCIGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BundleContext context;

    public RENCIGATEService(BundleContext context) {
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
        logger.debug("ENTERING getSiteList()");

        List<GATESite> siteList = new ArrayList<GATESite>();

        ServiceReference serviceReference = context.getServiceReference(SitePersistenceService.class.getName());
        SitePersistenceService sitePersistenceService = (SitePersistenceService) context.getService(serviceReference);

        List<? extends org.renci.gate.api.persistence.Site> dbSiteList = null;
        try {
            dbSiteList = sitePersistenceService.findAll();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }

        if (dbSiteList != null && dbSiteList.size() > 0) {
            for (org.renci.gate.api.persistence.Site s : dbSiteList) {

                GATESite site = new GATESite();
                site.setGatekeeper(s.getGatekeeperHost());
                site.setGridType(s.getGridType().toString());
                site.setJobManager(s.getJobManager());
                site.setMaxIdleCount(s.getMaxIdleCount());
                site.setMaxMultipleJobs(s.getMaxMultipleJobs());
                site.setMaxNoClaimTime(s.getMaxNoClaimTime());
                site.setMaxQueueTime(s.getMaxQueueTime());
                site.setMaxRunTime(s.getMaxRunTime());
                site.setMaxTotalCount(s.getMaxTotalCount());
                site.setMultiplier(s.getMultiplier());
                site.setName(s.getName());
                site.setProject(s.getProject());
                site.setQueue(s.getQueue());
                site.setCredential(new RENCICredential(context));

                siteList.add(site);

            }
        }

        return siteList;
    }

    @Override
    public List<RuleBase> getFilterSiteRules() {
        return null;
    }

//    @Override
//    public RSPCredential getCredential() {
//        return new RENCICredential(context);
//    }

}

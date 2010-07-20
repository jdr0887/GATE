package org.renci.gate.renci.impl;

import static org.renci.gate.renci.Constants.CONFIG_FILE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.renci.gate.Site;
import org.renci.gate.api.persistence.PersistenceException;
import org.renci.gate.api.persistence.SitePersistenceService;
import org.renci.gate.services.SiteSelectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class SiteSelectorServiceImpl implements SiteSelectorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BundleContext context;

    private Properties properties;

    public SiteSelectorServiceImpl(BundleContext context) {
        super();
        this.context = context;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {

        String karafHome = System.getProperty("karaf.home");
        File config = new File(karafHome, CONFIG_FILE);
        if (!config.exists()) {
            FileUtils.touch(config);
        }
        this.properties = new Properties();
        FileInputStream fis = new FileInputStream(config);
        this.properties.load(fis);
        fis.close();

        // if (!this.properties.contains(PROP_KEY_CENTRAL_MASTER)) {
        // this.properties.setProperty(PROP_KEY_CENTRAL_MASTER, "osg-ress-1.fnal.gov");
        // }
        //
        // if (!this.properties.contains(PROP_KEY_VO_NAME)) {
        // this.properties.setProperty(PROP_KEY_VO_NAME, "Engage");
        // }

        FileOutputStream fos = new FileOutputStream(config);
        this.properties.store(fos, null);
        fos.flush();
        fos.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.osgmm.SiteSelectorService#getSiteList()
     */
    public List<Site> getSiteList() {
        logger.debug("ENTERING getSiteList()");

        List<Site> siteList = new ArrayList<Site>();
        
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

                Site site = new Site();
                site.setGatekeeper(s.getGatekeeperHost());
                site.setGridType(s.getGridType().toString());
                site.setJobmanager(s.getJobManager());
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

                siteList.add(site);

            }
        }

        return siteList;
    }
}

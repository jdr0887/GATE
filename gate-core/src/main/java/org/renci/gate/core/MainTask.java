package org.renci.gate.core;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.renci.gate.Site;
import org.renci.gate.services.SiteSelectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(MainTask.class);

    private BundleContext context;

    public MainTask(BundleContext context) {
        super();
        this.context = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {

        List<Site> siteList = new ArrayList<Site>();

        try {
            ServiceReference[] siteSelectorServiceRefArray = context.getServiceReferences(SiteSelectorService.class
                    .getName(), null);
            if (siteSelectorServiceRefArray != null) {
                for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                    SiteSelectorService siteSelectorService = (SiteSelectorService) context.getService(serviceRef);
                    siteList.addAll(siteSelectorService.getSiteList());
                    context.ungetService(serviceRef);
                }
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }

        for (Site site : siteList) {
            logger.info("site.getName() = " + site.getName());
        }

    }

}

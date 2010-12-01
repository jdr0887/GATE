package org.renci.gate.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.renci.gate.GATESite;
import org.renci.gate.services.GATEService;
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

        List<GATESite> siteList = new ArrayList<GATESite>();

        try {
            ServiceReference[] siteSelectorServiceRefArray = context.getServiceReferences(GATEService.class.getName(),
                    null);

            if (siteSelectorServiceRefArray != null) {
                for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                    logger.info(serviceRef.toString());
                    GATEService gateService = (GATEService) context.getService(serviceRef);
                    if (gateService != null) {
                        siteList.addAll(gateService.getSiteList());
                    }
                    context.ungetService(serviceRef);
                }

                CondorQCallable jobads = new CondorQCallable();
                List<PortalUserJobInfo> portalUserJobInfoList = jobads.call();

                if (siteList != null) {

                    // discovery routine
                    logger.debug("Starting discovery routine");
                    for (GATESite site : siteList) {
                        logger.debug("site.getUniqueId() = " + site.getUniqueId());
                    }
                    logger.debug("Finished discovery routine");

                    GlideinEngineRunnable glideinRunnable = new GlideinEngineRunnable(context, siteList, portalUserJobInfoList);
                    glideinRunnable.run();

                }

            }

        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }

        for (GATESite site : siteList) {
            logger.info("site.getName() = " + site.getName());
        }

    }

}

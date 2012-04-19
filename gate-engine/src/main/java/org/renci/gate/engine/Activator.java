package org.renci.gate.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.renci.gate.GATEService;
import org.renci.gate.config.GATEConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class Activator implements BundleActivator {

    private final Logger logger = LoggerFactory.getLogger(Activator.class);

    private Timer mainTimer = new Timer();

    public Activator() {
        super();
    }

    public void start(BundleContext context) throws Exception {
        logger.debug("ENTERING start(BundleContext)");

        Map<String, GATEService> gateServiceMap = new HashMap<String, GATEService>();

        ServiceReference[] siteSelectorServiceRefArray = context
                .getServiceReferences(GATEService.class.getName(), null);

        if (siteSelectorServiceRefArray != null) {
            for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                logger.info(serviceRef.toString());
                GATEService gateService = (GATEService) context.getService(serviceRef);
                if (gateService != null) {
                    gateServiceMap.put(gateService.getSiteInfo().getName(), gateService);
                }
                context.ungetService(serviceRef);
            }
        }

        ServiceReference gateConfigServiceRef = context.getServiceReference(GATEConfigurationService.class.getName());
        GATEConfigurationService configService = (GATEConfigurationService) context.getService(gateConfigServiceRef);
        String condorHome = configService.getCoreProperties().getProperty(GATEConfigurationService.CONDOR_HOME);
        
        // run every 5 minutes
        long delay = 5 * 1000;
        long period = 5 * 60 * 1000;

        mainTimer.scheduleAtFixedRate(new MainTask(gateServiceMap, condorHome), delay, period);

    }

    public void stop(BundleContext context) throws Exception {
        logger.debug("ENTERING stop(BundleContext)");

        mainTimer.purge();
        mainTimer.cancel();

    }

}

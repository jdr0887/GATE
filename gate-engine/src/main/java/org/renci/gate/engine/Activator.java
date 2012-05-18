package org.renci.gate.engine;

import java.util.Timer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
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

    private ServiceTracker tracker;
    
    public Activator() {
        super();
    }

    public void start(BundleContext context) throws Exception {
        logger.debug("ENTERING start(BundleContext)");

        this.tracker = new ServiceTracker(context, GATEService.class.getName(), null);
        this.tracker.open();

        ServiceReference gateConfigServiceRef = context.getServiceReference(GATEConfigurationService.class.getName());
        GATEConfigurationService configService = (GATEConfigurationService) context.getService(gateConfigServiceRef);

        long delay = 15 * 1000;
        // run every 5 minutes
        long period = 5 * 60 * 1000;

        mainTimer.scheduleAtFixedRate(new MainTask(tracker, configService), delay, period);

    }

    public void stop(BundleContext context) throws Exception {
        logger.debug("ENTERING stop(BundleContext)");

        mainTimer.purge();
        mainTimer.cancel();
        this.tracker.close();

    }

}

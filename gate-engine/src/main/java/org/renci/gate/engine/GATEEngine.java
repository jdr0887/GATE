package org.renci.gate.engine;

import java.util.Timer;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.renci.gate.GATEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class GATEEngine {

    private final Logger logger = LoggerFactory.getLogger(GATEEngine.class);

    private Timer mainTimer = new Timer();

    private BundleContext bundleContext;

    private ServiceTracker tracker;

    public GATEEngine() {
        super();
    }

    public void start() throws Exception {
        logger.debug("ENTERING start()");

        this.tracker = new ServiceTracker(bundleContext, GATEService.class.getName(), null);
        this.tracker.open();

        long delay = 15 * 1000;
        // run every 5 minutes
        long period = 5 * 60 * 1000;

        mainTimer.scheduleAtFixedRate(new MainTask(tracker), delay, period);

    }

    public void stop() throws Exception {
        logger.debug("ENTERING stop()");
        mainTimer.purge();
        mainTimer.cancel();
        this.tracker.close();
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

}

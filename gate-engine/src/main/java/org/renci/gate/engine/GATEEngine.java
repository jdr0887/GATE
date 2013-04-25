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

    private final Timer mainTimer = new Timer();

    private BundleContext bundleContext;

    private ServiceTracker tracker;

    private Long period;

    public GATEEngine() {
        super();
    }

    public void start() throws Exception {
        logger.debug("ENTERING start()");
        this.tracker = new ServiceTracker(bundleContext, GATEService.class.getName(), null);
        this.tracker.open();
        long delay = 1 * 60 * 1000;
        this.mainTimer.scheduleAtFixedRate(new MainTask(tracker), delay, this.period * 60 * 1000);
    }

    public void stop() throws Exception {
        logger.debug("ENTERING stop()");
        this.mainTimer.purge();
        this.mainTimer.cancel();
        this.tracker.close();
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

}

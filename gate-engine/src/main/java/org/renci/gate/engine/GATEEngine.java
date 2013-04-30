package org.renci.gate.engine;

import java.util.Timer;

import org.osgi.framework.BundleContext;
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

    private Long period;

    private GATEServiceTracker serviceTracker;
    
    public GATEEngine() {
        super();
    }

    public void start() throws Exception {
        logger.info("ENTERING start()");
        long delay = 1 * 60 * 1000;
        this.serviceTracker = new GATEServiceTracker(bundleContext);
        this.serviceTracker.open();
        MainTask mainTask = new MainTask(this.serviceTracker);
        this.mainTimer.scheduleAtFixedRate(mainTask, delay, this.period * 60 * 1000);
    }

    public void stop() throws Exception {
        logger.info("ENTERING stop()");
        this.mainTimer.purge();
        this.mainTimer.cancel();
        this.serviceTracker.close();
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

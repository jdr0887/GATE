package org.renci.gate.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class GATEEngine {

    private final Logger logger = LoggerFactory.getLogger(GATEEngine.class);

    private final ScheduledExecutorService scheduleExecutorService = Executors.newSingleThreadScheduledExecutor();

    private BundleContext bundleContext;

    private Long period;

    private GATEServiceTracker serviceTracker;

    public GATEEngine() {
        super();
    }

    public void start() throws Exception {
        logger.info("ENTERING start()");
        this.serviceTracker = new GATEServiceTracker(bundleContext);
        this.serviceTracker.open();
        GATEEngineRunnable runnable = new GATEEngineRunnable(this.serviceTracker);
        this.scheduleExecutorService.scheduleAtFixedRate(runnable, 60, this.period * 60, TimeUnit.SECONDS);
    }

    public void stop() throws Exception {
        logger.info("ENTERING stop()");
        this.scheduleExecutorService.shutdownNow();
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

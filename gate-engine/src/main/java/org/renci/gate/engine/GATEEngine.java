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

    private GATEEngineBeanService beanService;

    private GATEServiceTracker serviceTracker;

    public GATEEngine() {
        super();
    }

    public void start() throws Exception {
        logger.debug("ENTERING start()");
        this.serviceTracker = new GATEServiceTracker(bundleContext);
        this.serviceTracker.open();
        GATEEngineRunnable runnable = new GATEEngineRunnable(this.serviceTracker, this.beanService);
        this.scheduleExecutorService.scheduleAtFixedRate(runnable, 2, this.beanService.getPeriod(), TimeUnit.MINUTES);
    }

    public void stop() throws Exception {
        logger.debug("ENTERING stop()");
        this.scheduleExecutorService.shutdownNow();
        this.serviceTracker.close();
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public GATEEngineBeanService getBeanService() {
        return beanService;
    }

    public void setBeanService(GATEEngineBeanService beanService) {
        this.beanService = beanService;
    }

}

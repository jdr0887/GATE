package org.renci.gate.engine;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.renci.gate.GATEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GATEServiceTracker extends ServiceTracker {

    private final Logger logger = LoggerFactory.getLogger(GATEServiceTracker.class);

    public GATEServiceTracker(BundleContext bundleContext) {
        super(bundleContext, GATEService.class.getName(), null);
    }

    @Override
    public Object addingService(ServiceReference reference) {
        logger.info("ENTERING addingService(ServiceReference)");
        return super.addingService(reference);
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        logger.info("ENTERING removedService(ServiceReference)");
        super.removedService(reference, service);
    }

}

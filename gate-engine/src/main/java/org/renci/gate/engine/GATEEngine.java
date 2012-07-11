package org.renci.gate.engine;

import java.util.Map;
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

    private String condorHome;

    private ServiceTracker tracker;

    private Map<String, Object> properties;

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

        if (properties != null && properties.containsKey("condor_home")) {
            condorHome = properties.get("condor_home").toString();
        }
        
        mainTimer.scheduleAtFixedRate(new MainTask(tracker, condorHome), delay, period);

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

    public String getCondorHome() {
        return condorHome;
    }

    public void setCondorHome(String condorHome) {
        this.condorHome = condorHome;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}

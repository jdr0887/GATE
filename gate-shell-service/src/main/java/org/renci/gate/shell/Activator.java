package org.renci.gate.shell;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class Activator implements BundleActivator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void start(BundleContext context) throws Exception {
        logger.debug("ENTERING start(BundleContext)");
        context.registerService(ShellService.class.getName(), new ShellServiceImpl(), null);
    }

    public void stop(BundleContext context) throws Exception {
        logger.debug("ENTERING stop(BundleContext)");
    }

}

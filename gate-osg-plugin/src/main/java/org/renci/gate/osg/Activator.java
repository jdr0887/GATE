package org.renci.gate.osg;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.renci.gate.osg.impl.SiteSelectorServiceReSS;
import org.renci.gate.services.SiteSelectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class Activator implements BundleActivator {

    private final Logger logger = LoggerFactory.getLogger(Activator.class);

    public void start(BundleContext context) throws Exception {
        logger.debug("ENTERING start(BundleContext)");
        SiteSelectorService sss = new SiteSelectorServiceReSS(context);
        context.registerService(SiteSelectorService.class.getName(), sss, null);
    }

    public void stop(BundleContext context) throws Exception {
        logger.debug("ENTERING stop(BundleContext)");
    }

}

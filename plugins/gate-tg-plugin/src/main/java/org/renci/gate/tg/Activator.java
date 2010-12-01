package org.renci.gate.tg;

import static org.renci.gate.tg.Constants.CONFIG;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.renci.gate.services.GATEService;
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
		
        ServiceReference ref = context.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) context.getService(ref);
        Configuration configuration = configurationAdmin.createFactoryConfiguration(CONFIG);

        configuration.update();

		GATEService sss = new TeraGridGATEService(context);
		context.registerService(GATEService.class.getName(), sss, null);
	}

	public void stop(BundleContext context) throws Exception {
		logger.debug("ENTERING stop(BundleContext)");
	}

}

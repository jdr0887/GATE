package org.renci.gate.osg;

import static org.renci.gate.osg.Constants.CONFIG;
import static org.renci.gate.osg.Constants.PROP_KEY_CENTRAL_MASTER;
import static org.renci.gate.osg.Constants.PROP_KEY_VO_NAME;

import org.apache.commons.lang.StringUtils;
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

        Object prop = configuration.getProperties().get(PROP_KEY_CENTRAL_MASTER);
        if (prop == null || StringUtils.isEmpty(prop.toString())) {
            configuration.getProperties().put(PROP_KEY_CENTRAL_MASTER, "osg-ress-1.fnal.gov");
        }

        prop = configuration.getProperties().get(PROP_KEY_VO_NAME);
        if (prop == null || StringUtils.isEmpty(prop.toString())) {
            configuration.getProperties().put(PROP_KEY_VO_NAME, "Engage");
        }

        prop = configuration.getProperties().get("myproxy_host");
        if (prop == null || StringUtils.isEmpty(prop.toString())) {
            configuration.getProperties().put("myproxy_host", "orca.renci.org");
        }

        prop = configuration.getProperties().get("myproxy_port");
        if (prop == null || StringUtils.isEmpty(prop.toString())) {
            configuration.getProperties().put("myproxy_port", "7512");
        }

        prop = configuration.getProperties().get("username");
        if (prop == null || StringUtils.isEmpty(prop.toString())) {
            configuration.getProperties().put("username", "osg");
        }

        prop = configuration.getProperties().get("passphrase");
        if (prop == null || StringUtils.isEmpty(prop.toString())) {
            configuration.getProperties().put("passphrase", "");
        }

        configuration.update();

        GATEService sss = new OSGGATEService(context);
        context.registerService(GATEService.class.getName(), sss, null);
    }

    public void stop(BundleContext context) throws Exception {
        logger.debug("ENTERING stop(BundleContext)");
    }

}

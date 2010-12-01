package org.renci.gate.engine;

import static org.renci.gate.engine.Constants.CONFIG;
import static org.renci.gate.engine.Constants.MAINTENANCE_JOB_MINUTES;
import static org.renci.gate.engine.Constants.SCRATCH_DIRECTORY;
import static org.renci.gate.engine.Constants.SOFTWARE_PACKAGE_DIRECTORY;
import static org.renci.gate.engine.Constants.VERIFICATION_JOB_MINUTES;

import java.util.Dictionary;
import java.util.Properties;
import java.util.Timer;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class Activator implements BundleActivator {

    private final Logger logger = LoggerFactory.getLogger(Activator.class);

    private Timer advertiseTimer = new Timer();

    private Timer maintenanceTimer = new Timer();

    private Timer mainTimer = new Timer();

    private Timer graphTimer = new Timer();

    private Timer credentialTimer = new Timer();

    private Properties properties = null;

    public Activator() {
        super();
    }

    public void start(BundleContext context) throws Exception {
        logger.debug("ENTERING start(BundleContext)");

        ServiceReference ref = context.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) context.getService(ref);
        Configuration configuration = configurationAdmin.createFactoryConfiguration(CONFIG, null);

        if (configuration != null) {

            Dictionary props = configuration.getProperties();
            if (props == null) {
                props = new Properties();
            }
            
            Object prop = props.get(VERIFICATION_JOB_MINUTES);
            if (prop == null || StringUtils.isEmpty(prop.toString())) {
                props.put(VERIFICATION_JOB_MINUTES, "0");
            }

            prop = props.get(MAINTENANCE_JOB_MINUTES);
            if (prop == null || StringUtils.isEmpty(prop.toString())) {
                props.put(MAINTENANCE_JOB_MINUTES, "0");
            }

            prop = props.get(SOFTWARE_PACKAGE_DIRECTORY);
            if (prop == null || StringUtils.isEmpty(prop.toString())) {
                props.put(SOFTWARE_PACKAGE_DIRECTORY, "/nfs/software/prod/packages");
            }

            prop = props.get(SCRATCH_DIRECTORY);
            if (prop == null || StringUtils.isEmpty(prop.toString())) {
                props.put(SCRATCH_DIRECTORY, "/nfs/scratch");
            }

            configuration.update(props);
        }

        long delay;
        long period;

        // run graph tool
        delay = 5 * 1000;
        period = 5 * 60 * 1000; // run every 5 minutes
        graphTimer.scheduleAtFixedRate(new GraphTask(context), delay, period);

        // run credential task
        delay = 5 * 1000;
        period = 3 * 60 * 60 * 1000; // run every 3 hours
        credentialTimer.scheduleAtFixedRate(new CredentialTask(context), delay, period);

        // run core engine routine
        delay = 5 * 1000;
        period = 5 * 60 * 1000; // run every 5 minutes
        mainTimer.scheduleAtFixedRate(new MainTask(context), delay, period);

        // run maintenance jobs
        delay = 5 * 1000;
        period = 24 * 60 * 60 * 1000; // run every 24 hours
        maintenanceTimer.scheduleAtFixedRate(new PackageDeployerTask(context), delay, period);

        // advertise 
        delay = 5 * 1000;
        period = 24 * 60 * 60 * 1000; // run every 24 hours
        advertiseTimer.scheduleAtFixedRate(new AdvertiseTask(context), delay, period);

    }

    public void stop(BundleContext context) throws Exception {
        logger.debug("ENTERING stop(BundleContext)");

        mainTimer.purge();
        mainTimer.cancel();

        maintenanceTimer.purge();
        maintenanceTimer.cancel();

        advertiseTimer.purge();
        advertiseTimer.cancel();

        graphTimer.purge();
        graphTimer.cancel();

        credentialTimer.purge();
        credentialTimer.cancel();

    }

}

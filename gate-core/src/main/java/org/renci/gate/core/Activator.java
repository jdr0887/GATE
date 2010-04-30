package org.renci.gate.core;

import static org.renci.gate.core.Constants.CONFIG;
import static org.renci.gate.core.Constants.PROP_KEY_DELAY;
import static org.renci.gate.core.Constants.PROP_KEY_PERIOD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Timer;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class Activator implements BundleActivator {

    private final Logger logger = LoggerFactory.getLogger(Activator.class);

    private Timer mainTimer = new Timer();

    private Timer graphTimer = new Timer();

    private Timer credentialTimer = new Timer();

    private Properties properties = null;

    public Activator() {
        super();
    }

    private void init() throws Exception {
        String karafHome = System.getProperty("karaf.home");
        File config = new File(karafHome, CONFIG);
        if (!config.exists()) {
            FileUtils.touch(config);
        }
        this.properties = new Properties();
        FileInputStream fis = new FileInputStream(config);
        this.properties.load(fis);
        fis.close();

        if (!this.properties.contains(PROP_KEY_DELAY)) {
            this.properties.setProperty(PROP_KEY_DELAY, "5");
        }

        if (!this.properties.contains(PROP_KEY_PERIOD)) {
            this.properties.setProperty(PROP_KEY_PERIOD, "60");
        }

        FileOutputStream fos = new FileOutputStream(config);
        this.properties.store(fos, "time in seconds");
        fos.flush();
        fos.close();
    }

    public void start(BundleContext context) throws Exception {
        logger.debug("ENTERING start(BundleContext)");

        init();

        long delay = Long.valueOf(this.properties.getProperty(PROP_KEY_DELAY)) * 1000;
        long period = Long.valueOf(this.properties.getProperty(PROP_KEY_PERIOD)) * 1000;

        mainTimer.scheduleAtFixedRate(new MainTask(context), delay, period);

        delay = 5000;
        period = 3 * 60 * 60 * 1000; // run every 3 hours
        credentialTimer.scheduleAtFixedRate(new CredentialTask(context), delay, period);

    }

    public void stop(BundleContext context) throws Exception {
        logger.debug("ENTERING stop(BundleContext)");

        mainTimer.purge();
        mainTimer.cancel();

        graphTimer.purge();
        graphTimer.cancel();

        credentialTimer.purge();
        credentialTimer.cancel();

    }

}

package org.renci.gate.config;

import java.io.IOException;
import java.util.Properties;

public class GATEConfigurationServiceImpl implements GATEConfigurationService {

    private Properties coreProperties;

    public GATEConfigurationServiceImpl() {
        this.coreProperties = new Properties();
        try {
            this.coreProperties.load(this.getClass().getResourceAsStream("core.properties"));
            // overload with System props
            this.coreProperties.putAll(System.getProperties());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getCoreProperties() {
        return coreProperties;
    }

    public void setCoreProperties(Properties coreProperties) {
        this.coreProperties = coreProperties;
    }

}

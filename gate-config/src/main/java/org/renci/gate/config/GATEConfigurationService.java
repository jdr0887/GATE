package org.renci.gate.config;

import java.util.Properties;

public interface GATEConfigurationService {

    public static final String VERSION = "version";

    public static final String CONDOR_HOME = "condor-home";

    public Properties getCoreProperties();

}

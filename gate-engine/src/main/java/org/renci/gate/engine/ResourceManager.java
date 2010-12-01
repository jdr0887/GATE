package org.renci.gate.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * 
 * @author jdr0887
 */
public class ResourceManager {

    public static final String GLIDEIN_CONDOR_VM = "org/renci/gate/glidein/glidein.condor.vm";

    public static final String GLIDEIN_SH_VM = "org/renci/gate/glidein/glidein.sh.vm";

    public static final String CONDOR_CONFIG_VM = "org/renci/gate/glidein/condor_config.vm";
    
    public static final String USER_JOB_WRAPPER_SH = "org/renci/gate/glidein/user-job-wrapper.sh";

    public static final String ADVERTISE_CAPABILITIES_CONDOR_VM = "org/renci/gate/maintenance/advertise-capabilities.condor.vm";

    public static final String ADVERTISE_CAPABILITIES_SH = "org/renci/gate/maintenance/advertise-capabilities.sh";

    public static final String BUILD_DATABASES_CONDOR_VM = "org/renci/gate/maintenance/build-databases.condor.vm";

    public static final String BUILD_DATABASES_SH = "org/renci/gate/maintenance/build-databases.sh";

    public static final String BUILD_FORK_APPLICATIONS_CONDOR_VM = "org/renci/gate/maintenance/build-fork-applications.condor.vm";

    public static final String BUILD_FORK_APPLICATIONS_SH = "org/renci/gate/maintenance/build-fork-applications.sh";

    public static final String BUILD_LRM_APPLICATIONS_CONDOR_VM = "org/renci/gate/maintenance/build-lrm-applications.condor.vm";

    public static final String BUILD_LRM_APPLICATIONS_SH = "org/renci/gate/maintenance/build-lrm-applications.sh";

    public static final String CLEAN_CONDOR_VM = "org/renci/gate/maintenance/clean.condor.vm";

    public static final String CLEAN_SH = "org/renci/gate/maintenance/clean.sh";

    public static final String MASTER_DAG = "org/renci/gate/maintenance/master.dag";

    public static ResourceManager instance = null;

    private Map<String, String> store;

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    private ResourceManager() {
        super();
        this.store = new HashMap<String, String>();

        this.store.put(ADVERTISE_CAPABILITIES_CONDOR_VM, readResource(ADVERTISE_CAPABILITIES_CONDOR_VM));
        this.store.put(ADVERTISE_CAPABILITIES_SH, readResource(ADVERTISE_CAPABILITIES_SH));

        this.store.put(BUILD_DATABASES_CONDOR_VM, readResource(BUILD_DATABASES_CONDOR_VM));
        this.store.put(BUILD_DATABASES_SH, readResource(BUILD_DATABASES_SH));

        this.store.put(BUILD_FORK_APPLICATIONS_CONDOR_VM, readResource(BUILD_FORK_APPLICATIONS_CONDOR_VM));
        this.store.put(BUILD_FORK_APPLICATIONS_SH, readResource(BUILD_FORK_APPLICATIONS_SH));

        this.store.put(BUILD_LRM_APPLICATIONS_CONDOR_VM, readResource(BUILD_LRM_APPLICATIONS_CONDOR_VM));
        this.store.put(BUILD_LRM_APPLICATIONS_SH, readResource(BUILD_LRM_APPLICATIONS_SH));

        this.store.put(CLEAN_CONDOR_VM, readResource(CLEAN_CONDOR_VM));
        this.store.put(CLEAN_SH, readResource(CLEAN_SH));

        this.store.put(GLIDEIN_CONDOR_VM, readResource(GLIDEIN_CONDOR_VM));
        this.store.put(GLIDEIN_SH_VM, readResource(GLIDEIN_SH_VM));
        this.store.put(CONDOR_CONFIG_VM, readResource(CONDOR_CONFIG_VM));
        this.store.put(USER_JOB_WRAPPER_SH, readResource(USER_JOB_WRAPPER_SH));

        this.store.put(MASTER_DAG, readResource(MASTER_DAG));

    }

    public String getTemplate(String key) throws ResourceNotFoundException {
        if (!this.store.containsKey(key)) {
            throw new ResourceNotFoundException("Resource not found:" + key);
        }
        return this.store.get(key);
    }

    protected static String readResource(String resource) throws ResourceNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(resource);
        String ret;
        try {
            ret = IOUtils.toString(is);
        } catch (IOException e) {
            throw new ResourceNotFoundException(resource);
        }
        return (ret);
    }

}

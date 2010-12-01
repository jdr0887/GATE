package org.renci.gate.deployer;

import org.renci.gate.AbstractCLDeployer;
import org.renci.gate.annotation.Package;

/**
 * @author jdr0887
 * 
 */
@Package(name = "VCAKE", version = "08112008")
public class VCAKEDeployer extends AbstractCLDeployer {

    /**
     * 
     */
    public VCAKEDeployer() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getVersion()
     */
    @Override
    protected String getVersion() {
        return VCAKEDeployer.class.getAnnotation(Package.class).version();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getPackageName()
     */
    @Override
    protected String getPackageName() {
        return VCAKEDeployer.class.getAnnotation(Package.class).name();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getArchiveTemplate()
     */
    @Override
    protected String getArchiveTemplate() {
        return VCAKEDeployer.class.getAnnotation(Package.class).archiveNameTemplate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getResource()
     */
    @Override
    protected String getResource() {
        return "org/renci/gate/deployer/" + getPackageName() + ".vm";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getSourceURL()
     */
    @Override
    protected String getSourceURL() {
        return VCAKEDeployer.class.getAnnotation(Package.class).sourceURL();
    }

}

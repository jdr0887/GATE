package org.renci.gate.deployer;

import org.renci.gate.AbstractCLDeployer;
import org.renci.gate.annotation.Package;
import org.renci.gate.annotation.Package;

/**
 * @author jdr0887
 * 
 */
@Package(name = "WGS", version = "5.4", archiveNameTemplate = "${lowerCaseName}-${version}.tar.bz2")
public class WGSDeployer extends AbstractCLDeployer {

    /**
     * 
     */
    public WGSDeployer() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getVersion()
     */
    @Override
    protected String getVersion() {
        return WGSDeployer.class.getAnnotation(Package.class).version();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getPackageName()
     */
    @Override
    protected String getPackageName() {
        return WGSDeployer.class.getAnnotation(Package.class).name();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getArchiveTemplate()
     */
    @Override
    protected String getArchiveTemplate() {
        return WGSDeployer.class.getAnnotation(Package.class).archiveNameTemplate();
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
        return WGSDeployer.class.getAnnotation(Package.class).sourceURL();
    }

}

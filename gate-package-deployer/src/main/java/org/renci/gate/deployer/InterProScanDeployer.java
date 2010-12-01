package org.renci.gate.deployer;

import org.renci.gate.AbstractCLDeployer;
import org.renci.gate.annotation.Ignore;
import org.renci.gate.annotation.Package;

/**
 * @author jdr0887
 * 
 */
@Ignore
@Package(name = "InterProScan", version = "4.3.1", archiveNameTemplate = "iprscan_v${version}.tar.gz")
public class InterProScanDeployer extends AbstractCLDeployer {

    /**
     * 
     */
    public InterProScanDeployer() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getVersion()
     */
    @Override
    protected String getVersion() {
        return InterProScanDeployer.class.getAnnotation(Package.class).version();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getPackageName()
     */
    @Override
    protected String getPackageName() {
        return InterProScanDeployer.class.getAnnotation(Package.class).name();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.deployers.AbstractCLDeployer#getArchiveTemplate()
     */
    @Override
    protected String getArchiveTemplate() {
        return InterProScanDeployer.class.getAnnotation(Package.class).archiveNameTemplate();
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
        return InterProScanDeployer.class.getAnnotation(Package.class).sourceURL();
    }

}

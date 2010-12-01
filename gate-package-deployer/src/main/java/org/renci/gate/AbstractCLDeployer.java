package org.renci.gate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.renci.gate.deployer.DeployerException;
import org.renci.launcher.LaunchDescriptorBean;
import org.renci.launcher.Launcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public abstract class AbstractCLDeployer implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(AbstractCLDeployer.class);

    protected abstract String getVersion();

    protected abstract String getPackageName();

    protected abstract String getArchiveTemplate();

    protected abstract String getResource();

    protected abstract String getSourceURL();

    //pre-dispose to prod
    protected String runMode = "prod";

    protected Launcher launcher;

    private String username;

    private Long jobId;

    protected AbstractCLDeployer() {
    }

    public void run() {
        logger.debug("ENTERING run()");

        Launcher launcher = getLauncher();

        List<LaunchDescriptorBean> ldbList = launcher.getLaunchDescriptorBeans();

        try {
            Properties props = new Properties();
            props.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");
            Velocity.init(props);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String packageTemplate = readResourceToString(getResource());

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("package", getPackageName());
        velocityContext.put("lowerCaseName", getPackageName().toLowerCase());
        velocityContext.put("sourceURL", getSourceURL());
        velocityContext.put("runMode", runMode);

        String archive = null;
        String version = getVersion();

        for (LaunchDescriptorBean ldb : ldbList) {
            List<String> commands = ldb.getCommands();

            // version is the version of the tarball
            if (ldb.getRequiredInputMap().containsKey("version")) {
                version = ldb.getRequiredInputMap().get("version");
            }
            velocityContext.put("version", version);

            StringWriter archiveSW = new StringWriter();
            try {
                Velocity.evaluate(velocityContext, archiveSW, "deployer", getArchiveTemplate());
                archive = archiveSW.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // archive is the filename of tarball/zip
            if (ldb.getRequiredInputMap().containsKey("archive")) {
                archive = ldb.getRequiredInputMap().get("archive");
            }
            velocityContext.put("archive", archive);

            StringWriter sw = new StringWriter();
            try {
                Velocity.evaluate(velocityContext, sw, "deployer", packageTemplate);
            } catch (IOException e) {
                e.printStackTrace();
            }

            commands.add(sw.toString());
        }

        launcher.run();

    }

    protected void cleanup() throws DeployerException {
        logger.debug("ENTERING cleanup()");
        // cleanup
        // try {
        // launcher.cleanup();
        // } catch (LauncherException e) {
        // logger.warn("Unable to clean up work directory: " + e.getMessage());
        // throw new ApplicationException("Unable to clean up work dir");
        // }

        // check if the run was successful
        if (launcher.getExitCode() != 0) {
            throw new DeployerException("Command failed with exit code " + launcher.getExitCode() + ". Stderr: "
                    + launcher.getStdErr());
        }

    }

    protected String readResourceToString(String resource) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(resource);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (sb.toString());
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the jobId
     */
    public Long getJobId() {
        return jobId;
    }

    /**
     * @param jobId
     *            the jobId to set
     */
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.applications.IApplication#getLauncher()
     */
    public Launcher getLauncher() {
        return launcher;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.applications.IApplication#setLauncher(org.renci.sp.launcher .Launcher)
     */
    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

}

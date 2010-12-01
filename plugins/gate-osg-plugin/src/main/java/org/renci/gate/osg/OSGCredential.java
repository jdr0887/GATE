package org.renci.gate.osg;

import static org.renci.gate.osg.Constants.CONFIG;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.renci.cm.RSPCredential;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.renci.common.exec.Input;
import org.renci.common.exec.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class OSGCredential implements RSPCredential {

    private static final long serialVersionUID = 1L;

    protected static final int DEFAULT_LIFETIME = 12 * 60 * 60; // 12 hours

    private final Logger logger = LoggerFactory.getLogger(OSGCredential.class);

    private final Executor exec = Executor.getInstance();

    protected File jobDir;

    private String proxyFileName = ".osg_proxy";

    protected Date endOfLife = new Date(System.currentTimeMillis() + (4 * 24 * 60 * 60 * 1000));

    private BundleContext context;

    private Dictionary<Object, Object> properties;

    public OSGCredential(BundleContext context) {
        super();
        this.context = context;
        ServiceReference ref = context.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) context.getService(ref);
        try {
            Configuration configuration = configurationAdmin.createFactoryConfiguration(CONFIG);
            this.properties = configuration.getProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void remove() throws Exception {
        if (jobDir == null) {
            logger.error("NULL job directory...please set");
            return;
        }
        File proxyFile = new File(jobDir, getProxyFileName());
        proxyFile.delete();
    }

    public void create(Dictionary<Object, Object> properties) throws Exception {

        Enumeration<Object> e = properties.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            this.properties.put(key, properties.get(key));
        }

        if (jobDir == null) {
            logger.error("NULL job directory...please set");
            return;
        }
        logger.info("Renewing OSG credential in " + jobDir);

        String myproxyHost = this.properties.get("myproxy_host").toString();
        String myproxyPort = this.properties.get("myproxy_port").toString();
        String username = this.properties.get("username").toString();
        String passphrase = this.properties.get("passphrase").toString();

        File in = File.createTempFile("gate-osg", "pwd");
        FileUtils.writeStringToFile(in, passphrase);
        StringBuilder command = new StringBuilder();
        command.append("(");
        command.append(". /nfs/software/prod/osg-client/current/setup.sh");
        command.append(" && ");
        command.append("myproxy-logon --pshost ").append(myproxyHost).append(" --psport ").append(myproxyPort)
                .append(" --username ").append(username).append(" --quiet --out ").append(jobDir.getAbsolutePath())
                .append(File.separator).append(getProxyFileName()).append(" --stdin_pass <")
                .append(in.getAbsolutePath());
        command.append(");");
        in.delete();

        Input shellInput = new Input();
        shellInput.setCommand(command.toString());
        try {
            Output shellOutput = exec.run(shellInput);
            if ((shellOutput.getStderr() != null && shellOutput.getStderr().length() > 2)
                    || (shellOutput.getStdout() != null && shellOutput.getStdout().length() > 2)) {
                logger.error(command.toString());
                logger.error("myproxy error: " + shellOutput.getStderr() + "  " + shellOutput.getStdout());
                throw (new Exception("Unable to renew OSG proxy"));
            }
        } catch (ExecutorException e1) {
            logger.error(command.toString());
            logger.error("myproxy error: " + e1.getMessage());
            throw (new Exception("Unable to renew OSG proxy"));
        }

    }

    public boolean isDone() {

        // end of life
        Date now = new Date();
        if (now.after(endOfLife)) {
            return true;
        }

        // check log to see if job is still running
        boolean jobDone = false;
        File log = new File(jobDir + "/job.log");
        if (!log.exists()) {
            // maintenance jobs use jm.log
            log = new File(jobDir + "/jm.log");
        }
        if (log.exists()) {
            try {
                FileInputStream fstream = new FileInputStream(log);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("Job was aborted by the user") || line.contains("Job terminated")) {
                        jobDone = true;
                    }
                }
                in.close();
            } catch (Exception e) {// Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
        }

        return jobDone;
    }

    public String getTimeLeft() {
        Date now = new Date();
        long hours = (endOfLife.getTime() - now.getTime()) / (60 * 60 * 1000);
        long minutes = (endOfLife.getTime() - now.getTime()) % (60 * 60 * 1000);
        minutes = minutes / (60 * 1000);
        String s = "";
        if (hours == 1) {
            s += "1 hour, ";
        } else if (hours > 1) {
            s += "" + hours + " hours, ";
        }
        if (minutes == 1) {
            s += "1 minute";
        } else {
            s += "" + minutes + " minutes";
        }
        return s;
    }

    protected void writeProxy(GlobusCredential cred, String outPath) throws Exception {

        try {
            // Delete any previously written SAML GlobusCredential file
            File prevfile = new File(outPath);
            if (prevfile.exists()) {
                prevfile.delete();
            }

            BouncyCastleCertProcessingFactory certFactory = BouncyCastleCertProcessingFactory.getDefault();

            GlobusCredential proxy = certFactory.createCredential(cred.getCertificateChain(), cred.getPrivateKey(),
                    512, DEFAULT_LIFETIME, GSIConstants.GSI_4_IMPERSONATION_PROXY);

            // Write the SAML GlobusCredential to disc - for display only
            org.globus.gridshib.security.util.GSIUtil.writeCredentialToFile(proxy, outPath);

        } catch (Exception e) {
            logger.error("Failed to write credential to file: " + e.getMessage());
            throw (e);
        }
    }

    public Date getEndOfLife() {
        return endOfLife;
    }

    public void setEndOfLife(Date endOfLife) {
        this.endOfLife = endOfLife;
    }

    public File getJobDir() {
        return jobDir;
    }

    public void setJobDir(File jobDir) {
        this.jobDir = jobDir;
    }

    public String getProxyFileName() {
        return proxyFileName;
    }

    public void setProxyFileName(String proxyFileName) {
        this.proxyFileName = proxyFileName;
    }

}

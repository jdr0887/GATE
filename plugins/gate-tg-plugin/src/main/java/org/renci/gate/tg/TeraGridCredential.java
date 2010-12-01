package org.renci.gate.tg;

import static org.renci.gate.tg.Constants.CONFIG;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;

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
 * 
 */
public class TeraGridCredential implements RSPCredential {

    private static final long serialVersionUID = 1L;

    protected static final int DEFAULT_LIFETIME = 12 * 60 * 60; // 12 hours

    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    private final Logger logger = LoggerFactory.getLogger(TeraGridCredential.class);

    private final Executor exec = Executor.getInstance();

    protected File jobDir;

    private String proxyFileName = ".tg_proxy";

    protected Date endOfLife = new Date(System.currentTimeMillis() + (4 * 24 * 60 * 60 * 1000));

    private BundleContext context;

    private Dictionary<Object, Object> properties;

    public TeraGridCredential(BundleContext context) {
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

        logger.info("Renewing TeraGrid credential in " + jobDir);

        String portalUserName = this.properties.get("portalUserName").toString();
        String portalUserIP = this.properties.get("portalUserIP").toString();
        String portalUserAuthTimeStamp = this.properties.get("portalUserAuthTimeStamp").toString();
        String portalUserEmail = this.properties.get("portalUserEmail").toString();

        StringBuilder command = new StringBuilder();
        command.append("export GRIDSHIB_HOME=/nfs/software/prod/gridshib-saml-tools/current && $GRIDSHIB_HOME/bin/gridshib-saml-issuer");
        command.append(" --debug ");
        command.append("--user ").append(portalUserName);
        command.append(" --sender-vouches --authn --authnMethod urn:oasis:names:tc:SAML:1.0:am:password");
        command.append(" --authnInstant ").append(SDF.format(portalUserAuthTimeStamp));
        command.append(" --address ").append(portalUserIP);
        command.append(" --config $GRIDSHIB_HOME/etc/tg-gateway-config.properties");
        command.append(" --properties Attribute.mail.Name=urn:oid:0.9.2342.19200300.100.1.3 Attribute.mail.Value=")
                .append(portalUserEmail);
        command.append(" --x509 --outfile ").append(jobDir.getAbsolutePath()).append(File.separator)
                .append(getProxyFileName());

        Input shellInput = new Input();
        shellInput.setCommand(command.toString());
        try {
            Output shellOutput = exec.run(shellInput);
            if ((shellOutput.getStderr() != null && shellOutput.getStderr().length() > 2)
                    || (shellOutput.getStdout() != null && shellOutput.getStdout().length() > 2)) {
                logger.error(command.toString());
                logger.error("Gridshib error: " + shellOutput.getStderr() + "  " + shellOutput.getStdout());
                throw (new Exception("Unable to renew TeraGrid proxy"));
            }
        } catch (ExecutorException e1) {
            logger.error(command.toString());
            logger.error("myproxy error: " + e1.getMessage());
            throw (new Exception("Unable to renew TeraGrid proxy"));
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

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.gate.RSPCredential#isDone()
     */
    @Override
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

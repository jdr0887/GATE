package org.renci.gate.engine;

import static org.renci.gate.engine.ResourceManager.CONDOR_CONFIG_VM;
import static org.renci.gate.engine.ResourceManager.GLIDEIN_CONDOR_VM;
import static org.renci.gate.engine.ResourceManager.GLIDEIN_SH_VM;
import static org.renci.gate.engine.ResourceManager.USER_JOB_WRAPPER_SH;

import java.io.File;
import java.io.StringWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.renci.cm.RSPCredential;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.renci.common.exec.Input;
import org.renci.common.exec.Output;
import org.renci.gate.GATESite;
import org.renci.gate.api.persistence.GlideIn;
import org.renci.gate.api.persistence.GlideInPersistenceService;
import org.renci.gate.persistence.entity.GlideInImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateGlideInCallable implements Callable<Integer> {

    private static final ResourceManager resourceManager = ResourceManager.getInstance();

    private final Logger logger = LoggerFactory.getLogger(CreateGlideInCallable.class);

    private final Executor exec = Executor.getInstance();

    private int jobCount = 1;

    private String portalUser = "";

    private String portalUserEmail = "";

    private Date portalUserAuthTimestamp = null;

    private String portalUserIP = "";

    private String requirements = "";

    private String needs = "";

    private BundleContext context;

    private List<GATESite> siteList;
    
    public CreateGlideInCallable(BundleContext context, List<GATESite> siteList, int maxToSubmit, String portalUser, String portalUserEmail,
            Date portalUserAuthTimestamp, String portalUserIP, String requirements, String needs) {
        super();
        this.context = context;
        this.siteList = siteList;
        this.jobCount = maxToSubmit;
        this.portalUser = portalUser;
        this.portalUserEmail = portalUserEmail;
        this.portalUserAuthTimestamp = portalUserAuthTimestamp;
        this.portalUserIP = portalUserIP;
        this.requirements = requirements;
        this.needs = needs;
    }

    /**
     * Creates a new glide-in
     * 
     * @return number of glide-ins submitted
     */
    public Integer call() {

        logger.info("Creating new glidein for user " + portalUser);

        // find a site to submit to
        SiteSelectionCallable s = new SiteSelectionCallable(siteList, portalUser, requirements);
        GATESite site = s.call();
        if (site == null) {
            logger.error("No sites are available for " + portalUser + " - " + needs);
            return 0;
        }

        logger.info("Selected site: " + site.getName());

        // create glideIn db record
        ServiceReference glideInPersistenceSR = context.getServiceReference(GlideInPersistenceService.class.getName());
        GlideInPersistenceService glideinPersistenceService = (GlideInPersistenceService) context
                .getService(glideInPersistenceSR);

        GlideIn glideIn = new GlideInImpl();
        glideIn.setDateSubmitted(new Date());

        long glideinParentId = 0;
        try {
            glideinPersistenceService.save(glideIn);
            glideinParentId = glideIn.getId();
        } catch (Exception e) {
            logger.error("Unable to save glide-in information to database");
        }

        // how many glideins to submit
        jobCount = Math.min(jobCount, site.getMaxMultipleJobs());

        // will we exceed the max number of glideins to the site?
        jobCount = Math.min(jobCount, site.getMaxTotalCount() - site.getTotalCount());

        logger.info("Will try to submit " + jobCount + " glideins as 1 job");

        try {

            // workdir
            File workDir = createWorkDir(site.getName(), portalUser);
            logger.info("Work directory: " + workDir.getAbsolutePath());

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("glideinParentId", glideinParentId);
            velocityContext.put("siteName", site.getName());
            velocityContext.put("portalUser", portalUser);

            // RequirementType[] requirementTypeArray = RequirementType.values();
            // StringBuilder sb = new StringBuilder();
            // for (RequirementType rt : requirementTypeArray) {
            // sb.append(", ").append(rt.name());
            // }
            velocityContext.put("siteClassAdAttributesKeys", "HAS_SCIENCE_APPLICATIONS, HAS_FILE_DATABASES");
            // note that we want a lower max run time here, so that the glidein can shut down
            // gracefully before getting kicked off by the batch scheduler
            int maxRunTimeAdjusted = site.getMaxRunTime() - 180;
            if (maxRunTimeAdjusted < 0) {
                maxRunTimeAdjusted = site.getMaxRunTime() / 2;
            }
            velocityContext.put("siteMaxRunTimeMins", maxRunTimeAdjusted);
            velocityContext.put("siteMaxRunTimeSecs", maxRunTimeAdjusted * 60);
            velocityContext.put("siteMaxNoClaimTimeSecs", site.getMaxNoClaimTime() * 60);

            // write job wrapper
            writeTemplate(velocityContext, workDir.getAbsolutePath() + "/glidein.sh",
                    resourceManager.getTemplate(GLIDEIN_SH_VM));
            writeTemplate(velocityContext, workDir.getAbsolutePath() + "/condor_config",
                    resourceManager.getTemplate(CONDOR_CONFIG_VM));
            writeTemplate(velocityContext, workDir.getAbsolutePath() + "/user-job-wrapper.sh",
                    resourceManager.getTemplate(USER_JOB_WRAPPER_SH));

            // create proxy cert
            RSPCredential cred = site.getCredential();
            cred.setJobDir(workDir);
            Properties props = new Properties();
            props.put("portalUser", portalUser);
            props.put("portalUserEmail", portalUserEmail);
            props.put("portalUserIP", portalUserIP);
            props.put("portalUserAuthTimestamp", portalUserAuthTimestamp);
            cred.create(props);

            // submit
            String rsl = "";
            if (jobCount == 1) {
                rsl += "(jobType=single)";
                rsl += "(min_memory=950)(max_memory=950)";
            } else {
                rsl += "(jobType=multiple)(count=" + jobCount + ")";
            }
            rsl += "(maxWallTime=" + site.getMaxRunTime() + ")";
            if (StringUtils.isNotEmpty(site.getProject())) {
                rsl += "(project=" + site.getProject() + ")";
            }

            velocityContext = new VelocityContext();
            velocityContext.put("glideinParentId", glideinParentId);
            velocityContext.put("siteName", site.getName());
            velocityContext.put("gridType", site.getGridType());
            velocityContext.put("gatekeeper", site.getGatekeeper());
            velocityContext.put("jobmanager", site.getJobManager());
            velocityContext.put("rsl", rsl);
            velocityContext.put("x509userproxy", workDir.getAbsolutePath() + "/.proxy");
            velocityContext.put("siteMaxQueueTimeMins", site.getMaxQueueTime());
            velocityContext.put("portalUser", portalUser);
            velocityContext.put("jobCount", jobCount);

            StringWriter sw = new StringWriter();
            try {
                Velocity.evaluate(velocityContext, sw, null, resourceManager.getTemplate(GLIDEIN_CONDOR_VM));
                File outFile = new File(workDir.getAbsolutePath() + "/glidein.submit");
                FileUtils.writeStringToFile(outFile, sw.toString());
            } catch (Exception e) {
                throw e;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("cd ").append(workDir.getCanonicalPath())
                    .append(" && condor_submit glidein.submit > condor_submit.log 2>&1");
            logger.debug(sb.toString());
            Input shellInput = new Input();
            shellInput.setWorkDir(workDir);
            shellInput.setCommand(sb.toString());
            try {
                Output so = exec.run(shellInput);

                // System.exit(1);
                if (so.getExitCode() != 0) {
                    throw new Exception("Check " + workDir + "/condor_submit.log for details");
                }
            } catch (ExecutorException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            logger.error("Unable to submit new glidein: " + e.getMessage());
            return 0;
        }

        return jobCount;
    }

    /**
     * Creates a new work dir in scratch space
     * 
     * @return path to the new workdir
     */
    private File createWorkDir(String sitename, String portalUser) throws Exception {
        Date date = new Date();
        Format formatter = new SimpleDateFormat("MMM-dd");
        StringBuilder sb = new StringBuilder();
        sb.append("/nfs/scratch/glideins/").append(formatter.format(date)).append(File.separator).append(portalUser)
                .append(System.currentTimeMillis()).append(".").append(sitename);
        File f = new File(sb.toString());
        f.mkdirs();
        return f;
    }

    /**
     * Generates a new job file which is used to setup and start the glidein
     */
    private void writeTemplate(VelocityContext velocityContext, String fileName, String template) throws Exception {

        StringWriter sw = new StringWriter();
        try {
            Velocity.evaluate(velocityContext, sw, null, template);
            File outFile = new File(fileName);
            outFile.setReadable(true);
            outFile.setExecutable(true);
            outFile.setWritable(true, true);
            FileUtils.writeStringToFile(outFile, sw.toString());
        } catch (Exception e) {
            throw e;
        }
    }

}

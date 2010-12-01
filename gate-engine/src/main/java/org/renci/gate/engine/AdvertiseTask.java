package org.renci.gate.engine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.renci.common.exec.Input;
import org.renci.common.exec.Output;
import org.renci.gate.ClassAd;
import org.renci.gate.GATESite;
import org.renci.gate.services.GATEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvertiseTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(AdvertiseTask.class);

    private BundleContext context;

    public AdvertiseTask(BundleContext context) {
        super();
        this.context = context;
    }

    public void run() {

        try {
            ServiceReference[] siteSelectorServiceRefArray = context.getServiceReferences(GATEService.class.getName(),
                    null);

            if (siteSelectorServiceRefArray != null) {

                for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                    logger.info(serviceRef.toString());
                    GATEService gateService = (GATEService) context.getService(serviceRef);

                    if (gateService != null) {

                        for (GATESite site : gateService.getSiteList()) {

                            logger.debug("Advertising site: " + site.getName());
                            ClassAd ad = new ClassAd();

                            ad.setString("MyType", "GATESite");
                            ad.setString("TargetType", "Job");
                            // ad.set("StartdIpAddr", "152.54.1.88");
                            ad.set("GATESite", "True");

                            ad.setString("Name", site.getName());
                            ad.setString("SiteName", site.getName());
                            ad.set("SiteVerified", "True");
                            ad.set("Requirements", "False");

                            // special sauce to identify the site as site and not a glidein
                            ad.set("PGF_IS_SITE_AD", "True");

                            // some attributes needed to evaluate the users' job requirements
                            ad.set("IS_GLIDEIN", "True");
                            ad.setString("FileSystemDomain", "portal.renci.org");
                            ad.setString("Arch", "X86_64");
                            ad.setString("OpSys", "LINUX");
                            ad.set("HasFileTransfer", "True");

                            // FIXME: we should determine memory from the maintenance jobs
                            ad.set("Memory", "1000");
                            ad.set("Disk", "1000");

                            if (!readAttributesFromMaintenenceJob(site, ad)) {
                                logger.warn("Not advertising " + site.getName()
                                        + " because maintenance job was unsuccessful");
                                return;
                            }

                            ad.writeToFile("var/final-ads/" + site.getName());

                            logger.info("Advertising " + site.getName());

                            File cwd = new File(".");
                            StringBuilder sb = new StringBuilder();
                            sb.append(" cd ").append(cwd.getAbsolutePath())
                                    .append("/var/final-ads/ && condor_advertise UPDATE_AD_GENERIC " + site.getName());
                            Input shellInput = new Input();
                            shellInput.setCommand(sb.toString());
                            logger.info(sb.toString());
                            try {
                                Executor exec = Executor.getInstance();
                                Output shellOutput = exec.run(shellInput);
                                String out = shellOutput.getStdout() + "\n" + shellOutput.getStderr();
                                out.trim();
                                if (out.length() > 3) {
                                    logger.warn(out);
                                }
                            } catch (ExecutorException e) {
                                logger.error("Failed to advertise site: " + e.getMessage());
                            }

                        }
                    }
                    context.ungetService(serviceRef);
                }
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }

    }

    private boolean readAttributesFromMaintenenceJob(GATESite site, ClassAd ad) {

        boolean success = false;

        File file = new File("var/maintenance-jobs/" + site.getName() + "/advertise-capabilities.out");
        logger.info("Reading capabilities file: " + file.getAbsolutePath());
        if (!file.exists() || (file.exists() && file.length() == 0)) {
            return success;
        }

        try {
            List<String> lines = FileUtils.readLines(file);
            for (String line : lines) {
                if (line.indexOf("=== SUCCESSFUL ===") >= 0) {
                    success = true;
                } else if (line.indexOf("CLASSAD:") == 0) {
                    String info = line.substring(8);
                    ad.setRawLine(info);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading job out: " + e.getMessage());
            success = false;
        }

        return success;
    }

}

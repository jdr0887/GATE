package org.renci.gate.osg;

import static org.renci.gate.osg.Constants.CONFIG;
import static org.renci.gate.osg.Constants.PROP_KEY_CENTRAL_MASTER;
import static org.renci.gate.osg.Constants.PROP_KEY_VO_NAME;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.drools.RuleBase;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.renci.cm.RSPCredential;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.renci.common.exec.Input;
import org.renci.common.exec.Output;
import org.renci.gate.ClassAd;
import org.renci.gate.GATESite;
import org.renci.gate.services.GATEService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class OSGGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Executor exec = Executor.getInstance();

    private BundleContext context;

    public OSGGATEService(BundleContext context) {
        super();
        this.context = context;

    }

    @Override
    public List<RuleBase> getFilterSiteRules() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.osgmm.SiteSelectorService#getSiteList()
     */
    @Override
    public List<GATESite> getSiteList() {

        List<GATESite> siteList = new ArrayList<GATESite>();

        // main site list
        siteList.addAll(pullReSS());
        logger.info("siteList.size() = " + siteList.size());

        // SRM hack - replace ads for sites which support SRM
        siteList.addAll(pullReSSSRMHack());
        logger.info("siteList.size() = " + siteList.size());

        // get additional ads from the file system, useful for adding
        // your own resources, or resources outside OSG
        readAdditionalAds(siteList);

        return siteList;
    }

    private List<GATESite> pullReSS() {

        ServiceReference ref = context.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) context.getService(ref);
        Configuration configuration;
        List<GATESite> siteList = null;
        try {
            configuration = configurationAdmin.createFactoryConfiguration(CONFIG);
            String voName = configuration.getProperties().get(PROP_KEY_VO_NAME).toString();
            String centralMaster = configuration.getProperties().get(PROP_KEY_CENTRAL_MASTER).toString();

            // pull ress for classads for our VO
            logger.info("Pulling upstream ReSS server (" + centralMaster + ") for ads");

            String cmd = "(condor_status -any -long -constraint 'StringlistIMember(\"VO:" + voName
                    + "\";GlueCEAccessControlBaseRule)' -pool " + centralMaster + " -direct " + centralMaster
                    + " ; echo; echo)";
            siteList = createSiteList(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return siteList;
    }

    /**
     * pulls upstream server for ReSS ads
     */
    private List<GATESite> pullReSSSRMHack() {

        ServiceReference ref = context.getServiceReference(ConfigurationAdmin.class.getName());
        ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) context.getService(ref);
        Configuration configuration;
        List<GATESite> siteList = null;
        try {
            configuration = configurationAdmin.createFactoryConfiguration(CONFIG);
            String voName = configuration.getProperties().get(PROP_KEY_VO_NAME).toString();
            String centralMaster = configuration.getProperties().get(PROP_KEY_CENTRAL_MASTER).toString();

            String cmd = "(condor_status -any -long" + " -constraint 'StringlistIMember(\"VO:" + voName
                    + "\";GlueCEAccessControlBaseRule) && " + " (GlueSEControlProtocolType == \"SRM\")'" + " -pool "
                    + centralMaster + " -direct " + centralMaster + " ; echo; echo)";
            siteList = createSiteList(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return siteList;
    }

    private List<GATESite> createSiteList(String command) {

        List<GATESite> siteList = new ArrayList<GATESite>();

        // temp file for the ads
        File tmp = null;
        try {
            tmp = File.createTempFile("osgmm", "classads");
        } catch (IOException e) {
            logger.error("Unable to create temp file: " + e.getMessage());
        }

        String cmd = command + " >" + tmp.getPath();

        try {
            Input si = new Input();
            si.setCommand(cmd);
            Output so = exec.run(si);

            if (so.getStderr() != null && so.getStderr().length() > 2) {
                logger.error(so.getStderr().toString());
                return siteList;
            }
        } catch (ExecutorException e1) {
            logger.error("Shell error: " + e1.getMessage());
            return siteList;
        }

        ClassAd classAd;
        try {
            List<ClassAd> classAdList = new ArrayList<ClassAd>();
            List<String> lines = FileUtils.readLines(tmp, "UTF-8");
            classAd = new ClassAd();

            for (String line : lines) {
                if (line.trim().length() == 0) {
                    classAdList.add(classAd);
                    classAd = new ClassAd();
                }
                if (line.length() > 20000) {
                    logger.error("Huge line in ReSS classad: " + line);
                    continue;
                }
                classAd.parseLine(line);
            }

            GATESite site = null;
            for (ClassAd c : classAdList) {
                site = processReSSAd(c);
                if (site != null) {
                    logger.debug(site.toString());
                    siteList.add(site);
                }
            }
        } catch (IOException e) {
            logger.error("Unable to read ads from " + tmp.getPath() + ": " + e.getMessage());
            return siteList;
        } finally {
            tmp.delete();
        }
        return siteList;

    }

    /**
     * Processes a ReSS ad
     */
    private GATESite processReSSAd(ClassAd classAd) {

        // always key on GlueSiteName
        String siteName = classAd.getReallyTrimmed("GlueSiteName");
        logger.debug("siteName = " + siteName);

        if (classAd.getFullAd().length() < 200) {
            logger.debug("ignore short ads: " + siteName);
            return null;
        }

        if (siteName == null) {
            logger.debug("null siteName");
            // logger.error("ReSS: Found ad without GlueSiteName\n" +
            // classAd.getFullAd());
            return null;
        }

        // make sure GlueSiteName is properly formatted
        String patternStr = "[^a-zA-Z0-9_-]";
        Pattern pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(siteName);
        if (matcher.find()) {
            logger.error("Site will not be included due to invalid GlueSiteName: " + siteName);
            return null;
        }

        String uniqueId = siteName + "_" + classAd.getReallyTrimmed("GlueCEInfoContactString");
        uniqueId = uniqueId.replaceAll("/", "_");
        uniqueId = uniqueId.replaceAll(":", "_");

        // update the site with the new ress information
        GATESite site = new GATESite();
        site.setName(siteName);
        site.setUniqueId(uniqueId);
        site.setCredential(new OSGCredential(context));

        // change the unique Condor identifier for the add to the sitename
        classAd.set("Name", "\"" + uniqueId + "\"");
        site.setClassAd(classAd);
        return site;
    }

    /**
     * Read additional ads from etc/
     */
    private void readAdditionalAds(List<GATESite> siteList) {

        String karafHome = System.getProperty("karaf.home");
        File dir = new File(karafHome, "etc/additional-ads");
        dir.mkdir();

        // ignore dot files
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.startsWith(".");
            }
        };

        String[] adFiles = dir.list(filter);
        if (adFiles == null) {
            // Either dir does not exist or is not a directory
            return;
        }

        for (GATESite site : siteList) {
            ClassAd classAd = site.getClassAd();
            for (int i = 0; i < adFiles.length; i++) {
                String fileName = dir.getAbsolutePath() + File.separator + adFiles[i];
                try {
                    List<String> lines = FileUtils.readLines(new File(fileName), "UTF-8");
                    for (String line : lines) {
                        classAd.parseLine(line);
                    }
                } catch (IOException e) {
                    logger.error("Error reading ad from " + fileName + ": " + e.getMessage());
                }
            }
        }

    }

//    @Override
//    public RSPCredential getCredential() {
//        return new OSGCredential(context);
//    }

}

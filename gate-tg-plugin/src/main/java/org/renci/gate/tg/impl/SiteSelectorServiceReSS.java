package org.renci.gate.tg.impl;

import static org.renci.gate.tg.Constants.CONFIG_FILE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.renci.gate.ClassAd;
import org.renci.gate.Site;
import org.renci.gate.services.SiteSelectorService;
import org.renci.gate.shell.ShellException;
import org.renci.gate.shell.ShellInput;
import org.renci.gate.shell.ShellOutput;
import org.renci.gate.shell.ShellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class SiteSelectorServiceReSS implements SiteSelectorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private BundleContext context;
    
    private Properties properties;

    public SiteSelectorServiceReSS(BundleContext context) {
        super();
        this.context = context;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {

        String karafHome = System.getProperty("karaf.home");
        File config = new File(karafHome, CONFIG_FILE);
        if (!config.exists()) {
            FileUtils.touch(config);
        }
        this.properties = new Properties();
        FileInputStream fis = new FileInputStream(config);
        this.properties.load(fis);
        fis.close();

//        if (!this.properties.contains(PROP_KEY_CENTRAL_MASTER)) {
//            this.properties.setProperty(PROP_KEY_CENTRAL_MASTER, "osg-ress-1.fnal.gov");
//        }
//
//        if (!this.properties.contains(PROP_KEY_VO_NAME)) {
//            this.properties.setProperty(PROP_KEY_VO_NAME, "Engage");
//        }

        FileOutputStream fos = new FileOutputStream(config);
        this.properties.store(fos, null);
        fos.flush();
        fos.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.osgmm.SiteSelectorService#getSiteList()
     */
    public List<Site> getSiteList() {

        List<Site> siteList = new ArrayList<Site>();

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

    private List<Site> pullReSS() {
        String voName = this.properties.getProperty("vo_name");
        String centralMaster = this.properties.getProperty("central_master");

        // pull ress for classads for our VO
        logger.info("Pulling upstream ReSS server (" + centralMaster + ") for ads");

        String cmd = "(condor_status -any -long -constraint 'StringlistIMember(\"VO:" + voName
                + "\";GlueCEAccessControlBaseRule)' -pool " + centralMaster + " -direct " + centralMaster
                + " ; echo; echo)";
        List<Site> siteList = createSiteList(cmd);
        return siteList;
    }

    /**
     * pulls upstream server for ReSS ads
     */
    private List<Site> pullReSSSRMHack() {
        String voName = this.properties.getProperty("vo_name");
        String centralMaster = this.properties.getProperty("central_master");
        String cmd = "(condor_status -any -long" + " -constraint 'StringlistIMember(\"VO:" + voName
                + "\";GlueCEAccessControlBaseRule) && " + " (GlueSEControlProtocolType == \"SRM\")'" + " -pool "
                + centralMaster + " -direct " + centralMaster + " ; echo; echo)";

        List<Site> siteList = createSiteList(cmd);
        return siteList;
    }

    private List<Site> createSiteList(String command) {

        List<Site> siteList = new ArrayList<Site>();

        // temp file for the ads
        File tmp = null;
        try {
            tmp = File.createTempFile("osgmm", "classads");
        } catch (IOException e) {
            logger.error("Unable to create temp file: " + e.getMessage());
        }

        String cmd = command + " >" + tmp.getPath();

        ServiceReference serviceReference = context.getServiceReference(ShellService.class.getName());
        ShellService shellService = (ShellService) context.getService(serviceReference);

        try {
            ShellInput si = new ShellInput();
            si.setCommand(cmd);
            ShellOutput so = shellService.run(si);

            if (so.getStderr() != null && so.getStderr().length() > 2) {
                logger.error(so.getStderr().toString());
                return siteList;
            }
        } catch (ShellException e1) {
            logger.error("Shell error: " + e1.getMessage());
            return siteList;
        }

        context.ungetService(serviceReference);
        
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

            Site site = null;
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
    private Site processReSSAd(ClassAd classAd) {

        // always key on GlueSiteName
        String siteName = classAd.getReallyTrimmed("GlueSiteName");
        logger.debug("siteName = " + siteName);

        if (classAd.getFullAd().length() < 200) {
            logger.debug("ignore short ads: " + siteName);
            return null;
        }

        if (siteName == null) {
            logger.debug("null siteName");
            // logger.error("ReSS: Found ad without GlueSiteName\n" + classAd.getFullAd());
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
        Site site = new Site();
        site.setName(siteName);
        site.setUniqueId(uniqueId);

        // change the unique Condor identifier for the add to the sitename
        classAd.set("Name", "\"" + uniqueId + "\"");
        site.setClassAd(classAd);
        return site;
    }

    /**
     * Read additional ads from etc/
     */
    private void readAdditionalAds(List<Site> siteList) {

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

        for (Site site : siteList) {
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

}

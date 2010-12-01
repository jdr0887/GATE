package org.renci.gate.engine;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.renci.cm.RSPCredential;
import org.renci.commons.reflection.ReflectionManager;
import org.renci.condor.CondorUniverse;
import org.renci.gate.AbstractCLDeployer;
import org.renci.gate.GATESite;
import org.renci.gate.annotation.Ignore;
import org.renci.gate.annotation.JobManagerType;
import org.renci.gate.annotation.Package;
import org.renci.gate.services.GATEService;
import org.renci.launcher.LaunchDescriptorBean;
import org.renci.launcher.Launcher;
import org.renci.launcher.condor.CondorLauncher;
import org.renci.launcher.condor.cli.application.CondorLauncherCLI;
import org.renci.launcher.condor.cli.deployer.CondorDAGLauncherCLI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageDeployerTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(PackageDeployerTask.class);

    private BundleContext context;

    public PackageDeployerTask(BundleContext context) {
        super();
        this.context = context;
    }

    @Override
    public void run() {
        logger.info("Submitting maintenance jobs");

        try {

            ReflectionManager reflectionManager = ReflectionManager.getInstance();

            List<Class<?>> filteredClassList = new ArrayList<Class<?>>();

            List<Class<?>> classList = new ArrayList<Class<?>>();

            classList.addAll(reflectionManager.lookupClassList("org.renci.sp.deployer", null, Package.class));

            for (Class<?> c : classList) {
                if (!c.isAnnotationPresent(Ignore.class)) {
                    filteredClassList.add(c);
                }
            }

            Properties props = new Properties();
            props.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");
            try {
                Velocity.init(props);
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            for (Class<?> appClass : filteredClassList) {

                Object appInstance;
                try {
                    appInstance = appClass.newInstance();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                    return;
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                    return;
                }

                Class<?> annotatedClass = lookupAnnotatedClass(appInstance.getClass());

                if (appInstance instanceof AbstractCLDeployer) {

                    AbstractCLDeployer clDeployer = (AbstractCLDeployer) appInstance;

                    // set up work directory
                    File maintenanceJobHome = new File(System.getProperty("karaf.home") + File.separator + "var"
                            + File.separator + "maintenance-jobs");
                    File workDir = new File(maintenanceJobHome, appClass.getSimpleName());

                    if (workDir.exists()) {
                        workDir.delete();
                    }
                    workDir.mkdirs();

                    Launcher launcher = new CondorDAGLauncherCLI();
                    launcher.setWorkDirectory(workDir);

                    ServiceReference[] siteSelectorServiceRefArray = context.getServiceReferences(
                            GATEService.class.getName(), null);

                    if (siteSelectorServiceRefArray != null) {

                        for (ServiceReference serviceRef : siteSelectorServiceRefArray) {
                            logger.info(serviceRef.toString());
                            GATEService gateService = (GATEService) context.getService(serviceRef);

                            if (gateService != null) {

                                List<LaunchDescriptorBean> launchDescriptorBeanList = new LinkedList<LaunchDescriptorBean>();

                                for (GATESite site : gateService.getSiteList()) {
                                    logger.debug("Starting maintenance routine for: " + site.getName());

                                    // create proxy cert
                                    RSPCredential cred = site.getCredential();
                                    cred.setJobDir(workDir);
                                    try {
                                        props.put("portalUser", "PortalMaintenanceJob");
                                        props.put("portalUserEmail", "portal-help@renci.org");
                                        props.put("portalUserIP", "152.54.1.82");
                                        props.put("portalUserAuthTimestamp", new Date());
                                        cred.create(props);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }

                                    LaunchDescriptorBean ldb = new LaunchDescriptorBean();
                                    // ldb.setJobId(packageDeploy.getId());
                                    ldb.setGatekeeperHost(site.getGatekeeper());
                                    ldb.setGridType(site.getGridType().toString().toLowerCase());

                                    if (JobManagerType.FORK.equals(annotatedClass.getAnnotation(Package.class)
                                            .jobManagerType())) {
                                        ldb.setJobManager(annotatedClass.getAnnotation(Package.class).jobManagerType()
                                                .toString().toLowerCase());
                                    } else {
                                        ldb.setJobManager(site.getJobManager());
                                    }

                                    String rsl = "(maxWallTime=" + site.getMaxRunTime() + ")";
                                    if (StringUtils.isNotEmpty(site.getProject())) {
                                        rsl += "(project=" + site.getProject() + ")";
                                    }
                                    ldb.setRsl(rsl);

                                    Map<String, String> requiredInputMap = new HashMap<String, String>();
                                    requiredInputMap.put("version", annotatedClass.getAnnotation(Package.class)
                                            .version());
                                    String packageFilename = "";
                                    String archiveTemplate = annotatedClass.getAnnotation(Package.class)
                                            .archiveNameTemplate();
                                    StringWriter sw = new StringWriter();
                                    try {
                                        VelocityContext velocityContext = new VelocityContext();
                                        velocityContext.put("name", annotatedClass.getAnnotation(Package.class).name());
                                        velocityContext.put("lowerCaseName", annotatedClass
                                                .getAnnotation(Package.class).name().toLowerCase());
                                        velocityContext.put("version", annotatedClass.getAnnotation(Package.class)
                                                .version());
                                        Velocity.evaluate(velocityContext, sw, "package name", archiveTemplate);
                                        packageFilename = sw.toString();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    requiredInputMap.put("archive", packageFilename);
                                    ldb.setRequiredInputMap(requiredInputMap);

                                    Map<String, String> optionalInputMap = new HashMap<String, String>();
                                    ldb.setOptionalInputMap(optionalInputMap);

                                    ldb.setCredential(cred);

                                    launchDescriptorBeanList.add(ldb);

                                }

                                launcher.setLaunchDescriptorBeans(launchDescriptorBeanList);

                            }
                        }
                        if (launcher instanceof CondorLauncher) {
                            CondorLauncher condorLauncher = (CondorLauncher) launcher;
                            condorLauncher.setUniverse(CondorUniverse.GRID);
                        }

                        if (launcher instanceof CondorLauncherCLI) {
                            CondorLauncherCLI condorLauncher = (CondorLauncherCLI) launcher;
                            condorLauncher.setPortalUsername("PortalMaintenanceJob");
                        }

                        // this is the *real* invoker/worker
                        try {

                            Method setLauncherMethod = clDeployer.getClass().getMethod("setLauncher", Launcher.class);
                            setLauncherMethod.invoke(clDeployer, launcher);

                            logger.info("STARTING...." + annotatedClass.getAnnotation(Package.class).name());
                            if (appInstance instanceof Runnable) {
                                // this should always be true
                                Runnable runnable = (Runnable) appInstance;
                                runnable.run();
                            }
                            logger.info("FINISHED...." + annotatedClass.getAnnotation(Package.class).name());
                        } catch (Exception e) {
                            logger.error("Failed to run job", e);
                            return;
                        }

                    }

                }

                logger.debug("Finished maintenance routine");
            }

        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }

    }

    protected Class<?> lookupAnnotatedClass(Class<?> app) {

        if (app != null && app.isAnnotationPresent(Package.class)) {
            return app;
        }

        Class<?>[] runnerInterfaces = app.getInterfaces();
        for (Class<?> runnerInterface : runnerInterfaces) {
            if (runnerInterface.isAnnotationPresent(Package.class)) {
                app = runnerInterface;
                break;
            }
        }
        return app;
    }

}

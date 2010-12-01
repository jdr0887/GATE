package org.renci.gate.engine;

import java.io.File;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
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
public class GraphTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(CredentialTask.class);

    private BundleContext context;

    public GraphTask(BundleContext context) {
        super();
        this.context = context;
    }

    @Override
    public void run() {
        logger.debug("Updating rrd job statistics in var/stats/");

        File cwd = new File(".");
        String outputDir = cwd.getAbsolutePath() + "/var/stats";

        StringBuilder command = new StringBuilder();
        command.append("unset DISPLAY; ");
        command.append(cwd.getAbsolutePath()).append("/bin/condor_grid_overview  --updaterrd ");
        command.append(outputDir).append("/jobs.rrd && ");
        command.append(cwd.getAbsolutePath()).append("/bin/condor_grid_overview > ");
        command.append(outputDir).append("/condor_grid_overview.txt");

        logger.info("command = " + command.toString());

        try {

            Executor exec = Executor.getInstance();
            Input si = new Input();
            si.setCommand(command.toString());
            Output so = exec.run(si);

            if (so.getStderr() != null && so.getStderr().length() > 1) {
                logger.error(so.getStdout() + "  " + so.getStderr());
            }

        } catch (ExecutorException e1) {
            logger.warn(command.toString());
            logger.error("Failed to run GraphTask", e1);
        }

    }

}

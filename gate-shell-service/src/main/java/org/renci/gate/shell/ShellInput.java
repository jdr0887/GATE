package org.renci.gate.shell;

import java.io.File;
import java.util.Map;

public class ShellInput {

    // the directory where to run the command from
    protected File workDir;

    // the shell environment
    protected Map<String, String> environment;

    // the command to run
    protected String command;

    // timeout for the command - in seconds
    protected long maxRunTime = 60;

    // stdin
    protected StringBuffer stdin;

    /**
     * 
     * @param command
     *            command to run
     */
    public ShellInput() {
        environment = null;
        workDir = new File("/tmp");
    }

    /**
     * @return the workDir
     */
    public File getWorkDir() {
        return workDir;
    }

    /**
     * @param workDir
     *            the workDir to set
     */
    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    /**
     * @return the environment
     */
    public Map<String, String> getEnvironment() {
        return environment;
    }

    /**
     * @param environment
     *            the environment to set
     */
    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command
     *            the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @return the maxRunTime
     */
    public long getMaxRunTime() {
        return maxRunTime;
    }

    /**
     * @param maxRunTime
     *            the maxRunTime to set
     */
    public void setMaxRunTime(long maxRunTime) {
        this.maxRunTime = maxRunTime;
    }

    /**
     * @return the stdin
     */
    public StringBuffer getStdin() {
        return stdin;
    }

    /**
     * @param stdin
     *            the stdin to set
     */
    public void setStdin(StringBuffer stdin) {
        this.stdin = stdin;
    }

}

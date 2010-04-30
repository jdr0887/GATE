package org.renci.gate.shell;

public class ShellOutput {

    protected int exitCode;

    protected StringBuffer stdout;

    protected StringBuffer stderr;

    /**
     * 
     * @param command
     *            command to run
     */
    public ShellOutput() {
    }

    /**
     * clear resources used
     */
    public void clear() {
        stdout = null;
        stderr = null;
    }

    /**
     * @return the exitCode
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * @param exitCode
     *            the exitCode to set
     */
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * @return the stdout
     */
    public StringBuffer getStdout() {
        return stdout;
    }

    /**
     * @param stdout
     *            the stdout to set
     */
    public void setStdout(StringBuffer stdout) {
        this.stdout = stdout;
    }

    /**
     * @return the stderr
     */
    public StringBuffer getStderr() {
        return stderr;
    }

    /**
     * @param stderr
     *            the stderr to set
     */
    public void setStderr(StringBuffer stderr) {
        this.stderr = stderr;
    }

}

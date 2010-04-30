package org.renci.gate.shell;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

public class ShellServiceImpl implements ShellService {

    public ShellServiceImpl() {
    }

    /**
     * run the command
     * 
     * @return exit code
     * @throws ShellException
     */
    public ShellOutput run(ShellInput input) throws ShellException {

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        BufferedOutputStream stdinStream = null;
        StreamGobbler stdoutGobbler = null;
        StreamGobbler stderrGobbler = null;
        int exitCode = -1;
        File wrapperFile = null;
        String wrapperContents = null;
        long processStartTime = System.currentTimeMillis();
        String delayedError = null;

        ShellOutput output = new ShellOutput();

        // create a shell script with the command line in it
        wrapperContents = "#!/bin/bash -e\n" + "cd " + input.getWorkDir().getAbsolutePath() + "\n" + input.getCommand()
                + "\n";
        try {
            wrapperFile = File.createTempFile("shellwrapper-", ".sh", input.getWorkDir());
            FileUtils.writeStringToFile(wrapperFile, wrapperContents, "UTF-8");
        } catch (IOException e) {
            throw new ShellException("Unable to create tmp file");
        }

        // chmod the temp command file
        try {
            process = runtime.exec("/bin/chmod 755 " + wrapperFile.getAbsolutePath());
            process.waitFor();
        } catch (IOException ioe) {
            throw new ShellException("chmod problem: " + ioe.getMessage());
        } catch (InterruptedException e) {
            // ignore
        }

        try {
            String[] env = null;
            if (input.getEnvironment() != null) {
                env = environmentToArray(input.getEnvironment());
            }
            process = runtime.exec(wrapperFile.getAbsolutePath(), env, input.getWorkDir());

            // outputs
            stdoutGobbler = new StreamGobbler(process.getInputStream());
            stdoutGobbler.start();
            stderrGobbler = new StreamGobbler(process.getErrorStream());
            stderrGobbler.start();

            // inputs
            if (input.getStdin() != null) {
                stdinStream = new BufferedOutputStream(process.getOutputStream());
                stdinStream.write(input.getStdin().toString().getBytes());
            }

            // do we have a max runtime to consider?
            if (input.getMaxRunTime() > 0) {
                boolean stillRunning = true;
                while (stillRunning) {

                    long currentRunTime = (System.currentTimeMillis() - processStartTime) / 1000;

                    // check timeout
                    if (currentRunTime > input.getMaxRunTime()) {
                        process.destroy();
                        delayedError = "Process timed out";
                    }

                    // sleep
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // ignore
                    }

                    // now check the process
                    try {
                        exitCode = process.exitValue();
                        output.setExitCode(exitCode);
                        // can't get here until process has finished
                        stillRunning = false;
                    } catch (IllegalThreadStateException e) {
                        // ignore
                    }
                }
            }

            try {
                exitCode = process.waitFor();
                output.setExitCode(exitCode);
            } catch (InterruptedException exp) {
                delayedError = exp.getMessage();
            }

            // let the io streams buffering catch up
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // ignore
            }

        } catch (IOException ioe) {
            throw new ShellException("Process error: " + ioe.getMessage());
        } finally {

            // get the outputs weather the process failed or not
            if (stdoutGobbler != null) {
                output.setStdout(stdoutGobbler.getOutput());
            }
            if (stderrGobbler != null) {
                output.setStderr(stderrGobbler.getOutput());
            }

            try {

                if (stdinStream != null) {
                    stdinStream.close();
                }

                if (stdoutGobbler != null) {
                    stdoutGobbler.close();
                }
                if (stderrGobbler != null) {
                    stderrGobbler.close();
                }

                // close streams
                if (process != null) {
                    InputStream istr = process.getInputStream();
                    if (istr != null) {
                        istr.close();
                    }
                    OutputStream ostr = process.getOutputStream();
                    if (ostr != null) {
                        ostr.close();
                    }
                    istr = process.getErrorStream();
                    if (istr != null) {
                        istr.close();
                    }
                    process.destroy();
                }

            } catch (IOException exp) {
                // ignore
            }

        }

        // clean up
        wrapperFile.delete();

        if (delayedError != null) {
            throw new ShellException(delayedError);
        }

        return output;
    }

    /**
     * Converts an environment map to an string array
     * 
     * @return the string array
     */
    protected String[] environmentToArray(Map<String, String> env) {
        Set<String> keySet = env.keySet();
        Iterator<String> keys = keySet.iterator();
        Vector<String> envVec = new Vector<String>();
        String[] envArr = new String[0];

        while (keys.hasNext()) {
            Object key = keys.next();
            Object val = env.get(key);
            envVec.add("" + key + "=" + val + "");
        }

        envArr = (String[]) envVec.toArray(new String[0]);

        return envArr;
    }
}

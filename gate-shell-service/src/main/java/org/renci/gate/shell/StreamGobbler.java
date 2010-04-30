package org.renci.gate.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
    
    private InputStream inputStream;

    private StringBuffer output;

    StreamGobbler(InputStream inputStream) {
        this.inputStream = inputStream;
        this.output = new StringBuffer();
    }

    public void run() {
        try {
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(isr);
                String line = null;

                while ((line = br.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
        } catch (Exception ioe) {
            // ignore io errors
        }
    }

    public StringBuffer getOutput() {
        return output;
    }
    
    public void close() {
        if (inputStream != null) {
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                // ignore
            }
        }
    }
}

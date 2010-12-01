package org.renci.gate.deployer;

/**
 * 
 * @author jdr
 */
public class DeployerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DeployerException() {
        super();
    }

    public DeployerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeployerException(String message) {
        super(message);
    }

    public DeployerException(Throwable cause) {
        super(cause);
    }

}

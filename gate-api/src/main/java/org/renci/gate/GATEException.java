package org.renci.gate;

public class GATEException extends Exception {

    private static final long serialVersionUID = 6872480536157508515L;

    public GATEException() {
        super();
    }

    public GATEException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GATEException(String message, Throwable cause) {
        super(message, cause);
    }

    public GATEException(String message) {
        super(message);
    }

    public GATEException(Throwable cause) {
        super(cause);
    }

}

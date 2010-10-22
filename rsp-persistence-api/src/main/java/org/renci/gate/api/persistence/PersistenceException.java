package org.renci.gate.api.persistence;

public class PersistenceException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PersistenceException() {
        super();
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(String message) {
        super(message);
    }

}

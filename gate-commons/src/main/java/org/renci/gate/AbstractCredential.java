package org.renci.gate;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

public abstract class AbstractCredential implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    protected Date endOfLife;

    protected Properties properties;

    public AbstractCredential(Date endOfLife, Properties properties) {
        super();
        this.endOfLife = endOfLife;
        this.properties = properties;
    }

    public abstract boolean isDone();

    public abstract void renew() throws Exception;

    public abstract void remove() throws Exception;

    public abstract void create(Properties properties) throws Exception;


}

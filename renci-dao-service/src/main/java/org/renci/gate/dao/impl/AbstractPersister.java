package org.renci.gate.dao.impl;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * 
 * @author jdr0887
 */
public abstract class AbstractPersister {

    protected EntityManagerFactory entityManagerFactory;

    public AbstractPersister() {
        super();
        init();
    }

    private void init() {

        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        Properties props = new Properties();
        entityManagerFactory = Persistence.createEntityManagerFactory("rsp", props);
        if (entityManagerFactory == null)
            throw new RuntimeException("Creating EntityManagerFactory failed!");
        else
            System.out.println("Init JPA ok.");
        Thread.currentThread().setContextClassLoader(oldCL);
    }

}

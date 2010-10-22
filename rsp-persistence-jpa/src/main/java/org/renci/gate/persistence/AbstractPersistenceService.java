package org.renci.gate.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;

/**
 * 
 * @author jdr0887
 */
public abstract class AbstractPersistenceService {

    @PersistenceUnit(unitName = "rsp")
    private EntityManager entityManager;

    public AbstractPersistenceService() {
        super();
    }

    public AbstractPersistenceService(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}

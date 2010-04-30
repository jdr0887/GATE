package org.renci.gate.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.renci.gate.dao.InputPersistence;
import org.renci.gate.dao.domain.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class InputPersister extends AbstractPersister implements InputPersistence {

    private final Logger logger = LoggerFactory.getLogger(InputPersister.class);
    
    public InputPersister() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findById(java.lang.Long)
     */
    public Input findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Input ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from Input a where a.id = :id");
            query.setParameter("id", id);
            ret = (Input) query.getSingleResult();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain.JobHistory)
     */
    public void save(Input input) {
        logger.debug("ENTERING save(Input)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(input);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

}

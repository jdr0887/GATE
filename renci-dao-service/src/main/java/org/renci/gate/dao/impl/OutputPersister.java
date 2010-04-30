package org.renci.gate.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.renci.gate.dao.OutputPersistence;
import org.renci.gate.dao.domain.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class OutputPersister extends AbstractPersister implements OutputPersistence{

    private final Logger logger = LoggerFactory.getLogger(OutputPersister.class);
    
    public OutputPersister() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findById(java.lang.Long)
     */
    public Output findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Output ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from Output a where a.id = :id");
            query.setParameter("id", id);
            ret = (Output) query.getSingleResult();
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
    public void save(Output output) {
        logger.debug("ENTERING save(Output)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(output);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

}

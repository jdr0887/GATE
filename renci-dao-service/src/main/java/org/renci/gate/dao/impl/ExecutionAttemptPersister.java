package org.renci.gate.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.renci.gate.dao.ExecutionAttemptPersistence;
import org.renci.gate.dao.domain.ExecutionAttempt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class ExecutionAttemptPersister extends AbstractPersister implements ExecutionAttemptPersistence {

    private final Logger logger = LoggerFactory.getLogger(ExecutionAttemptPersister.class);

    public ExecutionAttemptPersister() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findById(java.lang.Long)
     */
    public ExecutionAttempt findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        ExecutionAttempt ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from ExecutionAttempt a where a.id = :id");
            query.setParameter("id", id);
            ret = (ExecutionAttempt) query.getSingleResult();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.ExecutionAttemptDAO#findByJobId(java.lang.Long)
     */
    public ExecutionAttempt findByJobId(Long id) {
        logger.debug("ENTERING findByJobId(Long)");
        ExecutionAttempt ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from ExecutionAttempt a where a.job.id = :id");
            query.setParameter("id", id);
            ret = (ExecutionAttempt) query.getSingleResult();
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
    public void save(ExecutionAttempt ea) {
        logger.debug("ENTERING save(ExecutionAttempt)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(ea);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

}

package org.renci.gate.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.renci.gate.dao.GlideInPersistence;
import org.renci.gate.dao.domain.GlideIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class GlideInPersister extends AbstractPersister implements GlideInPersistence {

    private final Logger logger = LoggerFactory.getLogger(GlideInPersister.class);
    
    public GlideInPersister() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobAuditService#findById(java.lang.Long)
     */
    public GlideIn findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        GlideIn ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from GlideIn a where a.id = :id");
            query.setParameter("id", id);
            ret = (GlideIn) query.getSingleResult();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain .JobHistory)
     */
    public void save(GlideIn glideIn) {
        logger.debug("ENTERING save(GlideIn)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(glideIn);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

}

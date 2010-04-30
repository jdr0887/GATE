package org.renci.gate.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.renci.gate.dao.PhoneHomePersistence;
import org.renci.gate.dao.domain.PhoneHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class PhoneHomePersister extends AbstractPersister implements PhoneHomePersistence {

    private final Logger logger = LoggerFactory.getLogger(PhoneHomePersister.class);
    
    public PhoneHomePersister() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findById(java.lang.Long)
     */
    public PhoneHome findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        PhoneHome ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from PhoneHome a where a.id = :id");
            query.setParameter("id", id);
            ret = (PhoneHome) query.getSingleResult();
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
    public void save(PhoneHome jobMetric) {
        logger.debug("ENTERING save(PhoneHome)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(jobMetric);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#delete(org.renci.portal.dao.domain .JobHistory)
     */
    public void delete(PhoneHome phonehome) {
        logger.debug("ENTERING delete(PhoneHome)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(phonehome);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#delete(java.lang.Long)
     */
    public void delete(Long id) {
        logger.info("ENTERING delete()");
        PhoneHome jobHistory = findById(id);
        delete(jobHistory);
    }

}

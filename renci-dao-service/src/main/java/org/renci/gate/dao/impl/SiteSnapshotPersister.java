package org.renci.gate.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.renci.gate.dao.SiteSnapshotPersistence;
import org.renci.gate.dao.domain.SiteSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class SiteSnapshotPersister extends AbstractPersister implements SiteSnapshotPersistence {

    private final Logger logger = LoggerFactory.getLogger(SiteSnapshotPersister.class);

    public SiteSnapshotPersister() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobAuditService#findById(java.lang.Long)
     */
    public SiteSnapshot findById(Long id) {
        logger.debug("ENTERING findById(Long)");

        SiteSnapshot ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("from SiteSnapshot a where a.id = :id");
            ret = (SiteSnapshot) query.getSingleResult();
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
    public void save(SiteSnapshot siteSnapshot) {
        logger.debug("ENTERING save(SiteSnapshot)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(siteSnapshot);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

}

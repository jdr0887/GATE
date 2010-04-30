package org.renci.gate.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.renci.gate.dao.SitePersistence;
import org.renci.gate.dao.domain.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class SitePersister extends AbstractPersister implements SitePersistence {

    private final Logger logger = LoggerFactory.getLogger(SitePersister.class);

    public SitePersister() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findById(java.lang.Long)
     */
    public Site findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Site ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from Site a where a.id = :id");
            query.setParameter("id", id);
            ret = (Site) query.getSingleResult();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.SiteDAO#findAll()
     */
    public List<Site> findAll() throws PersistenceException {
        logger.debug("ENTERING findAll()");
        List<Site> ret = new ArrayList<Site>();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select s from Site");
            ret = query.getResultList();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.SiteDAO#findEnabled()
     */
    public List<Site> findEnabled() throws PersistenceException {
        logger.debug("ENTERING findEnabled()");
        List<Site> ret = new ArrayList<Site>();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from Site a where a.enabled = :enabled and a.name != 'RENCI_SP_TEST'");
            query.setParameter("enabled", Boolean.TRUE);
            ret = query.getResultList();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.SiteDAO#findByName(java.lang.String)
     */
    public Site findByName(String name) {
        logger.debug("ENTERING findByName(String)");
        Site ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from Site a where a.name = :name");
            query.setParameter("name", name);
            ret = (Site) query.getSingleResult();
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
    public void save(Site site) {
        logger.debug("ENTERING save(Site)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(site);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

}

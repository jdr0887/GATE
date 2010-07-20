package org.renci.gate.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.renci.gate.api.persistence.Site;
import org.renci.gate.api.persistence.SitePersistenceService;
import org.renci.gate.persistence.entity.SiteImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class SitePersistenceServiceImpl extends AbstractPersistenceService implements SitePersistenceService {

    private final Logger logger = LoggerFactory.getLogger(SitePersistenceServiceImpl.class);

    public SitePersistenceServiceImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findById(java.lang.Long)
     */
    public SiteImpl findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Query query = getEntityManager().createQuery("select a from SiteImpl a where a.id = :id");
        query.setParameter("id", id);
        SiteImpl ret = (SiteImpl) query.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.SiteDAO#findAll()
     */
    public List<SiteImpl> findAll() throws PersistenceException {
        logger.debug("ENTERING findAll()");
        Query query = getEntityManager().createQuery("select s from SiteImpl s");
        List<SiteImpl> ret = query.getResultList();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.SiteDAO#findEnabled()
     */
    public List<SiteImpl> findEnabled() throws PersistenceException {
        logger.debug("ENTERING findEnabled()");
        Query query = getEntityManager().createQuery(
                "select a from SiteImpl a where a.enabled = :enabled and a.name != 'RENCI_SP_TEST'");
        query.setParameter("enabled", Boolean.TRUE);
        List<SiteImpl> ret = query.getResultList();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.SiteDAO#findByName(java.lang.String)
     */
    public SiteImpl findByName(String name) {
        logger.debug("ENTERING findByName(String)");
        Query query = getEntityManager().createQuery("select a from SiteImpl a where a.name = :name");
        query.setParameter("name", name);
        SiteImpl ret = (SiteImpl) query.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain .JobHistory)
     */
    public void save(Site site) {
        logger.debug("ENTERING save(Site)");
        getEntityManager().persist(site);
    }

}

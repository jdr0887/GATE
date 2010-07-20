package org.renci.gate.persistence;

import javax.persistence.Query;

import org.renci.gate.api.persistence.GlideIn;
import org.renci.gate.api.persistence.GlideInPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class GlideInPersistenceServiceImpl extends AbstractPersistenceService implements GlideInPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(GlideInPersistenceServiceImpl.class);

    public GlideInPersistenceServiceImpl() {
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
        Query query = getEntityManager().createQuery("select a from GlideIn a where a.id = :id");
        query.setParameter("id", id);
        ret = (GlideIn) query.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain .JobHistory)
     */
    public void save(GlideIn glideIn) {
        logger.debug("ENTERING save(GlideIn)");
        getEntityManager().persist(glideIn);
    }

}

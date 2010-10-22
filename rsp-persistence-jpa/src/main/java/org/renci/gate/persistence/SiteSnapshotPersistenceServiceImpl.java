package org.renci.gate.persistence;

import javax.persistence.Query;

import org.renci.gate.api.persistence.SiteSnapshot;
import org.renci.gate.api.persistence.SiteSnapshotPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class SiteSnapshotPersistenceServiceImpl extends AbstractPersistenceService implements
        SiteSnapshotPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(SiteSnapshotPersistenceServiceImpl.class);

    public SiteSnapshotPersistenceServiceImpl() {
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
        Query query = getEntityManager().createQuery("from SiteSnapshot a where a.id = :id");
        ret = (SiteSnapshot) query.getSingleResult();

        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain .JobHistory)
     */
    public void save(SiteSnapshot siteSnapshot) {
        logger.debug("ENTERING save(SiteSnapshot)");
        getEntityManager().persist(siteSnapshot);
    }

}

package org.renci.gate.persistence;

import javax.persistence.Query;

import org.renci.gate.api.persistence.ExecutionAttempt;
import org.renci.gate.api.persistence.ExecutionAttemptPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class ExecutionAttemptPersistenceServiceImpl extends AbstractPersistenceService implements
        ExecutionAttemptPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(ExecutionAttemptPersistenceServiceImpl.class);

    public ExecutionAttemptPersistenceServiceImpl() {
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
        Query query = getEntityManager().createQuery("select a from ExecutionAttempt a where a.id = :id");
        query.setParameter("id", id);
        ret = (ExecutionAttempt) query.getSingleResult();
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
        Query query = getEntityManager().createQuery("select a from ExecutionAttempt a where a.job.id = :id");
        query.setParameter("id", id);
        ret = (ExecutionAttempt) query.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain.JobHistory)
     */
    public void save(ExecutionAttempt ea) {
        logger.debug("ENTERING save(ExecutionAttempt)");
        getEntityManager().persist(ea);
    }

}

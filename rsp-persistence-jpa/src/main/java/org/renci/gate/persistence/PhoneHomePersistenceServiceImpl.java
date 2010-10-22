package org.renci.gate.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.renci.gate.api.persistence.PhoneHome;
import org.renci.gate.api.persistence.PhoneHomePersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class PhoneHomePersistenceServiceImpl extends AbstractPersistenceService implements PhoneHomePersistenceService {

    private final Logger logger = LoggerFactory.getLogger(PhoneHomePersistenceServiceImpl.class);

    public PhoneHomePersistenceServiceImpl() {
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
        Query query = getEntityManager().createQuery("select a from PhoneHome a where a.id = :id");
        query.setParameter("id", id);
        ret = (PhoneHome) query.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain .JobHistory)
     */
    public void save(PhoneHome jobMetric) {
        logger.debug("ENTERING save(PhoneHome)");
        getEntityManager().persist(jobMetric);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#delete(org.renci.portal.dao.domain .JobHistory)
     */
    public void delete(PhoneHome phonehome) {
        logger.debug("ENTERING delete(PhoneHome)");
        EntityManager entityManager = getEntityManager();
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

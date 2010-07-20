package org.renci.gate.persistence;

import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.renci.gate.api.persistence.Job;
import org.renci.gate.api.persistence.JobPersistenceService;
import org.renci.gate.api.persistence.OriginationType;
import org.renci.gate.api.persistence.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class JobPersistenceServiceImpl extends AbstractPersistenceService implements JobPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(JobPersistenceServiceImpl.class);

    public JobPersistenceServiceImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findById(java.lang.Long)
     */
    public Job findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Job ret = null;
        Query q = getEntityManager().createQuery("select a from Job a where a.id = :id");
        q.setParameter("id", id);
        ret = (Job) q.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findByUserId(java.lang.Long)
     */
    public List<Job> findByUserName(String username) {
        logger.debug("ENTERING findByUserName(String)");
        List<Job> ret = null;

        CriteriaBuilder queryBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Job> crit = queryBuilder.createQuery(Job.class);
        Root<Job> root = crit.from(Job.class);

        Predicate condition1 = queryBuilder.equal(root.get("userName"), username);
        Predicate condition2 = queryBuilder.equal(root.get("purged"), Boolean.FALSE);
        Predicate condition3 = queryBuilder.isNull(root.get("parent_fid"));

        // criteria.add(Restrictions.sqlRestriction("{alias}.parent_fid is null"));
        crit.orderBy(queryBuilder.desc(root.get("dateSubmitted")));

        queryBuilder.and(condition1, condition2, condition3);

        Query query = getEntityManager().createQuery(crit);
        ret = query.getResultList();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.JobDAO#findById(java.lang.Long)
     */
    public StatusType findStatus(Long id) {
        logger.debug("ENTERING findStatus(Long)");
        StatusType ret = null;
        Query q = getEntityManager().createQuery("select a.status from Job a where a.id = :id");
        q.setParameter("id", id);
        ret = (StatusType) q.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findQueuedJobs()
     */
    public List<Job> findQueuedJobs() {
        logger.debug("ENTERING findQueuedJobs()");
        List<Job> ret = null;
        CriteriaBuilder queryBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Job> crit = queryBuilder.createQuery(Job.class);
        Root<Job> root = crit.from(Job.class);
        Predicate condition1 = queryBuilder.equal(root.get("status"), StatusType.QUEUED);
        Predicate condition2 = queryBuilder.equal(root.get("purged"), Boolean.FALSE);
        Predicate condition3 = queryBuilder.in(root.get("origination").in(OriginationType.PORTLET,
                OriginationType.ASYNCHRONOUS_WEB_SERVICE, OriginationType.ASYNCHRONOUS_ADVANCED_WEB_SERVICE));
        queryBuilder.and(condition1, condition2, condition3);

        crit.orderBy(queryBuilder.desc(root.get("dateSubmitted")));
        Query query = getEntityManager().createQuery(crit);
        ret = query.getResultList();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain .JobHistory)
     */
    public void save(Job jobHistory) {
        logger.debug("ENTERING save()");
        getEntityManager().persist(jobHistory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#delete(org.renci.portal.dao.domain .JobHistory)
     */
    public void delete(Job jobHistory) {
        logger.info("ENTERING delete()");
        getEntityManager().remove(jobHistory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#delete(java.lang.Long)
     */
    public void delete(Long id) {
        logger.info("ENTERING delete()");
        Job jobHistory = findById(id);
        delete(jobHistory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.JobHistoryDAO#findByDateRange(java.util.Date, java.util.Date)
     */
    public List<Job> findByDateRange(Date startDate, Date endDate) {
        logger.debug("ENTERING findByDateRange(Date startDate, Date endDate)");
        List<Job> ret = null;
        CriteriaBuilder critBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Job> crit = critBuilder.createQuery(Job.class);
        Root<Job> root = crit.from(Job.class);

        // Predicate condition1 = critBuilder.le(root.get("dateSubmitted"), endDate);
        //            
        // crit.where(condition1);

        crit.orderBy(critBuilder.desc(root.get("dateSubmitted")));

        // criteria.add(Restrictions.between("dateSubmitted", startDate, endDate));

        Query query = getEntityManager().createQuery(crit);
        ret = query.getResultList();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.JobDAO#findByUserNameAndOrigination(java.lang.String,
     * org.renci.sp.dao.domain.OriginationType)
     */
    public List<Job> findByUserNameAndOriginationList(String username, List<OriginationType> originationTypeList) {
        logger.debug("ENTERING findByUserNameAndOrigination(String, List<OriginationType>)");
        List<Job> ret = null;
        CriteriaBuilder critBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Job> crit = critBuilder.createQuery(Job.class);
        Root<Job> root = crit.from(Job.class);

        crit.orderBy(critBuilder.desc(root.get("dateSubmitted")));

        // Criteria criteria = session.createCriteria(Job.class);
        // criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        // criteria.add(Restrictions.eq("userName", username));
        // criteria.add(Restrictions.eq("purged", Boolean.FALSE));
        // criteria.add(Restrictions.sqlRestriction("{alias}.parent_fid is null"));
        // // criteria.add(Restrictions.sizeGt("jobs", 0));
        // Object[] originationTypeArray = originationTypeList.toArray();
        // criteria.add(Restrictions.in("origination", originationTypeArray));
        // ret = criteria.list();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.JobDAO#findByUserNameAndDateRange(java.lang.String, java.util.Date, java.util.Date)
     */
    public List<Job> findByUserNameAndDateRange(String username, Date startDate, Date endDate)
            throws PersistenceException {
        logger.debug("ENTERING findByUserNameAndDateRange(String, Date, Date)");
        List<Job> ret = null;
        CriteriaBuilder critBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Job> crit = critBuilder.createQuery(Job.class);
        Root<Job> root = crit.from(Job.class);

        crit.orderBy(critBuilder.desc(root.get("dateSubmitted")));

        // Criteria criteria = session.createCriteria(Job.class);
        // criteria.add(Restrictions.eq("userName", username));
        // criteria.add(Restrictions.between("dateSubmitted", startDate, endDate));
        // ret = criteria.list();
        return ret;
    }

}

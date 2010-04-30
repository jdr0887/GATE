package org.renci.gate.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.renci.gate.dao.AccountPersistence;
import org.renci.gate.dao.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class AccountPersister extends AbstractPersister implements AccountPersistence {

    private final Logger logger = LoggerFactory.getLogger(AccountPersister.class);

    public AccountPersister() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#delete(org.renci.sp.dao.domain.Account)
     */
    public void delete(Account account) {
        logger.debug("ENTERING delete(Account)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(account);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#findById(java.lang.Long)
     */
    public Account findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Account ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from Account a where a.id = :id");
            query.setParameter("id", id);
            ret = (Account) query.getSingleResult();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountDAO#findActive()
     */
    public List<Account> findActive() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Account> ret = null;
        try {
            CriteriaBuilder critBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> crit = critBuilder.createQuery(Account.class);
            Root<Account> root = crit.from(Account.class);
            Predicate condition1 = critBuilder.equal(root.get("active"), Boolean.TRUE);
            crit.where(condition1);
            Query query = entityManager.createQuery(crit);
            ret = query.getResultList();
        } finally {
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#findByUsername(java.lang.String)
     */
    public Account findByUsername(String username) {
        logger.debug("ENTERING findByUsername(String)");
        Account ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Query query = entityManager.createQuery("select a from Account a where a.username = :username");
            query.setParameter("username", username);
            ret = (Account) query.getSingleResult();
        } finally {
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountDAO#findByWSAccessKey(java.lang.String)
     */
    public Account findByWSAccessKey(String key) throws PersistenceException {
        logger.debug("ENTERING findByWSAccessKey(String)");
        Account ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Query query = entityManager.createQuery("select a from Account a where wsAccessKey = :wsAccessKey");
            query.setParameter("wsAccessKey", key);
            ret = (Account) query.getSingleResult();
        } finally {
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#save(org.renci.sp.dao.domain.Account)
     */
    public void save(Account account) {
        logger.debug("ENTERING save(Account)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(account);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#findByExample(org.renci.sp.dao.domain.Account)
     */
    public List<Account> findByExample(Account account) {
        logger.debug("ENTERING findByExample(Account)");
        List<Account> ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            CriteriaBuilder critBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> crit = critBuilder.createQuery(Account.class);
            Root<Account> root = crit.from(Account.class);
            
            
//            crit.where(condition1);
//            
//            Query query = entityManager.createQuery(crit);
//            ret = query.getResultList();
//
//            // Criteria crit = entityManager.createCriteria(Account.class);
//            if (account != null) {
//                
//                Predicate condition1 = critBuilder.like(root.get()get("fullName"), account.getFullName());
//                
//                
//                Predicate condition2 = critBuilder.equal(root.get("active"), Boolean.TRUE);
//
//                String fullName = account.getFullName().replace("%", "");
//
//                if (StringUtils.isNotEmpty(fullName)) {
//                    crit.add(Restrictions.like("fullName", "%" + fullName + "%"));
//                }
//
//                String username = account.getUsername().replace("%", "");
//                if (StringUtils.isNotEmpty(username)) {
//                    crit.add(Restrictions.like("username", "%" + username + "%"));
//                }
//
//                String email = account.getEmail().replace("%", "");
//                if (StringUtils.isNotEmpty(email)) {
//                    crit.add(Restrictions.like("email", "%" + email + "%"));
//                }
//            }
            //
            // ret = crit.list();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#findCurrentRequests()
     */
    public List<Account> findCurrentRequests() {
        logger.debug("ENTERING findCurrentRequests()");
        List<Account> ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            CriteriaBuilder critBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> crit = critBuilder.createQuery(Account.class);
            Root<Account> root = crit.from(Account.class);
            
            Predicate condition1 = critBuilder.equal(root.get("active"), Boolean.FALSE);
            Predicate condition2 = critBuilder.isNull(root.get("datePasswordChanged"));
            Predicate condition3 = critBuilder.isNull(root.get("dateApproved"));
            Predicate condition4 = critBuilder.isNotNull(root.get("dateVerified"));
            crit.where(condition1, condition2, condition3, condition4);

            Query query = entityManager.createQuery(crit);
            ret = query.getResultList();
        } finally {
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#findApprovedRequests()
     */
    public List<Account> findApprovedRequests() {
        logger.debug("ENTERING findApprovedRequests()");
        List<Account> ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            CriteriaBuilder critBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> crit = critBuilder.createQuery(Account.class);
            Root<Account> root = crit.from(Account.class);
            
            Predicate condition1 = critBuilder.equal(root.get("active"), Boolean.FALSE);
            Predicate condition2 = critBuilder.isNull(root.get("datePasswordChanged"));
            Predicate condition3 = critBuilder.isNotNull(root.get("dateApproved"));
            Predicate condition4 = critBuilder.isNotNull(root.get("dateVerified"));
            
            crit.where(condition1, condition2, condition3, condition4);

            Query query = entityManager.createQuery(crit);
            ret = query.getResultList();
        } finally {
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountDAO#findPasswordChangedAccounts()
     */
    public List<Account> findPasswordChangedAccounts() {
        logger.debug("ENTERING findPasswordChangedAccounts()");
        List<Account> ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            CriteriaBuilder critBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Account> crit = critBuilder.createQuery(Account.class);
            Root<Account> root = crit.from(Account.class);
            
            Predicate condition1 = critBuilder.equal(root.get("active"), Boolean.TRUE);
            Predicate condition2 = critBuilder.isNotNull(root.get("datePasswordChanged"));
            Predicate condition3 = critBuilder.isNotNull(root.get("dateApproved"));
            Predicate condition4 = critBuilder.isNotNull(root.get("dateVerified"));
            crit.where(condition1, condition2, condition3, condition4);

            Query query = entityManager.createQuery(crit);
            ret = query.getResultList();
        } finally {
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#findByHash(java.lang.String)
     */
    public Account findByVerificationHash(String hash) {
        logger.debug("ENTERING findByVerificationHash(String)");
        Account ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from Account a where a.verificationHash = :hash");
            query.setParameter("hash", hash);
            ret = (Account) query.getSingleResult();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountDAO#findByPasswordResetHash(java.lang.String)
     */
    public Account findByPasswordResetHash(String hash) {
        logger.debug("ENTERING findByPasswordResetHash(String)");
        Account ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from Account a where a.passwordResetHash = :hash");
            query.setParameter("hash", hash);
            ret = (Account) query.getSingleResult();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountDAO#findByEmail(java.lang.String)
     */
    public Account findByEmail(String email) {
        logger.debug("ENTERING findByEmail(String)");
        Account ret = null;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from Account a where a.email = :email");
            query.setParameter("email", email);
            ret = (Account) query.getSingleResult();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

}

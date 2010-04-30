package org.renci.gate.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.renci.gate.dao.AccountGroupPersistence;
import org.renci.gate.dao.domain.AccountGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class AccountGroupPersister extends AbstractPersister implements AccountGroupPersistence {

    private final Logger logger = LoggerFactory.getLogger(AccountGroupPersister.class);

    public AccountGroupPersister() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#delete(org.renci.sp.dao.domain.Account)
     */
    public void delete(AccountGroup accountGroup) {
        logger.debug("ENTERING delete(AccountGroup)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(accountGroup);
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
    public AccountGroup findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        AccountGroup ret = null;
        try {
            transaction.begin();
            Query query = entityManager.createQuery("select a from AccountGroup a where a.id = :id");
            query.setParameter("id", id);
            ret = (AccountGroup) query.getSingleResult();
        } finally {
            transaction.commit();
            entityManager.close();
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#save(org.renci.sp.dao.domain.Account)
     */
    public void save(AccountGroup accountGroup) {
        logger.debug("ENTERING save(AccountGroup)");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(accountGroup);
        } finally {
            transaction.commit();
            entityManager.close();
        }
    }

}

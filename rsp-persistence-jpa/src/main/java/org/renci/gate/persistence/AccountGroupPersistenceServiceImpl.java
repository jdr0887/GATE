package org.renci.gate.persistence;

import javax.persistence.Query;

import org.renci.gate.api.persistence.AccountGroup;
import org.renci.gate.api.persistence.AccountGroupPersistenceService;
import org.renci.gate.persistence.entity.AccountGroupImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class AccountGroupPersistenceServiceImpl extends AbstractPersistenceService implements
        AccountGroupPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(AccountGroupPersistenceService.class);

    public AccountGroupPersistenceServiceImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#delete(org.renci.sp.dao.domain.Account)
     */
    public void delete(AccountGroup accountGroup) {
        logger.debug("ENTERING delete(AccountGroup)");
        getEntityManager().remove(accountGroup);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#findById(java.lang.Long)
     */
    public AccountGroupImpl findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        AccountGroupImpl ret = null;
        Query query = getEntityManager().createQuery("select a from AccountGroup a where a.id = :id");
        query.setParameter("id", id);
        ret = (AccountGroupImpl) query.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.sp.dao.AccountService#save(org.renci.sp.dao.domain.Account)
     */
    public void save(AccountGroup accountGroup) {
        logger.debug("ENTERING save(AccountGroup)");
        getEntityManager().persist(accountGroup);
    }

}

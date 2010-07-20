package org.renci.gate.persistence;

import javax.persistence.Query;

import org.renci.gate.api.persistence.Input;
import org.renci.gate.api.persistence.InputPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class InputPersistenceServiceImpl extends AbstractPersistenceService implements InputPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(InputPersistenceServiceImpl.class);

    public InputPersistenceServiceImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findById(java.lang.Long)
     */
    public Input findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Input ret = null;
        Query query = getEntityManager().createQuery("select a from Input a where a.id = :id");
        query.setParameter("id", id);
        ret = (Input) query.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain.JobHistory)
     */
    public void save(Input input) {
        logger.debug("ENTERING save(Input)");
        getEntityManager().persist(input);
    }

}

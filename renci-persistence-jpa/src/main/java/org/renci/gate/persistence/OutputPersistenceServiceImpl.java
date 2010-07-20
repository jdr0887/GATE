package org.renci.gate.persistence;

import javax.persistence.Query;

import org.renci.gate.api.persistence.Output;
import org.renci.gate.api.persistence.OutputPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class OutputPersistenceServiceImpl extends AbstractPersistenceService implements OutputPersistenceService {

    private final Logger logger = LoggerFactory.getLogger(OutputPersistenceServiceImpl.class);

    public OutputPersistenceServiceImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#findById(java.lang.Long)
     */
    public Output findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Output ret = null;
            Query query = getEntityManager().createQuery("select a from Output a where a.id = :id");
            query.setParameter("id", id);
            ret = (Output) query.getSingleResult();
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.renci.portal.dao.JobHistoryService#save(org.renci.portal.dao.domain.JobHistory)
     */
    public void save(Output output) {
        logger.debug("ENTERING save(Output)");
        getEntityManager().persist(output);
    }

}

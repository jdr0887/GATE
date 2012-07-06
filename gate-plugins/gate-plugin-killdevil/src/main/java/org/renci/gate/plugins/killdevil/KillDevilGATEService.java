package org.renci.gate.plugins.killdevil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.renci.gate.GATEService;
import org.renci.gate.GlideinMetric;
import org.renci.jlrm.JLRMException;
import org.renci.jlrm.Queue;
import org.renci.jlrm.Site;
import org.renci.jlrm.lsf.LSFJobStatusInfo;
import org.renci.jlrm.lsf.ssh.LSFSSHFactory;
import org.renci.jlrm.lsf.ssh.LSFSSHJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class KillDevilGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(KillDevilGATEService.class);

    private final List<LSFSSHJob> jobCache = new ArrayList<LSFSSHJob>();

    private Site site;

    private String collectorHost;

    public KillDevilGATEService() {
        super();
    }

    @Override
    public Map<String, GlideinMetric> lookupMetrics() {
        Map<String, GlideinMetric> metricsMap = new HashMap<String, GlideinMetric>();
        LSFSSHFactory lsfSSHFactory = LSFSSHFactory.getInstance(this.site, System.getProperty("user.name"));

        try {

            Set<LSFJobStatusInfo> jobStatusSet = lsfSSHFactory.lookupStatus(jobCache.toArray(new LSFSSHJob[jobCache
                    .size()]));

            // get unique list of queues
            Set<String> queueSet = new HashSet<String>();
            if (jobStatusSet != null) {
                for (LSFJobStatusInfo info : jobStatusSet) {
                    queueSet.add(info.getQueue());
                }
            }

            Iterator<LSFSSHJob> jobCacheIter = jobCache.iterator();
            while (jobCacheIter.hasNext()) {
                LSFSSHJob job = jobCacheIter.next();
                for (String queue : queueSet) {
                    int running = 0;
                    int pending = 0;
                    for (LSFJobStatusInfo info : jobStatusSet) {
                        GlideinMetric metrics = new GlideinMetric();
                        if (info.getQueue().equals(queue) && job.getId().equals(info.getJobId())) {
                            switch (info.getType()) {
                                case PENDING:
                                    ++pending;
                                    break;
                                case RUNNING:
                                    ++running;
                                    break;
                                case EXIT:
                                case UNKNOWN:
                                case ZOMBIE:
                                case DONE:
                                    jobCacheIter.remove();
                                    break;
                                case SUSPENDED_BY_SYSTEM:
                                case SUSPENDED_BY_USER:
                                case SUSPENDED_FROM_PENDING:
                                default:
                                    break;
                            }
                        }
                        metrics.setQueue(queue);
                        metrics.setPending(pending);
                        metrics.setRunning(running);
                        metricsMap.put(queue, metrics);
                    }
                }
            }

        } catch (JLRMException e) {
            e.printStackTrace();
            logger.error("Error:", e);
        }
        return metricsMap;
    }

    @Override
    public void createGlidein(Queue queue) {
        logger.info("ENTERING createGlidein(Queue)");
        File submitDir = new File("/tmp", System.getProperty("user.name"));
        submitDir.mkdirs();
        LSFSSHJob job = null;
        try {
            logger.info("siteInfo: {}", this.site);
            logger.info("queueInfo: {}", queue);
            LSFSSHFactory lsfSSHFactory = LSFSSHFactory.getInstance(this.site, System.getProperty("user.name"));
            job = lsfSSHFactory.submitGlidein(submitDir, this.getCollectorHost(), queue, 40);
            if (job != null && StringUtils.isNotEmpty(job.getId())) {
                logger.info("job.getId(): {}", job.getId());
                jobCache.add(job);
            }
        } catch (JLRMException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteGlidein(Queue queue) {
        logger.info("ENTERING deleteGlidein(QueueInfo)");
        if (jobCache.size() > 0) {
            try {
                logger.info("siteInfo: {}", this.site);
                logger.info("queueInfo: {}", queue);
                LSFSSHFactory lsfSSHFactory = LSFSSHFactory.getInstance(this.site, System.getProperty("user.name"));
                LSFSSHJob job = jobCache.get(0);
                lsfSSHFactory.killGlidein(job);
                jobCache.remove(0);
            } catch (JLRMException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCollectorHost() {
        return collectorHost;
    }

    public void setCollectorHost(String collectorHost) {
        this.collectorHost = collectorHost;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

}

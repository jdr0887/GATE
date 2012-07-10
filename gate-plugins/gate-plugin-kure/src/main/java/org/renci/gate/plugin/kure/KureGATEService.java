package org.renci.gate.plugin.kure;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
public class KureGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(KureGATEService.class);

    private final List<LSFSSHJob> jobCache = new ArrayList<LSFSSHJob>();

    private Site site;

    private String collectorHost;

    private Map<String, Object> properties;

    public KureGATEService() {
        super();
    }

    @Override
    public Map<String, GlideinMetric> lookupMetrics() {
        logger.debug("ENTERING lookupMetrics()");
        Map<String, GlideinMetric> metricsMap = new HashMap<String, GlideinMetric>();
        LSFSSHFactory lsfSSHFactory = LSFSSHFactory.getInstance(this.site, System.getProperty("user.name"));

        try {

            Set<LSFJobStatusInfo> jobStatusSet = lsfSSHFactory.lookupStatus(jobCache.toArray(new LSFSSHJob[jobCache
                    .size()]));

            logger.info("jobStatusSet.size(): {}", jobStatusSet.size());

            // get unique list of queues
            Set<String> queueSet = new HashSet<String>();
            if (jobStatusSet != null && jobStatusSet.size() > 0) {
                for (LSFJobStatusInfo info : jobStatusSet) {
                    logger.info("info: {}", info.toString());
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
                        logger.info("metrics: {}", metrics.toString());
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

        if (properties != null && properties.containsKey("active_queues")) {
            String[] activeQueues = StringUtils.split(properties.get("active_queues").toString(), ',');
            List<String> activeQueueList = Arrays.asList(activeQueues);
            if (!activeQueueList.contains(queue.getName())) {
                logger.warn("queue name is not in active queue list...see etc/org.renci.gate.plugin.kure.cfg");
                return;
            }
        }

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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}

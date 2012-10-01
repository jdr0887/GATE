package org.renci.gate.plugin.lineberger;

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
import org.renci.jlrm.sge.SGEJobStatusInfo;
import org.renci.jlrm.sge.ssh.SGESSHFactory;
import org.renci.jlrm.sge.ssh.SGESSHJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jdr0887
 */
public class LinebergerGATEService implements GATEService {

    private final Logger logger = LoggerFactory.getLogger(LinebergerGATEService.class);

    private final List<SGESSHJob> jobCache = new ArrayList<SGESSHJob>();

    private Site site;

    private String collectorHost;

    private String activeQueues;

    public LinebergerGATEService() {
        super();
    }

    @Override
    public Map<String, GlideinMetric> lookupMetrics() {
        Map<String, GlideinMetric> metricsMap = new HashMap<String, GlideinMetric>();
        SGESSHFactory lsfSSHFactory = SGESSHFactory.getInstance(site, System.getProperty("user.name"));

        try {

            Set<SGEJobStatusInfo> jobStatusSet = lsfSSHFactory.lookupStatus(jobCache.toArray(new SGESSHJob[jobCache
                    .size()]));

            // get unique list of queues
            Set<String> queueSet = new HashSet<String>();
            if (jobStatusSet != null) {
                for (SGEJobStatusInfo info : jobStatusSet) {
                    queueSet.add(info.getQueue());
                }
            }

            Iterator<SGESSHJob> jobCacheIter = jobCache.iterator();
            while (jobCacheIter.hasNext()) {
                SGESSHJob job = jobCacheIter.next();
                for (String queue : queueSet) {
                    int running = 0;
                    int pending = 0;
                    for (SGEJobStatusInfo info : jobStatusSet) {
                        GlideinMetric metrics = new GlideinMetric();
                        if (info.getQueue().equals(queue) && job.getId().equals(info.getJobId())) {
                            switch (info.getType()) {
                                case WAITING:
                                    ++pending;
                                    break;
                                case RUNNING:
                                    ++running;
                                    break;
                                case DELETION:
                                case ERROR:
                                case DONE:
                                case THRESHOLD:
                                    jobCacheIter.remove();
                                    break;
                                case SUSPENDED:
                                case HOLD:
                                case RESTARTED:
                                case TRANSFERING:
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
            logger.error("Error:", e);
        }
        return metricsMap;
    }

    @Override
    public void createGlidein(Queue queue) {
        logger.info("ENTERING createGlidein(Queue)");

        if (StringUtils.isNotEmpty(activeQueues) && !activeQueues.contains(queue.getName())) {
            logger.warn("queue name is not in active queue list...see etc/org.renci.gate.plugin.kure.cfg");
            return;
        }

        File submitDir = new File("/tmp", System.getProperty("user.name"));
        submitDir.mkdirs();
        SGESSHJob job = null;
        try {
            SGESSHFactory lsfSSHFactory = SGESSHFactory.getInstance(this.site, System.getProperty("user.name"));
            job = lsfSSHFactory.submitGlidein(submitDir, this.collectorHost, queue, 40);
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
        if (jobCache.size() > 0) {
            try {
                SGESSHFactory lsfSSHFactory = SGESSHFactory.getInstance(this.site, System.getProperty("user.name"));
                SGESSHJob job = jobCache.get(0);
                lsfSSHFactory.killGlidein(job);
                jobCache.remove(0);
            } catch (JLRMException e) {
                e.printStackTrace();
            }
        }
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getCollectorHost() {
        return collectorHost;
    }

    public void setCollectorHost(String collectorHost) {
        this.collectorHost = collectorHost;
    }

    public String getActiveQueues() {
        return activeQueues;
    }

    public void setActiveQueues(String activeQueues) {
        this.activeQueues = activeQueues;
    }

}

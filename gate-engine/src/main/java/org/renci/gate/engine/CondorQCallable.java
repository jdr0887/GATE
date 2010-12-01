package org.renci.gate.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;
import org.renci.common.exec.Input;
import org.renci.common.exec.Output;
import org.renci.gate.ClassAd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Watches what is happening in the system by running condor_q
 */
public class CondorQCallable implements Callable<ArrayList<PortalUserJobInfo>> {

    private final Logger logger = LoggerFactory.getLogger(AdvertiseTask.class);

    private final Executor exec = Executor.getInstance();

    public CondorQCallable() {
        super();
    }

    /**
     * Runs condor_q and parses output
     */
    public ArrayList<PortalUserJobInfo> call() {

        // The hashtable indexed by the portal username that tracks user jobs' info
        Hashtable<String, PortalUserJobInfo> htPortalUserInfo = new Hashtable<String, PortalUserJobInfo>();

        int totalPendingGlidein = 0;
        int totalActiveGlidein = 0;
        int totalIdleJobs = 0;
        int totalActiveJobs = 0;

        // run condor_q
        logger.info("Invoking condor_q");

        StringBuilder sb = new StringBuilder("(condor_q -global");
        sb.append(" -format '\\nGlobalJobId=%s' GlobalJobId");
        sb.append(" -format ',PortalUser=%s' PortalUser");
        sb.append(" -format ',JobStatus=%s' JobStatus");
        sb.append(" -format ',IsPilotGlideIn=%s' IsPilotGlideIn");
        sb.append(" -format ',Requirements=%s' Requirements");
        sb.append(" -format ',MyNeeds=%s' MyNeeds");
        sb.append(" -format ',PortalUserAuthTimestamp=%s' PortalUserAuthTimestamp");
        sb.append(" -format ',PortalUserIP=%s' PortalUserIP");
        sb.append(" -format ',PortalUserEmail=%s' PortalUserEmail;");
        sb.append(" echo)");
        Input shellInput = new Input();
        shellInput.setCommand(sb.toString());
        logger.info(sb.toString());
        try {
            Output shellOutput = exec.run(shellInput);

            if (shellOutput.getStderr() != null && shellOutput.getStderr().length() > 2) {
                logger.error("Shell error: " + shellOutput.getStderr());
                return null;
            }
            
            String[] lines = shellOutput.getStdout().toString().split("\n");
            
            for (String currentLine : lines) {
                ClassAd ad = new ClassAd();
                
                ad.parseLine(currentLine);

                // we have an add, process it
                String jobId = ad.getReallyTrimmed("GlobalJobId");
                if (StringUtils.isEmpty(jobId)) {
                    continue;
                }

                String portalUser = ad.getReallyTrimmed("PortalUser");
                if (StringUtils.isEmpty(portalUser)) {
                    continue;
                }

                int jobCount = ad.getAsInt("GLIDEIN_COUNT");
                if (jobCount == 0) {
                    jobCount = 1;
                }

                // create/load userinfo object for the given user
                PortalUserJobInfo jobInfo = null;
                if (!htPortalUserInfo.containsKey(portalUser)) { // new portal user
                    jobInfo = new PortalUserJobInfo();
                    htPortalUserInfo.put(portalUser, jobInfo);
                    jobInfo.setPortalUserName(portalUser);
                }
                jobInfo = htPortalUserInfo.get(portalUser);

                int jobStatus = ad.getAsInt("JobStatus");
                if (jobStatus == 3 || jobStatus == 4 || jobStatus == 5) {
                    continue;
                }

                int isPilotGlideIn = ad.getAsInt("IsPilotGlideIn");

                // if we get to this point...log some details before doing anything
                logger.debug("jobId = " + jobId);
                logger.debug("jobStatus = " + jobStatus);
                logger.debug("portalUser = " + portalUser);
                logger.debug("isPilotGlideIn = " + isPilotGlideIn);

                if (1 == jobStatus && 1 == isPilotGlideIn) { // idle and isPilotGlidein
                    totalPendingGlidein++;
                    jobInfo.setNumPendingGlidein(jobInfo.getNumPendingGlidein() + jobCount);
                } else if (2 == jobStatus && 1 == isPilotGlideIn) { // running and isPilotGlidein
                    totalActiveGlidein++;
                    jobInfo.setNumActiveGlidein(jobInfo.getNumActiveGlidein() + jobCount);
                } else if (2 == jobStatus && 0 == isPilotGlideIn) { // running and !(isPilotGlidein)
                    totalActiveJobs++;
                    jobInfo.setNumActiveJobs(jobInfo.getNumActiveJobs() + 1);
                } else if (1 == jobStatus && 0 == isPilotGlideIn) { // idle and !(isPilotGlidein)
                    totalIdleJobs++;
                    jobInfo.setNumIdleJobs(jobInfo.getNumIdleJobs() + 1);

                    // Special case: we want to keep one of the Requirements and MyNeeds from a random job
                    // we will give them all to jobInfo, and let it pick at random
                    jobInfo.addRequirements(ad.getReallyTrimmed("Requirements"));
                    jobInfo.addMyNeeds(ad.getReallyTrimmed("MyNeeds"));

                    // also keep information from portal
                    String portalUserAuthTimestampStr = ad.getReallyTrimmed("PortalUserAuthTimestamp");
                    if (portalUserAuthTimestampStr == null || "".equals(portalUserAuthTimestampStr)) {
                        logger.error("PortalUserAuthTimestamp not set for job " + jobId);
                        portalUserAuthTimestampStr = "" + System.currentTimeMillis();
                    }
                    Date portalUserAuthTimestamp = new Date(Long.parseLong(portalUserAuthTimestampStr));
                    String portalUserIP = ad.getReallyTrimmed("PortalUserIP");
                    if (portalUserIP == null || "".equals(portalUserIP)) {
                        logger.error("PortalUserIP not set for job " + jobId);
                        portalUserIP = "0.0.0.0";
                    }
                    String portalUserEmail = ad.getReallyTrimmed("PortalUserEmail");
                    if (portalUserEmail == null || "".equals(portalUserEmail)) {
                        logger.error("PortalUserEmail not set for job " + jobId);
                        portalUserIP = "noemail@portal.renci.org";
                    }
                    jobInfo.setAuthInfo(portalUserAuthTimestamp, portalUserIP, portalUserEmail);

                }
            }

        } catch (ExecutorException e) {
            e.printStackTrace();
        }
        logger.info("CondorQ loop done");

        // convert to a sorted list based on number of running glideins
        ArrayList<PortalUserJobInfo> portalUserJobInfoList = new ArrayList(htPortalUserInfo.values());
        Collections.sort(portalUserJobInfoList);
        return portalUserJobInfoList;
    }
}

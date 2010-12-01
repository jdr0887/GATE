package org.renci.gate.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class PortalUserJobInfo implements Comparable {

    private String portalUserName = "";

    // number of active glideins for the portal user
    private int numActiveGlidein;

    // number of pending glideins for portal user
    private int numPendingGlidein;

    // number of active jobs for portal user
    private int numActiveJobs;

    // number of idle jobs for portal user
    private int numIdleJobs;

    // number of alloted glideins for portal user
    private int allotedGlidein;

    // list of resource requirements
    private ArrayList<String> requirements = new ArrayList<String>();

    // index of the currently selected random requirement
    private int randomRequirementIndex = -1;

    // list of needs
    private ArrayList<String> needs = new ArrayList<String>();

    // index of the currently selected random myneeds
    private int randomNeedIndex = -1;

    // when the authentication happened
    private Date authTimestamp = null;

    // ip the user came from
    private String authIP = "";

    // user email
    private String userEmail = "";

    /**
	 * 
	 */
    public PortalUserJobInfo() {
        this.numActiveGlidein = 0;
        this.numPendingGlidein = 0;
        this.numActiveJobs = 0;
        this.numIdleJobs = 0;
        this.allotedGlidein = 0;
    }

    /**
     * @param numActiveGlidein
     * @param numPendingGlidein
     * @param numActiveJobs
     * @param numIdleJobs
     * @param allotedGlidein
     */
    public PortalUserJobInfo(int numActiveGlidein, int numPendingGlidein, int numActiveJobs, int numIdleJobs,
            int allotedGlidein) {
        super();
        this.numActiveGlidein = numActiveGlidein;
        this.numPendingGlidein = numPendingGlidein;
        this.numActiveJobs = numActiveJobs;
        this.numIdleJobs = numIdleJobs;
        this.allotedGlidein = allotedGlidein;
    }

    public String getPortalUserName() {
        return portalUserName;
    }

    public void setPortalUserName(String portalUserName) {
        this.portalUserName = portalUserName;
    }

    /**
     * @return the numActiveGlidein
     */
    public int getNumActiveGlidein() {
        return numActiveGlidein;
    }

    /**
     * @param numActiveGlidein
     *            the numActiveGlidein to set
     */
    public void setNumActiveGlidein(int numActiveGlidein) {
        this.numActiveGlidein = numActiveGlidein;
    }

    /**
     * @return the numPendingGlidein
     */
    public int getNumPendingGlidein() {
        return numPendingGlidein;
    }

    /**
     * @param numPendingGlidein
     *            the numPendingGlidein to set
     */
    public void setNumPendingGlidein(int numPendingGlidein) {
        this.numPendingGlidein = numPendingGlidein;
    }

    /**
     * @return the numActiveJobs
     */
    public int getNumActiveJobs() {
        return numActiveJobs;
    }

    /**
     * @param numActiveJobs
     *            the numActiveJobs to set
     */
    public void setNumActiveJobs(int numActiveJobs) {
        this.numActiveJobs = numActiveJobs;
    }

    /**
     * @return the numIdleJobs
     */
    public int getNumIdleJobs() {
        return numIdleJobs;
    }

    /**
     * @param numIdleJobs
     *            the numIdleJobs to set
     */
    public void setNumIdleJobs(int numIdleJobs) {
        this.numIdleJobs = numIdleJobs;
    }

    /**
     * @return the allotedGlidein
     */
    public int getAllotedGlidein() {
        return allotedGlidein;
    }

    /**
     * @param allotedGlidein
     *            the allotedGlidein to set
     */
    public void setAllotedGlidein(int allotedGlidein) {
        this.allotedGlidein = allotedGlidein;
    }

    /**
     * Adds a my needs requirement to the list of needs for this user
     * 
     * @param myNeeds
     */
    public void addRequirements(String req) {
        if (req != null) {
            requirements.add(req);

            // pick a need at random
            Random rand = new Random();
            randomRequirementIndex = rand.nextInt(requirements.size());
        }
    }

    /**
     * Adds a my needs requirement to the list of needs for this user
     * 
     * @param myNeeds
     */
    public void addMyNeeds(String myNeeds) {
        if (myNeeds != null) {
            needs.add(myNeeds);

            // pick a need at random
            Random rand = new Random();
            randomNeedIndex = rand.nextInt(needs.size());
        }
    }

    /**
     * gives a randomly selected requirements string
     * 
     * @return
     */
    public String getRequirements() {
        if (requirements.size() == 0) {
            return "";
        }
        return (String) requirements.get(randomRequirementIndex);
    }

    /**
     * gives a randomly selected needs string
     * 
     * @return
     */
    public String getNeeds() {
        if (needs.size() == 0) {
            return "";
        }
        return (String) needs.get(randomNeedIndex);
    }

    public void setAuthInfo(Date authTimestamp, String authIP, String userEmail) {
        this.authTimestamp = authTimestamp;
        this.authIP = authIP;
        this.userEmail = userEmail;
    }

    public Date getAuthTimestamp() {
        return authTimestamp;
    }

    public String getAuthIP() {
        return authIP;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int compareTo(Object o) {

        if (!(o instanceof PortalUserJobInfo))
            throw new ClassCastException("A PortalUserJobInfo object expected.");

        PortalUserJobInfo other = (PortalUserJobInfo) o;

        return (this.getNumActiveGlidein() - other.getNumActiveGlidein());
    }

}

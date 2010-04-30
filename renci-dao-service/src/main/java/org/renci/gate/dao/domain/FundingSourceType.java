package org.renci.gate.dao.domain;

/**
 * 
 * @author jdr0887
 */
public enum FundingSourceType {

    NSF(1, "National Science Foundation"), DOE(2, "Department of Energy"), NIH(3, "National Institutes of Health"), NOAA(
            4, "National Oceanic and Atmospheric Administration"), OTHER_FEDERAL_AGENCIES(5, "Other Federal Agencies"), PRIVATE_FOUNDATION(
            6, "Private Foundation"), OTHER(7, "Other");

    private int id;

    private String name;

    private FundingSourceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}

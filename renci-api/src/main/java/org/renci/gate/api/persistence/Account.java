package org.renci.gate.api.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public interface Account extends Serializable {

    /**
     * @return the accountRole
     */
    public abstract AccountRoleType getAccountRole();

    /**
     * @param accountRole
     *            the accountRole to set
     */
    public abstract void setAccountRole(AccountRoleType accountRole);

    /**
     * @return the active
     */
    public abstract Boolean getActive();

    /**
     * @param active
     *            the active to set
     */
    public abstract void setActive(Boolean active);

    /**
     * @return the awardNumbers
     */
    public abstract String getAwardNumbers();

    /**
     * @param awardNumbers
     *            the awardNumbers to set
     */
    public abstract void setAwardNumbers(String awardNumbers);

    /**
     * @return the dateApproved
     */
    public abstract Date getDateApproved();

    /**
     * @param dateApproved
     *            the dateApproved to set
     */
    public abstract void setDateApproved(Date dateApproved);

    /**
     * @return the datePasswordChanged
     */
    public abstract Date getDatePasswordChanged();

    /**
     * @param datePasswordChanged
     *            the datePasswordChanged to set
     */
    public abstract void setDatePasswordChanged(Date datePasswordChanged);

    /**
     * @return the dateRequested
     */
    public abstract Date getDateRequested();

    /**
     * @param dateRequested
     *            the dateRequested to set
     */
    public abstract void setDateRequested(Date dateRequested);

    /**
     * @return the dateVerified
     */
    public abstract Date getDateVerified();

    /**
     * @param dateVerified
     *            the dateVerified to set
     */
    public abstract void setDateVerified(Date dateVerified);

    /**
     * @return the email
     */
    public abstract String getEmail();

    /**
     * @param email
     *            the email to set
     */
    public abstract void setEmail(String email);

    /**
     * @return the fullName
     */
    public abstract String getFullName();

    /**
     * @param fullName
     *            the fullName to set
     */
    public abstract void setFullName(String fullName);

    /**
     * @return the fundingSource
     */
    public abstract FundingSourceType getFundingSource();

    /**
     * @param fundingSource
     *            the fundingSource to set
     */
    public abstract void setFundingSource(FundingSourceType fundingSource);

    /**
     * @return the fundingSourceOther
     */
    public abstract String getFundingSourceOther();

    /**
     * @param fundingSourceOther
     *            the fundingSourceOther to set
     */
    public abstract void setFundingSourceOther(String fundingSourceOther);

    /**
     * @return the id
     */
    public abstract Long getId();

    /**
     * @param id
     *            the id to set
     */
    public abstract void setId(Long id);

    /**
     * @return the md5Hash
     */
    public abstract String getMd5Hash();

    /**
     * @param md5Hash
     *            the md5Hash to set
     */
    public abstract void setMd5Hash(String md5Hash);

    /**
     * @return the organization
     */
    public abstract String getOrganization();

    /**
     * @param organization
     *            the organization to set
     */
    public abstract void setOrganization(String organization);

    /**
     * @return the passwordResetHash
     */
    public abstract String getPasswordResetHash();

    /**
     * @param passwordResetHash
     *            the passwordResetHash to set
     */
    public abstract void setPasswordResetHash(String passwordResetHash);

    /**
     * @return the phoneNumber
     */
    public abstract String getPhoneNumber();

    /**
     * @param phoneNumber
     *            the phoneNumber to set
     */
    public abstract void setPhoneNumber(String phoneNumber);

    /**
     * @return the purposeOfUse
     */
    public abstract String getPurposeOfUse();

    /**
     * @param purposeOfUse
     *            the purposeOfUse to set
     */
    public abstract void setPurposeOfUse(String purposeOfUse);

    /**
     * @return the requestIPAddress
     */
    public abstract String getRequestIPAddress();

    /**
     * @param requestIPAddress
     *            the requestIPAddress to set
     */
    public abstract void setRequestIPAddress(String requestIPAddress);

    /**
     * @return the username
     */
    public abstract String getUsername();

    /**
     * @param username
     *            the username to set
     */
    public abstract void setUsername(String username);

    /**
     * @return the verificationHash
     */
    public abstract String getVerificationHash();

    /**
     * @param verificationHash
     *            the verificationHash to set
     */
    public abstract void setVerificationHash(String verificationHash);

    /**
     * @return the accountGroup
     */
    public abstract AccountGroup getAccountGroup();

    /**
     * @param accountGroup
     *            the accountGroup to set
     */
    public abstract void setAccountGroup(AccountGroup accountGroup);

    /**
     * @return the sites
     */
    public abstract Set<Site> getSites();

    /**
     * @param sites
     *            the sites to set
     */
    public abstract void setSites(Set<Site> sites);

    /**
     * @return the wsAccessKey
     */
    public abstract String getWsAccessKey();

    /**
     * @param wsAccessKey
     *            the wsAccessKey to set
     */
    public abstract void setWsAccessKey(String wsAccessKey);

}
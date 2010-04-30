package org.renci.gate.dao.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author jdr0887
 */
@Entity
@Table(name = "account")
@SequenceGenerator(name = "account_seq", sequenceName = "account_seq")
public class Account implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "account_role")
    @Enumerated(EnumType.STRING)
    private AccountRoleType accountRole;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "award_numbers")
    private String awardNumbers;

    @Column(name = "date_approved")
    private Date dateApproved;

    @Column(name = "date_password_changed")
    private Date datePasswordChanged;

    @Column(name = "date_requested")
    private Date dateRequested;

    @Column(name = "date_verified")
    private Date dateVerified;

    @Column(name = "email")
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "funding_source", nullable = true)
    @Enumerated(EnumType.STRING)
    private FundingSourceType fundingSource;

    @Column(name = "funding_source_other")
    private String fundingSourceOther;

    @Column(name = "md5_hash")
    private String md5Hash;

    @Column(name = "organization")
    private String organization;

    @Column(name = "password_reset_hash")
    private String passwordResetHash;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "purpose_of_use")
    private String purposeOfUse;

    @Column(name = "request_ip_address")
    private String requestIPAddress;

    @Column(name = "username")
    private String username;

    @Column(name = "ws_access_key", nullable = false, unique = true)
    private String wsAccessKey;

    @Column(name = "verification_hash")
    private String verificationHash;

    @ManyToOne
    @JoinColumn(name = "account_group_fid")
    private AccountGroup accountGroup;

    @ManyToMany(targetEntity = org.renci.gate.dao.domain.Site.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(name = "account_site", joinColumns = @JoinColumn(name = "account_fid"), inverseJoinColumns = @JoinColumn(name = "site_fid"))
    private Set<Site> sites;

    public Account() {
        super();
    }

    /**
     * @return the accountRole
     */
    public AccountRoleType getAccountRole() {
        return accountRole;
    }

    /**
     * @param accountRole
     *            the accountRole to set
     */
    public void setAccountRole(AccountRoleType accountRole) {
        this.accountRole = accountRole;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active
     *            the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return the awardNumbers
     */
    public String getAwardNumbers() {
        return awardNumbers;
    }

    /**
     * @param awardNumbers
     *            the awardNumbers to set
     */
    public void setAwardNumbers(String awardNumbers) {
        this.awardNumbers = awardNumbers;
    }

    /**
     * @return the dateApproved
     */
    public Date getDateApproved() {
        return dateApproved;
    }

    /**
     * @param dateApproved
     *            the dateApproved to set
     */
    public void setDateApproved(Date dateApproved) {
        this.dateApproved = dateApproved;
    }

    /**
     * @return the datePasswordChanged
     */
    public Date getDatePasswordChanged() {
        return datePasswordChanged;
    }

    /**
     * @param datePasswordChanged
     *            the datePasswordChanged to set
     */
    public void setDatePasswordChanged(Date datePasswordChanged) {
        this.datePasswordChanged = datePasswordChanged;
    }

    /**
     * @return the dateRequested
     */
    public Date getDateRequested() {
        return dateRequested;
    }

    /**
     * @param dateRequested
     *            the dateRequested to set
     */
    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    /**
     * @return the dateVerified
     */
    public Date getDateVerified() {
        return dateVerified;
    }

    /**
     * @param dateVerified
     *            the dateVerified to set
     */
    public void setDateVerified(Date dateVerified) {
        this.dateVerified = dateVerified;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName
     *            the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the fundingSource
     */
    public FundingSourceType getFundingSource() {
        return fundingSource;
    }

    /**
     * @param fundingSource
     *            the fundingSource to set
     */
    public void setFundingSource(FundingSourceType fundingSource) {
        this.fundingSource = fundingSource;
    }

    /**
     * @return the fundingSourceOther
     */
    public String getFundingSourceOther() {
        return fundingSourceOther;
    }

    /**
     * @param fundingSourceOther
     *            the fundingSourceOther to set
     */
    public void setFundingSourceOther(String fundingSourceOther) {
        this.fundingSourceOther = fundingSourceOther;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the md5Hash
     */
    public String getMd5Hash() {
        return md5Hash;
    }

    /**
     * @param md5Hash
     *            the md5Hash to set
     */
    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    /**
     * @return the organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization
     *            the organization to set
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * @return the passwordResetHash
     */
    public String getPasswordResetHash() {
        return passwordResetHash;
    }

    /**
     * @param passwordResetHash
     *            the passwordResetHash to set
     */
    public void setPasswordResetHash(String passwordResetHash) {
        this.passwordResetHash = passwordResetHash;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber
     *            the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the purposeOfUse
     */
    public String getPurposeOfUse() {
        return purposeOfUse;
    }

    /**
     * @param purposeOfUse
     *            the purposeOfUse to set
     */
    public void setPurposeOfUse(String purposeOfUse) {
        this.purposeOfUse = purposeOfUse;
    }

    /**
     * @return the requestIPAddress
     */
    public String getRequestIPAddress() {
        return requestIPAddress;
    }

    /**
     * @param requestIPAddress
     *            the requestIPAddress to set
     */
    public void setRequestIPAddress(String requestIPAddress) {
        this.requestIPAddress = requestIPAddress;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the verificationHash
     */
    public String getVerificationHash() {
        return verificationHash;
    }

    /**
     * @param verificationHash
     *            the verificationHash to set
     */
    public void setVerificationHash(String verificationHash) {
        this.verificationHash = verificationHash;
    }

    /**
     * @return the accountGroup
     */
    public AccountGroup getAccountGroup() {
        return accountGroup;
    }

    /**
     * @param accountGroup
     *            the accountGroup to set
     */
    public void setAccountGroup(AccountGroup accountGroup) {
        this.accountGroup = accountGroup;
    }

    /**
     * @return the sites
     */
    public Set<Site> getSites() {
        return sites;
    }

    /**
     * @param sites
     *            the sites to set
     */
    public void setSites(Set<Site> sites) {
        this.sites = sites;
    }

    /**
     * @return the wsAccessKey
     */
    public String getWsAccessKey() {
        return wsAccessKey;
    }

    /**
     * @param wsAccessKey
     *            the wsAccessKey to set
     */
    public void setWsAccessKey(String wsAccessKey) {
        this.wsAccessKey = wsAccessKey;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Account.class.getName()).append(":\n");
        sb.append("username = ").append(this.username).append("\n");
        sb.append("fullName = ").append(this.fullName).append("\n");
        sb.append("email = ").append(this.email);
        return sb.toString();
    }

}

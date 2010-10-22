package org.renci.gate.api.persistence;

import java.io.Serializable;
import java.util.Set;

public interface AccountGroup extends Serializable {

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
     * @return the name
     */
    public abstract String getName();

    /**
     * @param name
     *            the name to set
     */
    public abstract void setName(String name);

    /**
     * @return the accounts
     */
    public abstract Set<Account> getAccounts();

    /**
     * @param accounts
     *            the accounts to set
     */
    public abstract void setAccounts(Set<Account> accounts);

}
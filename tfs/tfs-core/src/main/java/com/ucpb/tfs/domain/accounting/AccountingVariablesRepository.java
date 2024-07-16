package com.ucpb.tfs.domain.accounting;


import java.util.List;

/**
 * User: JAVA_training
 * Date: 9/30/12
 * Time: 3:05 PM
 */
public interface AccountingVariablesRepository {

    public void merge(AccountingVariable accountingVariable);

    public void persist(AccountingVariable accountingVariable);

    public void update(AccountingVariable accountingVariable);

    public AccountingVariable get(long id);

    public void delete(long id);

    public List<AccountingVariable> list();

    public int clear();
}

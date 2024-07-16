package com.ucpb.tfs.domain.sysparams;

import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 1/21/13
 */
public interface RefCustomerRepository {

    public RefCustomer save(RefCustomer refCustomer);

    public void delete(RefCustomer refCustomer);

    public void merge(RefCustomer refCustomer);

    public void update(RefCustomer refCustomer);

    public Long checkIfCustomerExists(String centralBankCode);

    public RefCustomer getCustomer(Long customerId);

    public Map getCustomerById(Long customerId);

    public List<RefCustomer> getRequestsMatching(String centralBankCode,
                                                 String clientTaxAccountNumber,
                                                 String cifLongName,
                                                 String cifLongNameB);

    public List<RefCustomer> getAllCustomers();


}

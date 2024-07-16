package com.ucpb.tfs.domain.sysparams;

import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 1/21/13
 */
public interface RefBankRepository {

    public void persist(RefBank refBank);

    public void merge(RefBank refBank);

    public void update(RefBank refBank);

    public RefBank getBank(String bicCode, String branchCode);

    public List<RefBank> getBanks(String bicCode);

    public List<RefBank> getNullRmaBanks();
    
    public Map getBankByBicAndBranch(String bicCode, String branchCode);

    public List<RefBank> getRequestsMatching(String bic, String branchCode, String institutionName, String depositoryFlag);

    public String getGlCode(String type, String account);
}

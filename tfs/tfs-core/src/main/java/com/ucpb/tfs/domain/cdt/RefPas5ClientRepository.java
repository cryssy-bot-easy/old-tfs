package com.ucpb.tfs.domain.cdt;

import java.util.List;

public interface RefPas5ClientRepository {

    public void persist(RefPas5Client refPas5Client);

    public void merge(RefPas5Client refPas5Client);

    public void update(RefPas5Client refPas5Client);

    public List<RefPas5Client> getClientsMatching(String importerName);

//    public List<RefPas5Client> getClientsMatching(String importerName, String uploader);
    public List<RefPas5Client> getClientsMatching(String importerName, String aabRefCode, String importerTin, String customsClientNumber, String unitCode);

    public List<RefPas5Client> getClientsMatching(String agentBankCode, String clientName);
    
    public RefPas5Client load(String agentBankCode);

}

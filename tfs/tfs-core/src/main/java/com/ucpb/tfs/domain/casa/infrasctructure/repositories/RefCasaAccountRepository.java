package com.ucpb.tfs.domain.casa.infrasctructure.repositories;

import com.ucpb.tfs.domain.casa.RefCasaAccount;

import java.util.List;

/**
 * Created by Marv on 2/27/14.
 */
public interface RefCasaAccountRepository {

    public List<RefCasaAccount> findRefCasaAccountMatching(String cifNumber, String currency);

    public void persist(RefCasaAccount refCasaAccount);

    public void deleteAllRefCasaAccount();
}

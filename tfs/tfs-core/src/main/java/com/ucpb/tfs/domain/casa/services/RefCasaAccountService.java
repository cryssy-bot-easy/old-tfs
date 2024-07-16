package com.ucpb.tfs.domain.casa.services;

import com.ucpb.tfs.domain.casa.RefCasaAccount;

import java.io.File;
//import java.io.InputStream;
import java.util.List;

/**
 * Created by Marv on 2/27/14.
 */
public interface RefCasaAccountService {

    public List<RefCasaAccount> findByCifNumberAndCurrency(String cifNumber, String currency);

    public void save(RefCasaAccount refCasaAccount);

    public void testRegex();

    public void populateRefCasaAccount() throws Exception;

}

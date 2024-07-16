package com.ucpb.tfs.domain.sysparams;

import org.hibernate.Session;

import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 1/21/13
 */
public interface RefCountryRepository {

    public RefCountry save(RefCountry refCountry);

    public void merge(RefCountry refCountry);

    public void update(RefCountry refCountry);

    //public String checkIfCountryCodeExists(String countryCode);

    public RefCountry getCountry(String countryCode);

    public Map getCountryByCode(String countryCode);

    public Map getCountryByISO(String countryISO);


    public List<RefCountry> getRequestsMatching(String countryCode,
                                                 String countryName,
                                                 String countryISO);

    public List<RefCountry> getAllCountries();

    public Long checkIfCountryExists(String countryCode);


}

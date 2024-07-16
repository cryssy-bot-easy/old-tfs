package com.ucpb.tfs.domain.reference;

import java.util.List;

/**
 * User: angol
 * Date: 10/8/12
 * Time: 7:37 PM
 */
public interface ChargeDefaultsReferenceRepository {

    public List<ChargeDefaultsReference> getList();

    public ChargeDefaultsReference get(long id);

    public ChargeDefaultsReference get(String matcher);

    public void persist(ChargeDefaultsReference chargeDefaultsReference);

    public void merge(ChargeDefaultsReference chargeDefaultsReference);

    public void update(ChargeDefaultsReference chargeDefaultsReference);

    public int clear();

    public void delete(long id);

}

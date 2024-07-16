package com.ucpb.tfs.domain.reference;

import java.util.List;

/**
 * User: JAVA_training
 * Date: 10/8/12
 * Time: 7:37 PM
 */
public interface ChargeRepository {

    public List<Charge> getChargeIdList();

    public Charge load(ChargeId chargeId);

    public Charge getByName(String displayName);
}

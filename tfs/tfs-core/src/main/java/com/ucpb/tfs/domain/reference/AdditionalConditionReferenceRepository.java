package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.condition.ConditionCode;

import java.util.List;

/**
 * User: Marv
 * Date: 11/4/12
 */

public interface AdditionalConditionReferenceRepository {

    public void save(AdditionalConditionReference conditionReference);

    public AdditionalConditionReference load(ConditionCode conditionCode);

    public List<AdditionalConditionReference> getAllConditionReference();

    public void clear();

}

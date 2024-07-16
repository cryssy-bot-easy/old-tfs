package com.ucpb.tfs.domain.condition;

/**
 * User: Marv
 * Date: 11/4/12
 */

public interface AdditionalConditionRepository {

    public void persist(AdditionalCondition additionalCondition);

    public void merge(AdditionalCondition additionalCondition);

    public void update(AdditionalCondition additionalCondition);

    public AdditionalCondition load(ConditionCode conditionCode);

}

package com.ucpb.tfs.domain.condition;

import com.ucpb.tfs.domain.product.DocumentNumber;

import java.util.Set;

/**
 * User: Marv
 * Date: 11/4/12
 */

public interface LcAdditionalConditionRepository {

    public void persist(LcAdditionalCondition lcAdditionalCondition);

    public void merge(LcAdditionalCondition lcAdditionalCondition);

    public void update(LcAdditionalCondition lcAdditionalCondition);

    public LcAdditionalCondition load(ConditionCode conditionCode);

    public Set<LcAdditionalCondition> load(DocumentNumber documentNumber);

}

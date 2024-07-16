package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.List;

/**
 * User: angulo
 * Date: 4/8/13
 * Time: 4:50 PM
 */
public interface ChargeAccountingCodeRepository {

    public void save(ChargeAccountingCode chargeAccountingCode);

    public ChargeAccountingCode getChargeAccountingCode(ProductId productId, ServiceType serviceType, ChargeId chargeId);

    public List<String> getChargeAccountingCodeList();

    public Long getCount();

    public void clear();
}

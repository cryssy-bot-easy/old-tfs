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
public interface ProfitLossHolderRepository {

    public void save(ProfitLossHolder profitLossHolder);

    public void delete(String tradeServiceId);

    public void delete(String tradeServiceId, String paymentId);

}

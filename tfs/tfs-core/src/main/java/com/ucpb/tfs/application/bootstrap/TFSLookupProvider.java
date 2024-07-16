package com.ucpb.tfs.application.bootstrap;


import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.TradeServiceChargeReference;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.List;

public interface TFSLookupProvider {

    public List<TradeServiceChargeReference> getChargesForService(ProductId productId, ServiceType serviceType);

}

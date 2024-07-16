package com.ucpb.tfs.domain.mtmessage;

import com.ucpb.tfs.domain.service.TradeServiceReferenceNumber;

/**
 * User: Marv
 * Date: 10/10/12
 */

public interface MtMessageRepository {

    public void persist(MtMessage mtMessage);

    public void merge(MtMessage mtMessage);

    public void update(MtMessage mtMessage);

//    public MtMessage load(DocumentNumber documentNumber);
    public MtMessage load(Long id);

    public MtMessage load(TradeServiceReferenceNumber tradeServiceReferenceNumber);

}

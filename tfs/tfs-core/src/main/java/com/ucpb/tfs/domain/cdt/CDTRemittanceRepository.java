package com.ucpb.tfs.domain.cdt;

import com.ucpb.tfs.domain.cdt.enums.PaymentRequestType;

import java.util.Date;
import java.util.List;

public interface CDTRemittanceRepository {

    public void persist(CDTRemittance cdtRemittance);

    public void merge(CDTRemittance cdtRemittance);

    public void update(CDTRemittance cdtRemittance);

//    public List<CDTRemittance> getAllBy(String reportType,
//                                        Date remittanceDateFrom,
//                                        Date remittanceDateTo,
//                                        Date collectionFrom,
//                                        Date collectionTo);

    public List<CDTRemittance> getAllBy(List<PaymentRequestType> reportTypeList,
                                        Date remittanceDateFrom,
                                        Date remittanceDateTo,
                                        Date collectionFrom,
                                        Date collectionTo);

}

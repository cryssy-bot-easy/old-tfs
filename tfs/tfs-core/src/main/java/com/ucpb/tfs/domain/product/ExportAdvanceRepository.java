package com.ucpb.tfs.domain.product;


import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface ExportAdvanceRepository {

    public void persist(ExportAdvancePayment exportAdvancePayment);

    public void merge(ExportAdvancePayment exportAdvancePayment);

    public void update(ExportAdvancePayment exportAdvancePayment);

    public ExportAdvancePayment load(DocumentNumber documentNumber);

    public List<ExportAdvancePayment> getAllExportAdvancePayments(String cifName,
                                                                  DocumentNumber documentNumber,
                                                                  Currency currency,
                                                                  BigDecimal amountFrom,
                                                                  BigDecimal amountTo,
                                                                  String unitCode,
                                                                  String unitcode);
}

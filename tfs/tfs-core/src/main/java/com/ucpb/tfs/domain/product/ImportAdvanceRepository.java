package com.ucpb.tfs.domain.product;


import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface ImportAdvanceRepository {

    public void persist(ImportAdvancePayment importAdvancePayment);

    public void merge(ImportAdvancePayment importAdvancePayment);

    public void update(ImportAdvancePayment importAdvancePayment);

//    public ImportAdvancePayment load(String documentNumber);
    public ImportAdvancePayment load(DocumentNumber documentNumber);

    public List<ImportAdvancePayment> getAllImportAdvancePayments(String cifName,
                                                                  DocumentNumber documentNumber,
                                                                  Currency currency,
                                                                  BigDecimal amountFrom,
                                                                  BigDecimal amountTo,
                                                                  String unitcode,
                                                                  String unitCode);
}

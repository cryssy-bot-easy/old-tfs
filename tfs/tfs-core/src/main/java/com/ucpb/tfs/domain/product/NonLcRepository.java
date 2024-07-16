package com.ucpb.tfs.domain.product;

/**
 */
public interface NonLcRepository {

    public DirectRemittance getDirectRemittance(DocumentNumber documentNumber);

    public OpenAccount getOpenAccount(DocumentNumber documentNumber);

    public DocumentAgainstPayment getDocumentAgainstPayment(DocumentNumber documentNumber);

    public DocumentAgainstAcceptance getDocumentAgainstAcceptance(DocumentNumber documentNumber);

}

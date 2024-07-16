package com.ucpb.tfs.domain.reference;

/**
 */
public interface CorrespondentBankRepository {

    public CorrespondentBank getCorrespondentBankByBankCode(String bankCode);

    public void saveCorrespondentBank(CorrespondentBank correspondentBank);


}

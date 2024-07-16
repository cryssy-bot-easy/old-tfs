package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CasaTransactionLog;

/**
 */
public interface CasaTransactionLogRepository {


    public void save(CasaTransactionLog transaction);


}

package com.ucpb.tfs.domain.corresCharges;

import com.ucpb.tfs.domain.product.DocumentNumber;

import java.util.List;

/**
 * User: IPCVal
 * Date: 11/4/12
 */
public interface CorresChargeActualRepository {

    public CorresChargeActual load(Long id);

    public void save(CorresChargeActual corresChargeActual);

    public List<CorresChargeActual> getAllByDocumentNumber(DocumentNumber documentNumber);
}

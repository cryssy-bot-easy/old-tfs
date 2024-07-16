package com.ucpb.tfs.domain.corresCharges;

import com.ucpb.tfs.domain.product.DocumentNumber;

import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 11/4/12
 */
public interface CorresChargeAdvanceRepository {

    public CorresChargeAdvance load(Long id);

    public void save(CorresChargeAdvance corresChargeAdvance);

    public void merge(CorresChargeAdvance corresChargeAdvance);

    public List<CorresChargeAdvance> getAllByDocumentNumber(DocumentNumber documentNumber);

    public List<Map<String, Object>> findAllByDocumentNumber(String documentNumber, String unitCode, String unitcode);

}

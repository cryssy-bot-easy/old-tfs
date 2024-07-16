package com.ucpb.tfs.domain.product;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 2/22/13
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ExportAdvisingRepository {

    public List<ExportAdvising> getAllExportAdvising(DocumentNumber documentNumber,
                                                           DocumentNumber lcNumber,
                                                           String exporterName,
                                                           Date processDate,
                                                           String unitCode);

    public List<ExportAdvising> autoCompleteExportAdvising(String documentNumber);

    public List<ExportAdvising> autoCompleteExportAdvising(String cifNumber, String documentNumber);
}

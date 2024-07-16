package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CifNormalizationLog;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/22/14
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CifNormalizationLogRepository {

    public void persist(CifNormalizationLog cifNormalizationLog);

    public List<CifNormalizationLog> getLogMatching(String oldCifNumber,
                                                    String oldCifName,
                                                    String newCifNumber,
                                                    String newCifName,
                                                    String oldMainCifNumber,
                                                    String oldMainCifName,
                                                    String newMainCifNumber,
                                                    String newMainCifName,
                                                    BigDecimal oldFacilityId,
                                                    BigDecimal newFacilityId,
                                                    Date normalizationDate);

    public List<CifNormalizationLog> getAllLogs();
}

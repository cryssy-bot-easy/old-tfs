package com.ucpb.tfs.application.service;

import com.ucpb.tfs.domain.audit.CifDetails;
import com.ucpb.tfs.domain.audit.CifNormalizationLog;
import com.ucpb.tfs.domain.audit.MainCifDetails;
import com.ucpb.tfs.domain.audit.infrastructure.repositories.CifNormalizationLogRepository;
import com.ucpb.tfs.utils.WriteFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/22/14
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class CifNormalizationLogService {

    @Autowired
    CifNormalizationLogRepository cifNormalizationLogRepository;

    public void saveCifNormalizationLog(Map<String, String> cifNormalizationLogMap, Date normalizationDate,
                                        BigDecimal oldFacilityId, BigDecimal newFacilityId) {

        CifDetails cifDetails = new CifDetails(cifNormalizationLogMap.get("oldCifNumber"),
                cifNormalizationLogMap.get("oldCifName"),
                cifNormalizationLogMap.get("newCifNumber"),
                cifNormalizationLogMap.get("newCifName"));

        MainCifDetails mainCifDetails = new MainCifDetails(cifNormalizationLogMap.get("oldMainCifNumber"),
                cifNormalizationLogMap.get("oldMainCifName"),
                cifNormalizationLogMap.get("newMainCifNumber"),
                cifNormalizationLogMap.get("newMainCifName"));

        CifNormalizationLog cifNormalizationLog = new CifNormalizationLog(normalizationDate,
                cifDetails,
                mainCifDetails,
                oldFacilityId,
                newFacilityId);

        cifNormalizationLogRepository.persist(cifNormalizationLog);

    }

    private String generateFileName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        return "CIFNORM_LOG_" + simpleDateFormat.format(new Date());
    }

    public void createLogFile() throws IOException {

        WriteFile data = new WriteFile("C:/Workspace/ucpb-tfs/latest/log_files_test/"+generateFileName()+".txt", true);

        data.writeToFile("OLDCIFNO\tOLDCIFNAME\t\tNEWCIFNO\tNEWCIFNAME\t\tOLDMCIFNO\tOLDMCIFNAME\t\tNEWMCIFNO\t" +
                "NEWMCIFNAME\t\tOLDFACID\tNEWFACID\tNORMDATE");

        List<CifNormalizationLog> cifNormalizationLogList = cifNormalizationLogRepository.getAllLogs();

        for (CifNormalizationLog cifNormalizationLog: cifNormalizationLogList) {
            String logEntry = cifNormalizationLog.getCifDetails().getOldCifNumber() + "\t" +
                    cifNormalizationLog.getCifDetails().getOldCifName() + "\t\t" +
                    cifNormalizationLog.getCifDetails().getNewCifNumber() + "\t" +
                    cifNormalizationLog.getCifDetails().getNewCifName() + "\t\t" +
                    cifNormalizationLog.getMainCifDetails().getOldMainCifNumber() + "\t" +
                    cifNormalizationLog.getMainCifDetails().getOldMainCifName() + "\t\t" +
                    cifNormalizationLog.getMainCifDetails().getNewMainCifNumber() + "\t" +
                    cifNormalizationLog.getMainCifDetails().getNewMainCifName() + "\t\t" +
                    cifNormalizationLog.getOldFacilityId() + "\t" +
                    cifNormalizationLog.getNewFacilityId() + "\t" +
                    cifNormalizationLog.getNormalizationDate() + "\t";

            data.writeToFile(logEntry);
        }

        data.writeToFile("FETCH RESULT: " + cifNormalizationLogList.size());
    }

}

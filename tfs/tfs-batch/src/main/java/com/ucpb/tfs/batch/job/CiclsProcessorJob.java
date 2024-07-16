package com.ucpb.tfs.batch.job;

import com.ucpb.tfs.batch.report.dw.dao.CiclsDao;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CiclsProcessorJob implements SpringJob {

	private static final String DATE_FORMAT = "MMddyy";
    private CiclsDao ciclsDao;
    
    @Override
    public void execute() throws Exception {
        ciclsDao.processCiclsRecords(new Date());
    }

    @Override
    public void execute(String reportDate) throws Exception {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date runDate = dateFormat.parse(reportDate);
        ciclsDao.processCiclsRecords(runDate);
    }

    public void setCiclsDao(CiclsDao ciclsDao) {
        this.ciclsDao = ciclsDao;
    }
    
    public CiclsDao getCiclsDao() {
        return ciclsDao;
    }
}



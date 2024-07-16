package com.ucpb.tfs.batch.report.dw.dao;

import com.ucpb.tfs.batch.report.dw.CiclsHandoffRecord;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CiclsDao {
	
	public void processCiclsRecords(@Param("runDate") Date runDate);

	public List<CiclsHandoffRecord> getCiclsRecords(@Param("runDate") Date runDate);


}

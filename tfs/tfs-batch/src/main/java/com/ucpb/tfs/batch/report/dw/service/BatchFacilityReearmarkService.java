package com.ucpb.tfs.batch.report.dw.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ucpb.tfs.batch.facility.Availment;

/**
 * <pre>
 * Program_id    : BatchFacilityReearmarkService
 * Program_name  : Batch Facility Reearmarking Service
 * SCR_Number    : IBD-12-0502-01
 * Process_Mode  : WEB
 * Frequency     : Daily
 * Input         : N/A
 * Output        : N/A
 * Description   : Contains SIBS - related methods where mybatis calls from tfs-core are mapped to tfs-batch
 * Called In     : BatchFacilityRevaluationJob.java
 * </pre>
 * @author Arvin Patrick Guiam
 *
 */
public interface BatchFacilityReearmarkService {

	/**
	 * Initates the process to reearmarking of availments in SIBS
	 * @param availments the List of availments to be earmarked in SIBS 
	 * @return the status of the reearmarking process
	 */
	public boolean reearmark(List<Availment> availments);
	
	/**
	 * Obtains the conversion rate that is on or closest before the reference date
	 * @param currency the reference currency
	 * @param sibsDate the reference date used to obtain the conversion rate
	 * @return the conversion rate
	 */
	public List<Map<String, Object>> getConversionRateHistorical(String sibsDate);
}

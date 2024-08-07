package com.ucpb.tfs.batch.report.dw.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ucpb.tfs.batch.facility.Availment;
import com.ucpb.tfs.batch.report.dw.dao.SilverlakeLocalDao;

/**
 * <pre>
 * Program_id    : BatchFacilityReearmarkServiceImpl
 * Program_name  : Batch Facility Reearmarking Service
 * SCR_Number    : IBD-12-0502-01
 * Process_Mode  : WEB
 * Frequency     : Daily
 * Input         : N/A
 * Output        : N/A
 * Description   : Contains SIBS - related methods where mybatis calls from tfs-core are mapped
 * Called In     : BatchFacilityRevaluationJob.java
 * </pre>
 * @author Arvin Patrick Guiam
 * @see com.ucpb.tfs.batch.report.dw.service.BatchFacilityReearmarkService
 *
 * REVISIONS
 * 
 * Description:	Replaced usage of silverlakeDao into silverlakeLocalDao
 * Revised by: 	Cedrick C. Nungay
 * Date revised:01/25/2024
*/
public class BatchFacilityReearmarkServiceImpl implements BatchFacilityReearmarkService {

	/** Interface File that contains methods involving SIBS Batch Processes*/
	private SilverlakeLocalDao silverlakeLocalDao;
	
	@Override
	public boolean reearmark(List<Availment> availments) {
		boolean returnVal = false;
		List<Map<String,?>> originalEarmarkings = silverlakeLocalDao.selectEarmarksTrade();
		try{

			silverlakeLocalDao.deleteEarmarksTrade();
			for(Availment availment : availments){
				System.out.println("yosimitsuuuuuuuuuuuuuuuuuhooooooooooooooooooo" + silverlakeLocalDao.checkContingentExists(availment));
				if(silverlakeLocalDao.checkContingentExists(availment) == 0){
					silverlakeLocalDao.insertFacilityAvailment(availment);
					returnVal = true;
				}
				else{
					silverlakeLocalDao.updateFacilityAvailment(availment);
					returnVal = true;
				}
				// // if(silverlakeLocalDao.updateFacilityAvailment(availment) == 0){
				// // 	silverlakeLocalDao.insertFacilityAvailment(availment);
				// // 	returnVal = true;
				// // }
			}
			
		}catch (Exception e){
			System.out.println("ERrrrooorrr reearmark" + e);
			silverlakeLocalDao.deleteEarmarksTrade();
			for (Map earmarking : originalEarmarkings){
				silverlakeLocalDao.reinsertFacilityAvailment(earmarking);
			}
			returnVal = false;
		}
			
		return returnVal;
	}

    public void setSilverlakeLocalDao(SilverlakeLocalDao silverlakeLocalDao) {
    	this.silverlakeLocalDao = silverlakeLocalDao;
    }

	@Override
	public List<Map<String, Object>> getConversionRateHistorical(String sibsDate) {
		//reserve currency string for future revision
		String currency = "";
		System.out.println("set Date = " + sibsDate);
		List<Map<String, Object>> conversionRate = silverlakeLocalDao.getCurrentRevalueRate(sibsDate, currency, "PHP", 18);
		if(conversionRate != null){
			System.out.println("obtained today's conversion rate.");
		} else {
			conversionRate = silverlakeLocalDao.getHistoricalRevalueRate(sibsDate, currency, "PHP", 18);
			System.out.println("obtained historical conversion rate.");
		}
//		System.out.println("actual Date = " + conversionRate.get("JULIAN_DATE"));
//		return new BigDecimal(conversionRate.get("CONVERSION_RATE").toString());
		return conversionRate;
	}
}

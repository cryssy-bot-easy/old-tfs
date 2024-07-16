package com.ucpb.tfs.batch.report.dw.dao;

import com.ucpb.tfs.batch.allocationUnitCode.SibsAllocationUnitCodeRecord;
import com.ucpb.tfs.batch.cif.CibsDetailsTable;
import com.ucpb.tfs.batch.cif.CibsMasterTable;
import com.ucpb.tfs.batch.facility.Availment;
import com.ucpb.tfs.batch.facility.FacilityReference;
import com.ucpb.tfs.batch.report.dw.AllocationUnit;
import com.ucpb.tfs.batch.report.dw.Appraisal;
import com.ucpb.tfs.batch.report.dw.Earmark;
import com.ucpb.tfs.batch.report.dw.GLParameterRecord;
import com.ucpb.tfs.batch.report.dw.TradeProduct;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * <pre>
 * Program_id    : Silverlake Dao
 * Program_name  : TFS-SIBS MyBatis Batch Process Queries 
 * SCR_Number    : IBD-12-0502-01
 * Process_Mode  : BATCH
 * Frequency     : Daily
 * Input         : N/A
 * Output        : N/A
 * Description   : Universal interface file containing methods mapped in sibs-mapper.xml that contains the SQL queries for the SIBS database
 * Called In     : FixedFileReportGeneratorJob.java, AllocationFileServiceImpl.java, BatchFacilityReearmarkServiceImpl.java, MasterFileServiceImpl.java, CifNormalization.java, FacilityReferenceNormalization.java, AllocationUnitCodeTasklet.java
 * </pre>
 * @author Robbie Anonuevo
 * @author Marvin Volante
 * @author Val Pecaoco
 * @author Alvin Goya
 * @author Arvin Patrick Guiam
 * @author Gian Carlo Angulo
 * @author Alexander Bilalang
 */


/*  PROLOGUE:
 *  (revision)
    SCR/ER Number: IBD-15-0828-01
    SCR/ER Description: Comparison of Balances in DW and SIBS-GL
    [Revised by:] Jesse James Joson
    [Date revised:] 09/17/2015
    Program [Revision] Details: add new methods that will connect to the mapper
    INPUT: getGlBalance
    OUTPUT: Daily_Master_GL_Summary.xls & Daily_Master_GL_DailyBalance_Summary.xls
    PROCESS: Called by another class to call specify parameters needed to in the mapper
    PROJECT: CORE
    MEMBER TYPE  : Java
    Project Name: SilverlakeDao
 */


/*  PROLOGUE:
 *  (revision)
    SCR/ER Number: SCR IBD-16-0219-01
    SCR/ER Description: Generate CIC File
    [Revised by:] Jesse James Joson
    [Date Deployed:] 02/24/2016
    Program [Revision] Details: Add a method to extract the rates for CIC file 
    PROJECT: CORE
    MEMBER TYPE  : Java
    Project Name: SilverlakeDao
 */

/**  PROLOGUE:
 *  (revision)
    SCR/ER Number: ER# 20140909-038
    SCR/ER Description: CIF Normalization Not Working in TFS
    [Revised by:] Jesse James Joson
    [Date Deployed:] 08/05/2016
    Program [Revision] Details: Modify Method to make CIF normalization available for Adhoc.
    PROJECT: CORE
    MEMBER TYPE  : Java
    Project Name: SilverlakeDao
 */
 
 /**  PROLOGUE:
 *  (revision)
    SCR/ER Number: ER# 20180614-030
    SCR/ER Description: Update Allocation Unit Code module of TFS bactch encountered FAILED message during execution.
    [Revised by:] Jesse James Joson
    [Date Revised:] 07/13/2018
    Program [Revision] Details: Modify Method to be able to handle list of records, and not only one record.
    PROJECT: CORE
    MEMBER TYPE  : Java
    Project Name: SilverlakeDao
 */
 
 /**  PROLOGUE:
 *  (revision)
    Description: SIBS Deactivation, all methods are removed from SilverlakeDao.java and transferred here.
    [Revised by:] Cedrick C. Nungay
    [Date Revised:] 02/15/2024
    PROJECT: CORE
    MEMBER TYPE  : Java
    Project Name: SilverlakeLocalDao
 */
public interface SilverlakeLocalDao {

    public BigDecimal getAngolConversionRate(@Param("sourceCurrency")String sourceCurrency, @Param("targetCurrency")String targetCurrency,@Param("rateNumber")int rateNumber);

	public BigDecimal getAngolConversionRateForDailyBalance(@Param("sourceCurrency")String sourceCurrency, @Param("targetCurrency")String targetCurrency, @Param("rateNumber")int rateNumber, @Param("date") int date);

    public BigDecimal getHistoricalRevalRate(@Param("date") int date, @Param("sourceCurrency") String sourceCurrency, @Param("targetCurrency") String targetCurrency, @Param("rateNumber") int rateNumber);

    public BigDecimal getHistoricalRevalRateForCic(@Param("date") int date, @Param("sourceCurrency") String sourceCurrency, @Param("targetCurrency") String targetCurrency, @Param("rateNumber") int rateNumber); //IBD-16-0219-01

    public List<GLParameterRecord> getDwGlParameters();
    
    /**
     * Obtains the conversion rates within the reference date
     * @param sibsDate the reference date to be used
     * @param sourceCurrency the reference currency
     * @param targetCurrency the final currency
     * @param rateNumber the rate type designation
     * @return the conversion rate
     */
    public List<Map<String, Object>> getCurrentRevalueRate(@Param("sibsDate") String sibsDate, @Param("sourceCurrency")String sourceCurrency, @Param("targetCurrency")String targetCurrency,@Param("rateNumber")int rateNumber);
    /**
     * Obtains the historical conversion rates closest to the reference date
     * @param sibsDate the reference date to be used
     * @param sourceCurrency the reference currency
     * @param targetCurrency the final currency
     * @param rateNumber the rate type designation
     * @return the closest historical rate
     */
    public List<Map<String, Object>> getHistoricalRevalueRate(@Param("sibsDate") String sibsDate, @Param("sourceCurrency")String sourceCurrency, @Param("targetCurrency")String targetCurrency,@Param("rateNumber")int rateNumber);

	public List<TradeProduct> getGlBalance();

    public String getAllocationUnitCode(@Param("branchUnitCode")String branchUnitCode);

    public String getAllocationUnitCodeForNumericCif(@Param("branchUnitCode") String branchUnitCode);

    public AllocationUnit getBranchUnitCodeForAlphanumericOfficerCode(@Param("officerCode")String officerCode);

    public AllocationUnit getBranchUnitCodeForAlphaOfficerCode(@Param("officerCode")String officerCode);

    public AllocationUnit getBranchUnitCodeForNumericOfficerCode(@Param("officerCode")String officerCode);

    public String getAccountOfficerOfCif(@Param("cifNumber")String cifNumber);
    
    public String getAllocationUnitCodeForAlphanumericOfficerCode(@Param("officerCode")String officerCode);

    public String getAllocationUnitCodeForAlphaOfficerCode(@Param("officerCode")String officerCode);

    public String getAllocationUnitCodeForNumericOfficerCode(@Param("officerCode")String officerCode);   

    public List<SibsAllocationUnitCodeRecord> getSibsAllocationUnitCodeDetails(@Param("cifNumber") String cifNumber);

    public Appraisal getAppraisalDetails(@Param("mainCif")String mainCif);

    public String getIndustryCode(@Param("cifNumber")String cifNumber);
	
    public String getEarmarkingAccountStatus(@Param("documentNumber")String documentNumber);

    public String getFacilityReferenceNumber(@Param("facilityType")String facilityType, @Param("facilityId") BigDecimal facilityId,
                                             @Param("mainCifNumber") String mainCifNumber);

    public List<CibsMasterTable> getCibsMasterTable(@Param("julianDate")long julianDate, @Param("appDate")long appDate); //ER# 20140909-038

    public List<CibsDetailsTable> getCibsDetailsTable(@Param("julianDate")long julianDate, @Param("appDate")long appDate);//ER# 20140909-038


    public List<Earmark> getEarmarksTrade();
    
    public List<Earmark> getlnclstCIFNO();
    
    public void updateLnclstNorm(@Param("newCIFNO") String newCIFNO, @Param("oldCIFNO") String oldCIFNO);

    public int updateLocalOutstandingBalance(@Param("earmark") Earmark earmark);

    public int updateLocalOriginalAmount(@Param("earmark") Earmark earmark);

    public List<FacilityReference> getFacilityReferenceEntries();

    public int deleteOutstandingUnapprovedFacilityAvailment(@Param("documentNumbers") String... documentNumbers);
    public List<Map<String,?>> test(@Param("documentNumbers") String... documentNumbers);
    
    /**
     * deletes all earmarked availments of TFS in SIBS
     * @return the number of rows deleted
     */
    public int deleteEarmarksTrade();
    /**
     * Updates the earmarked availment in SIBS
     * @param availment the availment object used to update earmarking
     * @return the number of rows updated
     */
    public int updateFacilityAvailment(@Param("availment") Availment availment);
    /**
     * Earmarks availment in SIBS
     * @param availment the availment to be earmarked
     * @return the number of rows inserted
     */
    public int insertFacilityAvailment(@Param("availment") Availment availment);
    /**
     * Selects all Earmarked Availments of TFS in SIBS
     * @return List of Earmarked Availments of TFS
     */
    public List<Map<String,?>> selectEarmarksTrade();
    /**
     * Reearmarks deleted TFS Availments in SIBS 
     * @param earmarking the availment to be reearmarked
     * @return the number of rows inserted
     */
    public int reinsertFacilityAvailment(@Param("earmarking") Map earmarking);

     /**
     * Reearmarks deleted TFS Availments in SIBS 
     * @param availment the availment to be reearmarked
     * @return the number of rows
     */
    public int checkContingentExists(@Param("availment") Availment availment);
}

package com.ucpb.tfs.batch.dao;

import org.apache.ibatis.annotations.Param;

import com.ucpb.tfs.batch.report.dw.DailyBalanceRecord;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Program_id    : BatchProcessDao
 * Program_name  : TFS MyBatis Batch Process Queries
 * SCR_Number    : IBD-12-0502-01
 * Process_Mode  : BATCH
 * Frequency     : Daily
 * Input         : N/A
 * Output        : N/A
 * Description   : Universal interface file containing methods mapped in batch-process-mapper.xml that contains the SQL queries for the TFS database
 * Called In     : TransactionRoutingUpdateJob.java, BatchEtsPurgingJob.java, CancelExpiredLettersOfCreditJob.java,DailyBalanceRecorderJob.java
 * </pre>
 * @author Robbie Anonuevo
 * @author Val Pecaoco
 * @author Gian Carlo Angulo
 * @author Arvin Patrick Guiam
 * @author Alexander Bilalang
 */

	/*  PROLOGUE:
	 *	(revision)
		SCR/ER Number: ER# 20160505-030
		SCR/ER Description: 1.  The LC 909-03-929-16-00198-8 was amended last March 18, 2016 – only Tenor was amended from sight to usance.
								The AE are okay, debit the contingent for sight and credit to usance. But the DW Allocation reported the LC once 
								and the ADB are not reported separately  for sight and usance.
						2.  Adjustment on Standby LC tagging was not correctly reported in DW
		[Revised by:] Lymuel Arrome Saul
		[Date revised:] 05/05/2016
		Program [Revision] Details: Added functions getAuditRevId, checkIfTenorChange, getLetterOfCreditAuditByRevId and getOpeningStandbyTagging.
		Date deployment: 
		Member Type: JAVA
		Project: CORE
		Project Name: BatchProcessDao.java	
	 */

public interface BatchProcessDao {

    public void purgeUnactedEts(@Param("queryDate") Date queryDate);

    public List<Map<String,Object>> getAllUnapprovedTradeService();

    public void cancelExpiredLettersOfCredit(@Param("queryDate") Date queryDate);

    public int countCancelExpiredLettersOfCredit(@Param("queryDate") Date queryDate);

    public void deleteDailyBalance(@Param("queryDate") Date queryDate);

    public void insertToDailyBalance(@Param("queryDate") Date queryDate);
    
    /**
     * Deletes all routing Entries in the SEC_ROUTING table
     * @return the number of rows deleted
     */
    public int deleteRouting();
    
    /**
     * Inserts the Routing Information in the SEC_ROUTING table
     * @param userId the ID of the User
     * @param groupName the Region/Division the User is associated
     * @param unitDesignation the Branch/Area the User is assigned
     * @param role the Corresponding Role of the User in TFS
     * @return the number of rows inserted
     */
    public int insertRouting(@Param("userId") String userId, @Param("groupName") String groupName, @Param("unitDesignation") String unitDesignation, @Param("role") String role);
    
    /**
     * Checks if the User is already existing in the SEC_ROUTING table
     * @deprecated
     * @param userId the ID of the User
     * @return the number of matching results
     */
    public int checkIfExistingUser(@Param("userId") String userId);
    
    /**
     * Checks if the Role Provided in in the SEC_ROUTING_BRANCH_ROLE table
     * @deprecated
     * @param role the role to be evaluated
     * @return the number of matching results
     */
    public int checkIfValidRole(@Param("role") String role);
    
    public List<DailyBalanceRecord> getOriginalDailyBalance(@Param("queryDate")Date queryDate);
    
    public List<Map<String, Integer>> getCashFlags(@Param("documentNumber")String documentNumber);
    
    public void insertToDailyBalanceNew(@Param("dailyBalance") DailyBalanceRecord dailyBalanceRecord);
    
    public void insertToRefCifNumber(@Param("cifNumber") String cifNumber);
    
    public String checkIfTenorChange(@Param("documentNumber")String documentNumber, @Param("queryDate")Date queryDate);
    
    public List<Integer> getAuditRevId(@Param("docNum") String docNum, @Param("cutoffDate") Date cutoffDate);
    
    public List<DailyBalanceRecord> getLetterOfCreditAuditByRevId(@Param("revId") Integer revId);
    
    public String getOpeningStandbyTagging(@Param("documentNumber")String documentNumber);

}

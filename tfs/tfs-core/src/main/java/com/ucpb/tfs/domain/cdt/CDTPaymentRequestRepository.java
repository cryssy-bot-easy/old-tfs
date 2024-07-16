package com.ucpb.tfs.domain.cdt;

import com.ucpb.tfs.domain.cdt.enums.PaymentRequestType;

import java.util.Date;
import java.util.List;

/*  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CDTPaymentRequestRepository
 */
 


public interface CDTPaymentRequestRepository {

    public void persist(CDTPaymentRequest cdtPaymentRequest);

    public void merge(CDTPaymentRequest cdtPaymentRequest);

    public void update(CDTPaymentRequest cdtPaymentRequest);

    public List<CDTPaymentRequest> getAllRequests();
    public List<CDTPaymentRequest> getRequestsMatching(String refNumber,
                                                       String iedNumber,
                                                       String importer,
                                                       String requestType,
                                                       String status,
                                                       Date txDateFrom,
                                                       Date txDateTo,
                                                       String unitCode,
                                                       Date uploadDate,
                                                       String aabRefCode);
    public List<EmailNotif> getRequestsMatchingEmailTable(
											    	   String iedieirdNumber,
											           String emailAddress,
											           String emailStatus,
											           Date sentTime);
    public List<CDTPaymentRequest> getcdtTodays(String refNumber,
            String iedNumber,
            String importer,
            String requestType,
            String status,
            Date txDateFrom,
            Date txDateTo,
            String unitCode,
            Date uploadDate,
            String aabRefCode);
    

    public CDTPaymentRequest getPaymentRequestDetails(String iedNumber);

    public CDTPaymentRequest load(String iedieirdNumber);

    public List<CDTPaymentRequest> getHistoryUpdatedToday();

    public List<CDTPaymentRequest> getHistoryUpdatedToday(String unitCode);
    
    public List<CDTPaymentRequest> getHistoryUpdatedToday(String unitCode, Date bocDate);
    
    public List<CDTPaymentRequest> getAllPaidRequests();

    public List<CDTPaymentRequest> getAllPaidRequests(String unitCode);
    public List<CDTPaymentRequest> getAllPaidRequests(String unitCode, Date confdate);
    

    public List<CDTPaymentRequest> getAllSentRequests(Date from, Date to, List<PaymentRequestType> paymentRequestTypeList);

    public List<CDTPaymentRequest> getAllBranch();
    
    // Used by AMLA
    public List<CDTPaymentRequest> getAllRemittedRequestsWithCif(Date from, Date to);

    public List<CDTPaymentRequest> getAllRemittedRequests(Date from, Date to, List<PaymentRequestType> paymentRequestTypeList);

    public List<CDTPaymentRequest> getAllSentToMobBoc();

    public List<CDTPaymentRequest> getAllSentToMobBoc(String unitCode);

    public CDTPaymentRequest getOwnPaymentRequestDetails(String iedNumber, String unitCode);

    public List<CDTPaymentRequest> getNewPaymentRequestsYesterday();

    public List<CDTPaymentRequest> getNewPaymentRequestsToday();

    public List<CDTPaymentRequest> getAllUploadedToday(String unitCode);

    public List<CDTPaymentRequest> getConfirmedPayments(Date dateGenerated);

    public List<CDTPaymentRequest> getRejectedPayments(Date dateGenerated);

}

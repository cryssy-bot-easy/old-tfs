package com.ucpb.tfs.domain.cdt;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/13/14
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
 
 
/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number: SCR# IBD-16-1206-01
	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
	[Created by:] Allan Comboy and Lymuel Saul
	[Date Deployed:] 12/20/2016
	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
	PROJECT: CORE
	MEMBER TYPE  : Java
	Project Name: CDTPaymentHistoryRepository
 */
 
 
public interface CDTPaymentHistoryRepository {

    public CDTPaymentHistory load(String iedieirdNumber);

    public CDTPaymentHistory load(String iedieirdNumber, String unitCode);
    
    public CDTPaymentHistory load(String iedieirdNumber,String unitCode, Date confDate);
    
    public void delete(String unitCode, Date confDate);
    
    public void persist(CDTPaymentHistory cdtPaymentHistory);

    public void merge(CDTPaymentHistory cdtPaymentHistory);

    public BigDecimal getTotalAmount(Date dateUploaded, String unitCode);

}

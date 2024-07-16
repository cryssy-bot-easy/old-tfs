package com.ucpb.tfs.batch.report.dw;

import com.ancientprogramming.fixedformat4j.format.FixedFormatManager;
import com.ancientprogramming.fixedformat4j.format.impl.FixedFormatManagerImpl;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 */
public class MasterFileRecordFormattingTest {


    private static FixedFormatManager manager = new FixedFormatManagerImpl();


    @Test
    public void setup(){
        System.out.println("******** " + new Date().getTime());
    }

    @Test
    public void leftPadTest(){
        assertEquals("    -1213.55", StringUtils.leftPad(new BigDecimal("-1213.55").toString(),12," "));
    }

    @Test
    public void produceCorrectlyFormattedMasterFileRecord(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2012);
        cal.set(Calendar.DAY_OF_MONTH,13);
        cal.set(Calendar.MONDAY,Calendar.APRIL);

        TradeProduct product = new TradeProduct();
        product.setApplicationAccountId("ApplicationAccountId");
        product.setFacilityId("facilityId");
        product.setCustomerId("customerId");
        product.setAccountStatusId("status");
        product.setBranchId("BRN");
        product.setOutstandingBookCode("BK");
        product.setEntityId("ENTITY");
        product.setOutstandingCurrencyId("CUR");
        product.setProductId("PRODUCTID");
        product.setOpenDate(cal.getTime());
        product.setNegotiationDate(cal.getTime());
        product.setClosedDate(cal.getTime());
        product.setMaturityDate(cal.getTime());
        product.setLastAmendmentDate(cal.getTime());
        product.setLastReinstatementDate(cal.getTime());
        product.setOutstandingContingentAssets(new BigDecimal("1213414.12"));

        MasterFileRecord record = new MasterFileRecord(product);
        String expected = "ApplicationAccountId          facilityId          customerId                    MATURED   " +
                "BRN" + "BK" + "ENTITY    " + "CUR" + "PRODUCTID      201204132012041320120413201204132012041320120413                       0" +
                "              1213414.12";

        assertEquals(589,manager.export(record).length());
        assertEquals(expected,manager.export(record));
    }

    @Test
    @Ignore
    public void successfullyExportMasterRecord(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2012);
        cal.set(Calendar.DAY_OF_MONTH,13);
        cal.set(Calendar.MONDAY,Calendar.APRIL);

        TradeProduct product = new TradeProduct();
        product.setApplicationAccountId("ApplicationAccountId");
        product.setFacilityId("facilityId");
        product.setCustomerId("customerId");
        product.setAccountStatusId("status");
        product.setBranchId("BRN");
        product.setOutstandingBookCode("BK");
        product.setEntityId("ENTITY");
//        product.setOutstandingBookCode("CUR");
        product.setProductId("PRODUCTID");
        product.setOpenDate(cal.getTime());
        product.setNegotiationDate(cal.getTime());
        product.setClosedDate(cal.getTime());
        product.setMaturityDate(cal.getTime());
        product.setLastAmendmentDate(cal.getTime());
        product.setLastReinstatementDate(cal.getTime());
        product.setOutstandingContingentAssets(new BigDecimal("1213414.12"));
        product.setOutstandingContingentLiabilities(new BigDecimal("1213414.12"));
        product.setContingentAssetsGlNumber("ContingentAGlNumber");
        product.setContingentLiabilitiesGlNumber("ContingentLGLNumber");
        product.setSettlementBookCode("SL");
        product.setBillOfLadingDate(cal.getTime());
        product.setUaMaturityDate(cal.getTime());

        Appraisal appraisal = new Appraisal();
        appraisal.setAppraisalDate(11111111);
        appraisal.setAppraisedValue(new BigDecimal("1941.02"));

        product.setCorrespondingBank("CorrespondingBank");
        product.setImportStatusCode("1");
        product.setCountryCode("PH");
        product.setClientCbCode("CLIENTCB");
        product.setTransactionCode("TRN");
        product.setModeOfPayment("A");
//        product.setDocumentSubType1("");

        MasterFileRecord record = new MasterFileRecord(product);
        record.setAppraisal(appraisal);
        record.setCreditFacilityCode("1");
        record.setCounterpartyCode("counterparty");
        record.setIndustryCode("IND");

        String expected = "ApplicationAccountId          facilityId          customerId                    MATURED   " +
                "BRN" + "BK" + "ENTITY    " + "CUR";
//        + "PRODUCTID      201204132012041320120413201204132012041320120413                       0" +
//                "              1213414.12";

        assertEquals(expected,record.export());
    }

    @Ignore
    @Test
    public void correctOutputFile(){

        TradeProduct product = new TradeProduct();
        product.setApplicationAccountId("documentNumber");
        product.setFacilityId("1");
        product.setFacilityType("fType");
        product.setCustomerId("cifNumber");
        product.setAccountStatusId("ACTIVE");
        product.setBranchId("909");
        product.setOutstandingBookCode("RG");
        product.setEntityId("29");
        product.setOutstandingCurrencyId("PHP");
        product.setProductId("productCode");
        //Nov 6, 2012
        product.setOpenDate(new Date(1352136684758L));
        product.setNegotiationDate(new Date(1352136684758L));
        product.setClosedDate(null);
        //TODO put in future date (constant)
        product.setMaturityDate(new Date());
        product.setLastAmendmentDate(null);
        product.setLastReinstatementDate(null);
        product.setOutstandingContingentAssets(new BigDecimal("332233"));
        product.setOutstandingContingentLiabilities(new BigDecimal("222222"));
        product.setContingentAssetsGlNumber("assetsGlNumber");
        product.setContingentLiabilitiesGlNumber("liabilitiesGlNumber");

        //masterfile record - settlement book code

        product.setBillOfLadingDate(new Date(1352136684758L));
        product.setUaMaturityDate(new Date(1352136684758L));

        Appraisal appraisal = new Appraisal();
        appraisal.setAppraisalDate(90913);
        appraisal.setAppraisedValue(new BigDecimal("43019"));
        appraisal.setSecurityCode("SC");

        product.setCountryCode("country");
        product.setClientCbCode("clientCb");
        product.setTransactionCode("312");
        product.setModeOfPayment("1");


       MasterFileRecord record = new MasterFileRecord(product);
       record.setAppraisal(appraisal);
       record.setSettlementBlockCode("RG");
        record.setCreditFacilityCode("2");
        record.setCounterpartyCode("counter");
        record.setCorrespondingBank("corresponding bank");
        record.setImportStatusCode("2");
    }

}

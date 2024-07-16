package com.ucpb.tfs.application.domain.settlementaccount;

import com.ucpb.tfs.application.query.settlementaccount.IMarginalDepositFinder;
import com.ucpb.tfs.domain.settlementaccount.*;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Set;

/**
 * User: Val
 * Date: 7/22/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestSettlementAccountContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestMarginalDeposit {

//    @Inject
//    MarginalDepositRepository marginalDepositRepository;
//
//    @Autowired
//    IMarginalDepositFinder marginalDepositFinder;
//
//    @Test
//    public void AddMarginalDeposit() throws Exception {
//
//        /**/
//        MarginalDeposit md = new MarginalDeposit(new SettlementAccountNumber("1"));
//
//        BigDecimal casa = new BigDecimal("1000.00");
//        BigDecimal cash = new BigDecimal("2000.00");
//        BigDecimal ibt = new BigDecimal("3000.00");
//
//        md.credit(casa, Currency.getInstance("PHP"), ReferenceType.CASA, "bhnhteewdffef");
//        md.credit(cash, Currency.getInstance("PHP"), ReferenceType.CASH, "b45fgedfefdvf");
//        md.credit(ibt, Currency.getInstance("PHP"), ReferenceType.IBT_BRANCH, "dbgnytrfds");
//
//        marginalDepositRepository.persist(md);
//
//        MarginalDeposit md2 = marginalDepositRepository.load(md.getSettlementAccountNumber());
//
//        if (md2 != null) {
//            BigDecimal applyToLoan = new BigDecimal("1000.00");
//
//            md2.debit(applyToLoan, Currency.getInstance("PHP"), ReferenceType.APPLY_TO_LOAN, "frt4tgr45ef");
//
//            marginalDepositRepository.persist(md2);
//        }
//
//        Map<String, ?> md3 = marginalDepositFinder.findMarginalDeposit(md.getSettlementAccountNumber().toString());
//
//        if (md3 != null) {
//
//            System.out.println("settlementAccountNumber = " + md3.get("SETTLEMENTACCOUNTNUMBER"));
//            System.out.println("accountType = " + md3.get("ACCOUNTTYPE"));
//
//            Set<Map<String, ?>> mdActivities = marginalDepositFinder.getAllActivity((String)md3.get("SETTLEMENTACCOUNTNUMBER"));
//
//            System.out.println("mdActivities.size() = " + mdActivities.size() + "\n");
//
//            for(Map<String, ?> mdActivity : mdActivities) {
//                System.out.println("amount = " + mdActivity.get("AMOUNT"));
//                System.out.println("currency = " + mdActivity.get("CURRENCY"));
//                System.out.println("referenceNumber = " + mdActivity.get("REFERENCENUMBER"));
//                System.out.println("referenceType = " + mdActivity.get("REFERENCETYPE"));
//                System.out.println("activityType = " + mdActivity.get("ACTIVITYTYPE"));
//                System.out.println("");
//            }
//
//            Map<String, ?> total = marginalDepositFinder.getCreditsTotalAmountByCurrency((String)md3.get("SETTLEMENTACCOUNTNUMBER"), Currency.getInstance("PHP").getCurrencyCode());
//
//            System.out.println("documentNumber = " + total.get("SETTLEMENTACCOUNTNUMBER"));
//            System.out.println("currency = " + total.get("CURRENCY"));
//            System.out.println("total = " + total.get("TOTAL"));
//        }
//    }
}

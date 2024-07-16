package com.ucpb.tfs.application.domain.settlementaccount;

import com.ucpb.tfs.domain.settlementaccount.AccountsPayable;
import com.ucpb.tfs.domain.settlementaccount.AccountsPayableRepository;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber;
import com.ucpb.tfs.domain.settlementaccount.activity.Activity;
import com.ucpb.tfs.domain.settlementaccount.activity.ApActivity;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

/**
 * User: Val
 * Date: 7/22/12
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestSettlementAccountContext.xml")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestAccountsPayable {

    @Inject
    AccountsPayableRepository accountsPayableRepository;

    @Test
    public void AddAccountsPayable() throws Exception {

//        AccountsPayable ap = new AccountsPayable(new SettlementAccountNumber("1"));
//
//        BigDecimal setup = new BigDecimal("20000.00");
//
//        ap.credit(setup, Currency.getInstance("PHP"), ReferenceType.OUTSIDE_SETUP_AP, "bbb-ccc-aaa", "07/21/2012 9:00:00", "XXXXXX", "Setup AP");
//
//        accountsPayableRepository.saveOrUpdate(ap);
//
//        AccountsPayable ap2 = accountsPayableRepository.load(ap.getSettlementAccountNumber());
//
//        // System.out.println("OB = " + md2.getOutstandingBalance(Currency.getInstance("PHP")));
//
////        Set<Activity> activities = ap2.getActivities();
//        Set<ApActivity> activities = ap2.getActivities();
//        System.out.println("count = " + activities.size());
//        for(ApActivity activity : activities) {
//            ApActivity apActivity = (ApActivity)activity;
//            System.out.println("apActivity.getActivityType() = " + apActivity.getActivityType());
//            System.out.println("apActivity.getAmount() = " + apActivity.getAmount());
//            System.out.println("apActivity.getCurrency() = " + apActivity.getCurrency());
//            System.out.println("apActivity.getBookingDate() = " + apActivity.getBookingDate());
//            System.out.println("apActivity.getApplicationReferenceNumber() = " + apActivity.getApplicationReferenceNumber());
//            System.out.println("apActivity.getNatureOfTransaction() = " + apActivity.getNatureOfTransaction());
//        }
    }
}

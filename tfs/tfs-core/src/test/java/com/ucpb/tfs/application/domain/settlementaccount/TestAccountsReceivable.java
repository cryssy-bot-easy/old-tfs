package com.ucpb.tfs.application.domain.settlementaccount;

import com.ucpb.tfs.domain.settlementaccount.AccountsReceivable;
import com.ucpb.tfs.domain.settlementaccount.AccountsReceivableRepository;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber;
import com.ucpb.tfs.domain.settlementaccount.activity.Activity;
import com.ucpb.tfs.domain.settlementaccount.activity.ArActivity;
import com.ucpb.tfs.domain.settlementaccount.enumTypes.ReferenceType;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TestAccountsReceivable {

//    @Inject
//    AccountsReceivableRepository accountsReceivableRepository;
//
//    @Test
//    public void AddAccountsReceivable() throws Exception {
//
//        AccountsReceivable ar = new AccountsReceivable(new SettlementAccountNumber("1"));
//
//        BigDecimal setup = new BigDecimal("30000.00");
//
//        ar.credit(setup, Currency.getInstance("PHP"), ReferenceType.OUTSIDE_SETUP_AR, "aaa-bbb-ccc", "7/22/2012 16:00:00", "Setup AR");
//
//        accountsReceivableRepository.saveOrUpdate(ar);
//
//        AccountsReceivable ar2 = accountsReceivableRepository.load(ar.getSettlementAccountNumber());
//
//        // System.out.println("OB = " + md2.getOutstandingBalance(Currency.getInstance("PHP")));
//
//        Set<ArActivity> activities = ar2.getActivities();
//        System.out.println("count = " + activities.size());
//        for(ArActivity activity : activities) {
//            ArActivity arActivity = (ArActivity)activity;
//            System.out.println("arActivity.getActivityType() = " + arActivity.getActivityType());
//            System.out.println("arActivity.getAmount() = " + arActivity.getAmount());
//            System.out.println("arActivity.getCurrency() = " + arActivity.getCurrency());
//        }
//    }
}

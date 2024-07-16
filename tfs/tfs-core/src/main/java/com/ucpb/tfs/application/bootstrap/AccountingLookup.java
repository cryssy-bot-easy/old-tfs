package com.ucpb.tfs.application.bootstrap;

import com.ucpb.tfs.domain.accounting.*;
import com.ucpb.tfs.domain.accounting.enumTypes.BookCurrency;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.ProductReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: giancarlo
 * Date: 9/29/12
 * Time: 10:14 PM
 */
public class AccountingLookup implements ApplicationListener<ContextRefreshedEvent> {

    @Inject
    private TransactionTemplate tt;

    @Autowired
    ProductReferenceRepository productReferenceRepository;

    @Autowired
    AccountingEventRepository accountingEventsRepository;

    @Autowired
    AccountingEntryRepository accountingEntryRepository;

    @Autowired
    AccountingVariablesRepository accountingVariablesRepository;

    private Map<String, Object> accountingAmountsDefaultsRegistry;

    private Map<String, Object> accountingCodesDefaultsRegistry;

    List<String> original_settlementCurrencyList;

    // this will hold our accounting Entries
    //Level 1 ProductId //Product Reference
    //Level 2 Service Type //Service Type
    //Level 3 Removed This layer -->> Accounting Event Transaction Type
    //Level 4 FXLC Original Currency - Settlement Currency

    //Level 4
    // USD-USD
    // USD-PHP
    // THIRD-THIRD
    // THIRD-USD
    // THIRD-PHP
    // PHP-PHP
    private Map<String,//Level 1 ProductId //Product Reference
            Map<String,//Level 2 Service Type //Service Type
                    Map<String,//Level 4 FXLC Original Currency - Settlement Currency
                            List<AccountingEntry>>>> accountingServiceEntryRegistry;//TODO: Delete if not used

    public AccountingLookup() {
    }

    public AccountingLookup(PlatformTransactionManager tm) {
        tt.setTransactionManager(tm);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        tt.execute(new TransactionCallbackWithoutResult() {
            //            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    initialize();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    status.setRollbackOnly();
                }
            }
        });

    }

    public void initialize() {

        // instantiate a new hashmap
        // the outer map contains the product ids, the inner contains the services
        //Level 1 ProductId //Product Reference
        //Level 2 Service Type //Service Type
        //Removed this level --> Level 3 Accounting Event Transaction Type
        //Level 4 FXLC Original Currency - Settlement Currency

        //Level 4
        // USD-USD
        // USD-PHP
        // THIRD-THIRD
        // THIRD-USD
        // THIRD-PHP
        // PHP-PHP
        accountingServiceEntryRegistry = new HashMap<
                String,//Product Reference
                Map<String, //Service Type
                        Map<String, //FXLC Original Currency - Settlement Currency
                                List<AccountingEntry>>>>();//TODO: Delete if not used


        System.out.println("clearing Accounting Events");
        accountingEventsRepository.clear();

        System.out.println("bootstrapping Accounting entries");
        System.out.println("initialize Accounting Entries");
        initializeAccountingEntryLookup();//TODO: Delete if not used
        initializeAccountingAmountsDefaultsRegistry();

    }

    //TODO: Delete if not used
    private void initializeAccountingEntryLookup() {

        List<String> currencyTypesList = new ArrayList<String>();
        currencyTypesList.add("PHP");
        currencyTypesList.add("USD");
        currencyTypesList.add("THIRD");

        //initializeFX_LC_Opening_Accounting(currencyTypesList);
        //Level 1 ProductId
        //Level 2 Service Type
        //Removed this level --> Level 3 Accounting Event Transaction Type
        //Level 4 FXLC Original Currency - Settlement Currency


        //Level 4
        // USD-USD
        // USD-PHP
        // THIRD-THIRD
        // THIRD-USD
        // THIRD-PHP
        // PHP-PHP


    }

    //TODO: Delete if not used
    public List<AccountingEntry> getAccountingEntriesForService(ProductId productId, ServiceType serviceType, String OriginalCurrencyToSettlementCurrency) {

        System.out.println("Product Id To Search:" + productId.toString());

//        accountingServiceEntryRegistry = new HashMap<
//                String,//Product Reference
//                Map<String, //Service Type
//                        Map<String, //FXLC Original Currency - Settlement Currency
//                                List<AccountingEntry>>>>();

        Map<String, //Service Type
                Map<String, List<AccountingEntry>>> servicesMap = accountingServiceEntryRegistry.get(productId.toString());

        System.out.println("----------------------------------------------------------------------------");
        for (String s : servicesMap.keySet()) {
            System.out.println("Service list item key:" + s);
            System.out.println("Service list item value:" + servicesMap.get(s));
        }

        // check if the product is in our registry,
        if (servicesMap != null) {
            // return whatever map of Original Currency to Settlement Currency we need for this service type
            Map<String, List<AccountingEntry>> currencyToAccountingEntryListMap = servicesMap.get(serviceType.toString());

            if (currencyToAccountingEntryListMap != null) {
                return currencyToAccountingEntryListMap.get(OriginalCurrencyToSettlementCurrency);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //TODO: Delete if not used
    public List<AccountingEntry> getAccountingEntriesForProductServiceCombination(ProductId productId, ServiceType serviceType, BookCurrency originalCurrency, BookCurrency settlementCurrency) {
        List<AccountingEntry> accountingEntryList = accountingEntryRepository.getEntries(productId, serviceType, originalCurrency, settlementCurrency);

        if (accountingEntryList != null && accountingEntryList.size() > 0) {
            return accountingEntryList;
        } else {
            return null;
        }

    }

    public Map<String, List<AccountingEntry>> getAccountingEntriesForService(ProductId productId, ServiceType serviceType) {

        System.out.println("Product Id To Search:" + productId.toString());

        Map<String, //Service Type
                Map<String, List<AccountingEntry>>> servicesMap = accountingServiceEntryRegistry.get(productId.toString());

        System.out.println("----------------------------------------------------------------------------");
        for (String s : servicesMap.keySet()) {
            System.out.println("Service list item key:" + s);
            System.out.println("Service list item value:" + servicesMap.get(s));
        }

        // check if the product is in our registry,
        if (servicesMap != null) {
            // return whatever map of Original Currency to Settlement Currency we need for this service type
            Map<String, List<AccountingEntry>> currencyToAccountingEntryListMap = servicesMap.get(serviceType.toString());
            if (currencyToAccountingEntryListMap != null) {
                return currencyToAccountingEntryListMap;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Map<String, Object> getDefaultValuesForServiceMap() {
    	
    	System.out.println("=====getDefaultValuesForServiceMap=====");

        HashMap<String, Object> temp = new HashMap<String, Object>();

//        for (String keyed : accountingAmountsDefaultsRegistry.keySet()) {
//            Object ob = accountingAmountsDefaultsRegistry.get(keyed);
//            temp.put(keyed, ob);
//        }


        List<AccountingVariable> holder = accountingVariablesRepository.list();
//        System.out.println("getDefaultValuesForServiceMap:::::::::::" + holder.size());
        if (holder != null && !holder.isEmpty()) {
            for (AccountingVariable accountingVariable : holder) {
                temp.put(accountingVariable.getCode(), BigDecimal.ZERO);
            }
        }
//        System.out.println("getDefaultValuesForServiceMap:::::::::::" + temp.size());


        return temp;
    }


    private void initializeAccountingAmountsDefaultsRegistry() {

        //TODO Move this to query from a database table
        accountingAmountsDefaultsRegistry = new HashMap<String, Object>();

        accountingAmountsDefaultsRegistry.put("lcAmountPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("lcAmountUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("lcAmountTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("CASAproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("CASAproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("CASAproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("CheckproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("CheckproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("CheckproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("IBTtotalPaymentAmountPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("IBTtotalPaymentAmountUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("IBTtotalPaymentAmountTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("APRESOTHERSproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APRESOTHERSproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APRESOTHERSproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("APRemmittanceproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APRemmittanceproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APRemmittanceproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("ARproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("ARproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("ARproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("cableFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("cableFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("cableFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("cilexFeeGrossPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("cilexFeeGrossUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("cilexFeeGrossTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("cilexFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("cilexFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("cilexFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("docStampsFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("docStampsFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("docStampsFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("suppliesFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("suppliesFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("suppliesFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("advisingFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("advisingFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("advisingFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("bankCommissionCWTPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bankCommissionCWTUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bankCommissionCWTTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("bankCommissionGrossPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bankCommissionGrossUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bankCommissionGrossTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("bankCommissionPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bankCommissionUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bankCommissionTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("commitmentFeeGrossPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("commitmentFeeGrossUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("commitmentFeeGrossTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("commitmentFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("commitmentFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("commitmentFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("confirmingFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("confirmingFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("confirmingFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("suppliesFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("suppliesFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("suppliesFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("cancellationFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("cancellationFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("cancellationFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("remittanceFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("remittanceFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("remittanceFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("notarialFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("notarialFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("notarialFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("bookingCommissionFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bookingCommissionFeeUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bookingCommissionFeeTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("bookingCommissionFeeGrossPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bookingCommissionFeeGrossUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bookingCommissionFeeGrossTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("chargesAmountCWTPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("chargesAmountCWTUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("chargesAmountCWTTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("chargesCWTPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("chargesCWTUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("chargesCWTTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("productPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("productPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("productPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("APproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("RemittanceproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("RemittanceproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("RemittanceproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("DIB_DTRproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("DIB_DTRproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("DIB_DTRproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("APRESOTHERSproductPaymentTotalExcessPaymentPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APRESOTHERSproductPaymentTotalExcessPaymentUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APRESOTHERSproductPaymentTotalExcessPaymentTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("APRESOTHERSproductPaymentTotalExcessPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APRESOTHERSproductPaymentTotalExcessUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("APRESOTHERSproductPaymentTotalExcessTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("CASAProductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("CASAProductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("CASAProductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("chargesAmountPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("chargesAmountUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("chargesAmountTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("IBTproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("IBTproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("IBTproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("bspCommissionGrossPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bspCommissionGrossUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("bspCommissionGrossTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("CASAsettlementTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("CASAsettlementTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("CASAsettlementTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("MCsettlementTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("MCsettlementTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("MCsettlementTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("PDDTSsettlementTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("PDDTSsettlementTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("PDDTSsettlementTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("DBPDTRLoanproductPaymentTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("DBPDTRLoanproductPaymentTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("DBPDTRLoanproductPaymentTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("negoAmountPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("negoAmountUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("negoAmountTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("negotiationAmountPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("negotiationAmountUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("negotiationAmountTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("DUEFromCBsettlementTotalPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("DUEFromCBsettlementTotalUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("DUEFromCBsettlementTotalTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("fxProfitPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("fxProfitUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("fxProfitTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("fxLossPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("fxLossUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("fxLossTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("fxProfitMiscAssetPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("fxLossMiscAssetPHP", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("otherExportFeePHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("advisingExportFeePHP", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("amendmentAmountPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("amendmentAmountUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("amendmentAmountTHIRD", BigDecimal.ZERO);

        accountingAmountsDefaultsRegistry.put("remittanceFeeSpecialPHP", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("remittanceFeeSpecialUSD", BigDecimal.ZERO);
        accountingAmountsDefaultsRegistry.put("remittanceFeeSpecialTHIRD", BigDecimal.ZERO);

    }

}

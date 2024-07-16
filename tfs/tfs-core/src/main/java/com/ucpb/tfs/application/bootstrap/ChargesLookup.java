package com.ucpb.tfs.application.bootstrap;

import com.ucpb.tfs.domain.reference.*;
import com.ucpb.tfs.domain.service.enumTypes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Component
public class ChargesLookup implements ApplicationListener<ContextRefreshedEvent> {

    @Inject
    private TransactionTemplate tt;

    @Autowired
    ProductReferenceRepository productReferenceRepository;

    @Autowired
    TradeServiceChargeReferenceRepository tradeServiceChargeReferenceRepository;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    ChargeDefaultsReferenceRepository chargeDefaultsReferenceRepository;

    // this will hold our
    private Map<String, Map<String, List<TradeServiceChargeReference>>> tradeServiceChargesRegistry;

    public ChargesLookup() {
    }

    private Map<String, Object> tradeServiceChargesDefaultsRegistry;

    public ChargesLookup(PlatformTransactionManager tm) {
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
        tradeServiceChargesRegistry = new HashMap<String, Map<String, List<TradeServiceChargeReference>>>();

        // initialize Product Repository if it is not already there
        if (productReferenceRepository.getCount() == 0) {
            System.out.println("Errors no product reference found");
        }

        //Commenting out fully being pulled from DB
        System.out.println("bootstrapping charges");
        System.out.println("clearing Trade Service Charge");
        tradeServiceChargeReferenceRepository.clear();
        initializeTradeServiceChargesDefaultsRegistry();
        initializeTradeServiceChargesDefaultsRegistryTable();
        initializeChargesLookup();
        initializeTradeServiceChargesRegistryFXLC_All();
        initializeTradeServiceChargesRegistryDMLC_All();
        initializeTradeServiceChargesRegistryFXINDEMNITY_All();
        initializeTradeServiceChargesRegistryFX_Non_LC_All();
        initializeTradeServiceChargesRegistryDM_Non_LC_All();
        initializeTradeServiceChargesRegistryFXUA_All();
        initializeTradeServiceChargesRegistryDMUA_All();
        initializeTradeServiceChargesRegistryIMPORT_ADVANCE_PAYMENT_All();
//        initializeTradeServiceChargesRegistryIMPORT_ADVANCE_REFUND_All();
        initializeTradeServiceChargesRegistryEXPORT_ADVANCE_PAYMENT_All();
        initializeTradeServiceChargesRegistryEXPORT_ADVANCE_REFUND_All();
        initializeTradeServiceChargesRegistryEXPORT_LC_ADVISING_FIRST_All();
        initializeTradeServiceChargesRegistryEXPORT_LC_ADVISING_SECOND_All();
        initializeTradeServiceChargesRegistryFX_BC_All();
        initializeTradeServiceChargesRegistryFX_BP_All();
        initializeTradeServiceChargesRegistryDM_BC_All();
        initializeTradeServiceChargesRegistryDM_BP_All();
    }

    private void initializeTradeServiceChargesRegistryFXLC_All() {


        // populate the charges for FX LC cash
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;
        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, DocumentSubType2.SIGHT);

        for (ServiceType serviceType : ServiceType.values()) {


            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);


        // populate the charges for FX LC STANDBY
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {

                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);


        // populate the charges for FX LC REGULAR SIGHT
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.SIGHT);
        chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();
        for (ServiceType serviceType : ServiceType.values()) {
            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);


        // populate the charges for FX LC REGULAR USANCE
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }

    private void initializeTradeServiceChargesRegistryDMLC_All() {


        // populate the charges for DM LC cash
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;
        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.CASH, DocumentSubType2.SIGHT);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);


        // populate the charges for DM LC STANDBY
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        for (ServiceType serviceType : ServiceType.values()) {
            //System.out.println("Service Type:" + serviceType.toString());

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);


        // populate the charges for DM LC REGULAR SIGHT
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.SIGHT);
        chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);


        // populate the charges for DM LC REGULAR USANCE
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }

    private void initializeTradeServiceChargesRegistryFXINDEMNITY_All() {
        // populate the charges for FX LC cash
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;
        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.INDEMNITY, DocumentType.FOREIGN, null, null);

        for (ServiceType serviceType : ServiceType.values()) {


            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);


        productRef = productReferenceRepository.find(DocumentClass.INDEMNITY, DocumentType.FOREIGN, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);

        for (ServiceType serviceType : ServiceType.values()) {


            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }

    private void initializeTradeServiceChargesRegistryFXUA_All() {


        // populate the charges for FX LC cash
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;
        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.UA, DocumentType.FOREIGN, null, null);

        for (ServiceType serviceType : ServiceType.values()) {


            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);

    }

    private void initializeTradeServiceChargesRegistryFX_Non_LC_All() {
        // populate the charges for FX LC cash
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;
        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.DA, DocumentType.FOREIGN, null, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);

        productRef = productReferenceRepository.find(DocumentClass.DP, DocumentType.FOREIGN, null, null);

        for (ServiceType serviceType : ServiceType.values()) {


            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);

        productRef = productReferenceRepository.find(DocumentClass.DP, DocumentType.FOREIGN, null, null);

        for (ServiceType serviceType : ServiceType.values()) {


            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);

        productRef = productReferenceRepository.find(DocumentClass.OA, DocumentType.FOREIGN, null, null);

        for (ServiceType serviceType : ServiceType.values()) {


            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);

        productRef = productReferenceRepository.find(DocumentClass.DR, DocumentType.FOREIGN, null, null);

        for (ServiceType serviceType : ServiceType.values()) {


            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);


    }

    private void initializeTradeServiceChargesRegistryDM_Non_LC_All() {
        // populate the charges for DM NONLC
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;
        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();
        ProductReference productRef = productReferenceRepository.find(DocumentClass.DP, DocumentType.DOMESTIC, null, null);

        for (ServiceType serviceType : ServiceType.values()) {
            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);

    }

    private void initializeTradeServiceChargesRegistryDMUA_All() {


        // populate the charges for FX LC cash
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;
        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.UA, DocumentType.DOMESTIC, null, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);

    }

    private void initializeTradeServiceChargesRegistryIMPORT_ADVANCE_PAYMENT_All() {

        // populate the charges for IMPORT ADVANCE
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;

        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.IMPORT_ADVANCE, null, null, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }
    
//    private void initializeTradeServiceChargesRegistryIMPORT_ADVANCE_REFUND_All() {
//
//        // populate the charges for IMPORT ADVANCE
//        // todo: loop through our products and transactions
//        // get the product reference first, then loop through the service types
//        List<TradeServiceChargeReference> charges;
//
//        // create the inner map (with services and corresponding charges)
//        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();
//
//        ProductReference productRef = productReferenceRepository.find(DocumentClass.IMPORT_ADVANCE, null, null, null);
//
//        for (ServiceType serviceType : ServiceType.values()) {
//
//            // get the charges for this product
//            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
//            if (charges != null && !charges.isEmpty()) {
//                for (TradeServiceChargeReference charge : charges) {
//                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
//                }
//            }
//            if (charges != null && !charges.isEmpty()) {
//                chargesForService.put(serviceType.toString(), charges);
//            }
//        }
//        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
//    }

    private void initializeTradeServiceChargesRegistryEXPORT_ADVANCE_PAYMENT_All(){
        // populate the charges for EXPORT ADVANCE
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;

        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.EXPORT_ADVANCE, null, null, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }
    
    private void initializeTradeServiceChargesRegistryEXPORT_ADVANCE_REFUND_All(){
        // populate the charges for IMPORT ADVANCE
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;

        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.EXPORT_ADVANCE, null, null, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }
    
    private void initializeTradeServiceChargesRegistryEXPORT_LC_ADVISING_FIRST_All() {

        // populate the charges for EXPORT LC ADVISING
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;

        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.EXPORT_ADVISING, null, DocumentSubType1.FIRST_ADVISING, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }

    private void initializeTradeServiceChargesRegistryEXPORT_LC_ADVISING_SECOND_All() {

        // populate the charges for EXPORT LC ADVISING
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;

        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.EXPORT_ADVISING, null, DocumentSubType1.SECOND_ADVISING, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }

    private void initializeTradeServiceChargesRegistryFX_BC_All() {

        // populate the charges for EBC
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;

        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.BC, DocumentType.FOREIGN, null, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }

    private void initializeTradeServiceChargesRegistryFX_BP_All() {

        // populate the charges for EBC
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;

        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.BP, DocumentType.FOREIGN, null, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }

    private void initializeTradeServiceChargesRegistryDM_BC_All() {

        // populate the charges for EBC
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;

        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.BC, DocumentType.DOMESTIC, null, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }

    private void initializeTradeServiceChargesRegistryDM_BP_All() {

        // populate the charges for EBC
        // todo: loop through our products and transactions
        // get the product reference first, then loop through the service types
        List<TradeServiceChargeReference> charges;

        // create the inner map (with services and corresponding charges)
        Map<String, List<TradeServiceChargeReference>> chargesForService = new HashMap<String, List<TradeServiceChargeReference>>();

        ProductReference productRef = productReferenceRepository.find(DocumentClass.BP, DocumentType.DOMESTIC, null, null);

        for (ServiceType serviceType : ServiceType.values()) {

            // get the charges for this product
            charges = tradeServiceChargeReferenceRepository.getCharges(productRef.getProductId(), serviceType);
            if (charges != null && !charges.isEmpty()) {
                for (TradeServiceChargeReference charge : charges) {
                    System.out.println(productRef.getProductId().toString() + "_" + serviceType.toString() + "_" + charge.toString());
                }
            }
            if (charges != null && !charges.isEmpty()) {
                chargesForService.put(serviceType.toString(), charges);
            }
        }
        tradeServiceChargesRegistry.put(productRef.getProductId().toString(), chargesForService);
    }

    private void initializeShared_MD_AP_AR() {

    }

    private void initializeChargesLookup() {
        System.out.println("initializeChargesLookup");
        Map<String, ChargeId> chargeIds = new HashMap<String, ChargeId>();
        List<Charge> tChargeList = chargeRepository.getChargeIdList();
        for (Charge charge : tChargeList) {
            System.out.println("charge.getChargeId().toString():" + charge.getChargeId().toString());
            chargeIds.put(charge.getChargeId().toString(), charge.getChargeId());
        }

        ProductReference productRef = null;
        System.out.println("initializeFX_LC_Opening_Charges");
        initializeFX_LC_Opening_Charges(chargeIds);
        System.out.println("initializeFX_LC_Adjustment_Charges");
        initializeFX_LC_Adjustment_Charges(chargeIds);
        System.out.println("initializeFX_LC_Amendment_Charges");
        initializeFX_LC_Amendment_Charges(chargeIds);
        System.out.println("initializeFX_LC_Negotiation_Charges");
        initializeFX_LC_Negotiation_Charges(chargeIds);
        System.out.println("initializeFX_UA_Settlement_Charges");
        initializeFX_UA_Settlement_Charges(chargeIds);
        System.out.println("initializeFX_UA_LoanMaturityAdjustment_Charges");
        initializeFX_UA_LoanMaturityAdjustment_Charges(chargeIds);
        System.out.println("initializeFX_INDEMNITY_Cancellation_Charges");
        initializeFX_INDEMNITY_Cancellation_Charges(chargeIds);
        System.out.println("initializeFX_INDEMNITY_Issuance_Charges");
        initializeFX_INDEMNITY_Issuance_Charges(chargeIds);
        System.out.println("initializeFX_NON_LC_Settlement_Charges");
        initializeFX_NON_LC_Settlement_Charges(chargeIds);


        System.out.println("initializeDM_LC_Opening_Charges");
        initializeDM_LC_Opening_Charges(chargeIds);
        System.out.println("initializeDM_LC_Adjustment_Charges");
        initializeDM_LC_Adjustment_Charges(chargeIds);
        System.out.println("initializeDM_LC_Amendment_Charges");
        initializeDM_LC_Amendment_Charges(chargeIds);
        System.out.println("initializeDM_LC_Negotiation_Charges");
        initializeDM_LC_Negotiation_Charges(chargeIds);
        System.out.println("initializeDM_UA_Settlement_Charges");
        initializeDM_UA_Settlement_Charges(chargeIds);
        System.out.println("initializeDM_UA_Adjustment_Charges");
        initializeDM_UA_Adjustment_Charges(chargeIds);
        System.out.println("initializeDM_NON_LC_Settlement_Charges");
        initializeDM_NON_LC_Settlement_Charges(chargeIds);

        // Import Advance
        System.out.println("initializeIMPORT_ADVANCE_PAYMENT_Charges");
        initializeIMPORT_ADVANCE_PAYMENT_Charges(chargeIds);
        System.out.println("initializeIMPORT_ADVANCE_REFUND_Charges");
        initializeIMPORT_ADVANCE_REFUND_Charges(chargeIds);

        // Export Advance
        System.out.println("initializeEXPORT_ADVANCE_PAYMENT_Charges");
        initializeEXPORT_ADVANCE_PAYMENT_Charges(chargeIds);
        System.out.println("initializeEXPORT_ADVANCE_REFUND_Charges");
        initializeEXPORT_ADVANCE_REFUND_Charges(chargeIds);
        
        // Export LC Advising
        System.out.println("initializeEXPORT_LC_ADVISING_FIRST_Charges");
        initializeEXPORT_LC_ADVISING_FIRST_Charges(chargeIds);
        System.out.println("initializeEXPORT_LC_ADVISING_SECOND_Charges");
        initializeEXPORT_LC_ADVISING_SECOND_Charges(chargeIds);

        // Bills Payment
        System.out.println("initializeFX_BP_Charges");
        initializeFX_BP_Charges(chargeIds);
        System.out.println("initializeDM_BP_Charges");
        initializeDM_BP_Charges(chargeIds);

        // Bills Collection
        System.out.println("initializeFX_BC_Charges");
        initializeFX_BC_Charges(chargeIds);
        System.out.println("initializeDM_BC_Charges");
        initializeDM_BC_Charges(chargeIds);

    }

    private void initializeFX_LC_Opening_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

        // insert charges for Cash FXLC Opening
        // get product reference for FX LC Cash
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, DocumentSubType2.SIGHT);
//        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(expiryDate);\n" +
//                        "System.out.println(bankCommissionNumerator);\n" +
//                        "System.out.println(bankCommissionDenominator);\n" +
//                        "System.out.println(bankCommissionPercentage);\n" +
//                        "System.out.println(amount);\n" +
//                        "CalculatorUtils.getBankCommission( expiryDate, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, amount, cwtPercentage, cwtFlag )"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getDocStamps_FXLC_Opening( expiryDate, amount, docStampsAmountPer, docStampsRoundToThisNumber )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(default_Opening_FXLC_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_Opening_FXLC_cableFee) "
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_suppliesFee:\"+default_suppliesFee);\n" +
//                        " CalculatorUtils.getSuppliesFee(default_suppliesFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(expiryDate);\n" +
//                        "System.out.println(productChargeAmountNetOfPesoAmountPaid);\n" +
//                        "System.out.println(cilexNumerator);\n" +
//                        "System.out.println(cilexDenominator);\n" +
//                        "System.out.println(cilexDenominator);\n" +
//                        "System.out.println(cilexPercentage);\n" +
//                        "System.out.println(usdToPHPSpecialRate);\n" +
//                        "CalculatorUtils.getCilex( expiryDate, productChargeAmountNetOfPesoAmountPaid, cilexNumerator, cilexDenominator, cilexPercentage, usdToPHPSpecialRate, cwtFlag, cwtPercentage, cilexDollarMinimum)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-ADVISING"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "System.out.println(\"advisingFee:\"+advisingFee);\n" +
//                        "System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "System.out.println(\"default_advisingFeeMinimum:\"+default_advisingFeeMinimum);\n" +
//                        "CalculatorUtils.getAdvisingFee_FXLC_Opening( usdToPHPSpecialRate, advisingFee, advanceCorresChargesFlag, default_advisingFeeMinimum )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-CONFIRMING"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(expiryDate);\n" +
//                        "System.out.println(\"confirmingFeeNumerator:\"+confirmingFeeNumerator);\n" +
//                        "System.out.println(\"confirmingFeeDenominator:\"+confirmingFeeDenominator);\n" +
//                        "System.out.println(\"confirmingFeePercentage:\"+confirmingFeePercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "System.out.println(\"confirmationInstructionsFlag:\"+confirmationInstructionsFlag);\n" +
//                        "System.out.println(\"default_confirmingFeeMinimum:\"+default_confirmingFeeMinimum);\n" +
//                        "CalculatorUtils.getConfirmingFee_FXLC_Opening( expiryDate, confirmingFeeNumerator, confirmingFeeDenominator, confirmingFeePercentage, amount, advanceCorresChargesFlag, confirmationInstructionsFlag, usdToPHPSpecialRate, default_confirmingFeeMinimum );"
        ));

        // insert charges for Cash FXLC Opening
        // get product reference for FX LC Cash
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"bankCommissionNumerator:\"+bankCommissionNumerator);\n" +
//                        "System.out.println(\"bankCommissionDenominator:\"+bankCommissionDenominator);\n" +
//                        "System.out.println(\"bankCommissionPercentage:\"+bankCommissionPercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getBankCommission( expiryDate, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, amount, cwtPercentage, cwtFlag );"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"commitmentFeeNumerator:\"+commitmentFeeNumerator);\n" +
//                        "System.out.println(\"commitmentFeeDenominator:\"+commitmentFeeDenominator);\n" +
//                        "System.out.println(\"commitmentFeePercentage:\"+commitmentFeePercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getCommitmentFee_FXLC_Opening(amount, usancePeriod, expiryDate, documentSubType1, documentSubType2, commitmentFeeNumerator,  commitmentFeeDenominator, commitmentFeePercentage, cwtFlag, cwtPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getDocStamps_FXLC_Opening( expiryDate, amount, docStampsAmountPer, docStampsRoundToThisNumber )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(default_Opening_FXLC_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_Opening_FXLC_cableFee) "
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_suppliesFee:\"+default_suppliesFee);\n" +
//                        " CalculatorUtils.getSuppliesFee(default_suppliesFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-ADVISING"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "System.out.println(\"advisingFee:\"+advisingFee);\n" +
//                        "System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "System.out.println(\"default_advisingFeeMinimum:\"+default_advisingFeeMinimum);\n" +
//                        "CalculatorUtils.getAdvisingFee_FXLC_Opening( usdToPHPSpecialRate, advisingFee, advanceCorresChargesFlag, default_advisingFeeMinimum )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-CONFIRMING"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"confirmingFeeNumerator:\"+confirmingFeeNumerator);\n" +
//                        "System.out.println(\"confirmingFeeDenominator:\"+confirmingFeeDenominator);\n" +
//                        "System.out.println(\"confirmingFeePercentage:\"+confirmingFeePercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "System.out.println(\"confirmationInstructionsFlag:\"+confirmationInstructionsFlag);\n" +
//                        "System.out.println(\"default_confirmingFeeMinimum:\"+default_confirmingFeeMinimum);\n" +
//                        "CalculatorUtils.getConfirmingFee_FXLC_Opening( expiryDate, confirmingFeeNumerator, confirmingFeeDenominator, confirmingFeePercentage, amount, advanceCorresChargesFlag, confirmationInstructionsFlag, usdToPHPSpecialRate, default_confirmingFeeMinimum );"
        ));

        // insert charges for Regular Sight FXLC Opening
        // get product reference for FX LC Regular Sight
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"bankCommissionNumerator:\"+bankCommissionNumerator);\n" +
//                        "System.out.println(\"bankCommissionDenominator:\"+bankCommissionDenominator);\n" +
//                        "System.out.println(\"bankCommissionPercentage:\"+bankCommissionPercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getBankCommission( expiryDate, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, amount, cwtPercentage, cwtFlag )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getDocStamps_FXLC_Opening( expiryDate, amount, docStampsAmountPer, docStampsRoundToThisNumber )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(default_Opening_FXLC_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_Opening_FXLC_cableFee) "
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_suppliesFee:\"+default_suppliesFee);\n" +
//                        " CalculatorUtils.getSuppliesFee(default_suppliesFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-ADVISING"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "CalculatorUtils.getAdvisingFee_FXLC_Opening( usdToPHPSpecialRate, advisingFee, advanceCorresChargesFlag, default_advisingFeeMinimum )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-CONFIRMING"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"confirmingFeeNumerator:\"+confirmingFeeNumerator);\n" +
//                        "System.out.println(\"confirmingFeeDenominator:\"+confirmingFeeDenominator);\n" +
//                        "System.out.println(\"confirmingFeePercentage:\"+confirmingFeePercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "System.out.println(\"confirmationInstructionsFlag:\"+confirmationInstructionsFlag);\n" +
//                        "System.out.println(\"default_confirmingFeeMinimum:\"+default_confirmingFeeMinimum);\n" +
//                "CalculatorUtils.getConfirmingFee_FXLC_Opening( expiryDate, confirmingFeeNumerator, confirmingFeeDenominator, confirmingFeePercentage, amount, advanceCorresChargesFlag, confirmationInstructionsFlag, usdToPHPSpecialRate, default_confirmingFeeMinimum );"
        ));

        // insert charges for Regular USANCE FXLC Opening
        // get product reference for FX LC Regular USANCE
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"bankCommissionNumerator:\"+bankCommissionNumerator);\n" +
//                        "System.out.println(\"bankCommissionDenominator:\"+bankCommissionDenominator);\n" +
//                        "System.out.println(\"bankCommissionPercentage:\"+bankCommissionPercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getBankCommission( expiryDate, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, amount, cwtPercentage, cwtFlag )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usancePeriod:\"+usancePeriod);\n" +
//                        "System.out.println(\"commitmentFeeNumerator:\"+commitmentFeeNumerator);\n" +
//                        "System.out.println(\"commitmentFeeDenominator:\"+commitmentFeeDenominator);\n" +
//                        "System.out.println(\"commitmentFeePercentage:\"+commitmentFeePercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getCommitmentFee_FXLC_Opening(amount, usancePeriod, expiryDate, documentSubType1, documentSubType2, commitmentFeeNumerator,  commitmentFeeDenominator, commitmentFeePercentage, cwtFlag, cwtPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getDocStamps_FXLC_Opening( expiryDate, amount, docStampsAmountPer, docStampsRoundToThisNumber )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(default_Opening_FXLC_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_Opening_FXLC_cableFee) "
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_suppliesFee:\"+default_suppliesFee);\n" +
//                        " CalculatorUtils.getSuppliesFee(default_suppliesFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-ADVISING"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "System.out.println(\"advisingFee:\"+advisingFee);\n" +
//                        "System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "System.out.println(\"default_advisingFeeMinimum:\"+default_advisingFeeMinimum);\n" +
//                        "CalculatorUtils.getAdvisingFee_FXLC_Opening( usdToPHPSpecialRate, advisingFee, advanceCorresChargesFlag, default_advisingFeeMinimum )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-CONFIRMING"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"confirmingFeeNumerator:\"+confirmingFeeNumerator);\n" +
//                        "System.out.println(\"confirmingFeeDenominator:\"+confirmingFeeDenominator);\n" +
//                        "System.out.println(\"confirmingFeePercentage:\"+confirmingFeePercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "System.out.println(\"confirmationInstructionsFlag:\"+confirmationInstructionsFlag);\n" +
//                        "System.out.println(\"default_confirmingFeeMinimum:\"+default_confirmingFeeMinimum);\n" +
//                        "CalculatorUtils.getConfirmingFee_FXLC_Opening( expiryDate, confirmingFeeNumerator, confirmingFeeDenominator, confirmingFeePercentage, amount, advanceCorresChargesFlag, confirmationInstructionsFlag, usdToPHPSpecialRate, default_confirmingFeeMinimum );"
        ));
    }

    private void initializeFX_LC_Adjustment_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;
        // insert charges for CASH FXLC Adjustment
        // get product reference for FX LC CASH
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.ADJUSTMENT,
                "BigDecimal.TEN;"
//                "System.out.println(expiryDate);\n" +
//                        "System.out.println(usdToPHPSpecialRate);\n" +
//                        "System.out.println(cilexNumerator);\n" +
//                        "System.out.println(cilexDenominator);\n" +
//                        "System.out.println(cilexPercentage);\n" +
//                        "System.out.println(cashAmount);\n" +
//                        "CalculatorUtils.getCilex( expiryDate, productChargeAmountNetOfPesoAmountPaid, cilexNumerator, cilexDenominator, cilexPercentage, usdToPHPSpecialRate, cwtFlag, cwtPercentage, cilexDollarMinimum)"
        ));


        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.ADJUSTMENT,
                "BigDecimal.TEN;"
//                "System.out.println(expiryDate);\n" +
//                        "System.out.println(usdToPHPSpecialRate);\n" +
//                        "System.out.println(cilexNumerator);\n" +
//                        "System.out.println(cilexDenominator);\n" +
//                        "System.out.println(cilexPercentage);\n" +
//                        "System.out.println(cashAmount);\n" +
//                        "CalculatorUtils.getCilex( expiryDate, productChargeAmountNetOfPesoAmountPaid, cilexNumerator, cilexDenominator, cilexPercentage, usdToPHPSpecialRate, cwtFlag, cwtPercentage, cilexDollarMinimum)"
        ));

        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.ADJUSTMENT,
                "BigDecimal.TEN;"
//                "System.out.println(expiryDate);\n" +
//                        "System.out.println(usdToPHPSpecialRate);\n" +
//                        "System.out.println(cilexNumerator);\n" +
//                        "System.out.println(cilexDenominator);\n" +
//                        "System.out.println(cilexPercentage);\n" +
//                        "System.out.println(cashAmount);\n" +
//                        "CalculatorUtils.getCilex( expiryDate, productChargeAmountNetOfPesoAmountPaid, cilexNumerator, cilexDenominator, cilexPercentage, usdToPHPSpecialRate, cwtFlag, cwtPercentage, cilexDollarMinimum)"
        ));

    }

    private void initializeFX_LC_Amendment_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

        // insert charges for Regular USANCE REGULAR FXLC Amendment
        // get product reference for FX LC REGULAR USANCE
        // change in tenor from sight to usance
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "System.out.println(tenorSwitch);\n" +
//                        "        System.out.println(amountSwitch);\n" +
//                        "        System.out.println(lcAmountFlagDisplay);\n" +
//                        "        System.out.println(expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(expiryDateFlagDisplay);\n" +
//                        "        System.out.println(confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(narrativeSwitchDisplay);\n" +
//                        "        System.out.println(bankCommissionNumerator);\n" +
//                        "        System.out.println(bankCommissionDenominator);\n" +
//                        "        System.out.println(outstandingBalance );\n" +
//                        "        System.out.println(amountTo);\n" +
//                        "        System.out.println(expiryDateModifiedDays);\n" +
//                        "        System.out.println(expiryDate);\n" +
//                        "        System.out.println(expiryDateTo);\n" +
//                        "CalculatorUtils.getBankCommission_FXLC_Amendment( tenorSwitch,  amountSwitch,  lcAmountFlagDisplay,\n" +
//                        "                                                expiryDateSwitchDisplay,  expiryDateFlagDisplay,  confirmationInstructionsFlagSwitch,  narrativeSwitchDisplay,\n" +
//                        "                                                bankCommissionNumerator,  bankCommissionDenominator,\n" +
//                        "                                                outstandingBalance ,  amountTo,\n" +
//                        "                                                expiryDateModifiedDays, expiryDate, expiryDateTo, bankCommissionPercentage, cwtFlag, cwtPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlag:\" + lcAmountFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"usancePeriod:\" + usancePeriod);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"documentSubType1:\" + documentSubType1);\n" +
//                        "        System.out.println(\"documentSubType2:\" + documentSubType2);\n" +
//                        "        System.out.println(\"commitmentFeeNumerator:\" + commitmentFeeNumerator);\n" +
//                        "        System.out.println(\"commitmentFeeDenominator:\" + commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFeeFxlcAmendment(" +
//                        "tenorSwitch, amountSwitch, lcAmountFlagDisplay,\n" +
//                        "                outstandingBalance , amountTo,\n" +
//                        "                usancePeriod, expiryDate,\n" +
//                        "                documentSubType1, documentSubType2, commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "CalculatorUtils.getCableFeeFxlcAmendment( tenorSwitch,  amountSwitch,  expiryDateSwitchDisplay,  confirmationInstructionsFlagSwitch,  narrativeSwitchDisplay);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"outstandingBalance :\"+outstandingBalance );\n" +
//                        "        System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "CalculatorUtils.getDocStampsFxlcAmendment(amountSwitch,lcAmountFlagDisplay,outstandingBalance ,amountTo)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-ADVISING"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\"+tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "CalculatorUtils.getAdvisingFeeFxlcAmendment(tenorSwitch, amountSwitch, lcAmountFlagDisplay, confirmationInstructionsFlagSwitch, usdToPHPSpecialRate) ;"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-CONFIRMING"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"expiryDateSwitchDisplay:\"+expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(\"expiryDateFlag:\"+expiryDateFlag);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\"+outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "        System.out.println(\"expiryDateModifiedDays:\"+expiryDateModifiedDays);\n" +
//                        "        System.out.println(\"confirmingFeeNumerator:\"+confirmingFeeNumerator);\n" +
//                        "        System.out.println(\"confirmingFeeDenominator:\"+confirmingFeeDenominator);\n" +
//                        "        System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "        System.out.println(\"processingDate:\"+processingDate);\n" +
//                        "        System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "        System.out.println(\"default_confirmingFeeMinimum:\"+default_confirmingFeeMinimum);\n" +
//                        "CalculatorUtils.getConfirmingFeeFxlcAmendment( amountSwitch,  lcAmountFlagDisplay,\n" +
//                        "                 expiryDateSwitchDisplay,  expiryDateFlag,\n" +
//                        "                 confirmationInstructionsFlagSwitch,\n" +
//                        "                 advanceCorresChargesFlag,\n" +
//                        "                 outstandingBalance ,  amountTo,\n" +
//                        "                 expiryDateModifiedDays,\n" +
//                        "                 confirmingFeeNumerator,  confirmingFeeDenominator,  expiryDate, expiryDateTo,  processingDate, confirmingFeePercentage, " +
//                        "                 usdToPHPSpecialRate, default_confirmingFeeMinimum )"
        ));

        // insert charges for SIGHT REGULAR FXLC Amendment
        // get product reference for FX LC REGULAR SIGHT
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "System.out.println(tenorSwitch);\n" +
//                        "        System.out.println(amountSwitch);\n" +
//                        "        System.out.println(lcAmountFlagDisplay);\n" +
//                        "        System.out.println(expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(expiryDateFlagDisplay);\n" +
//                        "        System.out.println(confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(narrativeSwitchDisplay);\n" +
//                        "        System.out.println(bankCommissionNumerator);\n" +
//                        "        System.out.println(bankCommissionDenominator);\n" +
//                        "        System.out.println(outstandingBalance);\n" +
//                        "        System.out.println(amountTo);\n" +
//                        "        System.out.println(expiryDateModifiedDays);\n" +
//                        "        System.out.println(expiryDate);\n" +
//                        "        System.out.println(expiryDateTo);\n" +
//                        "CalculatorUtils.getBankCommission_FXLC_Amendment( tenorSwitch,  amountSwitch,  lcAmountFlagDisplay,\n" +
//                        "                                                expiryDateSwitchDisplay,  expiryDateFlagDisplay,  confirmationInstructionsFlagSwitch,  narrativeSwitchDisplay,\n" +
//                        "                                                bankCommissionNumerator,  bankCommissionDenominator,\n" +
//                        "                                                outstandingBalance ,  amountTo,\n" +
//                        "                                                expiryDateModifiedDays, expiryDate, expiryDateTo, bankCommissionPercentage, cwtFlag, cwtPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlag:\" + lcAmountFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"usancePeriod:\" + usancePeriod);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"documentSubType1:\" + documentSubType1);\n" +
//                        "        System.out.println(\"documentSubType2:\" + documentSubType2);\n" +
//                        "        System.out.println(\"commitmentFeeNumerator:\" + commitmentFeeNumerator);\n" +
//                        "        System.out.println(\"commitmentFeeDenominator:\" + commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFeeFxlcAmendment(" +
//                        "tenorSwitch, amountSwitch, lcAmountFlagDisplay,\n" +
//                        "outstandingBalance , amountTo,\n" +
//                        "usancePeriod, expiryDate,\n" +
//                        "documentSubType1, documentSubType2, " +
//                        "commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "CalculatorUtils.getCableFeeFxlcAmendment( tenorSwitch,  amountSwitch,  expiryDateSwitchDisplay,  confirmationInstructionsFlagSwitch,  narrativeSwitchDisplay) ;"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"outstandingBalance :\"+outstandingBalance );\n" +
//                        "        System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "CalculatorUtils.getDocStampsFxlcAmendment(amountSwitch,lcAmountFlagDisplay,outstandingBalance ,amountTo)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-ADVISING"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\"+tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "CalculatorUtils.getAdvisingFeeFxlcAmendment(tenorSwitch, amountSwitch, lcAmountFlagDisplay, confirmationInstructionsFlagSwitch, usdToPHPSpecialRate) ;"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-CONFIRMING"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"expiryDateSwitchDisplay:\"+expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(\"expiryDateFlag:\"+expiryDateFlag);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\"+outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "        System.out.println(\"expiryDateModifiedDays:\"+expiryDateModifiedDays);\n" +
//                        "        System.out.println(\"confirmingFeeNumerator:\"+confirmingFeeNumerator);\n" +
//                        "        System.out.println(\"confirmingFeeDenominator:\"+confirmingFeeDenominator);\n" +
//                        "        System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "        System.out.println(\"processingDate:\"+processingDate);\n" +
//                        "        System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "        System.out.println(\"default_confirmingFeeMinimum:\"+default_confirmingFeeMinimum);\n" +
//                        "CalculatorUtils.getConfirmingFeeFxlcAmendment( amountSwitch,  lcAmountFlagDisplay,\n" +
//                        "                 expiryDateSwitchDisplay,  expiryDateFlag,\n" +
//                        "                 confirmationInstructionsFlagSwitch,\n" +
//                        "                 advanceCorresChargesFlag,\n" +
//                        "                 outstandingBalance ,  amountTo,\n" +
//                        "                 expiryDateModifiedDays,\n" +
//                        "                 confirmingFeeNumerator,  confirmingFeeDenominator,  expiryDate, expiryDateTo,  processingDate, confirmingFeePercentage, " +
//                        "                 usdToPHPSpecialRate, default_confirmingFeeMinimum )"
        ));

        // insert charges for CASH FXLC Amendment
        // get product reference for FX LC CASH
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "System.out.println(tenorSwitch);\n" +
//                        "        System.out.println(amountSwitch);\n" +
//                        "        System.out.println(lcAmountFlagDisplay);\n" +
//                        "        System.out.println(expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(expiryDateFlagDisplay);\n" +
//                        "        System.out.println(confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(narrativeSwitchDisplay);\n" +
//                        "        System.out.println(bankCommissionNumerator);\n" +
//                        "        System.out.println(bankCommissionDenominator);\n" +
//                        "        System.out.println(outstandingBalance);\n" +
//                        "        System.out.println(amountTo);\n" +
//                        "        System.out.println(expiryDateModifiedDays);\n" +
//                        "        System.out.println(expiryDate);\n" +
//                        "        System.out.println(expiryDateTo);\n" +
//                        "CalculatorUtils.getBankCommission_FXLC_Amendment( tenorSwitch,  amountSwitch,  lcAmountFlagDisplay,\n" +
//                        "                                                expiryDateSwitchDisplay,  expiryDateFlagDisplay,  confirmationInstructionsFlagSwitch,  narrativeSwitchDisplay,\n" +
//                        "                                                bankCommissionNumerator,  bankCommissionDenominator,\n" +
//                        "                                                outstandingBalance ,  amountTo,\n" +
//                        "                                                expiryDateModifiedDays, expiryDate, expiryDateTo,  bankCommissionPercentage, cwtFlag, cwtPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlag:\" + lcAmountFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"usancePeriod:\" + usancePeriod);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"documentSubType1:\" + documentSubType1);\n" +
//                        "        System.out.println(\"documentSubType2:\" + documentSubType2);\n" +
//                        "        System.out.println(\"commitmentFeeNumerator:\" + commitmentFeeNumerator);\n" +
//                        "        System.out.println(\"commitmentFeeDenominator:\" + commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFeeFxlcAmendment(" +
//                        "tenorSwitch, amountSwitch, lcAmountFlagDisplay,\n" +
//                        "outstandingBalance , amountTo,\n" +
//                        "usancePeriod, expiryDate,\n" +
//                        "documentSubType1, documentSubType2, " +
//                        "commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "CalculatorUtils.getCableFeeFxlcAmendment( tenorSwitch,  amountSwitch,  expiryDateSwitchDisplay,  confirmationInstructionsFlagSwitch,  narrativeSwitchDisplay) ;"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"outstandingBalance :\"+outstandingBalance );\n" +
//                        "        System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "CalculatorUtils.getDocStampsFxlcAmendment(amountSwitch,lcAmountFlagDisplay,outstandingBalance ,amountTo)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-ADVISING"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\"+tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "CalculatorUtils.getAdvisingFeeFxlcAmendment(tenorSwitch, amountSwitch, lcAmountFlagDisplay, confirmationInstructionsFlagSwitch, usdToPHPSpecialRate) ;"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-CONFIRMING"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"expiryDateSwitchDisplay:\"+expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(\"expiryDateFlag:\"+expiryDateFlag);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\"+outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "        System.out.println(\"expiryDateModifiedDays:\"+expiryDateModifiedDays);\n" +
//                        "        System.out.println(\"confirmingFeeNumerator:\"+confirmingFeeNumerator);\n" +
//                        "        System.out.println(\"confirmingFeeDenominator:\"+confirmingFeeDenominator);\n" +
//                        "        System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "        System.out.println(\"processingDate:\"+processingDate);\n" +
//                        "        System.out.println(\"default_confirmingFeeMinimum:\"+default_confirmingFeeMinimum);\n" +
//                        "CalculatorUtils.getConfirmingFeeFxlcAmendment( amountSwitch,  lcAmountFlagDisplay,\n" +
//                        "                 expiryDateSwitchDisplay,  expiryDateFlag,\n" +
//                        "                 confirmationInstructionsFlagSwitch,\n" +
//                        "                 advanceCorresChargesFlag,\n" +
//                        "                 outstandingBalance ,  amountTo,\n" +
//                        "                 expiryDateModifiedDays,\n" +
//                        "                 confirmingFeeNumerator,  confirmingFeeDenominator,  expiryDate, expiryDateTo,  processingDate, confirmingFeePercentage, " +
//                        "                 usdToPHPSpecialRate, default_confirmingFeeMinimum )"
        ));

        // insert charges for STANDBY FXLC Amendment
        // get product reference for FX LC STANDBY
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "System.out.println(tenorSwitch);\n" +
//                        "        System.out.println(amountSwitch);\n" +
//                        "        System.out.println(lcAmountFlagDisplay);\n" +
//                        "        System.out.println(expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(expiryDateFlagDisplay);\n" +
//                        "        System.out.println(confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(narrativeSwitchDisplay);\n" +
//                        "        System.out.println(bankCommissionNumerator);\n" +
//                        "        System.out.println(bankCommissionDenominator);\n" +
//                        "        System.out.println(outstandingBalance);\n" +
//                        "        System.out.println(amountTo);\n" +
//                        "        System.out.println(expiryDateModifiedDays);\n" +
//                        "        System.out.println(expiryDate);\n" +
//                        "        System.out.println(expiryDateTo);\n" +
//                        "CalculatorUtils.getBankCommission_FXLC_Amendment( tenorSwitch,  amountSwitch,  lcAmountFlagDisplay,\n" +
//                        "                                                expiryDateSwitchDisplay,  expiryDateFlagDisplay,  confirmationInstructionsFlagSwitch,  narrativeSwitchDisplay,\n" +
//                        "                                                bankCommissionNumerator,  bankCommissionDenominator,\n" +
//                        "                                                outstandingBalance ,  amountTo,\n" +
//                        "                                                expiryDateModifiedDays, expiryDate, expiryDateTo,  bankCommissionPercentage, cwtFlag, cwtPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlag:\" + lcAmountFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"usancePeriod:\" + usancePeriod);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"documentSubType1:\" + documentSubType1);\n" +
//                        "        System.out.println(\"documentSubType2:\" + documentSubType2);\n" +
//                        "        System.out.println(\"commitmentFeeNumerator:\" + commitmentFeeNumerator);\n" +
//                        "        System.out.println(\"commitmentFeeDenominator:\" + commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFeeFxlcAmendment(" +
//                        "tenorSwitch, amountSwitch, lcAmountFlagDisplay,\n" +
//                        "outstandingBalance , amountTo,\n" +
//                        "usancePeriod, expiryDate,\n" +
//                        "documentSubType1, documentSubType2, commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "CalculatorUtils.getCableFeeFxlcAmendment( tenorSwitch,  amountSwitch,  expiryDateSwitchDisplay,  confirmationInstructionsFlagSwitch,  narrativeSwitchDisplay) ;"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"outstandingBalance :\"+outstandingBalance );\n" +
//                        "        System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "CalculatorUtils.getDocStampsFxlcAmendment(amountSwitch,lcAmountFlagDisplay,outstandingBalance ,amountTo)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-ADVISING"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\"+tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "CalculatorUtils.getAdvisingFeeFxlcAmendment(tenorSwitch, amountSwitch, lcAmountFlagDisplay, confirmationInstructionsFlagSwitch, usdToPHPSpecialRate) ;"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-CONFIRMING"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\"+lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"expiryDateSwitchDisplay:\"+expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(\"expiryDateFlag:\"+expiryDateFlag);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"advanceCorresChargesFlag:\"+advanceCorresChargesFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\"+outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "        System.out.println(\"expiryDateModifiedDays:\"+expiryDateModifiedDays);\n" +
//                        "        System.out.println(\"confirmingFeeNumerator:\"+confirmingFeeNumerator);\n" +
//                        "        System.out.println(\"confirmingFeeDenominator:\"+confirmingFeeDenominator);\n" +
//                        "        System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "        System.out.println(\"expiryDateTo:\"+expiryDateTo);\n" +
//                        "        System.out.println(\"processingDate:\"+processingDate);\n" +
//                        "        System.out.println(\"confirmingFeePercentage:\"+confirmingFeePercentage);\n" +
//                        "        System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "        System.out.println(\"default_confirmingFeeMinimum:\"+default_confirmingFeeMinimum);\n" +
//                        "CalculatorUtils.getConfirmingFeeFxlcAmendment( amountSwitch,  lcAmountFlagDisplay,\n" +
//                        "                 expiryDateSwitchDisplay,  expiryDateFlag,\n" +
//                        "                 confirmationInstructionsFlagSwitch,\n" +
//                        "                 advanceCorresChargesFlag,\n" +
//                        "                 outstandingBalance ,  amountTo,\n" +
//                        "                 expiryDateModifiedDays,\n" +
//                        "                 confirmingFeeNumerator,  confirmingFeeDenominator,  expiryDate, expiryDateTo, processingDate, confirmingFeePercentage, " +
//                        "                 usdToPHPSpecialRate, default_confirmingFeeMinimum )"
        ));

    }

    private void initializeFX_LC_Negotiation_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

        // insert charges for CASH FXLC NEGOTIATION
        // get product reference for FX LC CASH
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.CASH, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "        System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_cableFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("NOTARIAL"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "        System.out.println(\"notarialAmount:\"+default_notarialAmount);\n" +
//                        " CalculatorUtils.getNotarialAmount(default_notarialAmount);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
        ));

        // insert charges for STANDBY FXLC NEGOTIATION
        // get product reference for FX LC STANDBY
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "        System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_cableFee);"
        ));
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("NOTARIAL"), productRef.getProductId(), ServiceType.NEGOTIATION,
//                "BigDecimal.TEN;"
////                "        System.out.println(\"notarialAmount:\"+default_notarialAmount);\n" +
////                        " CalculatorUtils.getNotarialAmount(default_notarialAmount);"
//        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"oldAmount:\"+oldAmount);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getDocStampsAmount_FXLC_Nego(amount,oldAmount) "
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"cilexNumerator:\"+cilexNumerator);\n" +
//                        "System.out.println(\"cilexNumerator:\"+cilexNumerator);\n" +
//                        "System.out.println(\"cilexDenominator:\"+cilexDenominator);\n" +
//                        "System.out.println(\"cilexPercentage:\"+cilexPercentage);\n" +
//                        "System.out.println(\"productChargeAmountNetOfPesoAmountPaid:\"+productChargeAmountNetOfPesoAmountPaid);\n" +
//                        "CalculatorUtils.getCilex( expiryDate, productChargeAmountNetOfPesoAmountPaid, cilexNumerator, cilexDenominator, cilexPercentage, usdToPHPSpecialRate, cwtFlag, cwtPercentage, cilexDollarMinimum)"
        ));
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("INTEREST"), productRef.getProductId(), ServiceType.NEGOTIATION, "50B"));//TODO:Check in sir Jett's spreadsheet not in Payment FSD

        // insert charges for REGULAR SIGHT FXLC NEGOTIATION
        // get product reference for FX LC REGULAR SIGHT
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "        System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_cableFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("NOTARIAL"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "        System.out.println(\"notarialAmount:\"+default_notarialAmount);\n" +
//                        " CalculatorUtils.getNotarialAmount(default_notarialAmount);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"oldAmount:\"+oldAmount);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getDocStampsAmount_FXLC_Nego(amount,oldAmount) "
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "System.out.println(\"cilexNumerator:\"+cilexNumerator);\n" +
//                        "System.out.println(\"cilexDenominator:\"+cilexDenominator);\n" +
//                        "System.out.println(\"cilexPercentage:\"+cilexPercentage);\n" +
//                        "System.out.println(\"productChargeAmountNetOfPesoAmountPaid:\"+productChargeAmountNetOfPesoAmountPaid);\n" +
//                        "CalculatorUtils.getCilex( expiryDate, productChargeAmountNetOfPesoAmountPaid, cilexNumerator, cilexDenominator, cilexPercentage, usdToPHPSpecialRate, cwtFlag, cwtPercentage, cilexDollarMinimum)"
        ));
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("INTEREST"), productRef.getProductId(), ServiceType.NEGOTIATION, "50B"));//TODO:Check in sir Jett's spreadsheet not in Payment FSD

        // insert charges for REGULAR USANCE FXLC NEGOTIATION
        // get product reference for FX LC REGULAR USANCE
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "        System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        "CalculatorUtils.getCableFee(default_cableFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("NOTARIAL"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "        System.out.println(\"notarialAmount:\"+default_notarialAmount);\n" +
//                        "CalculatorUtils.getNotarialAmount(default_notarialAmount);"
        ));

    }

    private void initializeFX_UA_Settlement_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

        // insert charges for FX UA SETTLEMENT
        // get product reference for FX UA SETTLEMENT

//        productRef = productReferenceRepository.find(DocumentClass.UA, DocumentType.FOREIGN, DocumentSubType1.REGULAR,  DocumentSubType2.USANCE);
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.UA_LOAN_SETTLEMENT,
//                "CalculatorUtils.getCilex_UALOAN_Settlement( amount, settlementCurrency, usdToPHPSpecialRate)"
//        ));
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.UA_LOAN_SETTLEMENT,
//                "CalculatorUtils.getDocStampsUaLoanSettlement( amount, oldDocAmount)"
//        ));//TODO


        // insert charges for FX UA SETTLEMENT
        // get product reference for FX UA SETTLEMENT

        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.UA_LOAN_SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(cilexNumerator);\n" +
//                        "System.out.println(\"cilexDenominator:\"+cilexDenominator);\n" +
//                        "System.out.println(\"cilexPercentage:\"+cilexPercentage);\n" +
//                        "System.out.println(\"creationExchangeRateUsdToPHPSpecialRate:\"+creationExchangeRateUsdToPHPSpecialRate);\n" +
//                        "System.out.println(\"productChargeAmountNetOfPesoAmountPaid:\"+productChargeAmountNetOfPesoAmountPaid);\n" +
//                        "CalculatorUtils.getCilex( expiryDate, productChargeAmountNetOfPesoAmountPaid, cilexNumerator, cilexDenominator, cilexPercentage, usdToPHPSpecialRate, cwtFlag, cwtPercentage, cilexDollarMinimum)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.UA_LOAN_SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"oldDocAmount:\"+oldDocAmount);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getDocStamps_UALOAN_Settlement( amount, oldDocAmount)"
        ));//TODO
    }

    private void initializeFX_UA_LoanMaturityAdjustment_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

//        // insert charges for FX UA ADJUSTMENT
//        // get product reference for FX UA ADJUSTMENT
//        productRef = productReferenceRepository.find(DocumentClass.UA, DocumentType.FOREIGN,  DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.UA_LOAN_MATURITY_ADJUSTMENT,
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "System.out.println(\"commitmentFeeNumerator:\"+commitmentFeeNumerator);\n" +
//                        "System.out.println(\"commitmentFeeDenominator:\"+commitmentFeeDenominator);\n" +
//                        "System.out.println(\"commitmentFeePercentage:\"+commitmentFeePercentage);\n" +
//                        "BigDecimal months = CalculatorUtils.getMonthsTill(expiryDate);\n" +
//                        "System.out.println(\"months:\"+months);\n" +
//                        "if(months.compareTo(BigDecimal.ONE)<1 ) {months = BigDecimal.ONE;}\n" +
//                        "System.out.println(\"months:\"+months);\n" +
//                        "BigDecimal tmp = (CalculatorUtils.divideUp(commitmentFeeNumerator, commitmentFeeDenominator).multiply(commitmentFeePercentage).multiply(amount)).multiply(months);\n" +
//                        "System.out.println(\"tmp:\"+tmp);\n" +
//                        "tmp < 500.00B ? BigDecimal.valueOf(500.00B) : BigDecimal.valueOf(tmp)\n"
//        ));
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.UA_LOAN_MATURITY_ADJUSTMENT,
//                "System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_cableFee)"
//        ));


        // insert charges for FX UA ADJUSTMENT
        // get product reference for FX UA ADJUSTMENT
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.FOREIGN, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        //TODO: Copy from ChargesTest
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.UA_LOAN_MATURITY_ADJUSTMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"loanMaturityDateFrom:\"+loanMaturityDateFrom);\n" +
//                        "System.out.println(\"loanMaturityDateTo:\"+loanMaturityDateTo);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "System.out.println(\"commitmentFeeNumerator:\"+commitmentFeeNumerator);\n" +
//                        "System.out.println(\"commitmentFeeDenominator:\"+commitmentFeeDenominator);\n" +
//                        "System.out.println(\"commitmentFeePercentage:\"+commitmentFeePercentage);\n" +
//                        "CalculatorUtils.getCommitmentFee_FXLC_UALOAN_MaturityAdjustment( loanMaturityDateFrom, loanMaturityDateTo, amount, commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage, default_commitmentFeeMinimum, cwtFlag, cwtPercentage )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.UA_LOAN_MATURITY_ADJUSTMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_cableFee)"
        ));


    }

    private void initializeFX_INDEMNITY_Cancellation_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

        // insert charges for FX INDEMNITY CANCELLATION
        // get product reference for FX INDEMNITY CANCELLATION
        productRef = productReferenceRepository.find(DocumentClass.INDEMNITY, DocumentType.FOREIGN, null, null);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CANCEL"), productRef.getProductId(), ServiceType.CANCELLATION,
                "new BigDecimal(\"300\");"
//                "System.out.println(\"default_INDEMNITY_Cancel_Fee:\"+default_INDEMNITY_Cancel_Fee);\n" +
//                        "CalculatorUtils.getCancelFee_Indemnity_Issuance( default_INDEMNITY_Cancel_Fee )"
        ));


        // insert charges for FX INDEMNITY CANCELLATION
        // get product reference for FX INDEMNITY CANCELLATION
        productRef = productReferenceRepository.find(DocumentClass.INDEMNITY, DocumentType.FOREIGN, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CANCEL"), productRef.getProductId(), ServiceType.CANCELLATION,
                "new BigDecimal(\"300\");"
//                "System.out.println(\"default_INDEMNITY_Cancel_Fee:\"+default_INDEMNITY_Cancel_Fee);\n" +
//                        " CalculatorUtils.getCancelFee_Indemnity_Issuance( default_INDEMNITY_Cancel_Fee )"
        ));

        // insert charges for FX INDEMNITY CANCELLATION
        // get product reference for FX INDEMNITY CANCELLATION
        productRef = productReferenceRepository.find(DocumentClass.INDEMNITY, DocumentType.FOREIGN, DocumentSubType1.CASH, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CANCEL"), productRef.getProductId(), ServiceType.CANCELLATION,
                "new BigDecimal(\"300\");"
//                "System.out.println(\"default_INDEMNITY_Cancel_Fee:\"+default_INDEMNITY_Cancel_Fee);\n" +
//                        " CalculatorUtils.getCancelFee_Indemnity_Issuance( default_INDEMNITY_Cancel_Fee )"
        ));

    }

    private void initializeFX_INDEMNITY_Issuance_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

        // insert charges for FX INDEMNITY ISSUANCE
        // get product reference for FX INDEMNITY ISSUANCE
        productRef = productReferenceRepository.find(DocumentClass.INDEMNITY, DocumentType.FOREIGN, null, null);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.ISSUANCE,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_INDEMNITY_BC:\"+default_INDEMNITY_BC);\n" +
//                        "CalculatorUtils.getBankCommission_Indemnity_Issuance( default_INDEMNITY_BC )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.ISSUANCE,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_INDEMNITY_DOCSTAMPS:\"+default_INDEMNITY_DOCSTAMPS);\n" +
//                        "CalculatorUtils.getDocStamps_Indemnity_Issuance( default_INDEMNITY_DOCSTAMPS, indemnityType)"
        ));


        // insert charges for FX INDEMNITY ISSUANCE
        // get product reference for FX INDEMNITY ISSUANCE
        productRef = productReferenceRepository.find(DocumentClass.INDEMNITY, DocumentType.FOREIGN, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.ISSUANCE,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_INDEMNITY_BC:\"+default_INDEMNITY_BC);\n" +
//                        "CalculatorUtils.getBankCommission_Indemnity_Issuance( default_INDEMNITY_BC )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.ISSUANCE,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_INDEMNITY_DOCSTAMPS:\"+default_INDEMNITY_DOCSTAMPS);\n" +
//                        "CalculatorUtils.getDocStamps_Indemnity_Issuance( default_INDEMNITY_DOCSTAMPS, indemnityType)"
        ));
    }

    private void initializeDM_LC_Opening_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

        // insert charges for Cash DMLC Opening
        // get product reference for DM LC Cash
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.CASH, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"bankCommissionNumerator:\"+bankCommissionNumerator);\n" +
//                        "System.out.println(\"bankCommissionDenominator:\"+bankCommissionDenominator);\n" +
//                        "System.out.println(\"bankCommissionPercentage:\"+bankCommissionPercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getBankCommission( expiryDate, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, amount, cwtPercentage, cwtFlag )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_suppliesFee:\"+default_suppliesFee);\n" +
//                        " CalculatorUtils.getSuppliesFee(default_suppliesFee);"
        ));

        // insert charges for STANDBY DMLC Opening
        // get product reference for DM LC Standby
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(expiryDate);\n" +
//                        "System.out.println(\"bankCommissionNumerator:\"+bankCommissionNumerator);\n" +
//                        "System.out.println(\"bankCommissionDenominator:\"+bankCommissionDenominator);\n" +
//                        "System.out.println(\"bankCommissionPercentage:\"+bankCommissionPercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getBankCommission( expiryDate, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, amount, cwtPercentage, cwtFlag )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usancePeriod:\"+usancePeriod);\n" +
//                        "System.out.println(\"commitmentFeeNumerator:\"+commitmentFeeNumerator);\n" +
//                        "System.out.println(\"commitmentFeeDenominator:\"+commitmentFeeDenominator);\n" +
//                        "System.out.println(\"commitmentFeePercentage:\"+commitmentFeePercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"documentSubType1:\"+documentSubType1+\":\");\n" +
//                        "System.out.println(\"documentSubType2:\"+documentSubType2+\":\");\n" +
//                        "System.out.println(\"cwtFlag:\"+cwtFlag+\":\");\n" +
//                        "System.out.println(\"cwtPercentage:\"+cwtPercentage+\":\");\n" +
//                        "CalculatorUtils.getCommitmentFee_DMLC_Opening(amount, usancePeriod, expiryDate, documentSubType1, documentSubType2, commitmentFeeNumerator,  commitmentFeeDenominator, commitmentFeePercentage, cwtFlag, cwtPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_suppliesFee:\"+default_suppliesFee);\n" +
//                        " CalculatorUtils.getSuppliesFee(default_suppliesFee);"
        ));

        // insert charges for Regular Sight DMLC Opening
        // get product reference for DM LC Regular Sight
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"bankCommissionNumerator:\"+bankCommissionNumerator);\n" +
//                        "System.out.println(\"bankCommissionDenominator:\"+bankCommissionDenominator);\n" +
//                        "System.out.println(\"bankCommissionPercentage:\"+bankCommissionPercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getBankCommission( expiryDate, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, amount, cwtPercentage, cwtFlag )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usancePeriod:\"+usancePeriod);\n" +
//                        "System.out.println(\"commitmentFeeNumerator:\"+commitmentFeeNumerator);\n" +
//                        "System.out.println(\"commitmentFeeDenominator:\"+commitmentFeeDenominator);\n" +
//                        "System.out.println(\"commitmentFeePercentage:\"+commitmentFeePercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"documentSubType1:\"+documentSubType1+\":\");\n" +
//                        "System.out.println(\"documentSubType2:\"+documentSubType2+\":\");\n" +
//                        "System.out.println(\"cwtFlag:\"+cwtFlag+\":\");\n" +
//                        "System.out.println(\"cwtPercentage:\"+cwtPercentage+\":\");\n" +
//                        "CalculatorUtils.getCommitmentFee_DMLC_Opening(amount, usancePeriod, expiryDate, documentSubType1, documentSubType2, commitmentFeeNumerator,  commitmentFeeDenominator, commitmentFeePercentage, cwtFlag, cwtPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_suppliesFee:\"+default_suppliesFee);\n" +
//                        " CalculatorUtils.getSuppliesFee(default_suppliesFee);"
        ));

        // insert charges for Regular USANCE DMLC Opening
        // get product reference for DM LC Regular USANCE
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"bankCommissionNumerator:\"+bankCommissionNumerator);\n" +
//                        "System.out.println(\"bankCommissionDenominator:\"+bankCommissionDenominator);\n" +
//                        "System.out.println(\"bankCommissionPercentage:\"+bankCommissionPercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "CalculatorUtils.getBankCommission( expiryDate, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, amount, cwtPercentage, cwtFlag )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"usancePeriod:\"+usancePeriod);\n" +
//                        "System.out.println(\"commitmentFeeNumerator:\"+commitmentFeeNumerator);\n" +
//                        "System.out.println(\"commitmentFeeDenominator:\"+commitmentFeeDenominator);\n" +
//                        "System.out.println(\"commitmentFeePercentage:\"+commitmentFeePercentage);\n" +
//                        "System.out.println(\"amount:\"+amount);\n" +
//                        "System.out.println(\"expiryDate:\"+expiryDate);\n" +
//                        "System.out.println(\"documentSubType1:\"+documentSubType1+\":\");\n" +
//                        "System.out.println(\"documentSubType2:\"+documentSubType2+\":\");\n" +
//                        "System.out.println(\"cwtFlag:\"+cwtFlag+\":\");\n" +
//                        "System.out.println(\"cwtPercentage:\"+cwtPercentage+\":\");\n" +
//                        "CalculatorUtils.getCommitmentFee_DMLC_Opening(amount, usancePeriod, expiryDate, documentSubType1, documentSubType2, commitmentFeeNumerator,  commitmentFeeDenominator, commitmentFeePercentage, cwtFlag, cwtPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.OPENING,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_suppliesFee:\"+default_suppliesFee);\n" +
//                        " CalculatorUtils.getSuppliesFee(default_suppliesFee);"
        ));

    }

    private void initializeDM_LC_Adjustment_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;
        // insert charges for CASH DMLC Adjustment
        // get product reference for DM LC CASH
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.CASH, DocumentSubType2.SIGHT);
        //tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.ADJUSTMENT, "new BigDecimal(amount) * 0.10B"));
        //TODO: Note parang no charges for Adjustment of DM LC
    }

    private void initializeDM_LC_Amendment_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

        // insert charges for Regular USANCE REGULAR DMLC Amendment
        // get product reference for DM LC REGULAR USANCE
        // change in tenor from sight to usance

        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\" + lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"expiryDateSwitchDisplay:\" + expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(\"expiryDateFlag:\" + expiryDateFlag);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\" + confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"narrativeSwitchDisplay:\" + narrativeSwitchDisplay);\n" +
//                        "        System.out.println(\"bankCommissionNumerator:\" + bankCommissionNumerator);\n" +
//                        "        System.out.println(\"bankCommissionDenominator:\" + bankCommissionDenominator);\n" +
//                        "        System.out.println(\"amountFrom:\" + amountFrom);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"expiryDateModifiedDays:\" + expiryDateModifiedDays);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"expiryDateTo:\" + expiryDateTo);\n" +
//                        "        System.out.println(\"bankCommissionPercentage:\" + bankCommissionPercentage);\n" +
//                        "CalculatorUtils.getBankCommissionDmlcAmendment(tenorSwitch, amountSwitch, lcAmountFlagDisplay," +
//                        "expiryDateSwitchDisplay, expiryDateFlag, confirmationInstructionsFlagSwitch, narrativeSwitchDisplay," +
//                        "bankCommissionNumerator, bankCommissionDenominator, amountFrom, amountTo," +
//                        "expiryDateModifiedDays, expiryDate, expiryDateTo, bankCommissionPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlag:\" + lcAmountFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"usancePeriod:\" + usancePeriod);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"documentSubType1:\" + documentSubType1);\n" +
//                        "        System.out.println(\"documentSubType2:\" + documentSubType2);\n" +
//                        "        System.out.println(\"commitmentFeeNumerator:\" + commitmentFeeNumerator);\n" +
//                        "        System.out.println(\"commitmentFeeDenominator:\" + commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFeeAmendmentDmlc(" +
//                        "        tenorSwitch, amountSwitch, lcAmountFlagDisplay, " +
//                        "        outstandingBalance, amountTo, " +
//                        "        usancePeriod, expiryDate, " +
//                        "        documentSubType1, documentSubType2, " +
//                        "        commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "System.out.println(\"expiryDateSwitchDisplay:\"+expiryDateSwitchDisplay);\n" +
//                        "System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "System.out.println(\"narrativeSwitchDisplay:\"+narrativeSwitchDisplay);\n" +
//                        "CalculatorUtils.getSuppliesFeeDmlcAmendment(tenorSwitch, amountSwitch, expiryDateSwitchDisplay, " +
//                        "        confirmationInstructionsFlagSwitch, narrativeSwitchDisplay, default_suppliesFee)"
        ));

        // insert charges for SIGHT REGULAR FXLC Amendment
        // get product reference for DM LC REGULAR SIGHT
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\" + lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"expiryDateSwitchDisplay:\" + expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(\"expiryDateFlag:\" + expiryDateFlag);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\" + confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"narrativeSwitchDisplay:\" + narrativeSwitchDisplay);\n" +
//                        "        System.out.println(\"bankCommissionNumerator:\" + bankCommissionNumerator);\n" +
//                        "        System.out.println(\"bankCommissionDenominator:\" + bankCommissionDenominator);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"expiryDateModifiedDays:\" + expiryDateModifiedDays);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"expiryDateTo:\" + expiryDateTo);\n" +
//                        "CalculatorUtils.getBankCommissionDmlcAmendment(tenorSwitch, amountSwitch, lcAmountFlagDisplay," +
//                        "        expiryDateSwitchDisplay, expiryDateFlag, confirmationInstructionsFlagSwitch, narrativeSwitchDisplay," +
//                        "        bankCommissionNumerator, bankCommissionDenominator, outstandingBalance, amountTo," +
//                        "        expiryDateModifiedDays, expiryDate, expiryDateTo, bankCommissionPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlag:\" + lcAmountFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"usancePeriod:\" + usancePeriod);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"documentSubType1:\" + documentSubType1);\n" +
//                        "        System.out.println(\"documentSubType2:\" + documentSubType2);\n" +
//                        "        System.out.println(\"commitmentFeeNumerator:\" + commitmentFeeNumerator);\n" +
//                        "        System.out.println(\"commitmentFeeDenominator:\" + commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFeeAmendmentDmlc(" +
//                        "        tenorSwitch, amountSwitch, lcAmountFlagDisplay, " +
//                        "        outstandingBalance, amountTo, " +
//                        "        usancePeriod, expiryDate, " +
//                        "        documentSubType1, documentSubType2, " +
//                        "        commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "System.out.println(\"expiryDateSwitchDisplay:\"+expiryDateSwitchDisplay);\n" +
//                        "System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "System.out.println(\"narrativeSwitchDisplay:\"+narrativeSwitchDisplay);\n" +
//                        "CalculatorUtils.getSuppliesFeeDmlcAmendment(tenorSwitch, amountSwitch, expiryDateSwitchDisplay, " +
//                        "        confirmationInstructionsFlagSwitch, narrativeSwitchDisplay, default_suppliesFee)"
        ));

        // insert charges for CASH DMLC Amendment
        // get product reference for DM LC CASH
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.CASH, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\" + lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"expiryDateSwitchDisplay:\" + expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(\"expiryDateFlag:\" + expiryDateFlag);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\" + confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"narrativeSwitchDisplay:\" + narrativeSwitchDisplay);\n" +
//                        "        System.out.println(\"bankCommissionNumerator:\" + bankCommissionNumerator);\n" +
//                        "        System.out.println(\"bankCommissionDenominator:\" + bankCommissionDenominator);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"expiryDateModifiedDays:\" + expiryDateModifiedDays);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"expiryDateTo:\" + expiryDateTo);\n" +
//                        "CalculatorUtils.getBankCommissionDmlcAmendment(tenorSwitch, amountSwitch, lcAmountFlagDisplay," +
//                        "expiryDateSwitchDisplay, expiryDateFlag, confirmationInstructionsFlagSwitch, narrativeSwitchDisplay," +
//                        "bankCommissionNumerator, bankCommissionDenominator, outstandingBalance, amountTo, " +
//                        "expiryDateModifiedDays, expiryDate, expiryDateTo, bankCommissionPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlag:\" + lcAmountFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"usancePeriod:\" + usancePeriod);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"documentSubType1:\" + documentSubType1);\n" +
//                        "        System.out.println(\"documentSubType2:\" + documentSubType2);\n" +
//                        "        System.out.println(\"commitmentFeeNumerator:\" + commitmentFeeNumerator);\n" +
//                        "        System.out.println(\"commitmentFeeDenominator:\" + commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFeeAmendmentDmlc(" +
//                        "tenorSwitch, amountSwitch, lcAmountFlagDisplay, " +
//                        "outstandingBalance, amountTo, " +
//                        "usancePeriod, expiryDate, " +
//                        "documentSubType1, documentSubType2, " +
//                        "commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "System.out.println(\"expiryDateSwitchDisplay:\"+expiryDateSwitchDisplay);\n" +
//                        "System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "System.out.println(\"narrativeSwitchDisplay:\"+narrativeSwitchDisplay);\n" +
//                        "CalculatorUtils.getSuppliesFeeDmlcAmendment(tenorSwitch, amountSwitch, expiryDateSwitchDisplay, " +
//                        "confirmationInstructionsFlagSwitch, narrativeSwitchDisplay, default_suppliesFee)"
        ));

        // insert charges for STANDBY FXLC Amendment
        // get product reference for DM LC STANDBY
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlagDisplay:\" + lcAmountFlagDisplay);\n" +
//                        "        System.out.println(\"expiryDateSwitchDisplay:\" + expiryDateSwitchDisplay);\n" +
//                        "        System.out.println(\"expiryDateFlag:\" + expiryDateFlag);\n" +
//                        "        System.out.println(\"confirmationInstructionsFlagSwitch:\" + confirmationInstructionsFlagSwitch);\n" +
//                        "        System.out.println(\"narrativeSwitchDisplay:\" + narrativeSwitchDisplay);\n" +
//                        "        System.out.println(\"bankCommissionNumerator:\" + bankCommissionNumerator);\n" +
//                        "        System.out.println(\"bankCommissionDenominator:\" + bankCommissionDenominator);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"expiryDateModifiedDays:\" + expiryDateModifiedDays);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"expiryDateTo:\" + expiryDateTo);\n" +
//                        "CalculatorUtils.getBankCommissionDmlcAmendment(tenorSwitch, amountSwitch, lcAmountFlagDisplay, " +
//                        "expiryDateSwitchDisplay, expiryDateFlag, confirmationInstructionsFlagSwitch, narrativeSwitchDisplay," +
//                        "bankCommissionNumerator, bankCommissionDenominator, outstandingBalance, amountTo," +
//                        "expiryDateModifiedDays, expiryDate, expiryDateTo, bankCommissionPercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "        System.out.println(\"tenorSwitch:\" + tenorSwitch);\n" +
//                        "        System.out.println(\"amountSwitch:\" + amountSwitch);\n" +
//                        "        System.out.println(\"lcAmountFlag:\" + lcAmountFlag);\n" +
//                        "        System.out.println(\"outstandingBalance:\" + outstandingBalance);\n" +
//                        "        System.out.println(\"amountTo:\" + amountTo);\n" +
//                        "        System.out.println(\"usancePeriod:\" + usancePeriod);\n" +
//                        "        System.out.println(\"expiryDate:\" + expiryDate);\n" +
//                        "        System.out.println(\"documentSubType1:\" + documentSubType1);\n" +
//                        "        System.out.println(\"documentSubType2:\" + documentSubType2);\n" +
//                        "        System.out.println(\"commitmentFeeNumerator:\" + commitmentFeeNumerator);\n" +
//                        "        System.out.println(\"commitmentFeeDenominator:\" + commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFeeAmendmentDmlc(" +
//                        "tenorSwitch, amountSwitch, lcAmountFlagDisplay, " +
//                        "outstandingBalance, amountTo, " +
//                        "usancePeriod, expiryDate, " +
//                        "documentSubType1, documentSubType2, " +
//                        "commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage)"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("SUP"), productRef.getProductId(), ServiceType.AMENDMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"amountSwitch:\"+amountSwitch);\n" +
//                        "System.out.println(\"expiryDateSwitchDisplay:\"+expiryDateSwitchDisplay);\n" +
//                        "System.out.println(\"confirmationInstructionsFlagSwitch:\"+confirmationInstructionsFlagSwitch);\n" +
//                        "System.out.println(\"narrativeSwitchDisplay:\"+narrativeSwitchDisplay);\n" +
//                        "CalculatorUtils.getSuppliesFeeDmlcAmendment(tenorSwitch, amountSwitch, expiryDateSwitchDisplay, " +
//                        "confirmationInstructionsFlagSwitch, narrativeSwitchDisplay, default_suppliesFee)"
        ));

    }

    private void initializeDM_LC_Negotiation_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

        // insert charges for CASH DMLC NEGOTIATION
        // get product reference for DM LC CASH
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.CASH, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_cableFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
        ));

        // insert charges for STANDBY DMLC NEGOTIATION
        // get product reference for DM LC CASH
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.STANDBY, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_cableFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"remittanceFee:\"+remittanceFee);\n" +
//                        "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "CalculatorUtils.getRemittanceFee_DMLC_Nego(remittanceFee, usdToPHPSpecialRate)"
        ));//TODO:Check in sir Jett's spreadsheet not in Payment FSD
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
        ));

        // insert charges for REGULAR SIGHT FXLC NEGOTIATION
        // get product reference for FX LC REGULAR SIGHT
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.SIGHT);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_cableFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"remittanceFee:\"+remittanceFee);\n" +
//                        "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "CalculatorUtils.getRemittanceFee_DMLC_Nego(remittanceFee, usdToPHPSpecialRate )"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
        ));

        // insert charges for REGULAR USANCE FXLC NEGOTIATION
        // get product reference for FX LC REGULAR USANCE
        // increase in LC Amount
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        " CalculatorUtils.getCableFee(default_cableFee);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "BigDecimal.TEN;"
//                "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "System.out.println(\"remittanceFee:\"+remittanceFee);\n" +
//                        "CalculatorUtils.getRemittanceFee_DMLC_Nego(remittanceFee, usdToPHPSpecialRate)"
        ));


    }

    private void initializeDM_UA_Settlement_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;

//        // insert charges for DM UA SETTLEMENT
//        // get product reference for DM UA SETTLEMENT
//        productRef = productReferenceRepository.find(DocumentClass.UA, DocumentType.DOMESTIC, null, null);
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.SETTLEMENT,
//                "System.out.println(\"remittanceFeeInUsd:\"+remittanceFeeInUsd);\n" +
//                        "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        " CalculatorUtils.getRemitanceFee_UALOAN_Settlement(usdToPHPSpecialRate ,remittanceFeeInUsd) ; "
//        ));
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.SETTLEMENT,
//                "System.out.println(\"outstandingBalance:\"+outstandingBalance);\n" +
//                        "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        " CalculatorUtils.getDocStampsAmount_DMLC_UALOAN_Settlement(outstandingBalance,usdToPHPSpecialRate) ; "
//        ));
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.SETTLEMENT,
//                "System.out.println(\"default_cableFee:\"+default_cableFee);\n" +
//                        "default_cableFee;"
//        ));


        // insert charges for DM UA SETTLEMENT
        // get product reference for DM UA SETTLEMENT
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.UA_LOAN_SETTLEMENT,
                "BigDecimal.TEN;"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.UA_LOAN_SETTLEMENT,
                "BigDecimal.TEN;"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.UA_LOAN_SETTLEMENT,
                "BigDecimal.TEN;"
        ));
    }

    private void initializeDM_UA_Adjustment_Charges(Map<String, ChargeId> chargeIds) {
        ProductReference productRef;
//
//        // insert charges for DM UA ADJUSTMENT
//        // get product reference for DM UA ADJUSTMENT
//        productRef = productReferenceRepository.find(DocumentClass.UA, DocumentType.DOMESTIC, null, null);
//        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.ADJUSTMENT,
//                "System.out.println(\"amountFrom:\"+amountFrom);\n" +
//                        "System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "System.out.println(\"usancePeriod:\"+usancePeriod);\n" +
//                        "System.out.println(\"commitmentFeeNumerator:\"+commitmentFeeNumerator);\n" +
//                        "System.out.println(\"commitmentFeeDenominator:\"+commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFee_DMLC_UALOAN_MaturityAdjustment(\n" +
//                        "            amountFrom,  amountTo,\n" +
//                        "            usancePeriod,\n" +
//                        "            commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage);"
//        ));


        // insert charges for DM UA ADJUSTMENT
        // get product reference for DM UA ADJUSTMENT
        productRef = productReferenceRepository.find(DocumentClass.LC, DocumentType.DOMESTIC, DocumentSubType1.REGULAR, DocumentSubType2.USANCE);
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CF"), productRef.getProductId(), ServiceType.UA_LOAN_MATURITY_ADJUSTMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"amountFrom:\"+amountFrom);\n" +
//                        "System.out.println(\"amountTo:\"+amountTo);\n" +
//                        "System.out.println(\"usancePeriod:\"+usancePeriod);\n" +
//                        "System.out.println(\"commitmentFeeNumerator:\"+commitmentFeeNumerator);\n" +
//                        "System.out.println(\"commitmentFeeDenominator:\"+commitmentFeeDenominator);\n" +
//                        "CalculatorUtils.getCommitmentFee_DMLC_UALOAN_MaturityAdjustment(\n" +
//                        "            amountFrom,  amountTo, usancePeriod,\n" +
//                        "            commitmentFeeNumerator, commitmentFeeDenominator, commitmentFeePercentage," +
//                        "cwtFlag, cwtPercentage, default_commitmentFeeMinimum );"
        ));

    }

    private void initializeFX_NON_LC_Settlement_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for FX DA SETTLEMENT
        // get product reference for FX DA SETTLEMENT
        productRef = productReferenceRepository.find(DocumentClass.DA, DocumentType.FOREIGN, null, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "CalculatorUtils.getBankCommission_FX_NON_LC_SETTLEMENT(productAmount, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, cwtFlag, cwtPercentage );"
                //"CalculatorUtils.getBankCommission_FX_NON_LC_SETTLEMENT(productAmount);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_CABLE);\n" +
//                        " CalculatorUtils.getCableFee_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_CABLE);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"productAmount\"+productAmount);\n" +
//                        "System.out.println(\"productChargeAmountNetOfPesoAmountPaid\"+productChargeAmountNetOfPesoAmountPaid);\n" +
//                        "CalculatorUtils.getCilex_FX_NON_LC_SETTLEMENT(productChargeAmountNetOfPesoAmountPaid, FX_NON_LC_SETTLEMENT_cilexNumerator, FX_NON_LC_SETTLEMENT_cilexDenominator, FX_NON_LC_SETTLEMENT_cilexPercentage, cwtFlag, cwtPercentage, usdToPHPSpecialRate );"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "System.out.println(TR_LOAN_FLAG);\n" +
//                        "System.out.println(TR_LOAN_AMOUNT);\n" +
//                        "CalculatorUtils.getDocStampsAmount_FX_NON_LC_SETTLEMENT(productAmount, TR_LOAN_FLAG, TR_LOAN_AMOUNT);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BOOKING"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_BOOKING);\n" +
//                        "CalculatorUtils.getBookingCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_BOOKING);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("NOTARIAL"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_NOTARIAL);\n" +
//                        " CalculatorUtils.getNotarialFeeCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_NOTARIAL);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BSP"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_BSP);\n" +
//                        " CalculatorUtils.getBspCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_BSP);"
        ));


        productRef = productReferenceRepository.find(DocumentClass.OA, DocumentType.FOREIGN, null, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "CalculatorUtils.getBankCommission_FX_NON_LC_SETTLEMENT(productAmount, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, cwtFlag, cwtPercentage );"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_CABLE);\n" +
//                        " CalculatorUtils.getCableFee_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_CABLE);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "System.out.println(\"productChargeAmountNetOfPesoAmountPaid\"+productChargeAmountNetOfPesoAmountPaid);\n" +
//                        "CalculatorUtils.getCilex_FX_NON_LC_SETTLEMENT(productChargeAmountNetOfPesoAmountPaid, FX_NON_LC_SETTLEMENT_cilexNumerator, FX_NON_LC_SETTLEMENT_cilexDenominator, FX_NON_LC_SETTLEMENT_cilexPercentage, cwtFlag, cwtPercentage, usdToPHPSpecialRate );"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "System.out.println(TR_LOAN_FLAG);\n" +
//                        "System.out.println(TR_LOAN_AMOUNT);\n" +
//                        "CalculatorUtils.getDocStampsAmount_FX_NON_LC_SETTLEMENT(productAmount, TR_LOAN_FLAG, TR_LOAN_AMOUNT);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BOOKING"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_BOOKING);\n" +
//                        "CalculatorUtils.getBookingCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_BOOKING);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("NOTARIAL"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_NOTARIAL);\n" +
//                        " CalculatorUtils.getNotarialFeeCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_NOTARIAL);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BSP"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_BSP);\n" +
//                        " CalculatorUtils.getBspCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_BSP);"
        ));

        productRef = productReferenceRepository.find(DocumentClass.DR, DocumentType.FOREIGN, null, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "CalculatorUtils.getBankCommission_FX_NON_LC_SETTLEMENT(productAmount, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, cwtFlag, cwtPercentage );"
                //"CalculatorUtils.getBankCommission_FX_NON_LC_SETTLEMENT(productAmount);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_CABLE);\n" +
//                        " CalculatorUtils.getCableFee_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_CABLE);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "System.out.println(\"productChargeAmountNetOfPesoAmountPaid\"+productChargeAmountNetOfPesoAmountPaid);\n" +
//                        "CalculatorUtils.getCilex_FX_NON_LC_SETTLEMENT(productChargeAmountNetOfPesoAmountPaid, FX_NON_LC_SETTLEMENT_cilexNumerator, FX_NON_LC_SETTLEMENT_cilexDenominator, FX_NON_LC_SETTLEMENT_cilexPercentage, cwtFlag, cwtPercentage, usdToPHPSpecialRate );"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "System.out.println(TR_LOAN_FLAG);\n" +
//                        "System.out.println(TR_LOAN_AMOUNT);\n" +
//                        "CalculatorUtils.getDocStampsAmount_FX_NON_LC_SETTLEMENT(productAmount, TR_LOAN_FLAG, TR_LOAN_AMOUNT);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BOOKING"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_BOOKING);\n" +
//                        "CalculatorUtils.getBookingCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_BOOKING);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("NOTARIAL"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_NOTARIAL);\n" +
//                        " CalculatorUtils.getNotarialFeeCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_NOTARIAL);"
        ));

        productRef = productReferenceRepository.find(DocumentClass.DP, DocumentType.FOREIGN, null, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "CalculatorUtils.getBankCommission_FX_NON_LC_SETTLEMENT(productAmount, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, cwtFlag, cwtPercentage );"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_CABLE);\n" +
//                        "CalculatorUtils.getCableFee_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_CABLE);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "System.out.println(\"productChargeAmountNetOfPesoAmountPaid\"+productChargeAmountNetOfPesoAmountPaid);\n" +
//                        "CalculatorUtils.getCilex_FX_NON_LC_SETTLEMENT(productChargeAmountNetOfPesoAmountPaid, FX_NON_LC_SETTLEMENT_cilexNumerator, FX_NON_LC_SETTLEMENT_cilexDenominator, FX_NON_LC_SETTLEMENT_cilexPercentage, cwtFlag, cwtPercentage, usdToPHPSpecialRate );"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "System.out.println(TR_LOAN_FLAG);\n" +
//                        "System.out.println(TR_LOAN_AMOUNT);\n" +
//                        "CalculatorUtils.getDocStampsAmount_FX_NON_LC_SETTLEMENT(productAmount, TR_LOAN_FLAG, TR_LOAN_AMOUNT);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BOOKING"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_BOOKING);\n" +
//                        "CalculatorUtils.getBookingCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_BOOKING);"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("NOTARIAL"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_FX_NON_LC_SETTLEMENT_NOTARIAL);\n" +
//                        " CalculatorUtils.getNotarialFeeCommission_FX_NON_LC_SETTLEMENT(default_FX_NON_LC_SETTLEMENT_NOTARIAL);"
        ));

    }

    private void initializeDM_NON_LC_Settlement_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for DM DA SETTLEMENT
        // get product reference for DM DA SETTLEMENT
        productRef = productReferenceRepository.find(DocumentClass.DP, DocumentType.DOMESTIC, null, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(productAmount);\n" +
//                        "CalculatorUtils.getBankCommission_DM_NON_LC_SETTLEMENT(productAmount, bankCommissionNumerator, bankCommissionDenominator, bankCommissionPercentage, cwtFlag, cwtPercentage );"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(default_DM_NON_LC_SETTLEMENT_CABLE);\n" +
//                        " CalculatorUtils.getCableFee_DM_NON_LC_SETTLEMENT(default_DM_NON_LC_SETTLEMENT_CABLE, \"Y\");"
        ));
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"productAmount:\"+productAmount);\n" +
//                        "System.out.println(\"TR_LOAN_FLAG:\"+TR_LOAN_FLAG);\n" +
//                        "System.out.println(\"TR_LOAN_AMOUNT:\"+TR_LOAN_AMOUNT);\n" +
//                        "CalculatorUtils.getDocStampsAmount_DM_NON_LC_SETTLEMENT(productAmount, TR_LOAN_FLAG, TR_LOAN_AMOUNT);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "BigDecimal.TEN;"
//                "System.out.println(\"remittanceFeeInUsd:\"+remittanceFeeInUsd);\n" +
//                        "System.out.println(\"usdToPHPSpecialRate:\"+usdToPHPSpecialRate);\n" +
//                        "CalculatorUtils.getRemittanceFee_DM_NON_LC_SETTLEMENT(usdToPHPSpecialRate, remittanceFeeInUsd, \"Y\");"
        ));

    }

    private void initializeIMPORT_ADVANCE_PAYMENT_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for IMPORT ADVANCE PAYMENT
        // get product reference for IMPORT ADVANCE PAYMENT
        productRef = productReferenceRepository.find(DocumentClass.IMPORT_ADVANCE, null, null, null);
        
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.PAYMENT,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.PAYMENT,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.PAYMENT,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.PAYMENT,
                "BigDecimal.TEN;"
        ));
    }

    private void initializeIMPORT_ADVANCE_REFUND_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for IMPORT ADVANCE REFUND
        // get product reference for IMPORT ADVANCE REFUND
        productRef = productReferenceRepository.find(DocumentClass.IMPORT_ADVANCE, null, null, null);
        
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.REFUND,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.REFUND,
                "BigDecimal.TEN;"
        ));
    }

    private void initializeEXPORT_ADVANCE_PAYMENT_Charges(Map<String, ChargeId> chargeIds){
    	ProductReference productRef;
    	
        // insert charges for EXPORT ADVANCE PAYMENT
        // get product reference for export ADVANCE PAYMENT
    	productRef = productReferenceRepository.find(DocumentClass.EXPORT_ADVANCE, null, null, null);
    	
    	tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.PAYMENT,
                "BigDecimal.TEN;"
        ));
    	 
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.PAYMENT,
                "BigDecimal.TEN;"
        ));
         
        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.PAYMENT,
                "BigDecimal.TEN;"
        ));
    }
    
    private void initializeEXPORT_ADVANCE_REFUND_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for EXPORT ADVANCE REFUND
        // get product reference for EXPORT ADVANCE REFUND
        productRef = productReferenceRepository.find(DocumentClass.EXPORT_ADVANCE, null, null, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.REFUND,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.REFUND,
                "BigDecimal.TEN;"
        ));
    }
    
    private void initializeEXPORT_LC_ADVISING_FIRST_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for EXPORT LC ADVISING OPENING - FIRST ADVISING
        // get product reference for EXPORT LC ADVISING OPENING - FIRST ADVISING
        productRef = productReferenceRepository.find(DocumentClass.EXPORT_ADVISING, null, DocumentSubType1.FIRST_ADVISING, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("ADVISING-EXPORT"), productRef.getProductId(), ServiceType.OPENING_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.OPENING_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("ADVISING-EXPORT"), productRef.getProductId(), ServiceType.AMENDMENT_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.AMENDMENT_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("ADVISING-EXPORT"), productRef.getProductId(), ServiceType.CANCELLATION_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.CANCELLATION_ADVISING,
                "BigDecimal.TEN;"
        ));
    }

    private void initializeEXPORT_LC_ADVISING_SECOND_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for EXPORT LC ADVISING OPENING - SECOND ADVISING
        // get product reference for EXPORT LC ADVISING OPENING - SECOND ADVISING
        productRef = productReferenceRepository.find(DocumentClass.EXPORT_ADVISING, null, DocumentSubType1.SECOND_ADVISING, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("ADVISING-EXPORT"), productRef.getProductId(), ServiceType.OPENING_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("OTHER-EXPORT"), productRef.getProductId(), ServiceType.OPENING_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("ADVISING-EXPORT"), productRef.getProductId(), ServiceType.AMENDMENT_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("OTHER-EXPORT"), productRef.getProductId(), ServiceType.AMENDMENT_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("ADVISING-EXPORT"), productRef.getProductId(), ServiceType.CANCELLATION_ADVISING,
                "BigDecimal.TEN;"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("OTHER-EXPORT"), productRef.getProductId(), ServiceType.CANCELLATION_ADVISING,
                "BigDecimal.TEN;"
        ));
    }

    private void initializeFX_BP_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for Export Bill Payment
        // get product reference for Export Bill Payment
        productRef = productReferenceRepository.find(DocumentClass.BP, DocumentType.FOREIGN, null, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-EXPORT"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("POSTAGE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CORRES-EXPORT"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

    }

    private void initializeDM_BP_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for Export Bill Payment
        // get product reference for Export Bill Payment
        productRef = productReferenceRepository.find(DocumentClass.BP, DocumentType.DOMESTIC, null, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("OTHER-EXPORT"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("POSTAGE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.NEGOTIATION,
                "new BigDecimal(1000);"
        ));
    }

    private void initializeFX_BC_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for Export Bills Collection
        // get product reference for Export Bills Collection
        productRef = productReferenceRepository.find(DocumentClass.BC, DocumentType.FOREIGN, null, null);

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("POSTAGE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CABLE"), productRef.getProductId(), ServiceType.CANCELLATION,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("COURIER"), productRef.getProductId(), ServiceType.CANCELLATION,
                "new BigDecimal(1000);"
        ));
    }

    private void initializeDM_BC_Charges(Map<String, ChargeId> chargeIds) {

        ProductReference productRef;

        // insert charges for Domestic Bills Collection
        // get product reference for Domestic Bills Collection
        productRef = productReferenceRepository.find(DocumentClass.BC, DocumentType.DOMESTIC, null, null);


        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("DOCSTAMPS"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("BC"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("POSTAGE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("CILEX"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

        tradeServiceChargeReferenceRepository.save(new TradeServiceChargeReference(chargeIds.get("REMITTANCE"), productRef.getProductId(), ServiceType.SETTLEMENT,
                "new BigDecimal(1000);"
        ));

    }

    public List<TradeServiceChargeReference> getChargesForService(ProductId productId, ServiceType serviceType) {

        System.out.println("Product Id To Search:" + productId.toString());
        System.out.println("Service Type To Search:" + serviceType.toString());

        //From Map Implementation
        //return getChargesForServiceInMap(productId, serviceType);

        //From DB Implementation
        return getChargesForServiceFromDB(productId, serviceType);

    }

    public List<TradeServiceChargeReference> getChargesForServiceInMap(ProductId productId, ServiceType serviceType) {
        System.out.println("using getChargesForServiceInMap");
        Map<String, List<TradeServiceChargeReference>> servicesList = tradeServiceChargesRegistry.get(productId.toString());
        if (servicesList != null) {
            for (String s : servicesList.keySet()) {
                System.out.println("Service list item key:" + s);
                System.out.println("Service list item value:" + servicesList.get(s));
            }

            // return whatever charges we have for this service type
            return servicesList.get(serviceType.toString());
        } else {
            return null;
        }
    }

    public List<TradeServiceChargeReference> getChargesForServiceFromDB(ProductId productId, ServiceType serviceType) {
        System.out.println("using getChargesForServiceFromDB");
        return tradeServiceChargeReferenceRepository.getCharges(productId, serviceType);
    }

    private void initializeTradeServiceChargesDefaultsRegistry() {


        //TODO: Insert from table
        System.out.println("initializeTradeServiceChargesDefaultsRegistry");
        tradeServiceChargesDefaultsRegistry = new HashMap<String, Object>();
        tradeServiceChargesDefaultsRegistry.put("bankCommissionNumerator", new BigDecimal("1"));
        tradeServiceChargesDefaultsRegistry.put("bankCommissionDenominator", new BigDecimal("8"));
        tradeServiceChargesDefaultsRegistry.put("bankCommissionPercentage", new BigDecimal("0.01"));
        tradeServiceChargesDefaultsRegistry.put("default_bankCommissionNumerator", new BigDecimal("1"));
        tradeServiceChargesDefaultsRegistry.put("default_bankCommissionDenominator", new BigDecimal("8"));
        tradeServiceChargesDefaultsRegistry.put("default_bankCommissionPercentage", new BigDecimal("0.01"));
        tradeServiceChargesDefaultsRegistry.put("cableFee", new BigDecimal("500"));
        tradeServiceChargesDefaultsRegistry.put("default_cableFee", new BigDecimal("500"));
        tradeServiceChargesDefaultsRegistry.put("default_Opening_FXLC_cableFee", new BigDecimal("800"));
        tradeServiceChargesDefaultsRegistry.put("notarialAmount", new BigDecimal("50"));
        tradeServiceChargesDefaultsRegistry.put("default_notarialAmount", new BigDecimal("50"));
        tradeServiceChargesDefaultsRegistry.put("suppliesFee", new BigDecimal("50"));
        tradeServiceChargesDefaultsRegistry.put("default_suppliesFee", new BigDecimal("50"));
        tradeServiceChargesDefaultsRegistry.put("cilexNumerator", new BigDecimal("1"));
        tradeServiceChargesDefaultsRegistry.put("cilexDenominator", new BigDecimal("4"));
        tradeServiceChargesDefaultsRegistry.put("cilexPercentage", new BigDecimal("0.01"));
        tradeServiceChargesDefaultsRegistry.put("cilexDollarMinimum", new BigDecimal("20"));
        tradeServiceChargesDefaultsRegistry.put("default_cilexNumerator", new BigDecimal("1"));
        tradeServiceChargesDefaultsRegistry.put("default_cilexDenominator", new BigDecimal("4"));
        tradeServiceChargesDefaultsRegistry.put("default_cilexPercentage", new BigDecimal("0.01"));
        tradeServiceChargesDefaultsRegistry.put("FX_NON_LC_SETTLEMENT_cilexNumerator", new BigDecimal("1"));
        tradeServiceChargesDefaultsRegistry.put("FX_NON_LC_SETTLEMENT_cilexDenominator", new BigDecimal("4"));
        tradeServiceChargesDefaultsRegistry.put("FX_NON_LC_SETTLEMENT_cilexPercentage", new BigDecimal("0.01"));
        tradeServiceChargesDefaultsRegistry.put("commitmentFeeNumerator", new BigDecimal("1"));
        tradeServiceChargesDefaultsRegistry.put("commitmentFeeDenominator", new BigDecimal("4"));
        tradeServiceChargesDefaultsRegistry.put("commitmentFeePercentage", new BigDecimal("0.01"));
        tradeServiceChargesDefaultsRegistry.put("default_commitmentFeeNumerator", new BigDecimal("1"));
        tradeServiceChargesDefaultsRegistry.put("default_commitmentFeeDenominator", new BigDecimal("4"));
        tradeServiceChargesDefaultsRegistry.put("default_commitmentFeePercentage", new BigDecimal("0.01"));
        tradeServiceChargesDefaultsRegistry.put("advisingFee", new BigDecimal("50"));
        tradeServiceChargesDefaultsRegistry.put("default_advisingFee", new BigDecimal("50"));
        tradeServiceChargesDefaultsRegistry.put("advisingFeeFX_LC_OPENING", new BigDecimal("50"));
        tradeServiceChargesDefaultsRegistry.put("confirmingFeeNumerator", new BigDecimal("1"));
        tradeServiceChargesDefaultsRegistry.put("confirmingFeeDenominator", new BigDecimal("8"));
        tradeServiceChargesDefaultsRegistry.put("confirmingFeePercentage", new BigDecimal("0.01"));
        tradeServiceChargesDefaultsRegistry.put("default_confirmingFeeNumerator", new BigDecimal("1"));
        tradeServiceChargesDefaultsRegistry.put("default_confirmingFeeDenominator", new BigDecimal("8"));
        tradeServiceChargesDefaultsRegistry.put("default_confirmingFeePercentage", new BigDecimal("0.01"));
        tradeServiceChargesDefaultsRegistry.put("default_INDEMNITY_Cancel_Fee", new BigDecimal("300"));
        tradeServiceChargesDefaultsRegistry.put("default_INDEMNITY_BC", new BigDecimal("500"));
        tradeServiceChargesDefaultsRegistry.put("default_INDEMNITY_DOCSTAMPS", new BigDecimal("37.5"));
        tradeServiceChargesDefaultsRegistry.put("advising", "N");
        tradeServiceChargesDefaultsRegistry.put("advanceCorresChargesFlag", "N");
        tradeServiceChargesDefaultsRegistry.put("confirmationInstructionsFlag", "N");
        tradeServiceChargesDefaultsRegistry.put("usdToPHPSpecialRate", BigDecimal.ZERO);
        tradeServiceChargesDefaultsRegistry.put("tenorSwitch", "off");
        tradeServiceChargesDefaultsRegistry.put("expiryDateModifiedDays", BigDecimal.ZERO);
        tradeServiceChargesDefaultsRegistry.put("amountSwitch", "off");
        tradeServiceChargesDefaultsRegistry.put("confirmationInstructionsFlagSwitch", "off");
        tradeServiceChargesDefaultsRegistry.put("amountSwitch", "off");
        tradeServiceChargesDefaultsRegistry.put("lcAmountFlag", "none");
        tradeServiceChargesDefaultsRegistry.put("lcAmountFlagDisplay", "none");
        tradeServiceChargesDefaultsRegistry.put("expiryDateSwitchDisplay", "off");
        tradeServiceChargesDefaultsRegistry.put("expiryDateFlagDisplay", "none");
        tradeServiceChargesDefaultsRegistry.put("expiryDateFlag", "off");
        tradeServiceChargesDefaultsRegistry.put("confirmationInstructionsFlagSwitch", "off");
        tradeServiceChargesDefaultsRegistry.put("narrativeSwitchDisplay", "off");
        tradeServiceChargesDefaultsRegistry.put("expiryDateSwitch", "off");
        tradeServiceChargesDefaultsRegistry.put("amountFrom", BigDecimal.ZERO);
        tradeServiceChargesDefaultsRegistry.put("oldDocAmount", BigDecimal.ZERO);
        tradeServiceChargesDefaultsRegistry.put("amountTo", BigDecimal.ZERO);
        tradeServiceChargesDefaultsRegistry.put("usancePeriod", BigDecimal.ZERO);
        tradeServiceChargesDefaultsRegistry.put("expiryDateModifiedDays", BigDecimal.ZERO);
        tradeServiceChargesDefaultsRegistry.put("expiry", new Date().toString());
        tradeServiceChargesDefaultsRegistry.put("default_FX_NON_LC_SETTLEMENT_BSP", new BigDecimal("100"));
        tradeServiceChargesDefaultsRegistry.put("default_FX_NON_LC_SETTLEMENT_CABLE", new BigDecimal("1000"));
        tradeServiceChargesDefaultsRegistry.put("default_DM_NON_LC_SETTLEMENT_CABLE", new BigDecimal("500"));
        tradeServiceChargesDefaultsRegistry.put("default_FX_NON_LC_SETTLEMENT_BOOKING", new BigDecimal("500"));
        tradeServiceChargesDefaultsRegistry.put("default_FX_NON_LC_SETTLEMENT_NOTARIAL", new BigDecimal("50"));
        tradeServiceChargesDefaultsRegistry.put("TR_LOAN_AMOUNT", new BigDecimal("0"));
        tradeServiceChargesDefaultsRegistry.put("productChargeAmountNetOfPesoAmountPaid", new BigDecimal("0"));
        tradeServiceChargesDefaultsRegistry.put("remittanceFee", new BigDecimal("18"));
        tradeServiceChargesDefaultsRegistry.put("default_remittanceFee", new BigDecimal("18"));
        tradeServiceChargesDefaultsRegistry.put("remittanceFeeInUsd", new BigDecimal("18"));
        tradeServiceChargesDefaultsRegistry.put("remittanceFlag", "N");
        tradeServiceChargesDefaultsRegistry.put("default_remittanceFeeInUsd", new BigDecimal("18"));
        tradeServiceChargesDefaultsRegistry.put("EBC_SETTLEMENT_RemittanceFeeinUsd", new BigDecimal("20"));
        tradeServiceChargesDefaultsRegistry.put("EBC_SETTLEMENT_PostageFee", new BigDecimal("400"));
//        tradeServiceChargesDefaultsRegistry.put("expiryDateTo", "01/01/2050");
        tradeServiceChargesDefaultsRegistry.put("processingDate", "01/01/2050");
        tradeServiceChargesDefaultsRegistry.put("cwtPercentage", new BigDecimal("0.98"));
        tradeServiceChargesDefaultsRegistry.put("docStampsAmountPer", new BigDecimal("0.30"));
        tradeServiceChargesDefaultsRegistry.put("docStampsRoundToThisNumber", new BigDecimal("200"));
        tradeServiceChargesDefaultsRegistry.put("default_advisingFeeMinimum", new BigDecimal("50.0"));
        tradeServiceChargesDefaultsRegistry.put("default_confirmingFeeMinimum", new BigDecimal("50.0"));
        tradeServiceChargesDefaultsRegistry.put("default_commitmentFeeMinimum", new BigDecimal("500.0"));
        tradeServiceChargesDefaultsRegistry.put("amountTo", new BigDecimal("0.0"));
        tradeServiceChargesDefaultsRegistry.put("amountFrom", new BigDecimal("0.0"));
        tradeServiceChargesDefaultsRegistry.put("cableFeeFlag", "N");
    }

    private void initializeTradeServiceChargesDefaultsRegistryTable() {
        //TODO: Insert from table
        System.out.println("initializeTradeServiceChargesDefaultsRegistryTable");
        getDefaultValuesForServiceMap();
    }

    public Map<String, Object> getDefaultValuesForServiceMap() {

        //ORIGINAL
        HashMap<String, Object> temp = new HashMap<String, Object>();
//        if (tradeServiceChargesDefaultsRegistry != null && !tradeServiceChargesDefaultsRegistry.isEmpty()) {
//            for (String keyed : tradeServiceChargesDefaultsRegistry.keySet()) {
//                Object ob = tradeServiceChargesDefaultsRegistry.get(keyed);
//                temp.put(keyed, ob);
//            }
//        }

        List<ChargeDefaultsReference> holder =  chargeDefaultsReferenceRepository.getList();
        System.out.println("getDefaultValuesForServiceMap:::::::::::"+holder.size());
        if(holder !=null && !holder.isEmpty()){
            for (ChargeDefaultsReference chargeDefaultsReference: holder) {
                if(chargeDefaultsReference.getType().equalsIgnoreCase("TEXT")){
//                    System.out.println("TEXT:"+chargeDefaultsReference.getId());
                    //System.out.println("chargeDefaultsReference.getMatcher():"+chargeDefaultsReference.getMatcher());
                    temp.put(chargeDefaultsReference.getMatcher(), chargeDefaultsReference.getValue());
                } else if(chargeDefaultsReference.getType().equalsIgnoreCase("BIGDECIMAL")){
//                    System.out.println("BIGDECIMAL:"+chargeDefaultsReference.getId());
                    //System.out.println("chargeDefaultsReference.getMatcher():"+chargeDefaultsReference.getMatcher());
                    temp.put(chargeDefaultsReference.getMatcher(), new BigDecimal(chargeDefaultsReference.getValue()));
                } else if(chargeDefaultsReference.getType().equalsIgnoreCase("DATE")){
//                    System.out.println("DATE:"+chargeDefaultsReference.getId());
                    temp.put(chargeDefaultsReference.getMatcher(), new Date());
                } else {
//                    System.out.println("ELSE:"+chargeDefaultsReference.getId());
                    //System.out.println("chargeDefaultsReference.getMatcher():"+chargeDefaultsReference.getMatcher());
                    temp.put(chargeDefaultsReference.getMatcher(), chargeDefaultsReference.getValue());
                }
//                System.out.println("temp:"+temp.size());
            }
        }

        System.out.println("getDefaultValuesForServiceMap:::::::::::"+temp.size());
        return temp;
    }

}
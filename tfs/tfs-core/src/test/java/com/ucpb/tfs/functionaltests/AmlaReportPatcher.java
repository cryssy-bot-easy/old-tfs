package com.ucpb.tfs.functionaltests;

import com.ucpb.tfs.domain.product.*;
import com.ucpb.tfs.domain.product.event.*;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.event.AmlaInformationLogger;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ucpb.tfs.domain.service.enumTypes.DocumentClass.*;
import static com.ucpb.tfs.domain.service.enumTypes.ServiceType.*;
import static com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus.*;

/**
 * Not really a test class. Do not run this as part of the build.
 *
 * Convenience class used to regenerate the amla files saved in a target database.
 *
 */

//@Ignore("Should not be run as part of the build")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration
@ContextConfiguration("classpath:amla-report-integration-tests.xml")
public class AmlaReportPatcher {

    @Autowired
    private TradeServiceRepository tradeServiceRepository;

    @Autowired
    private AmlaInformationLogger amlaInformationLogger;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private TradeProductRepository tradeProductRepository;

    //TODO: refactor later?
//    @Ignore
    @Test
    @Rollback(false)
    public void regenerateAmlaLogs(){

        List<TradeService> tradeServices = tradeServiceRepository.list();
        for(TradeService tradeService : tradeServices){
            if(APPROVED.equals(tradeService.getStatus()) ||
                    POSTED.equals(tradeService.getStatus()) ||
                    POST_APPROVED.equals(tradeService.getStatus())){

                if(LC.equals(tradeService.getDocumentClass())){
                    if(OPENING.equals(tradeService.getServiceType())){
                        LetterOfCreditCreatedEvent event = new LetterOfCreditCreatedEvent(tradeService,null,"");
                        amlaInformationLogger.logLcCreatedEvent(event);
                    }

                    if(AMENDMENT.equals(tradeService.getServiceType())){
                        LCAmendedEvent event = new LCAmendedEvent(tradeService,null,null,"");
                        amlaInformationLogger.logLcAmendedEvent(event);
                    }

                    if(NEGOTIATION.equals(tradeService.getServiceType())){
                        LetterOfCredit letterOfCredit = new LetterOfCredit();
                        letterOfCredit.updateDetails(tradeService.getDetails());
                        LCNegotiationCreatedEvent event = new LCNegotiationCreatedEvent(tradeService,letterOfCredit,"");
                        amlaInformationLogger.logLcNegotiationEvent(event);
                    }

                    if(CANCELLATION.equals(tradeService.getServiceType())){
                        LetterOfCredit lcCancellation = (LetterOfCredit)tradeProductRepository.load(tradeService.getDocumentNumber());
                        LCCancelledEvent event = new LCCancelledEvent(tradeService,lcCancellation,"");
                        amlaInformationLogger.logLcCancelledEvent(event);
                    }
                }

                if(INDEMNITY.equals(tradeService.getDocumentClass()) && OPENING.equals(tradeService.getServiceType())){
                    IndemnityCreatedEvent event = new IndemnityCreatedEvent(tradeService,null,null,"");
                    // amlaInformationLogger.logIndemnityCreatedEvent(event);
                }


                if(DA.equals(tradeService.getDocumentClass())){
                    if(NEGOTIATION_ACKNOWLEDGEMENT.equals(tradeService.getServiceType())){
                        DACreatedEvent event = new DACreatedEvent(tradeService,"");
                        // amlaInformationLogger.logDaCreatedEvent(event);
                    }

                    if(SETTLEMENT.equals(tradeService.getServiceType())){
                        DocumentAgainstAcceptance daSettle = (DocumentAgainstAcceptance)tradeProductRepository.load(tradeService.getDocumentNumber());
                        DASettlementCreatedEvent event = new DASettlementCreatedEvent(tradeService,daSettle,"");
                        amlaInformationLogger.logDaSettlementCreatedEvent(event);
                    }

                    if(CANCELLATION.equals(tradeService.getServiceType())){
                        DocumentAgainstAcceptance da = (DocumentAgainstAcceptance)tradeProductRepository.load(tradeService.getDocumentNumber());
                        DACancelledEvent event = new DACancelledEvent(tradeService,da,"");
                        amlaInformationLogger.logDaCancelledEvent(event);
                    }
                }

                if(OA.equals(tradeService.getDocumentClass())){
                    if(NEGOTIATION.equals(tradeService.getServiceType())){
                        OACreatedEvent event = new OACreatedEvent(tradeService,"");
                        amlaInformationLogger.logOACreatedEvent(event);
                    }
                    if(SETTLEMENT.equals(tradeService.getServiceType())){
                        OpenAccount oaSettle = (OpenAccount)tradeProductRepository.load(tradeService.getDocumentNumber());
                        OASettlementCreatedEvent event = new OASettlementCreatedEvent(tradeService,oaSettle,"");
                        amlaInformationLogger.logOASettlementCreatedEvent(event);
                    }

                    if(CANCELLATION.equals(tradeService.getServiceType())){
                        OpenAccount oa = (OpenAccount)tradeProductRepository.load(tradeService.getDocumentNumber());
                        OACancelledEvent event = new OACancelledEvent(tradeService,oa,"");
                        amlaInformationLogger.logOACancelledEvent(event);
                    }
                }

                if(DP.equals(tradeService.getDocumentClass())){
                    if(NEGOTIATION.equals(tradeService.getServiceType())){
                        DPCreatedEvent dpCreatedEvent = new DPCreatedEvent(tradeService,"");
                        amlaInformationLogger.logDpCreatedEvent(dpCreatedEvent);
                    }

                    if(SETTLEMENT.equals(tradeService.getServiceType())){
                        DocumentAgainstPayment dpSettle = (DocumentAgainstPayment)tradeProductRepository.load(tradeService.getDocumentNumber());
                        DPSettlementCreatedEvent event = new DPSettlementCreatedEvent(tradeService,dpSettle,"");
                        amlaInformationLogger.logDpSettlementCreatedEvent(event);
                    }

                    if(CANCELLATION.equals(tradeService.getServiceType())){
                        DocumentAgainstPayment dp = (DocumentAgainstPayment)tradeProductRepository.load(tradeService.getDocumentNumber());
                        DPCancelledEvent event = new DPCancelledEvent(tradeService,dp,"");
                        amlaInformationLogger.logDpCancelledEvent(event);
                    }

                }

                if(DR.equals(tradeService.getDocumentClass())){
                    if(NEGOTIATION.equals(tradeService.getServiceType())){
                        DRCreatedEvent event = new DRCreatedEvent(tradeService,"");
                        amlaInformationLogger.logDirectRemittanceCreatedEvent(event);
                    }

                    if(SETTLEMENT.equals(tradeService.getServiceType())){
                        DirectRemittance drSettle = (DirectRemittance)tradeProductRepository.load(tradeService.getDocumentNumber());
                        DRSettlementCreatedEvent drSettlementCreatedEvent = new DRSettlementCreatedEvent(tradeService,drSettle,"");
                        amlaInformationLogger.logDirectRemittanceSettlementCreatedEvent(drSettlementCreatedEvent);
                    }

                    if(CANCELLATION.equals(tradeService.getServiceType())){
                        DirectRemittance dr = (DirectRemittance)tradeProductRepository.load(tradeService.getDocumentNumber());
                        DRCancelledEvent event = new DRCancelledEvent(tradeService,dr,"");
                        amlaInformationLogger.logDirectRemittanceCancelledEvent(event);
                    }
                }


            }
        }

        sessionFactory.getCurrentSession().flush();

    }

}

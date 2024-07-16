package com.ucpb.tfs.domain.service.event;

import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ucpb.tfs.domain.corresCharges.event.CorresChargeActualApprovedEvent;
import com.ucpb.tfs.domain.mtmessage.MtMessage;
import com.ucpb.tfs.domain.mtmessage.MtMessageRepository;
import com.ucpb.tfs.domain.mtmessage.enumTypes.MessageClass;
import com.ucpb.tfs.domain.payment.Payment;
import com.ucpb.tfs.domain.payment.PaymentRepository;
import com.ucpb.tfs.domain.product.UALoanSettledEvent;
import com.ucpb.tfs.domain.product.event.*;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.*;
import com.ucpb.tfs.domain.service.utils.SwiftMessageSenderUtils;
import com.ucpb.tfs.interfaces.services.SwiftMessageService;
import com.ucpb.tfs.interfaces.services.exception.ValidationException;
import com.ucpb.tfs.swift.message.MessageBlock;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.Tag;
import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder;
import com.ucpb.tfs.swift.message.writer.SwiftMessageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;

import com.ucpb.tfs2.utils.TradeServiceUtils;

/**
 */
@Component
public class SwiftMessageSender {

    @Autowired
    private SwiftMessageBuilder swiftMessageBuilder;

    @Autowired
    private SwiftMessageService swiftService;

    @Autowired
    private MtMessageRepository mtMessageRepository;

    @Autowired
    private SwiftMessageWriter writer;

    @Autowired
    private PaymentRepository paymentRepository;



    @EventListener
    public void generateLetterOfCreditCreationMessages(LetterOfCreditCreatedEvent event){

    	if(ServiceType.OPENING.equals(event.getTradeService().getServiceType()) &&
    			isApproved(event.getTradeService())){
	    	System.out.println("inside generateLetterOfCreditCreationMessages");
	    	System.out.println(event.getTradeService().getDocumentType());
	    	System.out.println(event.getTradeService().getDocumentSubType1());
	    	System.out.println(event.getTradeService().getServiceType());
	    	System.out.println(event.getTradeService().getDetails().get("destinationBank"));
	    	System.out.println(event.getTradeService().getDetails().get("reimbursingBankIdentifierCode"));
	    	System.out.println(event.getTradeService().getDetails().get("reimbursingBankIdentifierCodeTo"));
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("====================================================");
	        if(DocumentType.FOREIGN.equals(event.getTradeService().getDocumentType())) {
	            if(DocumentSubType1.REGULAR.equals(event.getTradeService().getDocumentSubType1()) ||
	                    DocumentSubType1.CASH.equals(event.getTradeService().getDocumentSubType1())) {
	                int numberOfMt = TradeServiceUtils.getSequenceNumber(event.getTradeService());
	                TradeService ts;
	                
	                if(numberOfMt > 0) {
	                	for (int i = 0; i < numberOfMt; i++) {
	                		if (i == 0) {
	                			sendSwiftMessage("700",TradeServiceUtils.getSplittedValues(event.getTradeService(), i), i + 1, numberOfMt);
	                		} else {
		                		ts = TradeServiceUtils.getSplittedValues(event.getTradeService(), i);
		                		ts.getDetails().put("sequenceOrder",(i + 1) + "/" + numberOfMt);
		                		sendSwiftMessage("701",ts, i + 1, numberOfMt);
	                		}
	                	}
	                }
	                
	                
	               	String reimbursingBankCode = SwiftMessageSenderUtils.getExistingValue(
	               			event.getTradeService().getDetails().get("reimbursingBankIdentifierCodeTo"),
	               			event.getTradeService().getDetails().get("reimbursingBankIdentifierCode"));
	               	
	            	String destinationBank = SwiftMessageSenderUtils.getExistingValue(
	            			event.getTradeService().getDetails().get("destinationBankTo"),
	            			event.getTradeService().getDetails().get("destinationBank"));
		
	            	System.out.println("====================================================");
	            	System.out.println("REIMBURSING BANK CODE:"+reimbursingBankCode);
	            	System.out.println("DESTINATION BANK CODE:"+destinationBank);
	            	System.out.println("====================================================");
        		if(!reimbursingBankCode.isEmpty() && !reimbursingBankCode.equals(destinationBank)){
	        			sendSwiftMessage("740",event.getTradeService());        			
	        		}
	        }
	        if(DocumentSubType1.STANDBY.equals(event.getTradeService().getDocumentSubType1()) &&
	        		DocumentType.FOREIGN.equals(event.getTradeService().getDocumentType())){
	            	sendSwiftMessage("760",event.getTradeService());
	        	}
	        }
    	}
    }

    @EventListener
    public void generateLcAmendmentStandbyMessages(LCAmendedEvent event){
    	if(ServiceType.AMENDMENT.equals(event.getTradeService().getServiceType()) &&
    			isApproved(event.getTradeService())){
	    	System.out.println("inside generateLcAmendmentStandbyMessages");
	    	System.out.println(event.getTradeService().getDocumentType());
	    	System.out.println(event.getTradeService().getDocumentSubType1());
	    	System.out.println(event.getTradeService().getDetails().get("destinationBank"));
	    	System.out.println(event.getTradeService().getDetails().get("destinationBankTo"));
	    	System.out.println(event.getTradeService().getDetails().get("reimbursingBankIdentifierCode"));
	    	System.out.println(event.getTradeService().getDetails().get("reimbursingBankIdentifierCodeTo"));
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("====================================================");
	        if(DocumentType.FOREIGN.equals(event.getTradeService().getDocumentType())){
	            if(DocumentSubType1.REGULAR.equals(event.getTradeService().getDocumentSubType1()) ||
	                    DocumentSubType1.CASH.equals(event.getTradeService().getDocumentSubType1())){
	                int numberOfMt = TradeServiceUtils.getSequenceNumber(event.getTradeService());
	                TradeService ts;
	                
	                if(numberOfMt > 0) {
	                	for (int i = 0; i < numberOfMt; i++) {
	                		if (i == 0) {
	                			sendSwiftMessage("707",TradeServiceUtils.getSplittedValues(event.getTradeService(), i), i + 1, numberOfMt);
	                		} else {
		                		ts = TradeServiceUtils.getSplittedValues(event.getTradeService(), i);
		                		ts.getDetails().put("sequenceOrder",(i + 1) + "/" + numberOfMt);
		                		sendSwiftMessage("708",ts, i + 1, numberOfMt);
	                		}
	                	}
	                }
	                
	                
	               	String reimbursingBankCode = SwiftMessageSenderUtils.getExistingValue(
	               			event.getTradeService().getDetails().get("reimbursingBankIdentifierCodeTo"),
	               			event.getTradeService().getDetails().get("reimbursingBankIdentifierCodeFrom"),
	               			event.getTradeService().getDetails().get("reimbursingBankIdentifierCode"));
	               	
	            	String destinationBank = SwiftMessageSenderUtils.getExistingValue(
	            			event.getTradeService().getDetails().get("destinationBankTo"),
	            			event.getTradeService().getDetails().get("destinationBankFrom"));	
	            	
	        		System.out.println("====================================================");
	        		System.out.println("REIMBURSING BANK CODE:"+reimbursingBankCode);
	        		System.out.println("DESTINATION BANK CODE:"+destinationBank);
	        		System.out.println("====================================================");
	        		
	        		if(
	        				SwiftMessageSenderUtils.isNotEmpty(
	        					event.getTradeService().getDetails().get("positiveToleranceLimitTo"),	
	        					event.getTradeService().getDetails().get("negativeToleranceLimitTo"),	
	        					event.getTradeService().getDetails().get("maximumCreditAmountTo"),	
	        					event.getTradeService().getDetails().get("additionalAmountsCoveredTo"),	
	        					event.getTradeService().getDetails().get("amountTo"),	
	        					event.getTradeService().getDetails().get("expiryDateTo")
	        				) &&
	        				(!reimbursingBankCode.isEmpty() && !reimbursingBankCode.equals(destinationBank))
	        		){
	        			System.out.println("MT 747 GENERATED");
	        			sendSwiftMessage("747",event.getTradeService());
	        		}
	            }
	            else if(DocumentSubType1.STANDBY.equals(event.getTradeService().getDocumentSubType1())){
	                sendSwiftMessage("767",event.getTradeService());
	            }
	        }
    	}
    }

    @EventListener
    public void generateMt752Mt202AndMt103ForLcNegotiation(LCNegotiationCreatedEvent event){
    	if(ServiceType.NEGOTIATION.equals(event.getTradeService().getServiceType()) &&
    			isApproved(event.getTradeService())){
	     	System.out.println("inside generateMt752Mt202AndMt103ForLcNegotiation");
	    	System.out.println(event.getTradeService().getDocumentType());
	    	System.out.println(event.getTradeService().getDocumentSubType1());
	    	System.out.println(event.getTradeService().getDetails().get("generateMt"));
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("====================================================");
	    	if(DocumentType.FOREIGN.equals(event.getTradeService().getDocumentType())){
	    		if(event.getTradeService().getDetails().get("generateMt") != null){
	    			if(event.getTradeService().getDetails().get("generateMt").toString().equals("MT752")){
	    				sendSwiftMessage("752",event.getTradeService());    			
	    			}else if(event.getTradeService().getDetails().get("generateMt").toString().equals("MT202")){
	    				sendSwiftMessage("202",event.getTradeService());    			    				
	    			}
	    		}
	    	}
	    	else if(DocumentType.DOMESTIC.equals(event.getTradeService().getDocumentType())){
	    		Payment payment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.SETTLEMENT);
	            if(payment != null && payment.hasSwift()){
	            	System.out.println("inside paymentRepository");
	        		System.out.println(payment.hasSwift());
	        		System.out.println("====================================================");
	                sendSwiftMessage("103",event.getTradeService());
	            }
	    	}
    	}
    }
    
    @EventListener
    public void generateLcNegotiationDiscrepancy(LCNegotiationDiscrepancyCreatedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateLcNegotiationDiscrepancy");
	    	System.out.println(event.getTradeService().getDocumentType());
	    	System.out.println(event.getTradeService().getDocumentSubType1());
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("====================================================");
	//    	For those with Discrepancy, always generated during Inward Bills for Collection (Data Entry)
	        if(DocumentType.FOREIGN.equals(event.getTradeService().getDocumentType())) {
	            sendSwiftMessage("734",event.getTradeService());
	        }
    	}
    }    

    @EventListener
    public void generateDpSettlementMessage(DPSettlementCreatedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateDpSettlementMessage");
			System.out.println(event.getTradeService().getDocumentType());
			System.out.println(event.getTradeService().getDocumentSubType1());
			System.out.println(event.getTradeService().getDocumentNumber().toString());
			System.out.println("====================================================");
	        if(DocumentType.DOMESTIC.equals(event.getTradeService().getDocumentType())){
	            Payment payment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.SETTLEMENT);
	            if(payment != null && payment.hasSwift()){
	            	System.out.println("inside paymentRepository");
	            	System.out.println(payment.hasSwift());
	            	System.out.println("====================================================");
	                sendSwiftMessage("103",event.getTradeService());
	            }
	        }else{
	        	sendSwiftMessage("400",event.getTradeService());
	        	sendSwiftMessage("202",event.getTradeService());        	
	        }
    	}
    }
    
    @EventListener
    public void generate410ForDpAcknowledgement(DPCreatedEvent event){
    	if(isApproved(event.getTradeService()) && event.getTradeService().getDocumentType().equals(DocumentType.FOREIGN)){
	    	System.out.println("inside generate410ForDpAcknowledgement");
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("============================================================");
	        sendSwiftMessage("410",event.getTradeService());
    	}
    }

    @EventListener
    public void generateDrSettlementMessage(DRSettlementCreatedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateDrSettlementMessage");
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("============================================================");
	    	sendSwiftMessage("103",event.getTradeService());
    	}
    }

    @EventListener
    public void generateOaSettlementMessage(OASettlementCreatedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateOaSettlementMessage");
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("============================================================");
	    	sendSwiftMessage("103",event.getTradeService());
    	}
    }

    @EventListener
    public void generateMt103ForImportAdvancePayment(ImportAdvancePaymentCreatedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt103ForImportAdvancePayment");
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("============================================================");
	    	sendSwiftMessage("103",event.getTradeService());
    	}
    }

    @EventListener
    public void generateDaCreatedEventMessages(DACreatedEvent event){
    	if(isApproved(event.getTradeService())){
    		System.out.println("inside generateDaCreatedEventMessages");
    		System.out.println(event.getTradeService().getDocumentNumber().toString());
    		System.out.println("============================================================");
    		sendSwiftMessage("410",event.getTradeService());
    	}
    }

    @EventListener
    public void generateDaSettlementMessages(DASettlementCreatedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateDaSettlementMessages");
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("============================================================");
	    	System.out.println("============================================================");
	        sendSwiftMessage("202",event.getTradeService());
	        sendSwiftMessage("400",event.getTradeService());
    	}
    }

    @EventListener
    public void generateDaNegotiationAcceptanceMessages(DAAcceptedEvent event){
    	if(isApproved(event.getTradeService())){
    		System.out.println("inside generateDaNegotiationAcceptanceMessages");
    		System.out.println(event.getTradeService().getDocumentNumber().toString());
    		System.out.println("============================================================");
    		sendSwiftMessage("412",event.getTradeService());
    	}
    }

    @EventListener
    public void generateMt202AndMt103ForUaLoanSettlement(UALoanSettledEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt202AndMt103ForUaLoanSettlement");
	    	System.out.println(event.getTradeService().getDetails().get("generateMt"));
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("============================================================");
	    	if(event.getTradeService().getDetails().get("generateMt") != null){
	    		if(event.getTradeService().getDetails().get("generateMt").toString().equals("MT202")){
	    			sendSwiftMessage("202",event.getTradeService());    			
	    		}
	    	}
	    	if(DocumentType.DOMESTIC.equals(event.getTradeService().getDocumentType())){
	    		Payment payment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.SETTLEMENT);
	            if(payment != null && payment.hasSwift()){
	            	System.out.println("inside paymentRepository");
	        		System.out.println(payment.hasSwift());
	        		System.out.println("====================================================");
	                sendSwiftMessage("103",event.getTradeService());
	            }
	    	}
    	}
    }


    @EventListener
    public void generateMt730ForExportAdvisingOpening(ExportAdvisingCreatedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt730ForExportAdvisingOpening");
	    	System.out.println(event.getExportAdvising().getWithMt730());
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("=======================================================");
	        if(event.getExportAdvising().getWithMt730()){
	            sendSwiftMessage("730",event.getTradeService());
	        }
    	}
    }

    @EventListener
    public void generateMt730ForExportAdvisingAmendment(ExportAdvisingAmendedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt730ForExportAdvisingAmendment");
	    	System.out.println(event.getAmendedExportAdvising().getWithMt730());
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("=======================================================");
	    	if(event.getAmendedExportAdvising().getWithMt730()){
	    		sendSwiftMessage("730",event.getTradeService());
	    	}
    	}
    }

    @EventListener
    public void generateMt799ForExportAdvisingCancellation(ExportAdvisingCancelledEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt799ForExportAdvisingCancellation");
	    	System.out.println(event.getTradeService().getDetails().get("sendMt799Flag"));
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("=======================================================");
	    	if(event.getTradeService().getDetails().get("sendMt799Flag") != null){
	    		if(event.getTradeService().getDetails().get("sendMt799Flag").toString().equals("1")){
	    			sendSwiftMessage("799",event.getTradeService());    			
	    		}
	    	}
    	}
    }

    @EventListener
    public void generateMt742ForBcNegotiation(BCNegotiatedEvent event){
    	if(event.getTradeService().getDetails().get("mtFlag") != null &&
    			isApproved(event.getTradeService())){
    		System.out.println("inside generateMt742ForBcNegotiation");
    		System.out.println(event.getTradeService().getDetails().get("mtFlag"));
    		System.out.println(event.getTradeService().getDocumentNumber().toString());
    		System.out.println("=======================================================");    		
    		if(event.getTradeService().getDetails().get("mtFlag").toString().equals("1")){
    			sendSwiftMessage("742",event.getTradeService());    			
    		}
    	}
    }
    
    @EventListener
    public void generateMt103ForBcSettlement(BCSettledEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt103ForBcSettlement");
			System.out.println(event.getTradeService().getDocumentType());
			System.out.println(event.getTradeService().getDocumentSubType1());
			System.out.println(event.getTradeService().getDocumentNumber().toString());
			System.out.println("====================================================");
	    	if(DocumentType.DOMESTIC.equals(event.getTradeService().getDocumentType())){
	    		Payment payment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.SETTLEMENT);
	            if(payment != null && payment.hasSwift()){
	            	System.out.println("inside paymentRepository");
	        		System.out.println(payment.hasSwift());
	        		System.out.println("====================================================");
	                sendSwiftMessage("103",event.getTradeService());
	            }
	    	}
    	}
    }

    @EventListener
    public void generateMt103ExportAdvancePayment(ExportAdvancePaymentCreatedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt103ExportAdvancePayment");
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("====================================================");
	        Payment payment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.SETTLEMENT);
	        if(payment != null && payment.hasSwift()){
	        	System.out.println("inside paymentRepository");
	        	System.out.println(payment.hasSwift());
	        	System.out.println("====================================================");
	            sendSwiftMessage("103",event.getTradeService());
	        }
    	}
    }
    
    @EventListener
    public void generateMt103ExportAdvanceRefund(ExportAdvancePaymentRefundCreatedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt103ExportAdvanceRefund");
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("====================================================");
			sendSwiftMessage("103",event.getTradeService());
    	}
    }
    
    @EventListener
    public void generateMt742ForBpNegotiation(BPNegotiatedEvent event){
    	if(event.getTradeService().getDetails().get("mtFlag") != null && 
    			isApproved(event.getTradeService())){
    		System.out.println("inside generateMt742ForBcNegotiation");
    		System.out.println(event.getTradeService().getDetails().get("mtFlag"));
    		System.out.println(event.getTradeService().getDocumentNumber().toString());
    		System.out.println("=======================================================");
    		if(event.getTradeService().getDetails().get("mtFlag").toString().equals("1")){
    			sendSwiftMessage("742",event.getTradeService());    			
    		}
    	}
    }
    
    @EventListener
    public void generateMt103ForBpSettlement(BPSettledEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt103ForBpSettlement");
			System.out.println(event.getTradeService().getDocumentType());
			System.out.println(event.getTradeService().getDocumentSubType1());
			System.out.println(event.getTradeService().getDocumentNumber().toString());
			System.out.println("====================================================");
	    	if(DocumentType.DOMESTIC.equals(event.getTradeService().getDocumentType())){
	    		Payment payment = paymentRepository.get(event.getTradeService().getTradeServiceId(), ChargeType.SETTLEMENT);
	            if(payment != null && payment.hasSwift()){
	            	System.out.println("inside paymentRepository");
	        		System.out.println(payment.hasSwift());
	        		System.out.println("====================================================");
	                sendSwiftMessage("103",event.getTradeService());
	            }
	    	}
    	}
    }

    @EventListener
    public void generateMt202ForCorresCharges(CorresChargeActualApprovedEvent event){
    	if(isApproved(event.getTradeService())){
	    	System.out.println("inside generateMt202ForCorresCharges");
	    	System.out.println(event.getTradeService().getDocumentClass());
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("====================================================");
	    	if(DocumentClass.CORRES_CHARGE.equals(event.getTradeService().getDocumentClass())){
	        	if(event.getTradeService().getDetails().get("remitCorresCharges") != null){
	        		if(event.getTradeService().getDetails().get("remitCorresCharges").toString().equals("Y")){
	        			sendSwiftMessage("202",event.getTradeService());    			
	        		}
	        	}
	    	}
    	}
    }

    @EventListener
    public void generateOutgoingMtMessage(TradeServiceRoutedEvent event){
    	if(event.getTradeServiceStatus().equals(TradeServiceStatus.APPROVED) && 
    			event.getTradeService().getDetails().get("messageType") != null ){
    		System.out.println("inside generateOutgoingMtMessage");
	    	System.out.println(event.getTradeService().getDetails().get("messageType"));
	    	System.out.println(event.getTradeService().getDocumentNumber().toString());
	    	System.out.println("====================================================");
    		sendSwiftMessage(event.getTradeService().getDetails().get("messageType").toString(),event.getTradeService());
    	}
    }
    
    private void sendSwiftMessage(String type, TradeService ts, int sequenceNumber, int total) {
    	try {
    		List<RawSwiftMessage> messages = swiftMessageBuilder.build(type, ts);
            for(RawSwiftMessage message : messages){
            	
            	MessageBlock messageBlock = message.getMessageBlock();
        		if("700".equals(type)){
        			messageBlock.update("27", "1/" + total);
                }else if("701".equals(type)){
                	messageBlock.update("27", sequenceNumber + "/" + total);
                }else if("707".equals(type)){
        			messageBlock.update("27", "1/" + total);
                }else if("708".equals(type)){
                	messageBlock.update("27", sequenceNumber + "/" + total);
                }
            	
                MtMessage swiftMessage = mapToMtMessage(message,ts.getTradeServiceId());
                String filename = swiftService.sendMessage(message);
                swiftMessage.setFilename(filename);
                mtMessageRepository.persist(swiftMessage);
            }
    	} catch (ValidationException e) {
    		throw new RuntimeException("Outgoing swift message failed validation",e);
    	} catch (Exception e1){
            e1.printStackTrace();
            throw new RuntimeException("Outgoing swift message failed validation",e1);
        }
    }

    private  void  sendSwiftMessage(String type, TradeService ts){
    	try {
    		List<RawSwiftMessage> messages = swiftMessageBuilder.build(type, ts);
            for(RawSwiftMessage message : messages){
            	for(Tag tag:message.getMessageBlock().getTags()){
            		if(tag.getTagName().equalsIgnoreCase("45B")||
            			tag.getTagName().equalsIgnoreCase("46B") ||
            			tag.getTagName().equalsIgnoreCase("47B")){
//            			message.getApplicationHeader().setMessageType("701");
            		}
            	}
                MtMessage swiftMessage = mapToMtMessage(message,ts.getTradeServiceId());
                String filename = swiftService.sendMessage(message);
                swiftMessage.setFilename(filename);
                mtMessageRepository.persist(swiftMessage);
            }
    	} catch (ValidationException e) {
    		throw new RuntimeException("Outgoing swift message failed validation",e);
    	} catch (Exception e1){
            e1.printStackTrace();
            throw new RuntimeException("Outgoing swift message failed validation",e1);
        }
    }

    private MtMessage mapToMtMessage(RawSwiftMessage rawSwiftMessage, TradeServiceId tradeServiceId){
        String contents = writer.write(rawSwiftMessage);
        MtMessage message = new MtMessage(rawSwiftMessage.getReference(), MessageClass.OUTGOING,
                contents,rawSwiftMessage.getMessageType());
        message.setTradeServiceId(tradeServiceId);
        message.setSequenceNumber(rawSwiftMessage.getSequenceNumber());
        message.setSequenceTotal(rawSwiftMessage.getSequenceTotal());
        return message;
    }
    
    private boolean isApproved(TradeService tradeService){
    	if(tradeService.getStatus().equals(TradeServiceStatus.POSTED) ||
    			tradeService.getStatus().equals(TradeServiceStatus.APPROVED)){
    		return true;
    	}
    	return false;
    }
}
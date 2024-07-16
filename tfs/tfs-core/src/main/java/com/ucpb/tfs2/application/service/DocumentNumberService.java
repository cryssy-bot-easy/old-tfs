package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.TradeProductRepository;
import com.ucpb.tfs.domain.product.utils.DocumentNumberGenerator;
import com.ucpb.tfs.domain.reference.ProductReference;
import com.ucpb.tfs.domain.reference.ProductReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentNumberService {

    @Autowired
    TradeProductRepository tradeProductRepository;

    @Autowired
    ProductReferenceRepository productReferenceRepository;

    // this is used for products that do not really have a document number but we need to generate one anyway
    public DocumentNumber generateDocumentNumber(String processingUnitCode,
                                                      DocumentClass documentClass,
                                                      DocumentType documentType,
                                                      DocumentSubType1 documentSubType1,
                                                      DocumentSubType2 documentSubType2,
                                                      ServiceType serviceType) {

        // TODO: put real code here

        if(documentClass == DocumentClass.MT) {

            if(serviceType == ServiceType.CREATE) {
                return new DocumentNumber("");
            }
        }

        if(documentClass == DocumentClass.MT) {
            return new DocumentNumber("");
        }

        //return new DocumentNumber("dummy");
        return new DocumentNumber("");
    }

    // this is for all "real' products
    public DocumentNumber generateDocumentNumber(String processingUnitCode,
                                                 String branchUnitCode,
                                                 DocumentClass documentClass,
                                                 DocumentType documentType,
                                                 DocumentSubType1 documentSubType1,
                                                 DocumentSubType2 documentSubType2,
                                                 ServiceType serviceType) {

        String documentCode = "";
        System.out.println("generating document number");
        if(documentType != null) {
            if(documentType == DocumentType.FOREIGN) {
                if(documentClass == DocumentClass.DP) {
                    if(serviceType == ServiceType.NEGOTIATION) {
                        documentCode = "05";
                    }
                }
                if(documentClass == DocumentClass.DA) {
                    if(serviceType == ServiceType.NEGOTIATION_ACKNOWLEDGEMENT) {
                        documentCode = "06";
                    }
                }
                if(documentClass == DocumentClass.OA) {
                    if(serviceType == ServiceType.NEGOTIATION) {
                        documentCode = "07";
                    }
                }
                if(documentClass == DocumentClass.DR) {
                    if(serviceType == ServiceType.NEGOTIATION) {
                        documentCode = "08";
                    }
                }

                if(documentClass == DocumentClass.BG) {
                    if(serviceType == ServiceType.ISSUANCE) {
                        documentCode = "21";
                    }
                }
                if(documentClass == DocumentClass.BE) {
                    if(serviceType == ServiceType.ISSUANCE) {
                        documentCode = "22";
                    }
                }

                 // EBP
                if (DocumentClass.BP.equals(documentClass)) {
                    documentCode = "12";
                }
                // EBC
                if (DocumentClass.BC.equals(documentClass)) {
                    documentCode = "11";
                }

            } else if(documentType == DocumentType.DOMESTIC) {

                if(documentClass == DocumentClass.DP) {
                    if(serviceType == ServiceType.NEGOTIATION) {
                        documentCode = "09";
                    }
                }

                // DBP
                if (DocumentClass.BP.equals(documentClass)) {
                    documentCode = "13";
                }
                // DBC
                if (DocumentClass.BC.equals(documentClass)) {
                    documentCode = "14";
                }

            }
        } else {

            // all other products that do not have a document type (e.g. CDT ... )
            if(documentClass == DocumentClass.CDT) {
                if(serviceType == ServiceType.PAYMENT) {
                    System.out.println("CDT PAYMENT DOC GENERATOR");
                    documentCode = "27";
                }
            }

            if(documentClass == DocumentClass.IMPORT_ADVANCE) {
                if(serviceType == ServiceType.PAYMENT) {
                    System.out.println("IMPORT ADVANCE DOC GENERATOR");
                    documentCode = "23";
                }
            }

            if(documentClass == DocumentClass.EXPORT_ADVANCE) {
                if(serviceType == ServiceType.PAYMENT) {
                    System.out.println("EXPORT ADVANCE DOC GENERATOR");
                    documentCode = "24";
                }
            }

            if (documentClass == DocumentClass.EXPORT_ADVISING) {
                documentCode = "10";
            }

            if (documentClass == DocumentClass.CORRES_CHARGE) {
                System.out.println("CORRES CHARGE");
                documentCode = "26";
            }

            if (documentClass == DocumentClass.REBATE) {
                documentCode = "28";
            }
        }

        if(documentCode.equalsIgnoreCase("")){
            ProductReference productReference =  productReferenceRepository.find(documentClass, documentType, documentSubType1, documentSubType2);
            if(productReference!=null){
                documentCode = productReference.getDocumentCode();
            }
        }
        DocumentNumberGenerator documentNumberGenerator = new DocumentNumberGenerator();
        documentNumberGenerator.setTradeProductRepository(tradeProductRepository);

        // default to 001 if branch unit code was not provide (this is a real business rule)
        if((branchUnitCode == null) || (branchUnitCode.equalsIgnoreCase(""))) {
            branchUnitCode = "001";
        }

        System.out.println("branchUnitCode:"+branchUnitCode);
        System.out.println("documentCode:"+documentCode);
        System.out.println("processingUnitCode:"+processingUnitCode);

        String documentNumber = documentNumberGenerator.generateDocumentNumber(branchUnitCode, documentCode, processingUnitCode);
        System.out.println("GENERATED DOCUMENT NUMBER: " + documentNumber);
        return new DocumentNumber(documentNumber);
    }

    public String generateReferenceNumber(String documentCode) {
        DocumentNumberGenerator documentNumberGenerator = new DocumentNumberGenerator();
        documentNumberGenerator.setTradeProductRepository(tradeProductRepository);

        String settlementAccountNumberSequence = documentNumberGenerator.generateSettlementAccountNumber(documentCode);

        return settlementAccountNumberSequence;
    }

}

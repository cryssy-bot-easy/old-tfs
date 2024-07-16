package com.ucpb.tfs.domain.product;


import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.product.enums.AccountType;
import com.ucpb.tfs.domain.product.enums.AdvancePaymentType;
import com.ucpb.tfs.domain.product.enums.CreditFacilityCode;
import com.ucpb.tfs.utils.UtilSetFields;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Audited
public class ImportAdvancePayment extends TradeProduct {

    PaymentStatus paymentStatus;

    AdvancePaymentType advancePaymentType;

    String importerCBCode;
    String importerName;

    String reimbursingBankCode;
    String reimbursingBankName;

    Currency reimbursingBankCurrency;

    AccountType accountType;
    //String accountNumber;
    String depositoryAccountNumber;

    String exporterCbCode;
    String beneficiaryName;
    String beneficiaryAddress;

    // MT103 fields follow
    String timeIndicationCode;
    String timeIndication;
    String bankOperationCode;
    String instructionCode;
    String transactionTypeCode;

    BigDecimal exchangeRate;

    String sendingInstitutionCode;
    String sendingInstitution;

    String orderingBankCode;
    String orderingBankNameAndAddress;

    String sendersCorrespondentCode;
    String sendersNameAndAddress;

    String receiversCorrespondentCode;
    String receiversNameAndAddress;

    String thirdReimbursementCode;
    String thirdReimbursementNameAndAddress;

    String intermediaryCode;
    String intermediaryNameAndAddress;

    String accountWithBankCode;
    String accountWithBankNameAndAddress;

    String detailsOfCharges;

    String remittanceInformationCode;
    String remittanceInformation;

    Currency sendersChargesCurrency;
    BigDecimal sendersChargesAmount;

    Currency receiversChargesCurrency;
    BigDecimal receiversChargesAmount;

    String regulatoryReportingCode;
    String regulatoryReporting;
    String envelopeContentCode;
    String envelopeContent;

    // refund
    Boolean cwtFlag;

    String corresBankCode;

    String countryCode;

    Date shipmentDate;

    CreditFacilityCode creditFacilityCode;

    public ImportAdvancePayment(){}

    public ImportAdvancePayment(Map<String, Object> details, DocumentNumber documentNumber) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);

        this.advancePaymentType = AdvancePaymentType.IMPORT;

        this.documentNumber = documentNumber;
        this.paymentStatus = PaymentStatus.PAID;
    }

    public void refundPayment(Map<String, Object> details) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);

        this.paymentStatus = PaymentStatus.REFUNDED;
    }

}

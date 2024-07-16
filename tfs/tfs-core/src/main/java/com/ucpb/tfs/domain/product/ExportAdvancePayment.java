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

/**
 * Created by IntelliJ IDEA.
 * User: Marv
 * Date: 2/5/13
 * Time: 12:00 PM
 */
@Audited
public class ExportAdvancePayment extends TradeProduct {

    PaymentStatus paymentStatus;

    AdvancePaymentType advancePaymentType;

    Boolean cwtFlag;

    String exporterCbCode;
    String exporterName;
    String importerCbCode;
    String importerName;
    String corresBankCode;
    AccountType accountType;
    String countryCode;
    Date shipmentDate;
    CreditFacilityCode creditFacilityCode;

    // mt103
    String receivingBank;
    String sendersReference;
    String bankOperationCode;
    Date valueDate;
    String orderingCustomerAcctNo;
    String orderingCustomerName;
    String orderingCustomerAddress;
    String accountWithInstitution;
    String accountWithInstitutionNameAddress;
    String beneficiaryName;
    String beneficiaryAddress;
    String beneficiaryAcctNo;
    String detailsOfCharges;
    String sendersToReceiverInfo;

    Currency sendersChargesCurrency;
    BigDecimal sendersChargesAmount;

    Currency receiversChargesCurrency;
    BigDecimal receiversChargesAmount;

    // pddts
    String fundingRefNo;
    String swift;
    String bank;
    String beneficiary;
    String accountNumber;
    String byByo;

    // refund
    String buyerCasaNumber;
    String buyerName;
    String buyerAddress;

    String sellerCasaNumber;
    String sellerName;
    String sellerAddress;


    public ExportAdvancePayment(){}

    public ExportAdvancePayment(Map<String, Object> details, DocumentNumber documentNumber) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);

        this.advancePaymentType = AdvancePaymentType.EXPORT;

        this.documentNumber = documentNumber;
        this.paymentStatus = PaymentStatus.PAID;
    }

    public void refundPayment(Map<String, Object> details) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);

        this.paymentStatus = PaymentStatus.REFUNDED;
    }
}

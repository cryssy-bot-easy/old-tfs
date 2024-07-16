package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.product.enums.LCNegotiationStatus;
import com.ucpb.tfs.utils.UtilSetFields;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Jett
 * Date: 7/25/12
 */
@Audited
public class LCNegotiation implements Serializable {

    private String id;

    private DocumentNumber documentNumber;

    private NegotiationNumber negotiationNumber;

    private LCNegotiationStatus lcNegotiationStatus;

    private ICNumber icNumber;

    private BigDecimal negotiationAmount;
    private Currency negotiationCurrency;

    private Date valueDate;                // todo: value Date?
    private Date processDate;
    private Date negotiationValueDate;     // todo: debit date too?

    private String sendersCorrespondentLocation;
    private String accountWithInstitutionLocation;
    private String interestTerm;
    private String sendersCorrespondentNameAndAddress;
    private String instructionAction;
    private BigDecimal originalAmount;
    private Currency originalCurrency;
    private String beneficiarysInstitution;
    private Currency reimbursingCurrency;
    private String shipmentNumber;
    private String accountWithInstitution;
    private BigDecimal outstandingBalance;
    private String loanTermCode;
    private String receiversCorrespondent;
    private String loanTerm;

    private String intermediary;
    private BigDecimal overdrawnNegotiationAmountInNegotiationCurrency;
    private BigDecimal negotiationAmountInReimbursingCurrency;
    private String interestTermCode;
    private BigDecimal outstandingAmount;
    private String accountWithInstitutionNameAndAddress;
    private Boolean cwtFlag;
    private String beneficiarysInstitutionNameAndAddress;
    private BigDecimal apCashAmountInNegotiationCurrency;
    private String reimbursingBank;
    private String negotiatingBanksReferenceNumber;
    private String agriAgraTagging;
    private String orderingInstitutionIdentifierCode;
    private String orderingInstitutionNameAndAddress;

    private String orderingInstitution;
    private String senderCorrespondentIdentifierCode;
    private String receiversCorrespondentNameAndAddress;
    private String typeOfLoan;
    private String receiversCorrespondentLocation;
    private String negotiationFacilityType;
    private BigDecimal discrepancyFeeCharge;
    private String receiversCorrespondentIdentifierCode;
    private String generateMt;
    private String senderToReceiverInformation;
    private Currency bookingCurrency;
    private String intermediaryNameAndAddress;
    private String negotiationFacilityId;
    private String beneficiarysInstitutionIdentifierCode;

    private String sendersCorrespondent;
    private String accountWithInstitutionIdentifierCode;
    private String intermediaryIdentifierCode;
    private String furtherIdentification;
    private Date expiryDate;
    private Date issueDate;
    private BigDecimal overdrawnAmount;
    private String reimbursingBankSpecialRate;
    private String interestRate;
    private Date loanMaturityDate;
    private String negotiatingBank;
    private String negotiationType;
    private BigDecimal netAmount;

    private Boolean cramFlag;

    private BigDecimal totalAmount;
    private Currency totalAmountCurrency;
    private String bank;
    private String beneficiaryCustomerName;
    private String byOrder;

    private String orderingCustomerAddress;
    private String detailsOfCharges;
    private String beneficiary;
    private String bankOperationCode;
    private String beneficiaryCustomerAddress;

    private String nameAndAddress;
    private String orderingCustomerName;
    private String beneficiarysAccountNumber;
    private String fundingReferenceNumber;

    private String senderReference;
    private String receivingBank;
    private String swift;
    private BigDecimal remittanceFee;
    private Currency remittanceFeeCurrency;

    private String pnNumber;

    public LCNegotiation() {}

    public LCNegotiation(String negotiationNumber) {
        this.negotiationNumber = new NegotiationNumber(negotiationNumber);
    }

    public void approveLcNegotiation() {
        this.lcNegotiationStatus = LCNegotiationStatus.APPROVED;
        this.processDate = new Date();
    }

    public void updateDetails(Map<String, Object> details) {
        System.out.println("\nLCNegotiation.updateDetails() ===========\n");
        UtilSetFields.copyMapToObject(this, (HashMap) details);
    }

    public BigDecimal getNegotiationAmount() {
        return negotiationAmount;
    }

    public NegotiationNumber getNegotiationNumber() {
        return negotiationNumber;
    }

    public LCNegotiationStatus getLcNegotiationStatus() {
        return lcNegotiationStatus;
    }
}

package com.ucpb.tfs.domain.payment;

import com.incuventure.ddd.domain.annotations.DomainEntity;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * the object containing the individual payments group within a Payment object.
 */
@DomainEntity
public class EtsPaymentDetail implements Serializable {

    private Long id;

    private PaymentInstrumentType paymentInstrumentType;

    private String referenceNumber; //Trade Suspense account

    private BigDecimal amount;
    private BigDecimal amountInLcCurrency;

    private Currency currency;

    private PaymentStatus status;

    // For loans
    private Currency bookingCurrency;
    private BigDecimal interestRate;
    private String interestTerm;
    private String interestTermCode;
    private String repricingTerm;
    private String repricingTermCode;
    private String loanTerm;
    private String loanTermCode;
    private Date loanMaturityDate;
    private Integer paymentTerm;
    private Integer facilityId;
    private String facilityType;
    private String facilityReferenceNumber;
    private Long numberOfFreeFloatDays;

    // for AP
    private String referenceId;

    // Rates
    private BigDecimal passOnRateThirdToUsd;
    private BigDecimal passOnRateThirdToPhp;
    private BigDecimal passOnRateUsdToPhp;
    private BigDecimal specialRateThirdToUsd;
    private BigDecimal specialRateThirdToPhp;
    private BigDecimal specialRateUsdToPhp;
    private BigDecimal urr;

    private String thirdToUsdRateName;
    private String thirdToPhpRateName;
    private String usdToPhpRateName;
    private String urrRateName;
    private String thirdToUsdRateDescription;
    private String thirdToPhpRateDescription;
    private String usdToPhpRateDescription;
    private String urrRateDescription;

    private Date paidDate;

    private Long pnNumber;

    private Integer paymentCode;

    private Long sequenceNumber;

    private Boolean withCramApproval;
    
    private String agriAgraTagging;
    
    private String accountName;


    public EtsPaymentDetail() {
        this.status = PaymentStatus.UNPAID;
    }

    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            String referenceId,
            BigDecimal amount,
            Currency currency,
            BigDecimal passOnRateThirdToUsd,
            BigDecimal passOnRateThirdToPhp,
            BigDecimal passOnRateUsdToPhp,
            BigDecimal specialRateThirdToUsd,
            BigDecimal specialRateThirdToPhp,
            BigDecimal specialRateUsdToPhp,
            BigDecimal urr) {

        this();

        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.currency = currency;
        this.referenceId = referenceId;

        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;
    }

    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            String referenceId,
            BigDecimal amount,
            Currency currency,
            BigDecimal passOnRateThirdToUsd,
            BigDecimal passOnRateThirdToPhp,
            BigDecimal passOnRateUsdToPhp,
            BigDecimal specialRateThirdToUsd,
            BigDecimal specialRateThirdToPhp,
            BigDecimal specialRateUsdToPhp,
            BigDecimal urr,
            BigDecimal amountInLcCurrency) {

        this();

        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.currency = currency;
        this.referenceId = referenceId;

        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;
        this.amountInLcCurrency = amountInLcCurrency;
    }


    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            BigDecimal amount,
            Currency currency,
            Currency bookingCurrency,
            BigDecimal interestRate,
            String interestTerm,
            String interestTermCode,
            String repricingTerm,
            String repricingTermCode,
            String loanTerm,
            String loanTermCode,
            Date loanMaturityDate) {

        this();

        this.amount = amount;
        this.currency = currency;
        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.bookingCurrency = bookingCurrency;
        this.interestRate = interestRate;
        this.interestTerm = interestTerm;
        this.interestTermCode = interestTermCode;
        this.repricingTerm = repricingTerm;
        this.repricingTermCode = repricingTermCode;
        this.loanTerm = loanTerm;
        this.loanTermCode = loanTermCode;
        this.loanMaturityDate = loanMaturityDate;
    }

    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            BigDecimal amount,
            Currency currency,
            BigDecimal passOnRateThirdToUsd,
            BigDecimal passOnRateThirdToPhp,
            BigDecimal passOnRateUsdToPhp,
            BigDecimal specialRateThirdToUsd,
            BigDecimal specialRateThirdToPhp,
            BigDecimal specialRateUsdToPhp,
            BigDecimal urr) {

        this();

        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.currency = currency;

        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;

        if (paymentInstrumentType.equals(PaymentInstrumentType.PDDTS) ||
                paymentInstrumentType.equals(PaymentInstrumentType.SWIFT)) {
            this.status = PaymentStatus.PAID;
        }
    }

    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            BigDecimal amount,
            Currency currency,
            BigDecimal passOnRateThirdToUsd,
            BigDecimal passOnRateThirdToPhp,
            BigDecimal passOnRateUsdToPhp,
            BigDecimal specialRateThirdToUsd,
            BigDecimal specialRateThirdToPhp,
            BigDecimal specialRateUsdToPhp,
            BigDecimal urr,
            BigDecimal amountInLcCurrency,
            String accountName) {

        this();

        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.currency = currency;

        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;
        this.amountInLcCurrency = amountInLcCurrency;
        this.accountName = accountName;

    }

    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            BigDecimal amount,
            Currency currency,
            BigDecimal passOnRateThirdToUsd,
            BigDecimal passOnRateThirdToPhp,
            BigDecimal passOnRateUsdToPhp,
            BigDecimal specialRateThirdToUsd,
            BigDecimal specialRateThirdToPhp,
            BigDecimal specialRateUsdToPhp,
            BigDecimal urr,
            BigDecimal amountInLcCurrency) {

        this();

        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.currency = currency;

        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;
        this.amountInLcCurrency = amountInLcCurrency;

        if (paymentInstrumentType.equals(PaymentInstrumentType.PDDTS) ||
                paymentInstrumentType.equals(PaymentInstrumentType.SWIFT)) {
            this.status = PaymentStatus.PAID;
        }
    }

    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            BigDecimal amount,
            Currency currency,
            Currency bookingCurrency,
            BigDecimal interestRate,
            String interestTerm,
            String interestTermCode,
            String repricingTerm,
            String repricingTermCode,
            String loanTerm,
            String loanTermCode,
            Date loanMaturityDate,
            Integer paymentTerm,
            BigDecimal passOnRateThirdToUsd,
            BigDecimal passOnRateThirdToPhp,
            BigDecimal passOnRateUsdToPhp,
            BigDecimal specialRateThirdToUsd,
            BigDecimal specialRateThirdToPhp,
            BigDecimal specialRateUsdToPhp,
            BigDecimal urr,
            Integer facilityId,
            String facilityType,
            String facilityReferenceNumber) {

        this();

        this.amount = amount;
        this.currency = currency;
        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.bookingCurrency = bookingCurrency;
        this.interestRate = interestRate;
        this.interestTerm = interestTerm;
        this.interestTermCode = interestTermCode;
        this.repricingTerm = repricingTerm;
        this.repricingTermCode = repricingTermCode;
        this.loanTerm = loanTerm;
        this.loanTermCode = loanTermCode;
        this.loanMaturityDate = loanMaturityDate;
        this.paymentTerm = paymentTerm;

        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;

        this.facilityId = facilityId;
        this.facilityType = facilityType;
        this.facilityReferenceNumber = facilityReferenceNumber;
    }


    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            BigDecimal amount,
            Currency currency,
            Currency bookingCurrency,
            BigDecimal interestRate,
            String interestTerm,
            String interestTermCode,
            String repricingTerm,
            String repricingTermCode,
            String loanTerm,
            String loanTermCode,
            Date loanMaturityDate,
            Integer paymentTerm,
            BigDecimal passOnRateThirdToUsd,
            BigDecimal passOnRateThirdToPhp,
            BigDecimal passOnRateUsdToPhp,
            BigDecimal specialRateThirdToUsd,
            BigDecimal specialRateThirdToPhp,
            BigDecimal specialRateUsdToPhp,
            BigDecimal urr,
            Integer facilityId,
            String facilityType,
            String facilityReferenceNumber,
            BigDecimal amountInLcCurrency) {

        this();

        this.amount = amount;
        this.currency = currency;
        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.bookingCurrency = bookingCurrency;
        this.interestRate = interestRate;
        this.interestTerm = interestTerm;
        this.interestTermCode = interestTermCode;
        this.repricingTerm = repricingTerm;
        this.repricingTermCode = repricingTermCode;
        this.loanTerm = loanTerm;
        this.loanTermCode = loanTermCode;
        this.loanMaturityDate = loanMaturityDate;
        this.paymentTerm = paymentTerm;

        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;

        this.facilityId = facilityId;
        this.facilityType = facilityType;
        this.facilityReferenceNumber = facilityReferenceNumber;
        this.amountInLcCurrency = amountInLcCurrency;
    }


    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            BigDecimal amount,
            Currency currency,
            Currency bookingCurrency,
            BigDecimal interestRate,
            String interestTerm,
            String interestTermCode,
            String repricingTerm,
            String repricingTermCode,
            String loanTerm,
            String loanTermCode,
            Date loanMaturityDate,
            Integer paymentTerm,
            BigDecimal passOnRateThirdToUsd,
            BigDecimal passOnRateThirdToPhp,
            BigDecimal passOnRateUsdToPhp,
            BigDecimal specialRateThirdToUsd,
            BigDecimal specialRateThirdToPhp,
            BigDecimal specialRateUsdToPhp,
            BigDecimal urr,
            Integer facilityId,
            String facilityType,
            String facilityReferenceNumber,
            BigDecimal amountInLcCurrency,
            Integer paymentCode,
            Boolean withCramApproval) {

        this();

        this.amount = amount;
        this.currency = currency;
        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.bookingCurrency = bookingCurrency;
        this.interestRate = interestRate;
        this.interestTerm = interestTerm;
        this.interestTermCode = interestTermCode;
        this.repricingTerm = repricingTerm;
        this.repricingTermCode = repricingTermCode;
        this.loanTerm = loanTerm;
        this.loanTermCode = loanTermCode;
        this.loanMaturityDate = loanMaturityDate;
        this.paymentTerm = paymentTerm;

        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;

        this.facilityId = facilityId;
        this.facilityType = facilityType;
        this.facilityReferenceNumber = facilityReferenceNumber;
        this.amountInLcCurrency = amountInLcCurrency;
        
        this.paymentCode = paymentCode;
        this.withCramApproval = withCramApproval;
    }

    public EtsPaymentDetail(
            PaymentInstrumentType paymentInstrumentType,
            String referenceNumber,
            BigDecimal amount,
            Currency currency,
            Currency bookingCurrency,
            BigDecimal interestRate,
            String interestTerm,
            String interestTermCode,
            String repricingTerm,
            String repricingTermCode,
            String loanTerm,
            String loanTermCode,
            Date loanMaturityDate,
            Integer paymentTerm,
            BigDecimal passOnRateThirdToUsd,
            BigDecimal passOnRateThirdToPhp,
            BigDecimal passOnRateUsdToPhp,
            BigDecimal specialRateThirdToUsd,
            BigDecimal specialRateThirdToPhp,
            BigDecimal specialRateUsdToPhp,
            BigDecimal urr,
            Integer facilityId,
            String facilityType,
            String facilityReferenceNumber,
            BigDecimal amountInLcCurrency,
            Integer paymentCode,
            Boolean withCramApproval,
            Long numberOfFreeFloatDays) {

        this();

        this.amount = amount;
        this.currency = currency;
        this.paymentInstrumentType = paymentInstrumentType;
        this.referenceNumber = referenceNumber;
        this.bookingCurrency = bookingCurrency;
        this.interestRate = interestRate;
        this.interestTerm = interestTerm;
        this.interestTermCode = interestTermCode;
        this.repricingTerm = repricingTerm;
        this.repricingTermCode = repricingTermCode;
        this.loanTerm = loanTerm;
        this.loanTermCode = loanTermCode;
        this.loanMaturityDate = loanMaturityDate;
        this.paymentTerm = paymentTerm;

        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;

        this.facilityId = facilityId;
        this.facilityType = facilityType;
        this.facilityReferenceNumber = facilityReferenceNumber;
        this.amountInLcCurrency = amountInLcCurrency;
        
        this.paymentCode = paymentCode;
        this.withCramApproval = withCramApproval;
        
        this.numberOfFreeFloatDays = numberOfFreeFloatDays;
    }
    
    public EtsPaymentDetail(String accountName, String agriAgraTagging,
				BigDecimal amount, BigDecimal amountInLcCurrency,
				Currency bookingCurrency, Currency currency,
				Integer facilityId, String facilityReferenceNumber,
				String facilityType, BigDecimal interestRate,
				String interestTerm, String interestTermCode,
				Date loanMaturityDate, String loanTerm,
				String loanTermCode, Long numberOfFreeFloatDays,
				BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateThirdToUsd,
				BigDecimal passOnRateUsdToPhp, Integer paymentCode,
				PaymentInstrumentType paymentInstrumentType, Integer paymentTerm,
				Long pnNumber, String referenceId,
				String referenceNumber, String repricingTerm,
				String repricingTermCode, Long sequenceNumber,
				BigDecimal specialRateThirdToPhp, BigDecimal specialRateThirdToUsd,
				BigDecimal specialRateUsdToPhp, String thirdToPhpRateDescription,
				String thirdToPhpRateName, String thirdToUsdRateDescription,
				String thirdToUsdRateName, BigDecimal urr,
				String urrRateDescription, String urrRateName,
				String usdToPhpRateDescription, String usdToPhpRateName,
				Boolean withCramApproval) {
    	
    	this();
    	
    	this.accountName = accountName;
    	this.agriAgraTagging = agriAgraTagging;
    	this.amount = amount;
    	this.amountInLcCurrency = amountInLcCurrency;
    	this.bookingCurrency = bookingCurrency;
    	this.currency = currency;
    	this.facilityId = facilityId;
    	this.facilityReferenceNumber = facilityReferenceNumber;
    	this.facilityType = facilityType;
    	this.interestRate = interestRate;
    	this.interestTerm = interestTerm;
    	this.interestTermCode = interestTermCode;
    	this.loanMaturityDate = loanMaturityDate;
    	this.loanTerm = loanTerm;
    	this.loanTermCode = loanTermCode;
    	this.numberOfFreeFloatDays = numberOfFreeFloatDays;
    	this.passOnRateThirdToPhp = passOnRateThirdToPhp;
    	this.passOnRateThirdToUsd = passOnRateThirdToUsd;
    	this.passOnRateUsdToPhp = passOnRateUsdToPhp;
    	this.paymentCode = paymentCode;
    	this.paymentInstrumentType = paymentInstrumentType;
    	this.paymentTerm = paymentTerm;
    	this.pnNumber = pnNumber;
    	this.referenceId = referenceId;
    	this.referenceNumber = referenceNumber;
    	this.repricingTerm = repricingTerm;
    	this.repricingTermCode = repricingTermCode;
    	this.specialRateThirdToPhp = specialRateThirdToPhp;
    	this.specialRateThirdToUsd = specialRateThirdToUsd;
    	this.specialRateUsdToPhp = specialRateUsdToPhp;
    	this.thirdToPhpRateDescription = thirdToPhpRateDescription;
    	this.thirdToPhpRateName = thirdToPhpRateName;
    	this.thirdToUsdRateDescription = thirdToUsdRateDescription;
    	this.thirdToUsdRateName = thirdToUsdRateName;
    	this.urr = urr;
    	this.urrRateDescription = urrRateDescription;
    	this.urrRateName = urrRateName;
    	this.usdToPhpRateDescription = usdToPhpRateDescription;
    	this.usdToPhpRateName = usdToPhpRateName;
    	this.withCramApproval = withCramApproval;
    }

    public Boolean matches(PaymentInstrumentType paymentInstrumentType, String referenceNumber) {
        if (this.paymentInstrumentType == null || this.referenceNumber == null) {
            return Boolean.FALSE;
        }
        return this.paymentInstrumentType.equals(paymentInstrumentType) && this.referenceNumber.equals(referenceNumber);
    }

    public Boolean matches(PaymentInstrumentType paymentInstrumentType, String referenceNumber, String referenceId) {
        //MD seems to not have a referenceNumber
        //TODO: verify
        if (paymentInstrumentType != null && paymentInstrumentType.equals(PaymentInstrumentType.MD)) {
            return this.paymentInstrumentType.equals(paymentInstrumentType);
        } else {
            if (this.paymentInstrumentType == null || this.referenceNumber == null || this.referenceId == null) {
                return Boolean.FALSE;
            }
            return this.paymentInstrumentType.equals(paymentInstrumentType) && this.referenceNumber.equals(referenceNumber) && this.referenceId.equals(referenceId);
        }
    }

    public void update(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }


    public void update(BigDecimal amount, Currency currency,
                       BigDecimal passOnRateThirdToUsd, BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateUsdToPhp,
                       BigDecimal specialRateThirdToUsd, BigDecimal specialRateThirdToPhp, BigDecimal specialRateUsdToPhp, BigDecimal urr) {
        this.amount = amount;
        this.currency = currency;
        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;
    }


    public void update(BigDecimal amount, Currency currency,
                       BigDecimal passOnRateThirdToUsd, BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateUsdToPhp,
                       BigDecimal specialRateThirdToUsd, BigDecimal specialRateThirdToPhp, BigDecimal specialRateUsdToPhp, BigDecimal urr, BigDecimal amountInLcCurrency) {
        this.amount = amount;
        this.currency = currency;
        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;
        this.amountInLcCurrency = amountInLcCurrency;
    }


    public void update(BigDecimal amount,
                       Currency currency,
                       Currency bookingCurrency,
                       BigDecimal interestRate,
                       String interestTerm,
                       String interestTermCode,
                       String repricingTerm,
                       String repricingTermCode,
                       String loanTerm,
                       String loanTermCode,
                       Date loanMaturityDate,
                       BigDecimal passOnRateThirdToUsd, BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateUsdToPhp,
                       BigDecimal specialRateThirdToUsd, BigDecimal specialRateThirdToPhp, BigDecimal specialRateUsdToPhp, BigDecimal urr,
                       Integer facilityId, String facilityType, String facilityReferenceNumber, Integer paymentCode) {
        this.amount = amount;
        this.currency = currency;
        this.bookingCurrency = bookingCurrency;
        this.interestRate = interestRate;
        this.interestTerm = interestTerm;
        this.interestTermCode = interestTermCode;
        this.repricingTerm = repricingTerm;
        this.repricingTermCode = repricingTermCode;
        this.loanTerm = loanTerm;
        this.loanTermCode = loanTermCode;
        this.loanMaturityDate = loanMaturityDate;
        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;
        this.facilityId = facilityId;
        this.facilityType = facilityType;
        this.facilityReferenceNumber = facilityReferenceNumber;
        this.paymentCode = paymentCode;
    }


    public void update(BigDecimal amount,
                       Currency currency,
                       Currency bookingCurrency,
                       BigDecimal interestRate,
                       String interestTerm,
                       String interestTermCode,
                       String repricingTerm,
                       String repricingTermCode,
                       String loanTerm,
                       String loanTermCode,
                       Date loanMaturityDate,
                       BigDecimal passOnRateThirdToUsd, BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateUsdToPhp,
                       BigDecimal specialRateThirdToUsd, BigDecimal specialRateThirdToPhp, BigDecimal specialRateUsdToPhp, BigDecimal urr,
                       Integer facilityId, String facilityType, String facilityReferenceNumber, Integer paymentCode, BigDecimal amountInLcCurrency) {
        this.amount = amount;
        this.currency = currency;
        this.bookingCurrency = bookingCurrency;
        this.interestRate = interestRate;
        this.interestTerm = interestTerm;
        this.interestTermCode = interestTermCode;
        this.repricingTerm = repricingTerm;
        this.repricingTermCode = repricingTermCode;
        this.loanTerm = loanTerm;
        this.loanTermCode = loanTermCode;
        this.loanMaturityDate = loanMaturityDate;
        this.passOnRateThirdToUsd = passOnRateThirdToUsd;
        this.passOnRateThirdToPhp = passOnRateThirdToPhp;
        this.passOnRateUsdToPhp = passOnRateUsdToPhp;
        this.specialRateThirdToPhp = specialRateThirdToPhp;
        this.specialRateThirdToUsd = specialRateThirdToUsd;
        this.specialRateUsdToPhp = specialRateUsdToPhp;
        this.urr = urr;
        this.facilityId = facilityId;
        this.facilityType = facilityType;
        this.facilityReferenceNumber = facilityReferenceNumber;
        this.paymentCode = paymentCode;
        this.amountInLcCurrency = amountInLcCurrency;
    }


    public void setPaymentInstrumentType(PaymentInstrumentType paymentInstrumentType) {
        this.paymentInstrumentType = paymentInstrumentType;
    }

    public void paid() {
        this.status = PaymentStatus.PAID;
        this.paidDate = new Date();
    }

    public void unPay() {
        this.status = PaymentStatus.UNPAID;
        this.paidDate = null;
        this.pnNumber = null;
        System.out.println("UNPAID");
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public void setForInquiry() {
        this.status = PaymentStatus.PROCESSING;
    }

    public void rejected() {
        this.status = PaymentStatus.REJECTED;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentInstrumentType getPaymentInstrumentType() {
        return paymentInstrumentType;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }


    // updates loan maturity date
    public void setLoanMaturityDate(Date newUaLoanMaturiyDate) {
        this.loanMaturityDate = newUaLoanMaturiyDate;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public BigDecimal getPassOnRateThirdToUsd() {
        return passOnRateThirdToUsd;
    }

    public BigDecimal getPassOnRateThirdToPhp() {
        return passOnRateThirdToPhp;
    }

    public BigDecimal getPassOnRateUsdToPhp() {
        return passOnRateUsdToPhp;
    }

    public BigDecimal getSpecialRateThirdToUsd() {
        return specialRateThirdToUsd;
    }

    public BigDecimal getSpecialRateThirdToPhp() {
        return specialRateThirdToPhp;
    }

    public BigDecimal getSpecialRateUsdToPhp() {
        return specialRateUsdToPhp;
    }

    public BigDecimal getUrr() {
        // returns zero in case for no URR. this is special case for CDT payments since all payments for CDT are in PHP
        // and the product payment for CDT is also in PHP
        if (urr != null) {
            return urr;
        }

        return BigDecimal.ZERO;
    }

    public Long getPnNumber() {
        return pnNumber;
    }

    public void setPnNumber(Long pnNumber) {
        this.pnNumber = pnNumber;
    }

    public Integer getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Integer facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getFacilityReferenceNumber() {
        return facilityReferenceNumber;
    }

    public void setFacilityReferenceNumber(String facilityReferenceNumber) {
        this.facilityReferenceNumber = facilityReferenceNumber;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public Integer getPaymentTerm() {
        return paymentTerm;
    }

    public Integer getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(Integer paymentCode) {
        this.paymentCode = paymentCode;
    }

    public void setPaymentTerm(Integer paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EtsPaymentDetail that = (EtsPaymentDetail) o;

        if (paymentInstrumentType != that.paymentInstrumentType) return false;
        if (referenceNumber != null ? !referenceNumber.equals(that.referenceNumber) : that.referenceNumber != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = paymentInstrumentType != null ? paymentInstrumentType.hashCode() : 0;
        result = 31 * result + (referenceNumber != null ? referenceNumber.hashCode() : 0);
        return result;
    }

    public boolean hasRates() {
        if (passOnRateThirdToUsd != null ||
                passOnRateThirdToPhp != null ||
                passOnRateUsdToPhp != null ||
                specialRateThirdToUsd != null ||
                specialRateThirdToPhp != null ||
                specialRateUsdToPhp != null ||
                urr != null) {
            return true;
        }

        return false;
    }


    public Date getLoanMaturityDate() {
        return loanMaturityDate;
    }

    public Currency getBookingCurrency() {
        return bookingCurrency;
    }

    public String getInterestTerm() {
        return interestTerm;
    }

    public String getRepricingTerm() {
        return repricingTerm;
    }

    public String getRepricingTermCode() {
        return repricingTermCode;
    }

    public String getLoanTerm() {
        return loanTerm;
    }

    public String getLoanTermCode() {
        return loanTermCode;
    }

    //Do not use for domestic
    public BigDecimal getAmountIn(Currency currencyRequired, Boolean domestic) {
        try {
            if (currencyRequired.equals(this.currency)) {
                //Handles PHP-PHP , USD-USD, THIRD-THIRD
                return this.amount;
            } else if (currencyRequired.equals(Currency.getInstance("PHP")) && this.currency.equals(Currency.getInstance("USD"))) {
                //USD-PHP
                if (domestic) {// for domestic use urr for php to usd conversion
                    if (this.urr != null) {
                        return this.amount.multiply(this.urr);
                    } else {
                        return BigDecimal.ZERO;
                    }
                } else {
                    if (this.specialRateUsdToPhp != null) {
                        return this.amount.multiply(this.specialRateUsdToPhp);
                    } else if (this.passOnRateUsdToPhp != null) {
                        return this.amount.multiply(this.passOnRateUsdToPhp);
                    } else if (this.urr != null) {
                        return this.amount.multiply(this.urr);
                    }
                }

            } else if (currencyRequired.equals(Currency.getInstance("PHP")) && !this.currency.equals(Currency.getInstance("USD"))) {
                //This means it needs to find the value of a Third currency PHP-THIRD
                if (this.specialRateThirdToPhp != null) {
                    return this.amount.multiply(this.specialRateThirdToPhp);
                } else if (this.passOnRateThirdToPhp != null) {
                    return this.amount.multiply(this.passOnRateThirdToPhp);
                } else {
                    return BigDecimal.ZERO;
                }
            } else if (currencyRequired.equals(Currency.getInstance("USD")) && this.currency.equals(Currency.getInstance("PHP"))) {
                //USD-PHP
                if (domestic) {// for domestic use urr for php to usd conversion
                    if (this.urr != null) {
                        return this.amount.divide(this.urr, 2, BigDecimal.ROUND_HALF_UP);
                    } else {
                        return BigDecimal.ZERO;
                    }
                } else {
                    if (this.specialRateUsdToPhp != null) {
                        return this.amount.divide(this.specialRateUsdToPhp, 2, BigDecimal.ROUND_HALF_UP);
                    } else if (this.passOnRateUsdToPhp != null) {
                        return this.amount.divide(this.passOnRateUsdToPhp, 2, BigDecimal.ROUND_HALF_UP);
                    } else if (this.urr != null) {
                        return this.amount.divide(this.urr, 2, BigDecimal.ROUND_HALF_UP);
                    }
                }

            } else if (currencyRequired.equals(Currency.getInstance("USD"))
                    && (!this.currency.equals(Currency.getInstance("PHP")) && !this.currency.equals(Currency.getInstance("USD")))
                    ) {//THIRD-USD conversion
                //first convert to PHP
                //second convert to third
                BigDecimal amountInPHP = BigDecimal.ZERO;
                if (this.specialRateThirdToPhp != null) {
                    amountInPHP = this.amount.multiply(this.specialRateThirdToPhp);
                } else if (this.passOnRateThirdToPhp != null) {
                    amountInPHP = this.amount.multiply(this.passOnRateThirdToPhp);
                }
                if (amountInPHP != null && amountInPHP.compareTo(BigDecimal.ZERO) == 0) {
                    if (this.urr != null) {
                        return amountInPHP.divide(this.urr, 2, BigDecimal.ROUND_HALF_UP);
                    } else {
                        return BigDecimal.ZERO;
                    }
                } else {
                    return BigDecimal.ZERO;
                }

            } else if ((!currencyRequired.equals(Currency.getInstance("PHP"))
                    && !currencyRequired.equals(Currency.getInstance("USD")))
                    && this.currency.equals(Currency.getInstance("PHP"))
                    ) {//THIRD-PHP conversion
                if (this.specialRateThirdToPhp != null) {
                    return this.amount.divide(this.specialRateThirdToPhp, 2, BigDecimal.ROUND_HALF_UP);
                } else if (this.passOnRateThirdToPhp != null) {
                    return this.amount.divide(this.passOnRateThirdToPhp, 2, BigDecimal.ROUND_HALF_UP);
                }

            } else if ((!currencyRequired.equals(Currency.getInstance("PHP"))
                    && !currencyRequired.equals(Currency.getInstance("USD")))
                    && this.currency.equals(Currency.getInstance("USD"))
                    ) {//THIRD-USD conversion
                //first convert to php
                //second convert to third

                BigDecimal amountPHP = BigDecimal.ZERO;
                if (this.specialRateUsdToPhp != null) {
                    amountPHP = this.amount.multiply(this.specialRateUsdToPhp);
                } else if (this.passOnRateUsdToPhp != null) {
                    amountPHP = this.amount.multiply(this.passOnRateUsdToPhp);
                } else if (this.urr != null) {
                    amountPHP = this.amount.multiply(this.urr);
                }

                if (amountPHP != null && amountPHP.compareTo(BigDecimal.ZERO) == 0) {
                    if (this.specialRateThirdToPhp != null) {
                        return amountPHP.divide(this.specialRateThirdToPhp, 2, BigDecimal.ROUND_HALF_UP);
                    } else if (this.passOnRateThirdToPhp != null) {
                        return amountPHP.divide(this.passOnRateThirdToPhp, 2, BigDecimal.ROUND_HALF_UP);
                    } else {
                        return BigDecimal.ZERO;
                    }
                } else {
                    return BigDecimal.ZERO;
                }
            }

        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPaid() {
        return PaymentStatus.PAID.equals(status);
    }

    public String getInterestTermCode() {
        return interestTermCode;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Boolean getWithCramApproval() {
        return withCramApproval;
    }

    public void setWithCramApproval(Boolean withCramApproval) {
        this.withCramApproval = withCramApproval;
    }

    public Long getId() {
        return id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountInLcCurrency() {
        return amountInLcCurrency;
    }

    public void setAmountInLcCurrency(BigDecimal amountInLcCurrency) {
        this.amountInLcCurrency = amountInLcCurrency;
    }

	public String getAgriAgraTagging() {
		return agriAgraTagging;
	}

	public void setAgriAgraTagging(String agriAgraTagging) {
		this.agriAgraTagging = agriAgraTagging;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

    public Date getPaidDate() {
        return paidDate;
    }

    public void updateRatesNameDescription(
            String thirdToUsdRateName,
            String thirdToPhpRateName,
            String usdToPhpRateName,
            String urrRateName,
            String thirdToUsdRateDescription,
            String thirdToPhpRateDescription,
            String usdToPhpRateDescription,
            String urrRateDescription) {
        this.thirdToUsdRateName = thirdToUsdRateName;
        this.thirdToPhpRateName = thirdToPhpRateName;
        this.usdToPhpRateName = usdToPhpRateName;
        this.urrRateName = urrRateName;
        this.thirdToUsdRateDescription = thirdToUsdRateDescription;
        this.thirdToPhpRateDescription = thirdToPhpRateDescription;
        this.usdToPhpRateDescription = usdToPhpRateDescription;
        this.urrRateDescription = urrRateDescription;
    }

    public String getThirdToUsdRateName() {
        return thirdToUsdRateName;
    }

    public String getThirdToPhpRateName() {
        return thirdToPhpRateName;
    }

    public String getUsdToPhpRateName() {
        return usdToPhpRateName;
    }

    public String getUrrRateName() {
        return urrRateName;
    }

    public String getThirdToUsdRateDescription() {
        return thirdToUsdRateDescription;
    }

    public String getThirdToPhpRateDescription() {
        return thirdToPhpRateDescription;
    }

    public String getUsdToPhpRateDescription() {
        return usdToPhpRateDescription;
    }

    public String getUrrRateDescription() {
        return urrRateDescription;
    }

    public Long getNumberOfFreeFloatDays() {
        return numberOfFreeFloatDays;
    }

    public void setNumberOfFreeFloatDays(Long numberOfFreeFloatDays) {
        this.numberOfFreeFloatDays = numberOfFreeFloatDays;
    }
}
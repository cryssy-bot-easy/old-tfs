package com.ucpb.tfs.domain.payment;

import com.incuventure.ddd.domain.annotations.DomainAggregateRoot;
import com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus;
import com.ucpb.tfs.domain.service.ChargeType;
import com.ucpb.tfs.domain.service.TradeServiceId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@DomainAggregateRoot
public class Payment implements Serializable {

    private Long id;

    private TradeServiceId tradeServiceId;

    private ChargeType chargeType;

    private PaymentStatus status;

    private Set<PaymentDetail> details;

    private Date paidDate;

    public Payment() {
        this.details = new HashSet<PaymentDetail>();
        this.status = PaymentStatus.UNPAID;
    }

    public Payment(TradeServiceId tradeServiceId, ChargeType chargeType) {
        this();
        this.tradeServiceId = tradeServiceId;
        this.chargeType = chargeType;
    }

    public void addNewPaymentDetails(Set<PaymentDetail> details) {
        this.details.addAll(details);
    }

    public void addOrUpdateItem(PaymentInstrumentType type, String referenceNumber, BigDecimal amount, Currency currency,
                                BigDecimal passOnRateThirdToUsd, BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateUsdToPhp,
                                BigDecimal specialRateThirdToUsd, BigDecimal specialRateThirdToPhp, BigDecimal specialRateUsdToPhp, BigDecimal urr,String accountName) {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber)) {
                detail.update(amount, currency,
                        passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                        specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);
                detail.setAccountName(accountName);
                exists = true;
                break;
            }
        }

        if (!exists) {
            PaymentDetail paymentDetail = new PaymentDetail(type, referenceNumber, amount, currency,
                    passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                    specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);
            paymentDetail.setAccountName(accountName);
            this.details.add(paymentDetail);
        }
    }

    public void addOrUpdateItem(PaymentInstrumentType type, String referenceNumber, BigDecimal amount, Currency currency,
                                BigDecimal passOnRateThirdToUsd, BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateUsdToPhp,
                                BigDecimal specialRateThirdToUsd, BigDecimal specialRateThirdToPhp, BigDecimal specialRateUsdToPhp, BigDecimal urr,String accountName, BigDecimal amountInLcCurrency) {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber)) {
                detail.update(amount, currency,
                        passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                        specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency);
                detail.setAccountName(accountName);
                exists = true;
                break;
            }
        }

        if (!exists) {
            PaymentDetail paymentDetail = new PaymentDetail(type, referenceNumber, amount, currency,
                    passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                    specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency);
            paymentDetail.setAccountName(accountName);
            this.details.add(paymentDetail);
        }
    }

    public void addOrUpdateItem(PaymentInstrumentType type, String referenceNumber, BigDecimal amount, Currency currency,
                                BigDecimal passOnRateThirdToUsd, BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateUsdToPhp,
                                BigDecimal specialRateThirdToUsd, BigDecimal specialRateThirdToPhp, BigDecimal specialRateUsdToPhp, BigDecimal urr, BigDecimal amountInLcCurrency) {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber)) {
                detail.update(amount, currency,
                        passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                        specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency);
                exists = true;
                break;
            }
        }

        if (!exists) {
            this.details.add(new PaymentDetail(type, referenceNumber, amount, currency,
                    passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                    specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency));
        }
    }

    public void addOrUpdateItem(PaymentInstrumentType type, String referenceNumber, BigDecimal amount, Currency currency, String referenceId,
                                BigDecimal passOnRateThirdToUsd, BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateUsdToPhp,
                                BigDecimal specialRateThirdToUsd, BigDecimal specialRateThirdToPhp, BigDecimal specialRateUsdToPhp, BigDecimal urr) {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber, referenceId)) {
                detail.setAmount(amount);
                detail.update(amount, currency,
                        passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                        specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr);
                exists = true;
                break;
            }
        }

        if (!exists) {
            this.details.add(new PaymentDetail(type, referenceNumber, referenceId, amount, currency,
                    passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                    specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr));
        }
    }

    public void addOrUpdateItem(PaymentInstrumentType type, String referenceNumber, BigDecimal amount, Currency currency, String referenceId,
                                BigDecimal passOnRateThirdToUsd, BigDecimal passOnRateThirdToPhp, BigDecimal passOnRateUsdToPhp,
                                BigDecimal specialRateThirdToUsd, BigDecimal specialRateThirdToPhp, BigDecimal specialRateUsdToPhp, BigDecimal urr, BigDecimal amountInLcCurrency) {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber, referenceId)) {
                detail.update(amount, currency,
                        passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                        specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency);
                exists = true;
                break;
            }
        }

        if (!exists) {
            this.details.add(new PaymentDetail(type, referenceNumber, referenceId, amount, currency,
                    passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                    specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr, amountInLcCurrency));
        }
    }

    public void addOrUpdateItem(PaymentInstrumentType type,
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
                                Integer paymentCode) {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber)) {
                detail.update(amount, currency, bookingCurrency, interestRate, interestTerm, interestTermCode, repricingTerm, repricingTermCode, loanTerm, loanTermCode, loanMaturityDate,
                        passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                        specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                        facilityId, facilityType, facilityReferenceNumber, paymentCode);
                exists = true;
                break;
            }
        }

        if (!exists) {
            this.details.add(new PaymentDetail(type, referenceNumber, amount, currency, bookingCurrency, interestRate, interestTerm, interestTermCode, repricingTerm, repricingTermCode, loanTerm, loanTermCode, loanMaturityDate, paymentTerm,
                    passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                    specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                    facilityId, facilityType, facilityReferenceNumber));
        }
    }

    public void addOrUpdateItem(PaymentInstrumentType type,
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
                                Integer paymentCode,
                                BigDecimal amountInLcCurrency) {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber)) {
                detail.update(amount, currency, bookingCurrency, interestRate, interestTerm, interestTermCode, repricingTerm, repricingTermCode, loanTerm, loanTermCode, loanMaturityDate,
                        passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                        specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                        facilityId, facilityType, facilityReferenceNumber, paymentCode, amountInLcCurrency);
                exists = true;
                break;
            }
        }

        if (!exists) {
            this.details.add(new PaymentDetail(type, referenceNumber, amount, currency, bookingCurrency, interestRate, interestTerm, interestTermCode, repricingTerm, repricingTermCode, loanTerm, loanTermCode, loanMaturityDate, paymentTerm,
                    passOnRateThirdToUsd, passOnRateThirdToPhp, passOnRateUsdToPhp,
                    specialRateThirdToUsd, specialRateThirdToPhp, specialRateUsdToPhp, urr,
                    facilityId, facilityType, facilityReferenceNumber, amountInLcCurrency));
        }
    }


    public void deleteAllPaymentDetails() {
        this.details.removeAll(this.details);
    }

    public void clearAllPaymentDetails() {
        this.details.clear();
    }

    public void updatePaymentDetails(Set<PaymentDetail> paymentDetails) {
        this.details.clear();
        this.details.addAll(paymentDetails);
    }

    public void deleteItem(PaymentInstrumentType type, String referenceNumber) throws Exception {

        Iterator<PaymentDetail> it = this.details.iterator();

//        settlement to beneficiary payments
        List<PaymentInstrumentType> settlementPayments = Arrays.asList(PaymentInstrumentType.MC_ISSUANCE,
                PaymentInstrumentType.SWIFT,
                PaymentInstrumentType.PDDTS);


        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber)) {
                if (detail.getStatus().equals(PaymentStatus.PAID) &&
                        !settlementPayments.contains(detail.getPaymentInstrumentType())) {
                    throw new Exception("Cannot delete a paid item.");
                } else {
                    it.remove();
                }
                break;
            }
        }
    }

    public void deleteItem(PaymentInstrumentType type, String referenceNumber, String referenceId) throws Exception {

        List<PaymentInstrumentType> settlementPayments = Arrays.asList(PaymentInstrumentType.MC_ISSUANCE,
                PaymentInstrumentType.SWIFT,
                PaymentInstrumentType.PDDTS);

        Iterator<PaymentDetail> it = this.details.iterator();

        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber, referenceId)) {
                if (detail.getStatus().equals(PaymentStatus.PAID) &&
                        !settlementPayments.contains(detail.getPaymentInstrumentType())) {
                    throw new Exception("Cannot delete a paid item.");
                } else {
                    it.remove();
                }
                break;
            }
        }
    }

    public void payItem(PaymentInstrumentType paymentInstrumentType, String settlementAccountNumber, String referenceNumber) throws Exception {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean exists = Boolean.FALSE;
        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(paymentInstrumentType, referenceNumber)) {
                detail.paid();
                exists = Boolean.TRUE;
            }
        }

        // TODO: Always check if payment is already in excess. If excess, create an AP GL data entry.

        if (!exists) {
            throw new Exception("No matching payment detail was found.");
        }

        // Set status of Payment as PAID if every PaymentDetail is PAID.
        if (fullyPaid()) {
            this.status = PaymentStatus.PAID;
            this.paidDate = new Date();
        }
    }

    public boolean hasSwift() {
        for (PaymentDetail paymentDetail : details) {
            if (PaymentInstrumentType.SWIFT.equals(paymentDetail.getPaymentInstrumentType())) {
                return true;
            }
        }
        return false;
    }

    public boolean payItem(Long paymentDetailId) {
        for (PaymentDetail detail : details) {
            if (detail.getId().equals(paymentDetailId)) {
                detail.paid();

                if (fullyPaid()) {
                    this.status = PaymentStatus.PAID;
                    this.paidDate = new Date();
                }
                return true;
            }
        }
        return false;
    }

    public void addPaymentDetail(PaymentDetail detail){
        if(details == null){
            details = new HashSet<PaymentDetail>();
        }
        details.add(detail);
    }

    public void payItem(PaymentInstrumentType paymentInstrumentType, String settlementAccountNumber, String referenceNumber, String referenceId) throws Exception {
        boolean exists = false;
        for (PaymentDetail detail : details) {
            if (detail.matches(paymentInstrumentType, referenceNumber, referenceId)) {
                detail.paid();
                exists = true;
            }
        }
        // TODO: Always check if payment is already in excess. If excess, create an AP GL data entry.
        if (!exists) {
            throw new Exception("No matching payment detail was found.");
        }

        // Set status of Payment as PAID if every PaymentDetail is PAID.
        if (fullyPaid()) {
            this.status = PaymentStatus.PAID;
            this.paidDate = new Date();
        }
    }

    public boolean fullyPaid() {
        for (PaymentDetail detail : details) {
            if (PaymentStatus.UNPAID.equals(detail.getStatus())) {
                return false;
            }
        }
        return true;
    }

    public void reverseItemPayment(PaymentInstrumentType type, String settlementAccountNumber, String referenceNumber) throws Exception {
        System.out.println("reverseItemPayment(PaymentInstrumentType type, String settlementAccountNumber, String referenceNumber)");
        Iterator<PaymentDetail> it = this.details.iterator();

        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber)) {
                if (detail.getStatus().equals(PaymentStatus.PAID)) {
                    detail.unPay();  // Unpay payment detail
                    this.unPay();    // Unpay payment
                }
                break;
            }
        }
    }

    public boolean reverseItemPayment(Long paymentDetailId){
        System.out.println("reverseItemPayment(Long paymentDetailId)");
        for(PaymentDetail detail : details){
            if(detail.getId().equals(paymentDetailId)){
                detail.unPay();
                this.unPay();
                return true;
            }
        }
        return false;
    }

    public void reverseItemPayment(PaymentInstrumentType type, String settlementAccountNumber, String referenceNumber, String referenceId) throws Exception {
        System.out.println("reverseItemPayment(PaymentInstrumentType type, String settlementAccountNumber, String referenceNumber, String referenceId)");

        Iterator<PaymentDetail> it = this.details.iterator();

        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.matches(type, referenceNumber, referenceId)) {
                if (detail.getStatus().equals(PaymentStatus.PAID)) {
                    detail.unPay();  // Unpay payment detail
                    this.unPay();    // Unpay payment
                }
                break;
            }
        }
    }

    public void reverseAllItemPayments() {
        System.out.println("reverseAllItemPayments()");
        Iterator<PaymentDetail> it = this.details.iterator();

        while (it.hasNext()) {
            PaymentDetail detail = (PaymentDetail) it.next();
            if (detail.getStatus().equals(PaymentStatus.PAID)) {
                detail.unPay();  // Unpay payment detail
            }
        }

        this.unPay();    // Unpay payment
    }

    public void unPay() {
        this.status = PaymentStatus.UNPAID;
        this.paidDate = null;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setChargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
    }

    //TODO: LOL! Check?
    public BigDecimal getTotalPaid(Currency paymentTargetCurrency) {

        BigDecimal totalPaid = BigDecimal.ZERO;
        totalPaid = totalPaid.setScale(2, RoundingMode.HALF_UP);

        Iterator<PaymentDetail> it = this.details.iterator();
        while (it.hasNext()) {
            PaymentDetail detail = it.next();
            if (detail.getStatus().equals(PaymentStatus.PAID)) {
                if (detail.getCurrency().equals(paymentTargetCurrency)) {
                    // Just add
                    totalPaid = totalPaid.add(detail.getAmount());
                } else {
                    BigDecimal convertedSettlementAmount = BigDecimal.ZERO;
                    convertedSettlementAmount = convertedSettlementAmount.setScale(2, RoundingMode.HALF_UP);
                    // Convert
                    // If paymentTargetCurrency = Peso
                    if (paymentTargetCurrency.equals(Currency.getInstance("PHP"))) {
                        if (detail.getCurrency().equals(Currency.getInstance("USD"))) {
                            convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateUsdToPhp() != null ? detail.getSpecialRateUsdToPhp() : detail.getUrr());
                        } else {
                            if (detail.getSpecialRateThirdToUsd() != null && detail.getSpecialRateUsdToPhp()!=null) {
                                convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd().multiply(detail.getSpecialRateUsdToPhp()));
                            } else {
                                //TODO: Fix computation
//                              System.out.println("ThirdToUsd:"+this.);
                                System.out.println("amount: " + detail.getAmount());
                                if (detail.getSpecialRateThirdToUsd() != null) {
                                    BigDecimal thirdToUsd = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                                    convertedSettlementAmount = thirdToUsd.multiply(detail.getSpecialRateUsdToPhp());
                                } else {

                                }
                            }
                        }

                        // If paymentTargetCurrency = US Dollar
                    } else if (paymentTargetCurrency.equals(Currency.getInstance("USD"))) {
                        if (detail.getCurrency().equals(Currency.getInstance("PHP"))) {
                            convertedSettlementAmount = detail.getAmount().divide(detail.getSpecialRateUsdToPhp(), RoundingMode.HALF_UP);
                        } else {
                            convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                        }

                        // If paymentTargetCurrency = Third Currency
                    } else {
                        if (detail.getCurrency().equals(Currency.getInstance("PHP"))) {
                            if (detail.getSpecialRateThirdToUsd() != null && detail.getSpecialRateUsdToPhp()!= null) {
                                convertedSettlementAmount = detail.getAmount().divide(detail.getSpecialRateThirdToUsd().multiply(detail.getSpecialRateUsdToPhp()), RoundingMode.HALF_UP);
                            } else {
                                BigDecimal thirdToUsd = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                                convertedSettlementAmount = thirdToUsd.multiply(detail.getSpecialRateUsdToPhp());
                            }
                        } else if (detail.getCurrency().equals(Currency.getInstance("USD"))) {
                            // Marv.13Feb2013 Third to USD should divide since the direction is to the left if the target currency is Third
                            convertedSettlementAmount = detail.getAmount().divide(detail.getSpecialRateThirdToUsd(), RoundingMode.HALF_UP);
                            // convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                        }
                    }
                    totalPaid = totalPaid.add(convertedSettlementAmount);
                }
            }
        }

        System.out.println("\nTOTAL PAID = " + totalPaid.doubleValue() + "\n");
        return totalPaid;
    }

    public Boolean checkIfPaymentCurrenciesIsNonPhp() {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean isNonPhp = false;
        while (it.hasNext()) {
            PaymentDetail detail = it.next();
            if (!detail.getCurrency().equals(Currency.getInstance("PHP"))) {
                isNonPhp = true;
                break;
            }
        }

        return isNonPhp;
    }

    public boolean hasTrLoan() {
        for (PaymentDetail detail : details) {
            if (PaymentInstrumentType.TR_LOAN.equals(detail.getPaymentInstrumentType())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUaLoan() {
        for (PaymentDetail detail : details) {
            if (PaymentInstrumentType.UA_LOAN.equals(detail.getPaymentInstrumentType())) {
                return true;
            }
        }
        return false;
    }


    public Boolean isPresent(PaymentInstrumentType paymentInstrumentType, String referenceNumber) {

        Iterator<PaymentDetail> it = this.details.iterator();

        Boolean exists = false;
        while (it.hasNext()) {
            PaymentDetail detail = it.next();
            if (detail.matches(paymentInstrumentType, referenceNumber)) {
                exists = true;
                break;
            }
        }

        return exists;
    }

    public Set<PaymentDetail> getDetails() {
        return details;
    }

    public PaymentDetail getLoanPayment() {
        for (PaymentDetail payment : details) {
            if (payment.getPaymentInstrumentType().isLoan()) {
                return payment;
            }
        }
        return null;
    }

    public PaymentDetail getTRLoanPayment() {
        for (PaymentDetail payment : details) {
            if (payment.getPaymentInstrumentType().equals(PaymentInstrumentType.TR_LOAN)) {
                return payment;
            }
        }
        return null;
    }

    public PaymentDetail getUALoanPayment() {
        for (PaymentDetail payment : details) {
            if (payment.getPaymentInstrumentType().equals(PaymentInstrumentType.UA_LOAN)) {
                return payment;
            }
        }
        return null;
    }


    // adjusts loan maturity date
    public void adjustUaLoanMaturityDate(String referenceNumber, Date newUaLoanMaturityDate) {

        Iterator<PaymentDetail> it = this.details.iterator();

        while (it.hasNext()) {
            PaymentDetail detail = it.next();
            if (detail.matches(PaymentInstrumentType.UA_LOAN, referenceNumber)) {
                detail.setLoanMaturityDate(newUaLoanMaturityDate);
                break;
            }
        }
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public PaymentDetail getPaymentDetail(PaymentInstrumentType type) {
        for (PaymentDetail payment : details) {
            if (type.equals(payment.getPaymentInstrumentType())) {
                return payment;
            }
        }
        return null;
    }

    public PaymentDetail getPaymentDetail(Long id) {
        for (PaymentDetail payment : details) {
            if(payment.getId().equals(id)){
                return payment;
            }
        }
        return null;
    }

    public void put(PaymentDetail detail) {
        Iterator<PaymentDetail> iterator = details.iterator();
        while (iterator.hasNext()) {
            PaymentDetail reference = iterator.next();
            if (detail.getPaymentInstrumentType().equals(reference.getPaymentInstrumentType())) {
                iterator.remove();
                break;
            }
        }
        details.add(detail);
    }

    // specifically used for CASA since CASA can be more than 1 instance but with different currency
    public void putById(PaymentDetail detail) {
        Iterator<PaymentDetail> iterator = details.iterator();
        while (iterator.hasNext()) {
            PaymentDetail reference = iterator.next();
            if (detail.getId().equals(reference.getId())) {
                iterator.remove();
                break;
            }
        }
        details.add(detail);
    }

    public String displayList() {
        //System.out.println("type: " + chargeType);
        //System.out.println("details: " + details.size());
        return "type: " + chargeType + "\n" + "details: " + details.size();
    }

    //Returns BigDecimal sum of payment details amount in target currency curr
    public BigDecimal getTotalPrePaymentWithCurrency(Currency curr) {

        BigDecimal totalPaid = BigDecimal.ZERO;
        totalPaid = totalPaid.setScale(2, RoundingMode.HALF_UP);

        for (PaymentDetail detail : this.details) {
            if (detail.getCurrency().equals(curr)) {
                totalPaid = totalPaid.add(detail.getAmount());
            }
        }

        System.out.println("\nTOTAL PRE PAID In Target Currency "+ curr + "= " + totalPaid.toPlainString() + "\n");
        return totalPaid;
    }

    //Returns BigDecimal sum of payment details amount in target currency curr
    public BigDecimal getTotalPrePaymentWithCurrency(String curr) {
        System.out.println("getTotalPrePaymentWithCurrency(String curr)");
        BigDecimal totalPaid = BigDecimal.ZERO;
        totalPaid = totalPaid.setScale(2, RoundingMode.HALF_UP);

        for (PaymentDetail detail : this.details) {
            if (detail.getCurrency().getCurrencyCode().equalsIgnoreCase(curr)) {
                totalPaid = totalPaid.add(detail.getAmount());
            }
        }

        System.out.println("\nTOTAL PRE PAID In Target Currency "+ curr + "= " + totalPaid.toPlainString() + "\n");
        return totalPaid;
    }

    public BigDecimal getTotalPrePaymentWithCurrencySellRate(String curr) {
        System.out.println("getTotalPrePaymentWithCurrencySellRate(String curr)");
        for (PaymentDetail detail : this.details) {
            if (detail.getCurrency().getCurrencyCode().equalsIgnoreCase(curr)) {
                System.out.println("detail.getSpecialRateThirdToUsd():"+detail.getSpecialRateThirdToUsd());

                return  detail.getSpecialRateThirdToUsd();
            }
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getPaymentUrr() {
        System.out.println("getTotalPrePaymentWithCurrencyUrr(String curr)");
        for (PaymentDetail detail : this.details) {
                System.out.println("detail.getUrr():"+detail.getUrr());
                // Just add
                return  detail.getUrr();
        }

        return BigDecimal.ZERO;
    }

    //TODO:Test
    public BigDecimal getTotalPrePayment(Currency paymentTargetCurrency) {

        BigDecimal totalPaid = BigDecimal.ZERO;
        totalPaid = totalPaid.setScale(2, RoundingMode.HALF_UP);


        for (PaymentDetail detail : this.details) {


            if (detail.getCurrency().equals(paymentTargetCurrency)) {

                // Just add
                totalPaid = totalPaid.add(detail.getAmount());

            } else {

                BigDecimal convertedSettlementAmount = BigDecimal.ZERO;
                convertedSettlementAmount = convertedSettlementAmount.setScale(2, RoundingMode.HALF_UP);

                // Convert

                // If paymentTargetCurrency = Peso
                if (paymentTargetCurrency.equals(Currency.getInstance("PHP"))) {

                    if (detail.getCurrency().equals(Currency.getInstance("USD"))) {

                        //convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateUsdToPhp());
                        if (detail.hasRates() == true) {
                            if(withMultiplePaymentCurrency()) {
                                //use sell-sell
                                convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateUsdToPhp());
                            } else if (!isForeignSettledWithForeign()){
                                //use sell-sell
                                convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateUsdToPhp());
                            } else {
                                //use sell-urr
                                convertedSettlementAmount = detail.getAmount().multiply(detail.getUrr());
                            }
                            //convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateUsdToPhp());
                        }

                    } else {

                        if(withMultiplePaymentCurrency()) {
                            //use sell-sell
                            BigDecimal thirdToUsd = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                            convertedSettlementAmount = thirdToUsd.multiply(detail.getSpecialRateUsdToPhp());
                        } else if (!isForeignSettledWithForeign()){
                            //use sell-sell
                            BigDecimal thirdToUsd = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                            convertedSettlementAmount = thirdToUsd.multiply(detail.getSpecialRateUsdToPhp());
                        } else {
                            //use sell-urr
                            BigDecimal thirdToUsd = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                            convertedSettlementAmount = thirdToUsd.multiply(detail.getUrr());
                        }

//                        if (detail.getSpecialRateThirdToPhp() != null) {
//
//                            convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateThirdToPhp());
//
//                        } else {
//                            //TODO: Fix computation
////                              System.out.println("ThirdToUsd:"+this.);
//                            System.out.println("amount: " + detail.getAmount());
//                            if (detail.getSpecialRateThirdToUsd() != null) {
//                                BigDecimal thirdToUsd = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
//                                convertedSettlementAmount = thirdToUsd.multiply(detail.getSpecialRateUsdToPhp());
//                            } else {
//
//                            }
//                        }
                    }

                    // If paymentTargetCurrency = US Dollar
                } else if (paymentTargetCurrency.equals(Currency.getInstance("USD"))) {
                    if (detail.getCurrency().equals(Currency.getInstance("PHP"))) {
                        if(withMultiplePaymentCurrency()) {
                            //use sell-sell
                            convertedSettlementAmount = detail.getAmount().divide(detail.getSpecialRateUsdToPhp(), RoundingMode.HALF_UP);
                        } else if (!isForeignSettledWithForeign()){
                            //use sell-sell
                            convertedSettlementAmount = detail.getAmount().divide(detail.getSpecialRateUsdToPhp(), RoundingMode.HALF_UP);
                        } else {
                            //use sell-urr
                            convertedSettlementAmount = detail.getAmount().divide(detail.getUrr(), RoundingMode.HALF_UP);
                        }
                        //convertedSettlementAmount = detail.getAmount().divide(detail.getSpecialRateUsdToPhp(), RoundingMode.HALF_UP);

                    } else {
                        if(withMultiplePaymentCurrency()) {
                            //use sell-sell
                            convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateUsdToPhp());
                        } else if (!isForeignSettledWithForeign()){
                            //use sell-sell
                            convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateUsdToPhp());
                        } else {
                            //use sell-urr
                            convertedSettlementAmount = detail.getAmount().multiply(detail.getUrr());
                        }
                        //convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                    }
                    // If paymentTargetCurrency = Third Currency
                } else {
                    if (detail.getCurrency().equals(Currency.getInstance("PHP"))) {
                        if(withMultiplePaymentCurrency()) {
                            //use sell-sell
                            BigDecimal thirdToUsd = detail.getSpecialRateUsdToPhp().multiply(detail.getSpecialRateThirdToUsd());
                            convertedSettlementAmount = detail.getAmount().divide(thirdToUsd, 2, BigDecimal.ROUND_HALF_UP);
                        } else if (!isForeignSettledWithForeign()){
                            //use sell-sell
                            BigDecimal thirdToUsd = detail.getSpecialRateUsdToPhp().multiply(detail.getSpecialRateThirdToUsd());
                            convertedSettlementAmount = detail.getAmount().divide(thirdToUsd, 2, BigDecimal.ROUND_HALF_UP);
                        } else {
                            //use sell-urr
                            BigDecimal thirdToUsd = detail.getUrr().multiply(detail.getSpecialRateThirdToUsd());
                            convertedSettlementAmount = detail.getAmount().divide(thirdToUsd, 2, BigDecimal.ROUND_HALF_UP);
                        }

//                        if (detail.getSpecialRateThirdToPhp() != null) {
//
//                            convertedSettlementAmount = detail.getAmount().divide(detail.getSpecialRateThirdToPhp(), RoundingMode.HALF_UP);
//
//                        } else {
//
//                            BigDecimal thirdToUsd = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
//                            convertedSettlementAmount = thirdToUsd.multiply(detail.getSpecialRateUsdToPhp());
//                        }
                    } else if (detail.getCurrency().equals(Currency.getInstance("USD"))) {

                        if(withMultiplePaymentCurrency()) {
                            //use sell-sell
                            convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                        } else if (!isForeignSettledWithForeign()){
                            //use sell-sell
                            convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                        } else {
                            //use sell-urr
                            convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                        }
                        //convertedSettlementAmount = detail.getAmount().multiply(detail.getSpecialRateThirdToUsd());
                    }
                }

                totalPaid = totalPaid.add(convertedSettlementAmount);
            }
        }

        System.out.println("\nTOTAL PRE PAID = " + totalPaid.doubleValue() + "\n");
        return totalPaid;
    }


    public void setReferenceId(PaymentInstrumentType paymentInstrumentType, String referenceNumber, String referenceId) throws Exception {
        boolean exists = false;
        for (PaymentDetail detail : details) {
            if (detail.matches(paymentInstrumentType, referenceNumber)) {
                detail.paid();
                detail.setReferenceId(referenceId);
                exists = true;
            }
        }
        // TODO: Always check if payment is already in excess. If excess, create an AP GL data entry.
        if (!exists) {
            throw new Exception("No matching payment detail was found.");
        }
    }

    public String getReferenceId(PaymentInstrumentType paymentInstrumentType, String referenceNumber) {
        String referenceIdReturn = null;
        for (PaymentDetail detail : details) {
            if (detail.matches(paymentInstrumentType, referenceNumber)) {
                referenceIdReturn = detail.getReferenceId();
            }
        }

        return referenceIdReturn;
    }

    public Boolean withMultiplePaymentCurrency() {
        Set<PaymentDetail> paymentDetails = this.getDetails();
        Boolean multiplePaymentCurrency = Boolean.FALSE;
        Currency tCurrency = null;
        for (PaymentDetail paymentDetail : paymentDetails) {
            Currency curr = paymentDetail.getCurrency();
            if (tCurrency == null) {
                tCurrency = curr;
            } else if (!tCurrency.equals(curr)) {
                multiplePaymentCurrency = Boolean.TRUE;
            }
        }
        return multiplePaymentCurrency;
    }

    public Boolean isForeignSettledWithForeign() {
        Set<PaymentDetail> paymentDetails = this.getDetails();
        Boolean settledWithForeign = Boolean.FALSE;
        Currency tCurrency = null;
        for (PaymentDetail paymentDetail : paymentDetails) {
            Currency curr = paymentDetail.getCurrency();

            if (!curr.getCurrencyCode().equalsIgnoreCase("PHP")) {
                settledWithForeign = Boolean.TRUE;
            }
        }
        return settledWithForeign;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }


    public BigDecimal getTotalPreUsdWithCurrencyUrrNotTr() {
        System.out.println("getTotalPrePaymentWithCurrencySellRate(String curr)");
        BigDecimal phpAmount=BigDecimal.ZERO;
        for (PaymentDetail detail : this.details) {
            if(!detail.getPaymentInstrumentType().toString().equalsIgnoreCase("TR_LOAN")){
                if(detail.getCurrency().getCurrencyCode().equalsIgnoreCase("PHP")){
                    phpAmount = phpAmount.add(detail.getAmount());
                } else if (detail.getCurrency().getCurrencyCode().equalsIgnoreCase("USD")){
                    phpAmount = phpAmount.add(detail.getAmount().multiply(detail.getUrr()));
                } else {
                    phpAmount = phpAmount.add(detail.getAmount().multiply(detail.getUrr().multiply(detail.getSpecialRateThirdToUsd())));
                }
            }
        }
        return phpAmount;
    }

    public Boolean containsPddtsOrSwift() {
        for (PaymentDetail paymentDetail : this.details) {
            if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.PDDTS) ||
                    paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.SWIFT)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

	public Boolean containsCasaOrIbt() {
		for (PaymentDetail paymentDetail : this.details) {
			if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.CASA) ||
					paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.IBT_BRANCH) ||
					paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.REMITTANCE)) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}

	public boolean contains(PaymentInstrumentType paymentInstrumentType){
		for(PaymentDetail paymentDetail : this.details){
			if(paymentDetail.getPaymentInstrumentType().equals(paymentInstrumentType)){
				return true;
			}
		}
		return false;
	}
	
    public void setTradeServiceId(TradeServiceId tradeServiceId) {
        this.tradeServiceId = tradeServiceId;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }
}
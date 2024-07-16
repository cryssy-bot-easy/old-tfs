package com.ucpb.tfs.domain.corresCharges;

import com.ucpb.tfs.domain.corresCharges.enumTypes.CorresChargeStatus;
import com.ucpb.tfs.domain.corresCharges.enumTypes.CorresChargeType;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 11/4/12
 *
 * Implements Comparator for automatic sorting for hierarchy allocation (for LC's only).
 * Will allocate the Actual Corres Charge in this order:
 * 1. Opening
 *    1.1. Advising
 *    1.2. Confirming
 * 2. Amendment
 *    2.1. Advising
 *    2.2. Confirming
 */
public class CorresChargeAdvance implements Comparator<CorresChargeAdvance> {

    private Long id;

    private TradeServiceId tradeServiceId;

    private DocumentNumber documentNumber;

    private ServiceType serviceType;

    private CorresChargeType corresChargeType;

    private CorresChargeStatus status;

    private BigDecimal amount;
    private Currency currency;

    private BigDecimal specialRateThirdToUsd;
    private BigDecimal specialRateUsdToPhp;
    private BigDecimal specialRateThirdToPhp;
    private BigDecimal specialRateUrr;

    private Date remittedDate;

    private Date createdDate;

    private BigDecimal coveredAmount;


    public CorresChargeAdvance() {
        this.createdDate = new Date();
        this.coveredAmount = BigDecimal.ZERO;
        this.status = CorresChargeStatus.MARV;
    }

    public CorresChargeAdvance(TradeServiceId tradeServiceId,
                               DocumentNumber documentNumber,
                               ServiceType serviceType,
                               CorresChargeType corresChargeType,
                               BigDecimal amount,
                               Currency currency) {
        this();
        this.tradeServiceId = tradeServiceId;
        this.documentNumber = documentNumber;
        this.serviceType = serviceType;
        this.corresChargeType = corresChargeType;
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public int compare(CorresChargeAdvance advance1, CorresChargeAdvance advance2) {

        int returnVal = 0;

        ServiceType serviceType1 = advance1.getServiceType();
        ServiceType serviceType2 = advance2.getServiceType();

        if (serviceType1 != null && serviceType2 != null) {

            // Compare for LC only, on this order:
            // 1. Opening
            //    1.1. Advising
            //    1.2. Confirming
            // 2. Amendment
            //    2.1. Advising
            //    2.2. Confirming
            if ((serviceType1.equals(ServiceType.OPENING) || serviceType1.equals(ServiceType.AMENDMENT)) &&
                (serviceType2.equals(ServiceType.OPENING) || serviceType2.equals(ServiceType.AMENDMENT))) {

                if (serviceType1.equals(ServiceType.OPENING) && serviceType2.equals(ServiceType.AMENDMENT)) {
                    returnVal = 1;
                }
                else if (serviceType1.equals(ServiceType.AMENDMENT) && serviceType2.equals(ServiceType.OPENING)) {
                    returnVal = -1;
                }
                else if (serviceType1.equals(ServiceType.OPENING) && serviceType2.equals(ServiceType.OPENING)) {

                    returnVal = compareCorresChargeType(advance1.getCorresChargeType(), advance2.getCorresChargeType());

                }
                else if (serviceType1.equals(ServiceType.AMENDMENT) && serviceType2.equals(ServiceType.AMENDMENT)) {

                    returnVal = compareCorresChargeType(advance1.getCorresChargeType(), advance2.getCorresChargeType());
                }
            }
        }
        return returnVal;
    }

    private int compareCorresChargeType(CorresChargeType corresChargeType1, CorresChargeType corresChargeType2) {
        int returnVal = 0;
        if (corresChargeType1 != null && corresChargeType2 != null) {
            if (corresChargeType1.equals(CorresChargeType.ADVISING) && corresChargeType2.equals(CorresChargeType.CONFIRMING)) {
                returnVal = 1;
            } else if (corresChargeType1.equals(CorresChargeType.CONFIRMING) && corresChargeType2.equals(CorresChargeType.ADVISING)) {
                returnVal = -1;
            }
        }
        return returnVal;
    }

    public void updateStatus(CorresChargeStatus status) {
        this.status = status;
    }

    public void updateRemittedDate() {
        this.remittedDate = new Date();
    }

    public TradeServiceId getTradeServiceId() {
        return tradeServiceId;
    }

    public DocumentNumber getDocumentNumber() {
        return documentNumber;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public CorresChargeType getCorresChargeType() {
        return corresChargeType;
    }

    public CorresChargeStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Date getRemittedDate() {
        return remittedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCoveredAmount(BigDecimal coveredAmount) {
        this.coveredAmount = coveredAmount;
    }

    public BigDecimal getCoveredAmount() {
        return coveredAmount;
    }

    /*
    public void addOrUpdateAmount(TradeServiceId tradeServiceId, ServiceType serviceType, CorresChargeType corresChargeType, BigDecimal amount, Currency currency, Boolean updateToActualAmount) {

        Iterator<CorresChargeDetail> it = this.corresChargeDetails.iterator();

        Boolean exists = Boolean.FALSE;
        while(it.hasNext()) {
            CorresChargeDetail corresChargeDetail = it.next();
            if (corresChargeDetail.matches(tradeServiceId, serviceType)) {
                if (updateToActualAmount) {
                    corresChargeDetail.updateActualAmount(amount, currency);
                } else {
                    corresChargeDetail.updateOriginalAmount(amount, currency);
                }
                exists = Boolean.TRUE;
                break;
            }
        }

        if (!exists) {
            this.corresChargeDetails.add(new CorresChargeDetail(tradeServiceId, serviceType, corresChargeType, amount, currency));
            this.lastPaidDate = new Date();
        }
    }

    public void remitCorresChargeDetail(ServiceType serviceType) {

        Iterator<CorresChargeDetail> it = this.corresChargeDetails.iterator();

        while (it.hasNext()) {
            CorresChargeDetail corresChargeDetail = it.next();
            if (corresChargeDetail.getServiceType().equals(serviceType)) {
                corresChargeDetail.updateStatus(CorresChargeStatus.REMITTED);
            }
        }
    }

    public void addNewCorresChargeDetails(Set<CorresChargeDetail> corresChargeDetails) {

        Iterator<CorresChargeDetail> it = corresChargeDetails.iterator();

        while(it.hasNext()) {
            CorresChargeDetail corresChargeDetail = (CorresChargeDetail)it.next();
            this.corresChargeDetails.add(corresChargeDetail);
        }
    }

    public void deleteAllCorresChargeDetails() {
        this.corresChargeDetails.removeAll(this.corresChargeDetails);
    }

    public Set<CorresChargeDetail> getCorresChargeDetails() {
        return this.corresChargeDetails;
    }
*/
}

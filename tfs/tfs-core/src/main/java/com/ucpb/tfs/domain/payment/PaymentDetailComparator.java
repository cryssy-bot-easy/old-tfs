package com.ucpb.tfs.domain.payment;

import java.util.Comparator;

/**
 * User: IPCVal
 * Date: 11/21/13
 */
public class PaymentDetailComparator implements Comparator<PaymentDetail> {

    @Override
    public int compare(PaymentDetail detail1, PaymentDetail detail2) {

        int returnVal = 0;
        if (detail1.getAmount().compareTo(detail2.getAmount()) > 0) {
            returnVal = 1;
        } else if (detail1.getAmount().compareTo(detail2.getAmount()) < 0) {
            returnVal = -1;
        }
        return returnVal;
    }
}

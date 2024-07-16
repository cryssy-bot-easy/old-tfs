package com.ucpb.tfs.domain.settlementaccount.utils;

import com.ucpb.tfs.domain.settlementaccount.enumTypes.TermCode;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 7/26/12
 */
public class SettlementAccountUtils {

    public static String checkNullOrBlankString(String value) throws Exception {
        if (value != null && !value.equals("")) {
            return value;
        } else {
            throw new IllegalArgumentException("Value is null or blank string.");
        }
    }

    public static Date checkNullOrBlankDate(String value) throws Exception {
        if (value != null && !value.equals("")) {
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            return df.parse(value);
        } else {
            throw new IllegalArgumentException("Value is null or blank string.");
        }
    }

    public static int checkNullOrBlankNumber(String value) throws Exception {
        if (value != null && !value.equals("")) {
            return Integer.parseInt(value);
        } else {
            throw new IllegalArgumentException("Value is null or blank string.");
        }
    }

    public static BigDecimal checkNullOrBlankDecimal(String value) throws Exception {
        if (value != null && !value.equals("")) {
            return new BigDecimal(value);
        } else {
            throw new IllegalArgumentException("Value is null or blank string.");
        }
    }

    public static TermCode checkNullOrBlankTermCode(String value) throws Exception {
        if (value != null && !value.equals("")) {
            return TermCode.valueOf(value);
        } else {
            throw new IllegalArgumentException("Value is null or blank string.");
        }
    }
}

package com.ucpb.tfs.domain.service.utils;

import com.ucpb.tfs.utils.DateUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

/**
 * User: IPCVal
 * Date: 9/12/12
 */
public class TradeServiceReferenceNumberGenerator {

    private JdbcTemplate jdbcTemplate;

    public TradeServiceReferenceNumberGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static String generateReferenceNumber() {

        // TODO: Rework Trade Service Reference Number Generator
        String random = "909-12-" + RandomStringUtils.random(5, false, true);
        return random;
    }

    public String generateReferenceNumber(String processingUnitCode) {
        int sequenceNumber = jdbcTemplate.queryForInt("VALUES (NEXT VALUE FOR TRADE_SERVICE_REF_NUM_SEQUENCE)");
        return String.format("%1$s-%2$s-%3$05d",processingUnitCode, DateUtil.getLastTwoDigitsOfYear(new Date()),sequenceNumber);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

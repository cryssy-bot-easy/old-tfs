package com.ucpb.tfs.domain.instruction.utils;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ucpb.tfs.utils.DateUtil;

/**
 * User: IPCVal
 * Date: 8/9/12
 */
public class EtsNumberGenerator {

	private static final int MAX_SEQUENCE_LENGTH = 5;
	private JdbcTemplate jdbcTemplate;
	
	public EtsNumberGenerator(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
    public static String generateServiceInstructionId() {
        // TODO: Rework ETS Number Generator
        String random = "932-12-" + RandomStringUtils.random(5, false, true);
        return random;
    }
    
    public String generateServiceInstructionId(String unitCode){
		int sequenceNumber = jdbcTemplate.queryForInt("VALUES (NEXT VALUE FOR ETS_NUM_SEQUENCE)");
    	return String.format("%1$s-%2$s-%3$05d",unitCode,DateUtil.getLastTwoDigitsOfYear(new Date()),sequenceNumber);
    }
}

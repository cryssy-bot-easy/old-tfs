package com.ucpb.tfs.batch.util;

import org.junit.Before;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class SpelEvaluatingStringArrayRowMapperIntegrationTest {

    private SpelEvaluatingStringArrayRowMapper mapper;

    private ResultSet rs;

    private static final Date FEB_7_2000 = new Date(949925429939L);
    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));


    @Test
    public void successfullyMapResults() throws SQLException {
        LinkedHashMap<String,String> rowMapping = new LinkedHashMap<String, String>();
        rowMapping.put("String",null);
        rowMapping.put("BigDecimal2Decimals","#this.setScale(2,T(java.math.BigDecimal).ROUND_HALF_UP)");
        rowMapping.put("Integer",null);
        rowMapping.put("DateMMddyyyy","T(com.ucpb.tfs.batch.util.DateUtil).convertToDateString(#this,'MM/dd/yyyy')");
        rowMapping.put("DateyyyyMM","T(com.ucpb.tfs.batch.util.DateUtil).convertToDateString(#this,'yyyyMM')");
        rowMapping.put("NULLVALUE",null);


        mapper = new SpelEvaluatingStringArrayRowMapper(rowMapping);

        rs = mock(ResultSet.class);
        when(rs.getObject("String")).thenReturn("Juan");
        when(rs.getObject("BigDecimal2Decimals")).thenReturn(new BigDecimal(121.444));
        when(rs.getObject("Integer")).thenReturn(Integer.valueOf(1));
        when(rs.getObject("DateMMddyyyy")).thenReturn(FEB_7_2000);
        when(rs.getObject("DateyyyyMM")).thenReturn(FEB_7_2000);
        when(rs.getObject("NULLVALUE")).thenReturn(null);


        String[] result = mapper.mapRow(rs,0);
        assertNotNull(result);
        assertEquals(6,result.length);
        assertEquals("Juan",result[0]);
        assertEquals("121.44",result[1]);
        assertEquals("1",result[2]);
        assertEquals("02/07/2000",result[3]);
        assertEquals("200002",result[4]);
        assertNull(result[5]);
    }

    @Test
    public void getNullOnNullRootObjectWithSpel() throws SQLException {
        LinkedHashMap<String,String> rowMapping = new LinkedHashMap<String, String>();
        rowMapping.put("NULL","#this?.toString()");
        mapper = new SpelEvaluatingStringArrayRowMapper(rowMapping);

        rs = mock(ResultSet.class);
        when(rs.getObject("NULL")).thenReturn(null);
        String[] result = mapper.mapRow(rs,0);
        assertNotNull(result);
        assertEquals(1,result.length);
        assertNull(result[0]);
    }

    @Test
    public void parseExpressionOnNullRootObject(){
        Expression e = parser.parseExpression("#this?.toString()");
        Object value = e.getValue(new StandardEvaluationContext(null));
        assertNull(value);
    }

}

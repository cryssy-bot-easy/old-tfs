package com.ucpb.tfs.batch.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Retrieves data from the result set then evaluates a SpEl Expression on it before
 *  it is mapped to a string.
 */
public class SpelEvaluatingStringArrayRowMapper implements RowMapper<String[]>{

    private static final String EMPTY = "";
    private LinkedHashMap<String,Expression> rowMapping = new LinkedHashMap<String,Expression>();

    private final ExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));

    public SpelEvaluatingStringArrayRowMapper(LinkedHashMap<String,String> rowMapping){
        for(Map.Entry<String,String> entry : rowMapping.entrySet()){
            Expression expression = null;
            if(!StringUtils.isEmpty(entry.getValue())){
                expression = parser.parseExpression(entry.getValue());
            }
            this.rowMapping.put(entry.getKey(),expression);
        }
    }

    @Override
    public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
        String[] rowData = new String[rowMapping.size()];
        int rowIndex = 0;
        for(Map.Entry<String,Expression> entry : rowMapping.entrySet()){
            String result = null;
            Object data = rs.getObject(entry.getKey());
            if(entry.getValue() != null){
                Object expressionValue = entry.getValue().getValue(new StandardEvaluationContext(data));
                result = expressionValue != null ? expressionValue.toString() : null;
            }else{
                result = data != null ? data.toString() : EMPTY;
            }
           
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("TXNDATE")) {
            	result = result.replaceAll("[,;()<>{}\\[\\]]", "");
            } else if (result != null) {
                // result = "abc ,;:()<>{}[] def";
                result = result.replaceAll("[,;:()<>{}\\[\\]]", "");
            }

            rowData[rowIndex] = result != null ? result.replaceAll("\\s"," ") : result;
            rowIndex++;
        }
        return rowData;
    }

}

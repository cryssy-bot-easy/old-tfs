package com.ucpb.tfs.batch.util;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 */
public class StringArrayRowMapper implements RowMapper<String[]> {

    private List<String> columnsToMap;


    public StringArrayRowMapper(List<String> columnsToMap){
        this.columnsToMap = columnsToMap;
    }

    @Override
    public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
        String[] row = new String[columnsToMap.size()];
        for(int ctr = 0; ctr < columnsToMap.size(); ctr++){
            row[ctr] = convertToString(rs.getObject(columnsToMap.get(ctr)));
        }

       return row;

    }

    private String convertToString(Object source){
        if(source == null){
            return "";
        }
        if(source instanceof Date){
            return formatDate((Date)source,"MM/dd/yyyy");
        }
        return source.toString().replaceAll("\\s"," ");
    }

    private String formatDate(Date date, String dateFormat){
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }
}

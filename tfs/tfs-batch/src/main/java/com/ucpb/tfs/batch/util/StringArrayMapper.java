package com.ucpb.tfs.batch.util;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class StringArrayMapper implements RowMapper<String[]> {

    private boolean mapZero = false;

    @Override
    public String[] mapRow(ResultSet resultSet, int i) throws SQLException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        String[] row = new String[columnCount];
        for(int column = 0; column < columnCount; column++){
            row[column] = convertToString(resultSet.getObject(column + 1));
        }
        return row;
    }

    private String convertToString(Object source){
        if(source == null){
            return null;
        }
        if(source instanceof Date){
            return formatDate((Date)source,"MM/dd/yyyy");
        }
        String result = source.toString().replaceAll("\n", "");
        return result;
    }

    private String formatDate(Date date, String dateFormat){
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }
}

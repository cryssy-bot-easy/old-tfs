package com.ucpb.tfs.interfaces.domain.mapper;

import org.apache.commons.httpclient.util.DateUtil;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 */
public class StringArrayMapper implements RowMapper<String[]> {
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
            return "";
        }
        if(source instanceof Date){
            return DateUtil.formatDate((Date)source,"MM/dd/yyyy");
        }
        return source.toString();
    }
}

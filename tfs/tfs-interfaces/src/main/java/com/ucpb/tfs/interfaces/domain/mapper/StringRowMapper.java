package com.ucpb.tfs.interfaces.domain.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StringRowMapper implements RowMapper<String> {

    private static final String defaultDelimeter = ",";

    private String delimiter;

	private List<String> columnMapping;
	
	public StringRowMapper(List<String> columnMapping){
		this.columnMapping = columnMapping;
	}

	@Override
	public String mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		
		StringBuilder builder = new StringBuilder();

        for(String columnName : columnMapping) {

            String value = resultSet.getString(columnName);

            if (value != null) {
                value = value.replaceAll("[,;:()<>{}\\[\\]]", "");
            }

			builder.append(value != null  ?  value : "");
			builder.append(getDelimiter());
		}

		if(builder.length() > 0){
			builder.deleteCharAt(builder.length()-1);
		}
		return builder.toString();
	}


    public String getDelimiter() {
        return delimiter == null ? defaultDelimeter : delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}

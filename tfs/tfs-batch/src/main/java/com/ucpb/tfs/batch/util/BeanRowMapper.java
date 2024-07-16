package com.ucpb.tfs.batch.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class BeanRowMapper<T> implements RowMapper<T> {

    private final Map<String,String> mappings;

    private final Class<T> beanClass;

    private Map<String,String> fixedValues;

    public BeanRowMapper(Map<String, String> mappings, Class<T> beanClass){
        this.mappings = mappings;
        this.beanClass = beanClass;
    }


    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T bean = BeanUtils.instantiate(beanClass);
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        wrapper.setAutoGrowNestedPaths(true);

        for(Map.Entry<String,String> entry : mappings.entrySet()){
            wrapper.setPropertyValue(entry.getKey(),rs.getObject(entry.getValue()));
        }

        if(fixedValues != null){
            for(Map.Entry<String,String> entry : fixedValues.entrySet()){
                wrapper.setPropertyValue(entry.getKey(),entry.getValue());
            }
        }
        return bean;
    }

    public void setFixedValues(Map<String, String> fixedValues) {
        this.fixedValues = fixedValues;
    }
}

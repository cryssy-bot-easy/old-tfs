package com.ucpb.tfs.utils;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Map;

/**
 *  Wrapper class for the Map<String,Object> interface.
 *  Provides convenience methods to convert its contents to different java types.
 *
 */
public class MapUtil {

    private Map<String,Object> params;

    public MapUtil(Map<String, Object> params){
        this.params = params;
    }

    public String getString(String key){
        return (String)params.get(key);
    }

    public Object get(String key){
        return params.get(key);
    }

    public Integer getAsInteger(String key){
        String value = getTrimmed(key);
        if(!StringUtils.isEmpty(value)){
            return Integer.valueOf(value);
        }
        return null;
    }

    public Long getAsLong(String key){
        String value = getTrimmed(key);
        if(!StringUtils.isEmpty(value)){
            return Long.valueOf(value);
        }
        return null;
    }

    public BigDecimal getAsBigDecimal(String key){
        String value = getTrimmed(key);
        if(!StringUtils.isEmpty(value)){
            return new BigDecimal(StringUtils.trim(value));
        }
        return null;
    }

    public BigDecimal formatTfsAmount(String key){
        String value = getTrimmed(key);
        if(!StringUtils.isEmpty(value)){
            return new BigDecimal(StringUtils.trim(value).replaceAll(",",""));
        }
        return null;
    }

    /**
     * Parses the string value to a date. The expected date format of the string is "MM/dd/yyyy".
     * Throws a runtime exception should parsing fail
     * @param key
     * @return parsed date
     */

    public Date getAsDate(String key) {
        String value = getTrimmed(key);
        try {
            if (!StringUtils.isEmpty(value)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                return dateFormat.parse(value);
            }
        } catch (ParseException e) {
            throw new RuntimeException("'" + key + "' cannot be parsed to date from format 'MM/dd/yyyy'", e);
        }
        return null;
    }

    public Boolean getAsBoolean(String key){
        String value = getTrimmed(key);
        if(!StringUtils.isEmpty(value)){
            return Boolean.valueOf(value);
        }
        return null;
    }

    public Currency getAsCurrency(String key){
        String value = getTrimmed(key);
        if(!StringUtils.isEmpty(key)){
            return Currency.getInstance(value);
        }
        return null;
    }

    private String getTrimmed(String key){
        String result = getString(key);
        if(!StringUtils.isEmpty(result)){
            return StringUtils.trim(result);
        }
        return result;
    }


}

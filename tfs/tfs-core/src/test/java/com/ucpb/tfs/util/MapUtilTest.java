package com.ucpb.tfs.util;

import com.ucpb.tfs.utils.MapUtil;
import org.apache.commons.httpclient.util.DateUtil;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 */
public class MapUtilTest {

    private MapUtil mapUtil;

    private Map<String,Object> originalMap;

    @Before
    public void setup(){
        originalMap = new HashMap<String,Object>();
        mapUtil = new MapUtil(originalMap);
    }

    @Test
    public void parseValidInteger(){
        originalMap.put("integer","3");
        assertEquals(Integer.valueOf(3),mapUtil.getAsInteger("integer"));
    }

    @Test
    public void parseValidIntegerWithWhitespaces(){
        originalMap.put("integer"," 3 ");
        assertEquals(Integer.valueOf(3), mapUtil.getAsInteger("integer"));
    }

    @Test
    public void returnNullIntegerOnNonExistentProperty(){
        assertNull(mapUtil.getAsInteger("nonexistent"));
    }

    @Test
    public void getValidBigDecimal(){
        originalMap.put("bigD","3.4123");
        assertEquals(new BigDecimal("3.4123"),mapUtil.getAsBigDecimal("bigD"));
    }

    @Test
    public void getValidBigDecimalWithWhitespace(){
        originalMap.put("bigD","   3.4123 ");
        assertEquals(new BigDecimal("3.4123"),mapUtil.getAsBigDecimal("bigD"));
    }

    @Test
    public void returnNullOnInvalidKey(){
        assertNull(mapUtil.getAsBigDecimal("nonExistent"));
    }

    @Test
    public void getValidDate(){
        originalMap.put("date","12/01/2012");
        Date parsedDate = mapUtil.getAsDate("date");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);

        assertEquals(11, calendar.get(Calendar.MONTH));
        assertEquals(1,calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(2012,calendar.get(Calendar.YEAR));
    }

    @Test
    public void getValidCurrency(){
        originalMap.put("curr","PHP");
        assertEquals(Currency.getInstance("PHP"),mapUtil.getAsCurrency("curr"));
    }
}

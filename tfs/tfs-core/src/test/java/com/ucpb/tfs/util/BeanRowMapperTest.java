package com.ucpb.tfs.util;

import com.ucpb.tfs.utils.BeanRowMapper;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class BeanRowMapperTest {

    private BeanRowMapper<Employee> employeeMapper;

    private ResultSet rs;

    private static final long OCT_15 = 1350289917805L;

    @Before
    public void setup() throws SQLException {

        Map<String,String> beanMapping = new HashMap<String,String>();
        beanMapping.put("firstName","NAME");
        beanMapping.put("dateHired","DATE_HIRED");

        Map<String,String> fixedMappings = new HashMap<String,String>();
        fixedMappings.put("department","999");
        fixedMappings.put("sickDays","9");
        fixedMappings.put("gender","MALE");

        employeeMapper = new BeanRowMapper<Employee>(beanMapping,Employee.class);
        employeeMapper.setFixedValues(fixedMappings);

        rs = mock(ResultSet.class);
        when(rs.getObject("NAME")).thenReturn("Juan");
        when(rs.getObject("DATE_HIRED")).thenReturn(new Date(OCT_15));
    }

    @Test
    public void successfullyMapResultSetToBean() throws SQLException {
        Employee employee = employeeMapper.mapRow(rs,0);
        assertNotNull(employee.getDateHired());
        assertEquals(OCT_15,employee.getDateHired().getTime());
        assertEquals("Juan",employee.getFirstName());
    }

    @Test
    public void successfullyMapFixedValuesToBean() throws SQLException {
        Employee employee = employeeMapper.mapRow(rs,0);
        assertNotNull(employee.getDateHired());
        assertEquals(OCT_15,employee.getDateHired().getTime());
        assertEquals("Juan",employee.getFirstName());
        assertEquals("999",employee.getDepartment());
        assertEquals(9,employee.getSickDays());
        assertEquals(Gender.MALE,employee.getGender());
    }

}

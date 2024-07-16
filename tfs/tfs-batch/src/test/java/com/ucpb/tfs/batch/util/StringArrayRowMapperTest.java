package com.ucpb.tfs.batch.util;

import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 */
public class StringArrayRowMapperTest {

    private StringArrayRowMapper stringArrayMapper;
    private ResultSet rs;


    @Before
    public void setup(){
        List<String> columns = new ArrayList<String>();
        columns.add("NAME");
        columns.add("LASTNAME");
        stringArrayMapper = new StringArrayRowMapper(columns);

        rs = mock(ResultSet.class);

    }




    @Test
    public void emptyStringsOnNullObjects() throws SQLException {
        when(rs.getObject("NAME")).thenReturn(null);
        when(rs.getObject("LASTNAME")).thenReturn(null);
        String[] row = stringArrayMapper.mapRow(rs,1);
        assertEquals("",row[0]);
        assertEquals("",row[1]);
    }

    @Test
    public void convertDateToDateString() throws SQLException {
        // 1352704334236 = nov 12, 2012
        when(rs.getObject("NAME")).thenReturn(new Date(1352704334236L));
        when(rs.getObject("LASTNAME")).thenReturn("LAST");
        String[] row = stringArrayMapper.mapRow(rs,1);
        assertEquals("11/12/2012",row[0]);
    }

    @Test
    public void convertInteger() throws SQLException {
        when(rs.getObject("NAME")).thenReturn(Integer.valueOf(1));
        String[] row = stringArrayMapper.mapRow(rs,112121);
        assertEquals("1",row[0]);
    }

    @Test
    public void replaceAllNewLines(){
        String address = "ADDRESS 101 - " + "\n" + "ADDRESS";
        System.out.println("*** BEFORE: " + address);
        String formatted = address.replaceAll("\n"," ");
        System.out.println("** AFTER: " + formatted);
        assertEquals("ADDRESS 101 -  ADDRESS",formatted);

    }

}

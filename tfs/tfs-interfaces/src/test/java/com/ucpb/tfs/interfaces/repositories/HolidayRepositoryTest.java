package com.ucpb.tfs.interfaces.repositories;

import com.ucpb.tfs.interfaces.domain.CalendarDay;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class HolidayRepositoryTest {

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        jdbcTemplate.update("INSERT INTO UCPARUCMN2.JHCLDR (CJDATE,HOLDAY,BUSDAY,CBRNBR,CDATE) values (131313,'Y','N',909,123413)");
    }

    @Test
    public void successfullyGetHolidayDetailsForBranch(){
        CalendarDay day = holidayRepository.getCalendarDay(123413,"909");
        assertNotNull(day);
        assertTrue(day.isNotABusinessDay());
        assertTrue(day.isHoliday());
    }


}

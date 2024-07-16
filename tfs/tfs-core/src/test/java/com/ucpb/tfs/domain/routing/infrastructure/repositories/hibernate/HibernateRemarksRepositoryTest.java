package com.ucpb.tfs.domain.routing.infrastructure.repositories.hibernate;

import com.ipc.rbac.domain.User;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.routing.Remark;
import org.hibernate.StaleStateException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 */
@TransactionConfiguration
@Transactional
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
public class HibernateRemarksRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private Date SEPT_5_2012 = new Date(1346841795122L);

    @Autowired
    private HibernateRemarksRepository repository;

    @Autowired
    @Qualifier("jdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    @Before
    public void setup(){
        jdbcTemplate.update("INSERT INTO RBAC_USER (USERACTIVEDIRECTORYID,FIRSTNAME, LASTNAME) values ('USER1','FIRSTNAME1','LASTNAME1')");
    }


    @Test
    public void successfullySaveToRepository(){
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM REMARKS"));

        Remark remark = new Remark();
        remark.setMessage("This is a recording.");
        remark.setRemarkId("R-1234321");
        remark.setDateCreated(SEPT_5_2012);

        User user = new User();
        user.setUserActiveDirectoryId(new UserActiveDirectoryId("USER1"));
        remark.setUser(user);

        repository.addRemark(remark);

        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM REMARKS"));

        Map<String,Object> retrievedRemark = jdbcTemplate.queryForMap("SELECT * FROM REMARKS WHERE USER_ID = 'USER1'");

        assertEquals("USER1",retrievedRemark.get("USER_ID"));
        assertEquals("This is a recording.", retrievedRemark.get("MESSAGE"));
        assertNotNull(retrievedRemark.get("DATE_CREATED"));
        assertEquals("USER1",retrievedRemark.get("USER_ID"));
        assertEquals("R-1234321",retrievedRemark.get("REMARK_ID"));
    }

    @Test
    public void successfullyEditRemark(){
        String insertPrefix = "INSERT INTO REMARKS (ID,REMARK_ID,DATE_CREATED,USER_ID,MESSAGE) VALUES ";
        jdbcTemplate.update(insertPrefix + "(1,'R-1234',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE')");
        jdbcTemplate.update(insertPrefix + "(2,'R-2222',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE2')");
        jdbcTemplate.update(insertPrefix + "(3,'R-3333',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE3')");

        Remark remark = new Remark();
        remark.setId(Long.valueOf(1));
        remark.setRemarkId("R-2222");
        remark.setMessage("NEW MESSAGE HERE");
        remark.setDateCreated(SEPT_5_2012);
        repository.editRemark(remark);

        Map<String,Object> returnedRemark = jdbcTemplate.queryForMap("SELECT * FROM REMARKS WHERE ID = 1");
        assertEquals("R-2222",returnedRemark.get("REMARK_ID"));
        assertEquals("NEW MESSAGE HERE",returnedRemark.get("MESSAGE"));
        assertNotNull(returnedRemark.get("DATE_CREATED"));
    }

    @Test
    public void successfullyQueryAllRemarksOfTheSameRemarkId(){
        String insertPrefix = "INSERT INTO REMARKS (ID,REMARK_ID,DATE_CREATED,USER_ID,MESSAGE) VALUES ";
        jdbcTemplate.update(insertPrefix + "(1,'R-1234',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE')");
        jdbcTemplate.update(insertPrefix + "(2,'R-2222',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE2')");
        jdbcTemplate.update(insertPrefix + "(3,'R-1234',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE3')");

        List<Remark> remarks = repository.getRemarks("R-1234");
        assertEquals(2,remarks.size());

        Remark firstRemark = remarks.get(0);
        assertEquals(Long.valueOf(1),firstRemark.getId());
        assertEquals("R-1234",firstRemark.getRemarkId());
        assertEquals("OLD MESSAGE",firstRemark.getMessage());
        assertNotNull(firstRemark.getDateCreated());
    }

    @Test
    public void retrieveEmptyListOnInvalidRemarkId(){
        String insertPrefix = "INSERT INTO REMARKS (ID,REMARK_ID,DATE_CREATED,USER_ID,MESSAGE) VALUES ";
        jdbcTemplate.update(insertPrefix + "(1,'R-1234',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE')");
        jdbcTemplate.update(insertPrefix + "(2,'R-2222',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE2')");
        jdbcTemplate.update(insertPrefix + "(3,'R-1234',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE3')");

        List<Remark> remarks = repository.getRemarks("NON-EXISTENT");
        assertTrue(remarks.isEmpty());
    }

    @Test(expected = StaleStateException.class)
    public void failToEditRemarkWithInvalidId(){
        String insertPrefix = "INSERT INTO REMARKS (ID,REMARK_ID,DATE_CREATED,USER_ID,MESSAGE) VALUES ";
        jdbcTemplate.update(insertPrefix + "(1,'R-1234',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE')");
        jdbcTemplate.update(insertPrefix + "(2,'R-2222',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE2')");
        jdbcTemplate.update(insertPrefix + "(3,'R-3333',CURRENT_TIMESTAMP,'USER1','OLD MESSAGE3')");

        Remark remark = new Remark();
        remark.setId(Long.valueOf(99999999));
        remark.setRemarkId("R-2222");
        remark.setMessage("NEW MESSAGE HERE");
        remark.setDateCreated(SEPT_5_2012);
        repository.editRemark(remark);

    }


}

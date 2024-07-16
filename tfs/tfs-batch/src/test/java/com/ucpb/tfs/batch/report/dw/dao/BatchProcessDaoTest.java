package com.ucpb.tfs.batch.report.dw.dao;

import com.ucpb.tfs.batch.dao.BatchProcessDao;
import com.ucpb.tfs.batch.util.FileUtil;
import com.ucpb.tfs.batch.util.SqlRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:repository-test-context.xml")
@Ignore("//TODO: fix test. test fails when run in a suite")
public class BatchProcessDaoTest {

    @Autowired
    @Qualifier("batchProcessDao")
    private BatchProcessDao batchProccessDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SqlRunner sqlRunner;


    @Before
    public void setup(){
        jdbcTemplate.execute("DELETE FROM TFS.SERVICEINSTRUCTION");
        jdbcTemplate.execute("DELETE FROM TFS.LETTEROFCREDIT");
        jdbcTemplate.execute("DELETE FROM TFS.TRADEPRODUCT");

    }

    @Test
    @Ignore("FIX LATER. fails when run in a test suite. passes when run alone")
    public void purgeAllUnactedEts() throws IOException, URISyntaxException {
        sqlRunner.run(FileUtil.getFileFromResource("/ddl/tfs/insert-service-instructions.sql"));
        sqlRunner.run(FileUtil.getFileFromResource("/ddl/testdata/insert-mock-routes.sql"));

        assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TFS.SERVICEINSTRUCTION WHERE (STATUS = 'PENDING' OR STATUS = 'CHECKED')"));

        batchProccessDao.purgeUnactedEts(new Timestamp(new Date().getTime()));
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TFS.SERVICEINSTRUCTION WHERE STATUS = 'CHECKED'"));

    }

    @Test
    public void setAllExpiredLettersOfCreditStatusToExpired() throws IOException, URISyntaxException {
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TFS.LETTEROFCREDIT"));
        assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TFS.TRADEPRODUCT"));

        sqlRunner.run(FileUtil.getFileFromResource("/ddl/testdata/insert-mock-letters-of-credit.sql"));
        assertEquals(3,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TFS.LETTEROFCREDIT LC, TFS.TRADEPRODUCT TP" +
                " WHERE LC.DOCUMENTNUMBER = TP.DOCUMENTNUMBER"));


        batchProccessDao.cancelExpiredLettersOfCredit(new Timestamp(new Date().getTime()));
        assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TFS.TRADEPRODUCT WHERE STATUS = 'EXPIRED'"));
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TFS.TRADEPRODUCT WHERE STATUS = 'OPEN'"));

    }
}

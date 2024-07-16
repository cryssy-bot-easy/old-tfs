package com.ucpb.tfs.interfaces.repositories;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.ucpb.tfs.interfaces.domain.AllocationUnit;
import com.ucpb.tfs.interfaces.domain.CustomerInformationFile;
import com.ucpb.tfs.interfaces.services.CustomerInformationFileService;
import org.apache.commons.lang.StringUtils;
import org.apache.derby.iapi.util.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*com/ucpb/tfs/interfaces/repositories/repository-test-context.xml")
public class CustomerInformationFileRepositoryTest {

	@Autowired
	@Qualifier("customerInformationFileRepository")
	private CustomerInformationFileRepository cifRepository;

    @Autowired
    private CustomerInformationFileService cifService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Before
	public void setup(){
		jdbcTemplate.execute("DELETE FROM UCDATUBWC2.CFMAST");
        jdbcTemplate.execute("DELETE FROM UCDATURBK2.DDMAST");
        jdbcTemplate.execute("DELETE FROM UCDATUBWC2.CFADDR");
        jdbcTemplate.execute("DELETE FROM UCPARUCMN2.JHOFFR");

    }


	@Test
	public void successfullyGetCifUsingCifNumber(){
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Batman','Wayne',301,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('1234567',1,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
	
		assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATUBWC2.CFMAST"));
		Map<String,Object> result = cifRepository.getCifByCifNumber("1234567");
		assertNotNull(result);
		assertEquals("1234567",result.get("CIF_NUMBER"));
		assertEquals("Batman", StringUtils.trim(result.get("CIF_NAME").toString()));
		assertEquals("Wayne",StringUtils.trim(result.get("LASTNAME").toString()));
		assertEquals("123",result.get("OFFICER_CODE"));
		assertEquals("A",result.get("DORSI_CODE"));

	}

    @Test
    public void successfullyQueryCifDetails(){
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6,CFBIRD)" +
                " VALUES ('Batman','Wayne',301,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887,2012060)");
        CustomerInformationFile cif = cifRepository.getCifDetailsByCifNumber("1234567");

        assertNotNull(cif);
        assertEquals(2012060,cif.getIncorporationDate());
        Calendar incorporationDate = Calendar.getInstance();
        incorporationDate.setTime(cif.getFormattedIncorporationDate());
        assertEquals(2012,incorporationDate.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY,incorporationDate.get(Calendar.MONTH));
    }


    @Test
    public void returnNullForNonExistentOfficerCode(){
        AllocationUnit unit = cifRepository.getBranchUnitCodeForAlphanumericOfficerCode("NON_EXISTENT");
        assertNull(unit);
    }
	
	@Test
	public void failToRetrieveAnyRecordUsingInvalidCifNumber(){
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Batman','Wayne',301,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887)");
	
		assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATUBWC2.CFMAST"));
		Map<String,Object> result = cifRepository.getCifByCifNumber("INVALID_CIF_NUMBER");
		assertNull(result);
	}
	
	@Test
	public void successfullyGetMainCifUsingChildCifNumber(){
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Batman','Wayne',301,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887)");
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Green Lantern','Jordan',302,'222','A','3234567','Hal','Berry','THISISMYTIN#','Y',020887)");
		
		//insert dummy facilities
		jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (AFFCDE,AFSEQ,AFAPNO,\"AFCIF#\",AFEXP6) VALUES ('FCN',1,'3234567','1234567',121213)");
		
	
		assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATUBWC2.CFMAST"));
		List<Map<String,Object>> result = cifRepository.getMainCifsByClientCifNumber("1234567","072512");
		assertNotNull(result);
		assertEquals(1,result.size());
		Map<String,Object> mainCif = result.get(0);
		assertEquals("3234567",mainCif.get("CIF_NUMBER"));
//		assertEquals("Green Lantern",mainCif.get("CIF_NAME"));
//		assertEquals("Jordan",mainCif.get("LASTNAME"));
//		assertEquals("Hal",mainCif.get("FIRSTNAME"));

	}
	
	@Test
	public void successfullyGetCifsOfTheSameName(){
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Batman','Wayne',301,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887)");
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Batman','Grayson',302,'124','A','7654321','Dick','Tracy','THISISMYTIN#','Y',010887)");
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Robin','Drake',303,'123','A','4654321','Tim','Oliver','THISISHISTIN#','Y',010887)");

        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('1234567',1,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR  (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('7654321',2,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR  (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('4654321',2,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
		
		assertEquals(3,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATUBWC2.CFMAST"));
		List<Map<String,Object>> result = cifRepository.getCifsByCifName("Batman              ");
		assertEquals(2,result.size());
		assertEquals("1234567",result.get(0).get("CIF_NUMBER"));
//		assertEquals("Batman",result.get(0).get("CIF_NAME").toString());
//		assertEquals("Wayne",result.get(0).get("LASTNAME").toString());
//		assertEquals(BigDecimal.valueOf(301),result.get(0).get("BRANCH_UNIT_CODE"));
//		assertEquals("123",result.get(0).get("OFFICER_CODE"));
//		assertEquals("A",result.get(0).get("DORSI_CODE"));
//
//		assertEquals("7654321",result.get(1).get("CIF_NUMBER"));
//		assertEquals("Batman",result.get(1).get("CIF_NAME").toString());
//		assertEquals("Grayson",result.get(1).get("LASTNAME").toString());
//		assertEquals(BigDecimal.valueOf(302),result.get(1).get("BRANCH_UNIT_CODE"));
//		assertEquals("124",result.get(1).get("OFFICER_CODE"));
//		assertEquals("A",result.get(1).get("DORSI_CODE"));

	}

    @Test
    public void successfulQueryForBothCifNameAndNumber(){
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Batman','Wayne',909,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Batman','Grayson',909,'124','A','7654321','Dick','Tracy','THISISMYTIN#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Robin','Drake',909,'123','A','2342341','Tim','Oliver','THISISHISTIN#','Y',010887)");

        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('1234567',1,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('7654321',2,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('2342341',3,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");

        
        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHOFFR (JHOOFF) VALUES ('123')");

        List<Map<String,Object>> result = cifRepository.getCifsByNameAndNumber("Batman              ","1234567");
        assertNotNull(result);
        assertEquals(1,result.size());
        Map<String,Object> row = result.get(0);
//        assertEquals("Batman",row.get("CIF_NAME"));
        assertEquals("1234567",row.get("CIF_NUMBER"));

    }

    @Test
    public void queryByCifNameOrNumberUsingValidName(){
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Batman','Wayne',909,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Batman','Grayson',909,'124','A','7654321','Dick','Tracy','THISISMYTIN#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Robin','Drake',909,'123','A','4654321','Tim','Oliver','THISISHISTIN#','Y',010887)");

        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('1234567',1,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR  (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('7654321',2,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR  (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('4654321',2,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");

        assertEquals(3,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATUBWC2.CFMAST"));
        
        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHOFFR (JHOOFF) VALUES ('123')");
        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHOFFR (JHOOFF) VALUES ('124')");
        

        List<Map<String,Object>> result = cifRepository.getCifsByNameOrNumber("Batman              ","");
        assertEquals(2,result.size());
        assertEquals("1234567",result.get(0).get("CIF_NUMBER"));
//        assertEquals("Batman",result.get(0).get("CIF_NAME"));
//        assertEquals("Wayne",result.get(0).get("LASTNAME"));
//        assertEquals(BigDecimal.valueOf(301),result.get(0).get("BRANCH_UNIT_CODE"));
//        assertEquals("123",result.get(0).get("OFFICER_CODE"));
//        assertEquals("A",result.get(0).get("DORSI_CODE"));
//
//        assertEquals("7654321",result.get(1).get("CIF_NUMBER"));
//        assertEquals("Batman",result.get(1).get("CIF_NAME"));
//        assertEquals("Grayson",result.get(1).get("LASTNAME"));
//        assertEquals(BigDecimal.valueOf(302),result.get(1).get("BRANCH_UNIT_CODE"));
//        assertEquals("124",result.get(1).get("OFFICER_CODE"));
//        assertEquals("A",result.get(1).get("DORSI_CODE"));

    }

    @Test
    public void queryByCifNameOrNumberUsingValidNumber(){
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Batman','Wayne',909,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Batman','Grayson',909,'124','A','7654321','Dick','Tracy','THISISMYTIN#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Robin','Drake',909,'123','A','4654321','Tim','Oliver','THISISHISTIN#','Y',010887)");

        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('1234567',1,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR  (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('7654321',2,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR  (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('4654321',3,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");

        
        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHOFFR (JHOOFF) VALUES ('123')");
        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHOFFR (JHOOFF) VALUES ('124')");


        assertEquals(3,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATUBWC2.CFMAST"));
        List<Map<String,Object>> result = cifRepository.getCifsByNameOrNumber("","1234567");
        assertEquals(1,result.size());
        assertEquals("1234567",result.get(0).get("CIF_NUMBER"));
//        assertEquals("Batman",result.get(0).get("CIF_NAME"));
//        assertEquals("Wayne",result.get(0).get("LASTNAME"));
//        assertEquals(BigDecimal.valueOf(301),result.get(0).get("BRANCH_UNIT_CODE"));
//        assertEquals("123",result.get(0).get("OFFICER_CODE"));
//        assertEquals("A",result.get(0).get("DORSI_CODE"));
    }

    @Test
    public void getAllCifsContainingTheSameNumberSequence(){
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Batman','Wayne',909,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Batman','Grayson',909,'124','A','7645321','Dick','Tracy','THISISMYTIN#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Robin','Drake',909,'123','A','4654321','Tim','Oliver','THISISHISTIN#','Y',010887)");

        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Robin','Drake',909,'123','A','4549321','Tim','Oliver','THISISHISTIN#','Y',010887)");

        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('1234567',1,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR  (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('7645321',2,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR  (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('4654321',3,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFADDR  (\"CFCIF#\",CFASEQ,CFNA2,CFNA3,CFNA4) VALUES ('4549321',3,'8017 TANGUELI ST.                       ','SAN ANOTONIO                            ','MAKATI CITY                             ')");

        
        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHOFFR (JHOOFF) VALUES ('123')");
        jdbcTemplate.execute("INSERT INTO UCPARUCMN2.JHOFFR (JHOOFF) VALUES ('124')");

        assertEquals(4,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATUBWC2.CFMAST"));
        List<Map<String,Object>> result = cifRepository.getCifsByNameOrNumber("","%45%");
        assertEquals(3,result.size());
        assertEquals("1234567",result.get(0).get("CIF_NUMBER"));
    }

    @Test
    public void successfullyQueryCasaAccountsByCifAndCurrency(){
        jdbcTemplate.execute("INSERT INTO UCDATURBK2.DDMAST (RECID,BRANCH,ACCTNO,ACTYPE,SNAME,CIFNO,ALTADD,ALTNAM,ALIASN,OFFICR,STATUS,DDNUM,CLOSTS,CLASS,DDCTYP,DDCDEC,DDGRUP,SPCODE,HDCODE,ATCODE,SICODE,PCHECK,DATOP7,DATST7,DATEN7,DTOD7,DPDA7,DLA7,DLC7,DLINP7,DPINP7,DLSTM7,DLFLM7,DTLDP7,DTSC7,DCARM7,DATOP6,DATST6,DATEN6,DTOD6,DPDA6,DLA6,DLC6,DLINP6,DPINP6,DLSTM6,DLFLM6,DTLDP6,DTSC6,DCARM6,AMTDP,HOLD,TTOC,TTOCP,TTODOC,TTMC,TTIBT,TTLLC,TTXTR,TTSA,TTFLDL,CBAL,YBAL,STMBAL,LYCBAL,MINBAL,MAXBAL,COLECT,ODINT,AGGDAY,AGGAMT,AGGCOL,AGGDAN,AGGCON,AGGAMN,AGCODN,MINBAI,MAXBAI,MINMTD,MAXMTD,AGGDAI,AGGAMI,AGGCOI,ACCRUE,ALTACR,YTDINT,LYTDIN,LINTPD,STCYC,STCODE,INTCYC,SCCYC,SCCODE,SCTYPE,DDUSER,DPZUS2,DPZUS3,DPZUS4,NODR,AMTDR,NOCR,AMTCR,SCDR,SCDRA,SCCR,SCCRA,NODRC,AMTDRC,NOCRC,AMTCRC,SCDRC,SCDRAC,SCCRC,SCCRAC,ATCRNO,ATCRAO,ATDRNO,ATDRAO,ATCRNF,ATCRAF,ATDRNF,ATDRAF,\"#DEPDY\",AMTNSF,ODLMT,SGCODE,ODPROT,CHKGTY,ATMCRD,COMBIN,MALHLD,AUTNSF,HIVOL,BADCKI,TIMOD,TIMOD1,TIMOD2,TIMOD3,TIMOD4,ODLSYR,ODLTD,RELACT,RACTTP,ONUSCK,FORNCK,LSTPST,ACTIRN,RATE,PVAR,PVAR1,PFLOOR,PCEIL,REVDT7,REVDT6,TERM,TERMCD,ORATE,OPVAR,OPVAR1,OPFLOR,OPCEIL,ORVDT7,ORVDT6,OTERM,OTRMCD,NPLFLG,NPLSTA,EXCDT6,EXCDT7,DR3,DR6,DR3EXC,DR6EXC,CASHDP,CASHPO,QAGDAY,QAGBAL,QAGCOL,ALEDQ1,ALEDQ2,ALEDQ3,ALEDQ4,ACOLQ1,ACOLQ2,ACOLQ3,ACOLQ4,DDSIC1,DDSIC2,DDSIC3,DDSIC4,SPMESG,XMASPL,TMNSF,TMNFQ1,TMNFQ2,TMNFQ3,TMNFQ4,TMNFLY,TNFLTD,TTFLOT,TTFLAV,TTFLCA,NOENCL,EFTFLG,MINCHG,\"FLUC#M\",\"FLUC#Y\",\"KITE#\",WHCODE,WHSCOD,STATE,WDATE6,WDATE7,WALTAC,WALTTP,WALTRT,WALTSR,WHTDY,WHMTD,WHQTD,WHYTD,WHSTDY,WHSMTD,WHSQTD,WHSYTD,WHLYR,WHSLYR,WHHIRT,NFCODE,AFCODE,STMPAS,TLRINQ,PRTCKO,MTDSC,MTDINT,MTDOIN,MTDFEE,MTDAGB,MTDAGD,MTDBGA,MTDRDC,MTDRDA,MTDRCC,MTDRCA,CLACOD,DDSNUM,BIPEMI,BILOK1,ATCLVL,ATCTYP,ATCRDN,DDSIC5,DDSIC6,DDSIC7,DDSIC8,DDVUSR,DDVDT6,DDVDT7,DDVTME,DDUIC1,DDUIC2,DDUIC3,DDUIC4,DDUIC5,DDUIC6,DDUIC7,DDUIC8,BOOKCD,YRCODE,MAINBT,MAINBL,TTEXM,F1CYC,STPACC,DAYNOI,MTDNOI,LTDNOI,INTEXP,TAXEXP,HLDBRN,ALTADR,PROCDE,MTDFEN,YTDFEE,YTDFEN,MTDMFE,MTDMFN,YTDMFE,YTDMFN,YTDAGB,YTDAGD,YTDOIN,MRTCOD,FDENDB,USERID,SUPRID,STAFID,MKTOFR,MKTBOK,REFCIF,MGMACT,LSTMBL,DTLWD7,DTLWD6,DTLRC7,DTLRC6,ODLIMT,ODDLIM,YODDLM,ODSLIM,ODCLAC,YCLAC,ODEXSS,ODSACC,ODSEXC,ODSUNA,ODSCLA,ODUNAC,YUNAC,ODWOD6,ODWOD7,ODRDT7,ODRDT6,ODPROV,DSPROV,ODPAMT,ODAANO,ODCPRV,ODPPRV,ODMAT7,ODMAT6,\"ODINT$\",\"ACCRU$\",\"ODACC$\",PSMBAL,RSCFLG,RSCDT6,RSCDT7,RSCTME,RSTRFG,RSTDT6,RSTDT7,COFAMT,COFDT7,COFDT6,ODRPRV,ODCYCL,ODCYCI,ODYFEE,ODSTL7,ODSTL6,DDDLW7,DDDLW6,DDSTPF,DPRATD,LPRDT6,LPRDT7,LPTIME,PENTYP,SUPGPR,PSKACT,INSRCC,MBRSHP,REFDSC,DDSPTF,AGGDBI,ACRTTD,ACRCYC,ODICYC,ODSICC,BONINT,BONRAT,DAFEED,DDCN01,DDCN02,DDCN03,DDCN04,DDCN05,DDCPRD,DDCEF6,DDCEF7,EMPYID,MRKSUP,PREMAG,MTDAGG,FIRD6,FIRD7,REFCD,BANKNO,RBRN,CPPURC,FSNAME,ACNAME,RDDNUM,OBAL,YOBAL,COFBAL,YOINT,EXACTD,ODATOD,ACRUE2,ACRUE3,INACTD,YTDODI,INPDTD,EXCPTD,ODCTOD,DPZUS5,DPZUS6,DPZUS7,DPZUS8,PARTID,RATN,CRFLAG,ORATN,ODFLAG,SWPFLG,DANFEE,DAFREQ,DAFREC,DAEFD6,DAEFD7,CARCFG,PCLACD,PCLAD7,PCLAD6,CLADT7,CLADT6,DDMWID,DDMUID,DDMTIM,DDMJNO,DDSUPV,ONSWEP,NOWTH,DORSTS,DORDT6,DORDT7,ODTYP,DPRDGR,DDMOPR,FDINT,CHKAUT,LEAFP,ODOVR6,ODOVR7,WTHCYC,INAMTD,SPIMTD,DDCAPF,EXCREM,DORREM,EX1DT6,EX1DT7,EX2DT6,EX2DT7,EX3DT6,EX3DT7,DDODOC,DPLST7,DPLST6,EXCDI6,EXCDI7,EXRLI6,EXRLI7,YTDEXT,LYTDEX,YREXDY,LREXDY,DRCALL,DRCDT6,DRCDT7,ODBPD7,ODBPD6,OTRCHG,YODLMT,MTDCHQ,YTDCHQ,BNSPAD,BNSDT6,BNSDT7,WTASOB,DMCOST,DMPRDC,OWTAMT,OWINTA,DDRFID,DDRFNM,DDMICD,MINBLT,CHGSTS,DDRAMT1,DDRAMT2,DDRAMT3,DDRAMT4,DDRAMT5,DDRACR1,DDRACR2,DDRACR3,DDRACC1,DDRACC2,DDRACC3,DDRACT1,DDRACT2,DDRACT3,DDRRTN1,DDRRTN2,DDRRTN3,DDRRAT1,DDRRAT2,DDRRAT3,DDRNFL1,DDRNFL2,DDRNFL3,DDRFLG1,DDRFLG2,DDRFLG3,DDRFLG4,DDRFLG5,DDRFLG6,DDRFLG7,DDRFLG8,DDRFLG9,DDRFLGA,DDRCFL1,DDRCFL2,DDRCFL3,DDRTYP1,DDRTYP2,DDRTYP3,DDRTYP4,DDRTYP5,DDRD61,DDRD62,DDRD63,DDRD64,DDRD65,DDRD71,DDRD72,DDRD73,DDRD74,DDRD75,DDRTI1,DDRTI2,DDRTI3,DDRTI4,DDRTI5) VALUES ('A',1,10092304,'D','LEPANTO CONSOLI     ','L000395','N','N','N','L38',1,0,0,'F','PHP','2',550,'Y','N','N','N','N',1991176,1991176,1991176,0,0,2009301,0,2012274,2012182,2011304,0,2011242,0,0,62591,62591,62591,0,0,102809,0,93012,63012,103111,0,83011,0,0,20.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00000,3,0.00,0.00,0,0.00,0,0.00,0.00,0.00,0.00,0.00,3,0.00,0.00,0.00000,0.00000,0.00,0.00,0.00,3,'  ',4,3,'7A','W','  ','  ','  ','  ',0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,0,' ','N',' ','N','N','A','W',' ',0,0,0,0,0,0,0,0,0,' ',0,0,'N',0,0.000000,0.000000,' ',0.000000,0.000000,0,0,0,' ',0.000000,0.000000,' ',0.000000,0.000000,0,0,0,' ','N','N',0,0,0,0,0,0,0,0,338,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,' ',' ','Y',' ',' ',0,0,0,0,0,0,0,0,0.00,0.00,0.00,0,' ',0.00,0,0,31,1,0,'   ',0,0,0,' ',0.000000,0.000000,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.000000,'N','N','S',' ','N',0.00,0.00,0.00,0.00,0.00,3,0.00,0,0.00,0,0.00,'  ',0,12,0,' ',' ','                              ','N',' ',' ',' ','          ',0,0,0,' ',' ',' ',' ',' ',' ',' ',' ','RG',2,'-',20000.00,0.00,' ',' ',0,0,0,0.00,0.00,0,0,'          ',0,0.00,0,0.00,0,0.00,0,0.00,0,0.00,'          ',0.00,'          ','          ',' ','          ','     ','0000000',0,0.00,2009337,120309,0,0,0.00,0.00,0.00,0.00,0.00000,0.00000,0.00000,0.00000,0.00000,0.00000,0.00000,0.00000,0.00000,0,0,0,0,0.00,0.00,0.00,'                    ',0.00,0.00,0,0,0.00000,0.00000,0.00000,0.00,' ',0,0,0,' ',0,0,0.00,0,0,0.00,0,0,0.00,0,0,0,0,' ',0E-9,120312,2012338,230130,'  ',' ',' ','          ','                    ','                                        ',' ',0,0.00000,0,0,0,0.00,0E-9,0,0,0,0,0,0,'  ',0,0,'          ','          ',0.00,0.00,0,0,'          ',0,0,'          ','                    ','                                        ',0,0.00,0.00,0.00,0.00000,0.00,0.00,0.00000,0.00000,0.00,0.00,0.00,0.00,0.00,'  ','  ','  ','  ',' ',0,' ',0,' ',' ',0.00,0,' ',0,0,' ','  ',0,0,0,0,'          ','          ',0,0,'          ',' ',0,' ',0,0,'  ',' ',' ',0,' ',0E-7,0,0,0,0.00000,0.00000,' ','  ','  ',0,0,0,0,0,0,' ',0,0,0,0,0,0,0,0,0,0,' ',0,0,0,0,0.00,0.00,0.00,0.00,0.00,0,0,' ',0,0,0.00,0.00000,0,'                                        ',0,' ','A',0.00,0.00,0.00,0.00,0.00,0.00000,0.00000,0.00000,0,0,0,' ',' ',' ',0,0,0,0E-9,0E-9,0E-9,0,0,0,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','   ','   ','   ','          ','          ','          ','          ','          ',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");

        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFACCT (CFACID,\"CFCIF#\",CFATYP,\"CFACC#\",CFSNME,CFRELA,CFRELP,CFAALA,CFAALN,CFAREF,\"CFGTE$\",CFGTEP,CFSHRP,CFNOTC,CFANOS,CFADLM,CFADL6,CFASEQ,CFVUSR,CFVDT6,CFVDT7,CFVTME) VALUES ('A','L000395','D',10092304,'LEPANTO CONSOLI     ','P ','  ',' ','N','                    ',0.00,0.000000,0.000000,'N',0,2009331,112709,14,'UCCIF     ',112709,2009331,235959)");

        List<Map<String,Object>> casaAccounts = cifRepository.getCasaAccountsByCifNumberAndCurrency("L000395","PHP");
        assertFalse(casaAccounts.isEmpty());
        assertEquals("L000395",casaAccounts.get(0).get("CIF_NUMBER"));
        assertEquals(new BigDecimal(10092304),casaAccounts.get(0).get("ACCOUNT_NUMBER"));
        assertEquals("PHP",StringUtils.trim((String)casaAccounts.get(0).get("CURRENCY")));
    }
	
	@Test
	public void failToRetrieveAnyRecordUsingInvalidCifName(){
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Batman','Wayne',301,'123','A','1234567','Bruce','Willis','THISISTHETIN#','Y',010887)");
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Batman','Grayson',302,'124','A','7654321','Dick','Tracy','THISISMYTIN#','Y',010887)");
		jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
				" VALUES ('Robin','Drake',303,'123','A','7654321','Tim','Oliver','THISISHISTIN#','Y',010887)");
		
		assertEquals(3,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATUBWC2.CFMAST"));
		List<Map<String,Object>> result = cifRepository.getCifsByCifName("BANE");
		assertNotNull(result);
		assertEquals(0,result.size());
	}

    @Test
    public void successfullyGetAllCasaAccounts(){
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFACCT (CFACID,\"CFCIF#\",CFATYP,\"CFACC#\",CFSNME,CFRELA,CFRELP,CFAALA,CFAALN,CFAREF,\"CFGTE$\",CFGTEP,CFSHRP,CFNOTC,CFANOS,CFADLM,CFADL6,CFASEQ,CFVUSR) VALUES ('A','A000001','S',1331207864,'PASCUAL MARIE       ','P ','  ',' ','N','                    ',0.00,0.000000,0.000000,'N',0,2009282,100909,1,'UCDEP     ')");

        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFACCT (CFACID,\"CFCIF#\",CFATYP,\"CFACC#\",CFSNME,CFRELA,CFRELP,CFAALA,CFAALN,CFAREF,\"CFGTE$\",CFGTEP,CFSHRP,CFNOTC,CFANOS,CFADLM,CFADL6,CFASEQ,CFVUSR) VALUES ('A','A000002','S',1481669660,'CANTOR GEORGE R     ','P ','  ',' ','N','                    ',0.00,0.000000,0.000000,'N',0,2009282,100909,3,'UCDEP     ')");


        List<Map<String,Object>> accountNumbers = cifRepository.getCasaAccounts("A000001");
        assertNotNull(accountNumbers);
        assertFalse(accountNumbers.isEmpty());
        assertEquals(new BigDecimal("1331207864"),accountNumbers.get(0).get("ACCOUNT_NUMBER"));
        assertEquals("S",accountNumbers.get(0).get("ACCOUNT_TYPE"));
    }


    @Test
    public void getAllChildCifsAttachedToAMainCif(){
        //child CIFS
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Robin','Wayne',200,'123','A','1234567','Damian','Something','DamiansTin#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Robin','Drake',201,'124','A','2222222','Timothy','Something','TimsTin#','Y',010887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Two Face','Dent',400,'124','A','3333333','Harvey','Something','HarveysTin#','Y',010887)");

        //mother CIFs
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Batman','Wayne',302,'222','A','3234567','Bruce','Something','BatmansTin#','Y',020887)");
        jdbcTemplate.execute("INSERT INTO UCDATUBWC2.CFMAST (CFSNME,CFNA1,CFBRNN,CFOFFR,CFUIC2,\"CFCIF#\",CFNA1A,CFNA1B,CFTINN,CFRESD,CFBIR6)" +
                " VALUES ('Layfield','Bradshaw',401,'124','A','4533333','John','Something','HarveysTin#','Y',010887)");

        //insert dummy facilities
        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (AFFCDE,AFSEQ,AFAPNO,\"AFCIF#\",AFEXP6) VALUES ('FCN',1,'3234567','1234567',121213)");
        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (AFFCDE,AFSEQ,AFAPNO,\"AFCIF#\",AFEXP6) VALUES ('FCN',2,'3234567','2222222',121213)");
        jdbcTemplate.execute("INSERT INTO UCDATULNS2.LNAPPF (AFFCDE,AFSEQ,AFAPNO,\"AFCIF#\",AFEXP6) VALUES ('FCN',3,'4533333','3333333',121213)");


        assertEquals(5,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM UCDATUBWC2.CFMAST"));
        List<Map<String,Object>> result = cifRepository.getChildCifsByMainCifNumber("3234567","052612");
        assertNotNull(result);
        assertEquals(2,result.size());
        Map<String,Object> childCif = result.get(0);
        assertEquals("1234567",childCif.get("CIF_NUMBER"));
//        assertEquals("Robin",childCif.get("CIF_NAME"));
//        assertEquals("Wayne",childCif.get("LASTNAME"));
//        assertEquals("Damian",childCif.get("FIRSTNAME"));
    }
	
	
}

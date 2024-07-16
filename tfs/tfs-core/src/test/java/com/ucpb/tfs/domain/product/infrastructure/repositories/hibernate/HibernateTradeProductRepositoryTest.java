package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.LetterOfCredit;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
@TransactionConfiguration
@Transactional
public class HibernateTradeProductRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private HibernateTradeProductRepository hibernateTradeProductRepository;
	
	@Autowired 
	private JdbcTemplate jdbcTemplate;

    @Autowired
    private SessionFactory sessionFactory;
	
	@Before
	public void setup(){
		jdbcTemplate.execute("DELETE FROM DOC_NUM_SEQUENCE");
	}


    @Test
    public void successfullyPersistLcNegotiationsWithoutInvokingPersist(){
        jdbcTemplate.execute("INSERT INTO TRADEPRODUCT (DOCUMENTNUMBER,PRODUCTTYPE,CIFNUMBER,CIFNAME,MAINCIFNUMBER,MAINCIFNAME,ACCOUNTOFFICER,CCBDBRANCHUNITCODE,FACILITYID,FACILITYTYPE,FACILITYREFERENCENUMBER,ALLOCATIONUNITCODE,STATUS,LONGNAME,ADDRESS1,ADDRESS2,PASSONRATETHIRDTOUSD,PASSONRATETHIRDTOPHP,PASSONRATEUSDTOPHP,SPECIALRATETHIRDTOUSD,SPECIALRATETHIRDTOPHP,SPECIALRATEUSDTOPHP,URR,AMOUNT,CURRENCY) VALUES ('909-01-1-13-00005-3','LC','L000395','LEPANTO CONSOLID','L000395','LEPANTO CONSOLID','REGINA TERESA RODRIGUEZ','929','4','FCN','909/FCN/071100019   ',null,'OPEN','  LEPANTO CONSOLIDATED MINING COMPANY','21ST FLOOR LEPANTO BLDG','8747 PASEO DE ROXAS',null,null,null,null,null,null,44.92000000,552424.00,'PHP')");

        jdbcTemplate.execute("INSERT INTO LETTEROFCREDIT (DOCUMENTNUMBER,DOCUMENTTYPE,PURPOSE,PROCESSDATE,EXPIRYDATE,REASONFORCANCELLATION,CANCELLATIONDATE,TENOR,TYPE,PAYMENTMODE,USANCEPERIOD,USANCEPERIODSTART,EXPIRYCOUNTRYCODE,PARTIALSHIPMENT,PARTIALDELIVERY,TRANSSHIPMENT,IRREVOCABLE,NEGOTIATIONRESTRICTION,LASTNEGOTIATIONDATE,ADVISETHROUGHBANK,PRICETERM,REVOLVINGAMOUNT,REVOLVINGPERIOD,DAYSREVOLVING,CUMULATIVE,AGGREGATEAMOUNT,CASHFLAG,TOTALNEGOTIATEDAMOUNT,CASHAMOUNT,TOTALNEGOTIATEDCASHAMOUNT,OUTSTANDINGBALANCE,REFUNDAMOUNT,PORTOFORIGINATION,PORTOFDESTINATION,PORTOFORIGINCOUNTRYCODE,IMPORTERADDRESS,BENEFICIARYADDRESS,BENEFICIARYNAME,ADVISINGBANKCODE,CONFIRMINGBANKCODE,REIMBURSINGCURRENCY,DRAWEE,ADVISEMEDIUM,LATESTSHIPMENTDATE,DISPATCHPLACE,FINALDESTINATIONPLACE,APPLICABLERULES,FORMOFDOCUMENTARYCREDIT,DESTINATIONBANK,ISSUEDATE,PRICETERMNARRATIVE,CONFIRMATIONINSTRUCTIONSFLAG,MARINEINSURANCE,GENERALDESCRIPTIONOFGOODS,CWTFLAG,ADVANCECORRESCHARGESFLAG,OTHERPRICETERM,ADVISETHROUGHBANKIDENTIFIERCODE,TENOROFDRAFTNARRATIVE,MAXIMUMCREDITAMOUNT,SHIPMENTPERIOD,AVAILABLEWITHFLAG,ADVISETHROUGHBANKLOCATION,PERIODFORPRESENTATION,PERIODFORPRESENTATIONADVISETHROUGHBANK,MIXEDPAYMENTDETAILS,IMPORTERNAME,PLACEOFFINALDESTINATION,EXPORTERNAME,PLACEOFTAKINGDISPATCHORRECEIPT,EXPORTERADDRESS,NEGATIVETOLERANCELIMIT,REIMBURSINGBANKFLAG,ADVISETHROUGHBANKNAMEANDADDRESS,IDENTIFIERCODE,AVAILABLEBY,REIMBURSINGBANKNAMEANDADDRESS,SENDERTORECEIVERINFORMATION,REIMBURSINGBANKIDENTIFIERCODE,NAMEANDADDRESS,REIMBURSINGACCOUNTTYPE,IMPORTERCBCODE,BSPCOUNTRYCODE,IMPORTERCIFNUMBER,DEFERREDPAYMENTDETAILS,REIMBURSINGBANKACCOUNTNUMBER,POSITIVETOLERANCELIMIT,LATESTDATESHIPMENT,AVAILABLEWITH,ADDITIONALAMOUNTSCOVERED,PORTOFDISCHARGEORDESTINATION,ADVISETHROUGHBANKFLAG,SENDERTORECEIVERINFORMATIONNARRATIVE,EXPORTERCBCODE,PORTOFLOADINGORDEPARTURE,STANDBYTAGGING,FURTHERIDENTIFICATION,PURPOSEOFSTANDBY,FORMATTYPE,DETAILSOFGUARANTEE,APPLICANTNAME,APPLICANTADDRESS,PLACEOFRECEIPT,PLACEOFDELIVERY,OTHERDOCUMENTSINSTRUCTIONS,SHIPMENTCOUNT,DATECLOSED,CURRENTAMOUNT,NUMBEROFAMENDMENTS,LASTAMENDMENTDATE,LASTREINSTATEMENTDATE,LASTTRANSACTION,LASTMODIFIEDDATE,NARRATIVE,RECEIVERSREFERENCE,SENDERSREFERENCE,EXPIRYPLACE) VALUES ('909-01-1-13-00005-3','DOMESTIC',null,{ts '2013-03-14 12:05:15'},{ts '2013-03-28 00:00:00'},null,null,'USANCE','REGULAR',null,14,null,null,null,'ALLOWED',null,null,null,null,null,null,null,null,null,null,null,null,0.00,0.00,0.00,552424.00,0.00,null,null,null,null,'beneficiary address','beneficiary name',null,null,null,null,null,{ts '2013-03-28 00:00:00'},null,null,null,null,null,{ts '2013-03-12 00:00:00'},null,'N',null,'description of goods and services','false','false',null,null,'tenor of draft',null,null,null,null,null,null,null,null,'',null,'',null,null,null,null,null,'A',null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,'DYNALAB CORPORAT','905 ATLANTA CENTRE\n" +
                "ANNAPOLIS ST GREENHILLS\n" +
                "SAN JUAN',null,null,null,0,null,552424.00,null,null,null,'DMLC Regular Opening',{ts '2013-03-14 12:05:15'},null,null,null,'Makati City')");



        LetterOfCredit lc = (LetterOfCredit) hibernateTradeProductRepository.load(new DocumentNumber("909-01-1-13-00005-3"));
        assertNotNull(lc);

        Map<String,Object> negotiationDetails = new HashMap<String,Object>();
        negotiationDetails.put("negotiationAmount","30000");

        lc.negotiate("negotiationNumber",negotiationDetails);

        sessionFactory.getCurrentSession().flush();
        assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM LCNEGOTIATION where negotiationAmount = 30000"));

    }

	
	@Test
	public void successfullyGetDocumentSequenceNumber(){
		assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM DOC_NUM_SEQUENCE"));
//		jdbcTemplate.update("INSERT INTO TFDOCNO (UNIT_CD, DOC_TYP, DOC_YR,LST_SEQ_NO,DOC_GRP) VALUES (?,?,?,?,?)",
//				"BRANCH","01",Integer.valueOf(1987),Integer.valueOf(1)," ");
        jdbcTemplate.update("INSERT INTO \"DOC_NUM_SEQUENCE\" (ID,DOCUMENT_TYPE,SEQUENCE,UNIT_CODE,SEQUENCE_YEAR) VALUES (1,'01',1,'BRANCH',1987)");
		
		String sequenceNumber = hibernateTradeProductRepository.getDocumentNumberSequence("01", "BRANCH", 1987);
		assertNotNull(sequenceNumber);
		assertEquals("1",sequenceNumber);
	}
	
	@Test
	public void failToRetrieveADocumentSequenceNumber(){
		assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM DOC_NUM_SEQUENCE"));
		String sequenceNumber = hibernateTradeProductRepository.getDocumentNumberSequence("01", "BRANCH", 1987);
		assertNull(sequenceNumber);
	}
	
	@Test
	public void successfullyUpdateSequenceNumber(){
		assertEquals(0,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM DOC_NUM_SEQUENCE"));
        jdbcTemplate.update("INSERT INTO \"DOC_NUM_SEQUENCE\" (ID,DOCUMENT_TYPE,SEQUENCE,UNIT_CODE,SEQUENCE_YEAR) VALUES (1,'01',1,'BRANCH',1987)");
		
		hibernateTradeProductRepository.incrementDocumentNumberSequence("01", "BRANCH", 1987);
		
		int sequence = jdbcTemplate.queryForInt("SELECT SEQUENCE FROM DOC_NUM_SEQUENCE WHERE UNIT_CODE = 'BRANCH' AND DOCUMENT_TYPE = '01'AND SEQUENCE_YEAR = 1987");
		assertEquals(2,sequence);
		
		hibernateTradeProductRepository.incrementDocumentNumberSequence("01", "BRANCH", 1987);
		assertEquals(3,jdbcTemplate.queryForInt("SELECT SEQUENCE FROM DOC_NUM_SEQUENCE WHERE UNIT_CODE = 'BRANCH' AND DOCUMENT_TYPE = '01' AND SEQUENCE_YEAR = 1987"));
	}
	
}

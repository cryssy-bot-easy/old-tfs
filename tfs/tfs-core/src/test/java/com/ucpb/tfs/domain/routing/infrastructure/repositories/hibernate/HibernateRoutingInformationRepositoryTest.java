package com.ucpb.tfs.domain.routing.infrastructure.repositories.hibernate;

import com.ipc.rbac.domain.User;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.infrastructure.repositories.hibernate.HibernateServiceInstructionRepository;
import com.ucpb.tfs.domain.routing.Route;
import com.ucpb.tfs.domain.routing.RoutingInformation;
import com.ucpb.tfs.domain.routing.RoutingInformationId;
import com.ucpb.tfs.domain.routing.infrastructure.repositories.hibernate.HibernateRoutingInformationRepository;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@TransactionConfiguration
@Transactional
@ContextConfiguration("classpath:*transactionlog-app-context.xml")
public class HibernateRoutingInformationRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private HibernateRoutingInformationRepository routingInformationRepository;

    @Autowired
    private HibernateServiceInstructionRepository serviceInstructionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        jdbcTemplate.update("INSERT INTO RBAC_USER (USERACTIVEDIRECTORYID,FIRSTNAME, LASTNAME) values ('USER1','FIRSTNAME1','LASTNAME1')");
        jdbcTemplate.update("INSERT INTO RBAC_USER (USERACTIVEDIRECTORYID,FIRSTNAME, LASTNAME) values ('USER2','FIRSTNAME2','LASTNAME2')");
        jdbcTemplate.update("INSERT INTO RBAC_USER (USERACTIVEDIRECTORYID,FIRSTNAME, LASTNAME) values ('USER3','FIRSTNAME3','LASTNAME3')");

        jdbcTemplate.update("INSERT INTO SERVICEINSTRUCTION (serviceInstructionId,createdDate,dateApproved,details,modifiedDate,status,type,userActiveDirectoryId) VALUES ('s-1',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null,CURRENT_TIMESTAMP,'PENDING','ETS','USER1')");
        jdbcTemplate.update("INSERT INTO SERVICEINSTRUCTION (serviceInstructionId,createdDate,dateApproved,details," +
                "modifiedDate,status,type,userActiveDirectoryId) VALUES ('s-2',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null,CURRENT_TIMESTAMP,'PENDING','ETS','USER1')");
        jdbcTemplate.update("INSERT INTO SERVICEINSTRUCTION (serviceInstructionId,createdDate,dateApproved,details," +
                "modifiedDate,status,type,userActiveDirectoryId) VALUES ('s-3',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null,CURRENT_TIMESTAMP,'PENDING','ETS','USER3')");
        jdbcTemplate.update("INSERT INTO SERVICEINSTRUCTION (serviceInstructionId,createdDate,dateApproved,details," +
                "modifiedDate,status,type,userActiveDirectoryId) VALUES ('s-7',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,null,CURRENT_TIMESTAMP,'APPROVED','ETS','USER3')");

        jdbcTemplate.update("INSERT INTO RoutingInformation (routingInformationId) VALUES ('s-7')");

        jdbcTemplate.update("INSERT INTO Routes (id,routingInformationId,receiverActiveDirectoryId,senderActiveDirectoryId,dateSent,status) VALUES" +
                "(9999,'s-7','USER2','USER3',CURRENT_TIMESTAMP,'APPROVED')");

    }

    @Before
    public void setupTradeServices(){
       jdbcTemplate.update("INSERT INTO TradeService (tradeServiceId,amount,userActiveDirectoryId) VALUES ('TRADE1',700,'USER1')");
    }

    @After
    public void tearDown(){
//        jdbcTemplate.update("DELETE FROM ROUTES");
//        jdbcTemplate.update("DELETE FROM ROUTINGINFORMATION");
//        jdbcTemplate.update("DELETE FROM RBAC_USER");
//        jdbcTemplate.update("DELETE FROM SERVICEINSTRUCTION");
//        jdbcTemplate.update("DELETE FROM TRADESERVICE");
    }

    @Test
    public void successfullySaveServiceInstructionRoutingToDatabase(){
        User sender = new User(new UserActiveDirectoryId("USER1") ,"FIRSTNAME1","LASTNAME1");
        User receiver = new User(new UserActiveDirectoryId("USER2") ,"FIRSTNAME2","LASTNAME2");

        Route route = new Route(sender, receiver, "PENDING");

        routingInformationRepository.addRoutingInformation(route,new RoutingInformationId("s-1"));

        assertEquals(3,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM RBAC_USER"));
        assertEquals(4,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM SERVICEINSTRUCTION"));
        assertEquals(2,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM ROUTINGINFORMATION"));

        Map<String,Object> routingInformation = jdbcTemplate.queryForMap("SELECT * FROM RoutingInformation info, Routes routes Where info.ROUTINGINFORMATIONID = 's-1'" +
                " and info.routingInformationId = routes.routingInformationId");

        assertNotNull(routingInformation);
        assertEquals("s-1", routingInformation.get("ROUTINGINFORMATIONID"));
        assertNotNull(routingInformation.get("DATESENT"));
        assertEquals("PENDING", routingInformation.get("STATUS"));
        assertEquals("USER1",routingInformation.get("SENDERACTIVEDIRECTORYID"));
        assertEquals("USER2",routingInformation.get("RECEIVERACTIVEDIRECTORYID"));

    }

    @Test
    public void successfullySaveTradeServiceRoutingToDatabase(){
//      assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM TRADESERVICE"));
      User sender = new User(new UserActiveDirectoryId("USER1") ,"FIRSTNAME1","LASTNAME1");
      User receiver = new User(new UserActiveDirectoryId("USER2") ,"FIRSTNAME2","LASTNAME2");

      Route route = new Route(sender,receiver,"PENDING");
      routingInformationRepository.addRoutingInformation(route, new RoutingInformationId("TRADE1"));

      assertEquals(1,jdbcTemplate.queryForInt("SELECT COUNT(*) FROM ROUTINGINFORMATION info, Routes route WHERE info.ROUTINGINFORMATIONID = 'TRADE1' AND senderActiveDirectoryId" +
              "='USER1' AND receiverActiveDirectoryId = 'USER2' and info.routingInformationId = route.routingInformationId"));
    }

    @Test
    public void successfullyQueryCompleteRoutingInformationDetails(){
        RoutingInformation routingInformation = routingInformationRepository.getRoutingInformation(new RoutingInformationId("s-7"));
        Route route = routingInformation.getRoutes().get(0);
        assertEquals("APPROVED",route.getStatus().toString());
        assertNotNull(route);
        assertEquals("USER2",route.getReceiver().getUserActiveDirectoryId().getUserActiveDirectoryId());
//        assertEquals("FIRSTNAME2",route.getReceiver().getFirstName());
//        assertEquals("LASTNAME2",route.getReceiver().getLastName());
//        assertEquals("USER3",route.getSender().getUserActiveDirectoryId().getUserActiveDirectoryId());
//        assertEquals("FIRSTNAME3",route.getSender().getFirstName());
//        assertEquals("LASTNAME3",route.getSender().getLastName());
        assertEquals("s-7",routingInformation.getRoutingInformationId().getRoutingInformationId());
    }

}

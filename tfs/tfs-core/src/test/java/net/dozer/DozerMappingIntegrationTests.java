package net.dozer;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.AddInstructionsCommand;
import com.ucpb.tfs.application.command.UpdateInstructionsCommand;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionTaggedEvent;
import com.ucpb.tfs.domain.routing.Remark;
import com.ucpb.tfs.domain.routing.Route;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.event.TradeServiceTaggedEvent;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for dozer mapping configurations
 */
@ContextConfiguration("classpath:*dozer-config-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class DozerMappingIntegrationTests {

    @Autowired
    @Qualifier("mapper")
    private Mapper mapper;


    @Before
    public void verifyMapperInjection(){
        assertNotNull(mapper);
    }


    @Test
    public void serviceInstructionTaggedEventToRouteMapping(){
        UserActiveDirectoryId user = new UserActiveDirectoryId("sender");

        ServiceInstruction serviceInstruction = new ServiceInstruction("932");
        String sId = serviceInstruction.getServiceInstructionId().getServiceInstructionId();
        serviceInstruction.updateStatus(ServiceInstructionStatus.APPROVED,user);

        ServiceInstructionTaggedEvent event = new ServiceInstructionTaggedEvent(serviceInstruction,user);

        Route route = mapper.map(event,Route.class);

        assertNotNull(route);
        assertNotNull(route.getSender());
        System.out.println(route);
        assertEquals("sender",route.getSender().getUserActiveDirectoryId().getUserActiveDirectoryId());
        assertEquals(ServiceInstructionStatus.APPROVED,event.getServiceInstruction().getStatus());
        assertEquals(ServiceInstructionStatus.APPROVED.toString(),route.getStatus());
    }

    @Ignore("TEST BEAN MAPPER")
    @Test
    public void successfulTradeServiceTaggedEventToRouteMapping(){
        UserActiveDirectoryId user = new UserActiveDirectoryId("sender");

//        ServiceInstruction serviceInstruction = new ServiceInstruction();
        TradeService tradeService = new TradeService();
        TradeServiceId sId = tradeService.getTradeServiceId();
        tradeService.updateStatus(TradeServiceStatus.APPROVED,user);
        TradeServiceTaggedEvent event = new TradeServiceTaggedEvent(sId,new HashMap<String,Object>(),user,"");

        Route route = mapper.map(event,Route.class);

        assertNotNull(route);
        assertNotNull(route.getSender());
        System.out.println(route);
        assertEquals("sender",route.getSender().getUserActiveDirectoryId().getUserActiveDirectoryId());
        assertEquals(TradeServiceStatus.APPROVED,event.getTradeServiceStatus());
        assertEquals(ServiceInstructionStatus.APPROVED.toString(),route.getStatus());
    }

    @Test
    public void addInstructionCommandToRemarkMapsSuccessfully(){
        AddInstructionsCommand command = new AddInstructionsCommand();
        command.setUserActiveDirectoryId("userId1234");
        command.putParameter("dateCreated",new Date(1346841795122L));
        command.putParameter("message","This is not a drill.");

        Remark remark = mapper.map(command,Remark.class);
        assertEquals("userId1234",remark.getUser().getUserActiveDirectoryId().getUserActiveDirectoryId());
//        assertEquals(new Date(1346841795122L),remark.getDateCreated());
        assertNotNull(remark.getDateCreated());
        assertEquals("This is not a drill.", remark.getMessage());
    }

    @Test
    public void updateInstructionsCommandToRemarkMapsSuccessfully(){
        UpdateInstructionsCommand command = new UpdateInstructionsCommand();
        command.setUserActiveDirectoryId("userId1234");
        command.putParameter("dateCreated",new Date(1346841795122L));
        command.putParameter("message","Hello World!");
        command.putParameter("id","12");

        Remark remark = mapper.map(command,Remark.class);
        assertEquals(Long.valueOf("12"),remark.getId());
        assertEquals("userId1234",remark.getUser().getUserActiveDirectoryId().getUserActiveDirectoryId());
//        assertEquals(new Date(1346841795122L),remark.getDateCreated());
        assertNotNull(remark.getDateCreated());
        assertEquals("Hello World!",remark.getMessage());
    }

}

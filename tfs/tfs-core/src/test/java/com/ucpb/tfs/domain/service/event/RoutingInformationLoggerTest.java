package com.ucpb.tfs.domain.service.event;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionTaggedEvent;
import com.ucpb.tfs.domain.instruction.event.ServiceInstructionUpdatedEvent;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import com.ucpb.tfs.domain.routing.Route;
import com.ucpb.tfs.domain.routing.RoutingInformationId;
import com.ucpb.tfs.domain.routing.RoutingInformationRepository;
import com.ucpb.tfs.domain.service.TradeProductNumber;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.enumTypes.*;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class RoutingInformationLoggerTest {

    @Mock
    private RoutingInformationRepository routingInformationRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private RoutingInformationLogger routingInformationLogger = new RoutingInformationLogger();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void successfullyPersistToDatabaseOnServiceInstructionUpdate(){
        ServiceInstruction serviceInstruction = new ServiceInstruction("932");
        ServiceInstructionId id = serviceInstruction.getServiceInstructionId();

        ServiceInstructionTaggedEvent event = new ServiceInstructionTaggedEvent(serviceInstruction,ServiceInstructionStatus.PENDING,new UserActiveDirectoryId("userId1"));
        routingInformationLogger.addRoutingInformation(event);
        verify(mapper).map(any(ServiceInstructionUpdatedEvent.class),eq(Route.class));
        verify(routingInformationRepository).addRoutingInformation(any(Route.class),any(RoutingInformationId.class));
    }

    @Test
    public void successfullyPersistToDatabaseOnTradeServiceTag(){
        ServiceInstructionId sId = new ServiceInstructionId("sId1234");
        DocumentNumber documentNumber = new DocumentNumber("d1234");
        TradeProductNumber tradeProductNumber = new TradeProductNumber("d1234");
        TradeService tradeService = new TradeService(sId,documentNumber,tradeProductNumber,DocumentClass.LC,DocumentType.FOREIGN,DocumentSubType1.CASH,DocumentSubType2.OTHER,ServiceType.ADJUSTMENT,new UserActiveDirectoryId("userId1"));

        TradeServiceTaggedEvent event = new TradeServiceTaggedEvent(tradeService.getTradeServiceId(),new HashMap<String,Object>(),new UserActiveDirectoryId("id2"), "");
        routingInformationLogger.addRoutingInformation(event);
        verify(mapper).map(any(TradeServiceUpdatedEvent.class),eq(Route.class));
        verify(routingInformationRepository).addRoutingInformation(any(Route.class),any(RoutingInformationId.class));
    }
}

package com.ipc.rbac.application.commandhandler;

import com.ucpb.tfs.application.command.UpdateInstructionsCommand;
import com.ucpb.tfs.application.commandHandler.UpdateInstructionsCommandHandler;
import com.ucpb.tfs.domain.routing.Remark;
import com.ucpb.tfs.domain.routing.RemarksRepository;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class UpdateInstructionsCommandHandlerTest {

    @Mock
    private RemarksRepository remarksRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private UpdateInstructionsCommandHandler commandHandler;


    @Before
    public void setup(){
        when(mapper.map(any(UpdateInstructionsCommand.class),eq(Remark.class))).thenReturn(new Remark());
    }

    @Test
    public void successfullyDelegateUpdateToRepository(){
        commandHandler.handle(new UpdateInstructionsCommand());
        verify(remarksRepository).editRemark(any(Remark.class));
        verify(mapper).map(any(UpdateInstructionsCommand.class),eq(Remark.class));
    }


}

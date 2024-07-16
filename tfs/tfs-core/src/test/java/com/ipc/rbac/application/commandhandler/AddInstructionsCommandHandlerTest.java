package com.ipc.rbac.application.commandhandler;

import com.ucpb.tfs.application.command.AddInstructionsCommand;
import com.ucpb.tfs.application.commandHandler.AddInstructionsCommandHandler;
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
public class AddInstructionsCommandHandlerTest {

    @Mock
    private Mapper mapper;

    @Mock
    private RemarksRepository remarksRepository;

    @InjectMocks
    private AddInstructionsCommandHandler addInstructionsCommandHandler;


    @Before
    public void setup(){
        when(mapper.map(any(AddInstructionsCommand.class),eq(Remark.class))).thenReturn(new Remark());
    }

    @Test
    public void successfullyDelegateAddRemarkCommandToRepository(){
        addInstructionsCommandHandler.handle(new AddInstructionsCommand());
        verify(mapper).map(any(AddInstructionsCommand.class),eq(Remark.class));
        verify(remarksRepository).addRemark(any(Remark.class));
    }

}

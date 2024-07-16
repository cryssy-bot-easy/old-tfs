package com.ucpb.tfs.application.commandHandler;

/**
 *
 * @author Marvin Volante <marvin.volante@incuventure.net>
 *
 */

import com.incuventure.cqrs.annotation.Command;
import com.ucpb.tfs.domain.routing.Remark;
import com.ucpb.tfs.domain.routing.RemarksRepository;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ucpb.tfs.application.command.AddInstructionsCommand;


@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class AddInstructionsCommandHandler implements CommandHandler<AddInstructionsCommand> {

    @Autowired
    private RemarksRepository remarksRepository;

    @Autowired
    private Mapper mapper;

	@Override
	public void handle(AddInstructionsCommand command) {
        remarksRepository.addRemark(mapper.map(command,Remark.class));
	}

}

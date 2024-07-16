package com.ucpb.tfs.application.commandHandler;

/**
 *
 * @author Marvin Volante <marvin.volante@incuventure.net>
 *
 */

import java.util.Iterator;
import java.util.Map;

import com.ipc.rbac.domain.User;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.routing.Remark;
import com.ucpb.tfs.domain.routing.RemarksRepository;
import com.ucpb.tfs.utils.MapUtil;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ucpb.tfs.application.command.UpdateInstructionsCommand;

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class UpdateInstructionsCommandHandler implements CommandHandler<UpdateInstructionsCommand>{

    @Autowired
    private Mapper mapper;

    @Autowired
    private RemarksRepository remarksRepository;

	@Override
	public void handle(UpdateInstructionsCommand command) {
//		remarksRepository.editRemark(mapToRemark(command));
        remarksRepository.editRemark(mapper.map(command,Remark.class));

    }

//    private Remark mapToRemark(UpdateInstructionsCommand command){
//        MapUtil params = new MapUtil(command.getParameterMap());
//        Remark remark = new Remark();
//        remark.setId(params.getAsLong("id"));
//        remark.setMessage(params.getString("message"));
//        remark.setRemarkId(params.getString("remarkId"));
//        User user = new User();
//        user.setUserActiveDirectoryId(new UserActiveDirectoryId(command.getUserActiveDirectoryId()));
//        remark.setUser(user);
//        return remark;
//    }


}

package com.ucpb.tfs.application.commandHandler;

import com.incuventure.cqrs.annotation.CommandHandler;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.application.command.RouteIncomingMtCommand;
import com.ucpb.tfs.domain.mtmessage.MtMessage;
import com.ucpb.tfs.domain.mtmessage.MtMessageRepository;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Marv
 * Date: 10/11/12
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class RouteIncomingMtCommandHandler implements CommandHandler<RouteIncomingMtCommand> {
    
    @Inject
    MtMessageRepository mtMessageRepository;

    @Override
    public void handle(RouteIncomingMtCommand command) {

        try {
            Map<String, Object> parameterMap = command.getParameterMap();
            printParameters(parameterMap);

            Long id = Long.parseLong((String)parameterMap.get("id"));

            MtMessage mtMessage = mtMessageRepository.load(id);

            UserActiveDirectoryId userRoutedTo = new UserActiveDirectoryId((String)parameterMap.get("userActiveDirectoryId"));
            mtMessage.routeMessage(userRoutedTo);

            mtMessageRepository.merge(mtMessage);

        } catch (Exception e) {

        }
    }

    private void printParameters(Map<String, Object> parameterMap) {
        System.out.println("inside route incoming mt command handler...");
        Iterator it = parameterMap.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
    }

}

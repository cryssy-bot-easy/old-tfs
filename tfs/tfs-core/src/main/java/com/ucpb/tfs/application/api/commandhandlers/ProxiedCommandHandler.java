package com.ucpb.tfs.application.api.commandhandlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.incuventure.cqrs.api.WebAPIHandler;
import com.incuventure.cqrs.infrastructure.StandardCommandBus;
import com.incuventure.cqrs.infrastructure.StandardQueryBus;
import com.incuventure.cqrs.query.QueryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Qualifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProxiedCommandHandler {

    @Autowired
    StandardCommandBus commandBusService;

    @Autowired
    StandardQueryBus myQueryBus;

    @WebAPIHandler(handles="dispatchProxiedCommand")
    public Object handleProxiedCommand(Map map) {

        System.out.println(">>>>>>>>>>>>>>>> dispatchProxiedCommand handled");
        System.out.println("the command issued was: " + map.get("commandName"));

        try {
            // only do this if the command and its contents were passed
            if(map.containsKey("commandName") && map.containsKey("command")) {

                Class classz = Class.forName(map.get("commandName").toString());

                System.out.println("the contents of the command: " + map.get("command"));

                // create an instance of the command specified using the values jsonified
                Object command = new Gson().fromJson(map.get("command").toString(), classz);

                commandBusService.dispatch(command);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    @WebAPIHandler(handles="dispatchProxiedQuery")
    public Object handleProxiedQuery(Map map) {

        System.out.println(">>>>>>>>>>>>>>>> dispatchProxiedQuery handled");

        try {
            // only do this if the command and its contents were passed
            if(map.containsKey("queryList")) {

                // create an instance of the command specified using the values jsonified

                Type listType = new TypeToken<List<QueryItem>>() {}.getType();

                List<QueryItem> queryItemList = new Gson().fromJson(map.get("queryList").toString(), listType);

                for(QueryItem qi : queryItemList) {
                    System.out.println("IFACE:" + qi.getInterfaceName());
                    System.out.println("Query:" + qi.getMethodName());
                }

                HashMap<String, Object> results =  myQueryBus.dispatch(queryItemList);

                System.out.println("returning results" + results);

//                return results;
                return new Gson().toJson(results).toString();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }
}

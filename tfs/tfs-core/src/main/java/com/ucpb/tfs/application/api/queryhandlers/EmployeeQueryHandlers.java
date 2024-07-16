package com.ucpb.tfs.application.api.queryhandlers;


import com.incuventure.cqrs.api.WebAPIHandler;
import com.ucpb.tfs.application.query.instruction.IServiceInstructionFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmployeeQueryHandlers {

    @Autowired
    private IServiceInstructionFinder siFinder;

    @WebAPIHandler(handles="getEmployee")
    public Object handleGetEmployee(Map map) {
        System.out.println(map);

        return siFinder.findAllServiceInstruction();
    }

    @WebAPIHandler(handles="getDummy")
    public Object handleGetDummy(Map map) {
        System.out.println(map);

        return siFinder.findAllServiceInstruction();
    }

}

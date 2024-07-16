package com.ucpb.tfs.application.api.queryhandlers;

import com.incuventure.cqrs.api.WebAPIHandler;
import com.ucpb.tfs.application.query2.CorresChargeFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: IPCVal
 * Date: 11/5/12
 */
@Component
public class CorresChargeHandlers {

    @Autowired
    CorresChargeFinder corresChargeFinder;

    @WebAPIHandler(handles="findCorresCharges")
    public Object handleFindCorresCharges(Map map) {

        String documentNumber = "";

        if(map.get("documentNumber") != null) {
            documentNumber = map.get("documentNumber").toString();
        }

        return corresChargeFinder.findCorresCharges(documentNumber);
    }

    @WebAPIHandler(handles="findCorresChargeByDocumentNumber")
    public Object handleFindCorresChargeByDocumentNumber(Map map) {

        String documentNumber = map.get("documentNumber").toString();

        return corresChargeFinder.findCorresChargeByDocumentNumber(documentNumber);
    }
}

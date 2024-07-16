package com.ucpb.tfs.application.api.queryhandlers;


import com.incuventure.cqrs.api.WebAPIHandler;
import com.ucpb.tfs.application.query2.ReferenceFinder;
import com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

//  PROLOGUE:
// 	(revision)
//	SCR/ER Number: SCR# IBD-16-1206-01
//	SCR/ER Description: To comply with the requirement for CIF archiving/purging of inactive accounts in TFS.
//	[Created by:] Allan Comboy and Lymuel Saul
//	[Date Deployed:] 12/20/2016
//	Program [Revision] Details: Add CDT Remittance and CDT Refund module.
//	PROJECT: CORE
//	MEMBER TYPE  : Java
//	Project Name: ReferenceHandlers



@Component
public class ReferenceHandlers {

    @Autowired
    ReferenceFinder referenceFinder;
    
    @Autowired
    CDTPaymentRequestRepository cdtPaymentRequestRepository;

    @WebAPIHandler(handles="getAllCountries")
    public Object handleGetAllCountries(Map map) {

        String keyword = "";

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }

        return referenceFinder.getAllCountries(keyword);
    }
    
    @WebAPIHandler(handles="getAllISOCountries")
    public Object handleGetAllISOCountries(Map map) {

        String keyword = "";

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }
       
        return referenceFinder.getAllISOCountries(keyword);
    }
    

    @WebAPIHandler(handles="findBanksByKeyword")
    public Object handleFindBanksByKeyword(Map map) {
        String keyword = "";

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }

        return referenceFinder.findBanksByKeyword(keyword);
    }
    

    @WebAPIHandler(handles="findLocalBanksByKeyword")
    public Object handleFindLocalBanksByKeyword(Map map) {
        String keyword = "";

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }

        return referenceFinder.findLocalBanksByKeyword(keyword);
    }

    @WebAPIHandler(handles="findBankBySwiftAddress")
    public Object handleFindBankBySwiftAddress(Map map) {
        String bic = "";
        String branchCode = "";

        if(map.get("bic") != null) {
            bic = map.get("bic").toString();
        }

        if(map.get("branchCode") != null){
            branchCode = map.get("branchCode").toString();
        }

        return referenceFinder.findBankBySwiftAddress(bic,branchCode);
    }
    
    @WebAPIHandler(handles="findRmaBanksByKeyword")
    public Object handleFindRmaBanksByKeyword(Map map) {
    	String keyword = "";
    	
    	if(map.get("keyword") != null) {
    		keyword = map.get("keyword").toString();
    	}
    	
    	return referenceFinder.findRmaBanksByKeyword(keyword);
    }

    @WebAPIHandler(handles="findDepositoryBanksByKeyword")
    public Object handleFindDepositoryBanksByKeyword(Map map) {
        String keyword = "";

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }

        return referenceFinder.findDepositoryBanksByKeyword(keyword);
    }

    @WebAPIHandler(handles="findDepositoryBanksByKeywordAndCurrency")
    public Object handleFindDepositoryBanksByKeywordAndCurrency(Map map) {
        String keyword = "";
        String currency = null;

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }

        if(map.get("currency") != null) {
            currency = map.get("currency").toString();
        }

        return referenceFinder.findDepositoryBanksByKeywordAndCurrency(keyword, currency);
    }

    @WebAPIHandler(handles="findImporterByKeyword")
    public Object handleFindImporterByKeyword(Map map) {
        String keyword = "";

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }


        try {
            return referenceFinder.findImporterByKeyword(keyword);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @WebAPIHandler(handles="findFirmLibByKeyword")
    public Object handleFindFirmLibByKeyword(Map map) {
        String keyword = "";

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }


        try {
            return referenceFinder.findFirmLibByKeyword(keyword);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @WebAPIHandler(handles="findAllImporterByKeyword")
    public Object handleFindAllImporterByKeyword(Map map) {
        String keyword = "";

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }


        try {
            return referenceFinder.findAllImporterByKeyword(keyword);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @WebAPIHandler(handles="findAllDigitalSignatories")
    public Object handleFindAllDigitalSignatories(Map map) {
    	String keyword = "";

        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }


        try {
            return referenceFinder.findAllDigitalSignatories(keyword);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @WebAPIHandler(handles="getAllProductService")
    public Object handleGetAllProductService(Map map) {
        return referenceFinder.findAllReferenceProductService();
    }
    
    
    @WebAPIHandler(handles="getAllBranchCode")
    public Object handlegetAllBranchCode(Map map) {
    	System.out.println("lost po: " + cdtPaymentRequestRepository.getAllBranch().toString());
        return cdtPaymentRequestRepository.getAllBranch();
    }

    @WebAPIHandler(handles="getAllProductServiceById")
    public Object handleGetAllProductServiceById(Map map) {

        Integer id= null;

        if(map.get("id") != null) {
            id = new Integer((String)map.get("id"));
        }
        return referenceFinder.findAllReferenceProductServiceById(id);
    }

    @WebAPIHandler(handles="getAllCharge")
    public Object handleGetAllCharge(Map map) {
        return referenceFinder.findAllReferenceCharge();
    }

    @WebAPIHandler(handles="findCommodityCode")
    public Object handleFindCommodityCode(Map map) {
        String keyword = "";
        if(map.get("keyword") != null) {
            keyword = map.get("keyword").toString();
        }

        return referenceFinder.findCommodityCode(keyword);
    }

    @WebAPIHandler(handles="findParticulars")
    public Object handleFindParticulars(Map map) {
    	String keyword = "";
    	if(map.get("keyword") != null) {
    		keyword = map.get("keyword").toString();
    	}

    	return referenceFinder.findParticulars(keyword);
    }

    @WebAPIHandler(handles="findParticipantCode")
    public Object handleFindParticipantCode(Map map) {
    	String keyword = "";
    	if(map.get("keyword") != null) {
    		keyword = map.get("keyword").toString();
    	}

    	return referenceFinder.findParticipantCode(keyword);
    }
}

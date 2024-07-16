package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.sysparams.RefBank;
import com.ucpb.tfs.domain.sysparams.RefBankRepository;
import com.ucpb.tfs.utils.UtilSetFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 1/24/13
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class RefBankService {

    @Autowired
    RefBankRepository refBankRepository;

    private final String LAST_UPDATER="SYSTEM";
    
    
    public void saveRefBankDetail(Map parameters) throws Exception {

        String saveMode  = (String) parameters.get("saveMode");

        /*System.out.println("\n###### SAVEMODE = " + saveMode + "\n");*/

        String bic = (String) parameters.get("bic");
        String branchCode  = (String) parameters.get("branchCode");
        if (bic != null && !bic.trim().isEmpty()) {
            bic = bic.toUpperCase();
        }
        if (branchCode != null && !branchCode.trim().isEmpty()) {
            branchCode = branchCode.toUpperCase();
        }

        RefBank refBank = new RefBank();
        UtilSetFields.copyMapToObject(refBank, (HashMap<String, Object>) parameters);
        refBank.setBic(bic);
        refBank.setBranchCode(branchCode);

        refBank.setLastUpdate((String)parameters.get("userId"));
        
        if (saveMode != null && saveMode.equals("edit")) {
        	
    		refBankRepository.merge(refBank);

        } else if (saveMode != null && saveMode.equals("add")) {

            if ((bic != null && !bic.trim().isEmpty()) && (branchCode != null && !branchCode.trim().isEmpty())) {

                RefBank existing = refBankRepository.getBank(bic.toUpperCase(), branchCode.toUpperCase());

                if (existing == null) {
                    refBankRepository.persist(refBank);
                } else {
                    String err = "Cannot add a bank that already exists.";
                    throw new Exception(err);
                }
            }
        }
    }
    
    public void processRefBankDetails(List<Map<String,Object>> resultList) throws Exception{
    	
//    	@SuppressWarnings("unchecked")
//    	List<Map<String,Object>> resultList= (List<Map<String,Object>>) parameters.get("resultList");
    	String modFlag="";
    	
    	try{
    		int ctr=0;
    		for(Map<String,Object> temp : resultList){
				modFlag=temp.get("MODIFICATION FLAG").toString();
				System.out.println(++ctr);
				if(modFlag.equals("A") || modFlag.equals("U") || modFlag.equals("M")){
					saveOrUpdateRefBank(temp);
				}else if(modFlag.equals("D")){
					setBankForDeletion(temp);
				}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw new Exception("ERROR IN PROCESSING REF BANKS: "+e.getMessage());
    	}
    }  

    public boolean processRefBankDetails(Map<String,Object> resultMap){
    	
    	String modFlag="";
    	
    	try{
			modFlag=resultMap.get("MODIFICATION FLAG").toString();
			
			if(modFlag.equals("A") || modFlag.equals("U") || modFlag.equals("M")){
				saveOrUpdateRefBank(resultMap);
			}else if(modFlag.equals("D")){
				setBankForDeletion(resultMap);
			}
			return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
//    		throw new Exception("ERROR IN PROCESSING REF BANKS: "+e.getMessage());
    	}
    }
    
    public int processRmaDocument(Map<String,Object> bankToSave){
    	Assert.notNull(bankToSave.get("bic"));
    	int banksProcessed=0;
    	
    	try{
    		List<RefBank> refBanks = refBankRepository.getBanks(bankToSave.get("bic").toString());
    		for(RefBank refBank:refBanks){
    			UtilSetFields.copyMapToObject(refBank, bankToSave);
    			refBank.setLastUpdate(LAST_UPDATER);
    			refBankRepository.merge(refBank);
    			banksProcessed++;
    		}
    		return banksProcessed;
    	}catch(Exception ex){
    		System.out.println("EXCEPTION IN updateRefBanks: "+ex.getMessage());
    		ex.printStackTrace();
    		return -1;
    	}
    }
    
    public void updateAllNullRmaRefBanks() throws Exception{
		List<RefBank> nullRmaRefBanks = refBankRepository.getNullRmaBanks();
		try{
			for(RefBank nullRmaBank:nullRmaRefBanks){
				nullRmaBank.setRmaFlag("N");
				nullRmaBank.setLastUpdate(LAST_UPDATER);
				refBankRepository.merge(nullRmaBank);
			}
		}catch(Exception ex){
    		System.out.println("EXCEPTION IN updateAllNonRmaRefBanks: "+ex.getMessage());
    		ex.printStackTrace();
    		throw new Exception(ex);
    	}
    }
   
    private void saveOrUpdateRefBank(Map<String,Object> parameters) throws Exception{
    	Assert.notNull(parameters.get("bic"));
    	Assert.notNull(parameters.get("branchCode"));
    	parameters.put("branchCode", normalizeBranchCode(parameters.get("branchCode").toString()));
        try{
        	RefBank existing = refBankRepository.getBank((String)parameters.get("bic"), (String) parameters.get("branchCode"));
        	if (existing == null) {
        		RefBank refBank = new RefBank();
        		UtilSetFields.copyMapToObject(refBank, parameters);
        		refBank.setLastUpdate(LAST_UPDATER);
        		refBankRepository.persist(refBank);
        	}else {
        		UtilSetFields.copyMapToObject(existing, parameters);
        		existing.setLastUpdate(LAST_UPDATER);
        		refBankRepository.merge(existing);          		        		
        	}
        }catch(Exception ex){
        	throw new Exception("EXCEPTION IN saveOrUpdateRefBank: ",ex);
        }
    }
    
    
    private void setBankForDeletion(Map<String,Object> parameters) throws Exception{
    	Assert.notNull(parameters.get("bic"));
    	Assert.notNull(parameters.get("branchCode"));
    	RefBank refBank= refBankRepository.getBank((String)parameters.get("bic"), (String)parameters.get("branchCode"));
    	try{
    		if(refBank != null){
    			refBank.setDeleteFlag("Y");
    			refBankRepository.merge(refBank);    		
    		}
    	}catch(Exception e){
    		throw new Exception("EXCEPTION IN setBankForDeletion: "+e.getMessage());
    	}
    }
    
    private String normalizeBranchCode(String branchCode){
    	if(branchCode.length() > 3){
    		return branchCode.substring(0,3);
    	}else{
    		return branchCode;
    	}
    }
}
/**
 * 
 */
package com.ucpb.tfs.domain.instruction;

import java.util.List;
import java.util.Map;

import com.ucpb.tfs.domain.service.TradeService;

/**
 * @author Val
 *
 */
public interface ServiceInstructionRepository {

	public void persist(ServiceInstruction serviceInstruction);
	
	public void merge(ServiceInstruction serviceInstruction);

    public void update(ServiceInstruction serviceInstruction);
	
	public ServiceInstruction load(ServiceInstructionId serviceInstructionId);

    public List<ServiceInstruction> getAllServiceInstruction();
    public Map getServiceInstructionBy(ServiceInstructionId serviceInstructionId);

    public List<ServiceInstruction> findActiveServiceInstructions(List<ServiceInstructionId> serviceInstructionIdList);

    public Integer getReversal(String serviceInstructionId, String serviceType);

    public List<Map<String, Object>> getNextBranchApprovers(String roleId, String unitCode, String lastUser, String currentOwner);
    
    public List<ServiceInstruction> getUnapprovedServiceInstructions();
}

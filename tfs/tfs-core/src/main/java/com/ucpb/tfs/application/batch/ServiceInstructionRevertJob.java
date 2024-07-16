package com.ucpb.tfs.application.batch;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.incuventure.ddd.domain.DomainEventPublisher;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.batch.job.SpringJob;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus;
import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;
import com.ucpb.tfs.interfaces.services.FacilityService;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class ServiceInstructionRevertJob implements SpringJob {

    private TaskRepository taskRepository;

    private DomainEventPublisher eventPublisher;
    
    private ServiceInstructionRepository serviceInstructionRepository;

	private TradeServiceRepository tradeServiceRepository;
	
	private FacilityService facilityService;

	@Override
	public void execute() {
		List<ServiceInstruction> unapprovedServiceInstructions = serviceInstructionRepository.getUnapprovedServiceInstructions();
		
		for(ServiceInstruction serviceInstruction : unapprovedServiceInstructions){
			
			ServiceInstructionId serviceInstructionId = serviceInstruction.getServiceInstructionId();
			
			TradeService tradeService = tradeServiceRepository.load(serviceInstructionId);
			
			if(DocumentClass.LC.equals(tradeService.getDocumentClass()) &&
					(DocumentSubType1.REGULAR.equals(tradeService.getDocumentSubType1()) ||
							DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()))){
				facilityService.unearmarkAvailment(tradeService.getDocumentNumber().toString().replaceAll("-", ""));
			}
			serviceInstruction.resetApprovers();
			serviceInstruction.updateStatus(ServiceInstructionStatus.PENDING, serviceInstruction.getCreatedBy());
			
			serviceInstructionRepository.update(serviceInstruction);
			
			tradeService.setStatus(TradeServiceStatus.MARV);
			tradeServiceRepository.update(tradeService);
			
			
			Task etsTask = taskRepository.load(new TaskReferenceNumber(serviceInstructionId.toString()));
			etsTask.setUserActiveDirectoryId(new UserActiveDirectoryId(serviceInstruction.getCreatedBy().toString()));
			etsTask.updateStatus(TaskStatus.PENDING, new UserActiveDirectoryId(serviceInstruction.getCreatedBy().toString()));
			
			taskRepository.persist(etsTask);
		}
	}

	@Override
	public void execute(String reportDate) {
		// TODO
		List<ServiceInstruction> unapprovedServiceInstructions = serviceInstructionRepository.getUnapprovedServiceInstructions();
		
		for(ServiceInstruction serviceInstruction : unapprovedServiceInstructions){
			
			ServiceInstructionId serviceInstructionId = serviceInstruction.getServiceInstructionId();
			
			TradeService tradeService = tradeServiceRepository.load(serviceInstructionId);
			
			if(DocumentClass.LC.equals(tradeService.getDocumentClass()) &&
					(DocumentSubType1.REGULAR.equals(tradeService.getDocumentSubType1()) ||
							DocumentSubType1.STANDBY.equals(tradeService.getDocumentSubType1()))){
				facilityService.unearmarkAvailment(tradeService.getDocumentNumber().toString().replaceAll("-", ""));
			}
			serviceInstruction.resetApprovers();
			serviceInstruction.updateStatus(ServiceInstructionStatus.PENDING, serviceInstruction.getCreatedBy());
			
			serviceInstructionRepository.update(serviceInstruction);
			
			Task etsTask = taskRepository.load(new TaskReferenceNumber(serviceInstructionId.toString()));
			etsTask.setUserActiveDirectoryId(new UserActiveDirectoryId(serviceInstruction.getCreatedBy().toString()));
			etsTask.updateStatus(TaskStatus.PENDING, new UserActiveDirectoryId(serviceInstruction.getCreatedBy().toString()));
			
			taskRepository.persist(etsTask);
		}
		
	}

    public void setTradeServiceRepository(TradeServiceRepository tradeServiceRepository) {
        this.tradeServiceRepository = tradeServiceRepository;
    }

    public void setServiceInstructionRepository(ServiceInstructionRepository serviceInstructionRepository) {
        this.serviceInstructionRepository = serviceInstructionRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void setEventPublisher(DomainEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    public void setFacilityService(FacilityService facilityService) {
    	this.facilityService = facilityService;
    }

}

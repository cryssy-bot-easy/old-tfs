package com.ucpb.tfs.domain.instruction.event;

import com.incuventure.ddd.infrastructure.events.EventListener;
import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.enumTypes.ServiceInstructionStatus;
import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import com.ucpb.tfs.domain.task.enumTypes.TaskReferenceType;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * User: IPCVal
 * Date: 8/14/12
 */
@Component
public class InstructionEventsListener {

    @Inject
    private TaskRepository taskRepository;

//    @EventListener
    public void saveTask(ServiceInstructionCreatedEvent serviceInstructionCreatedEvent) {

        try {

            ServiceInstruction serviceInstruction = serviceInstructionCreatedEvent.getServiceInstruction();
            ServiceInstructionStatus serviceInstructionStatus = serviceInstructionCreatedEvent.getServiceInstructionStatus();
            UserActiveDirectoryId userActiveDirectoryId = serviceInstructionCreatedEvent.getUserActiveDirectoryId();

            // Persist task
            TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(serviceInstruction.getServiceInstructionId().toString());
            TaskStatus taskStatus = null;

            if (serviceInstructionStatus != null) {
                if (serviceInstructionStatus.equals(ServiceInstructionStatus.DRAFT)) {
                    taskStatus = TaskStatus.PENDING;
                } else {
                    taskStatus = TaskStatus.valueOf(serviceInstructionStatus.toString());
                }
            }

            System.out.println();
            System.out.println("InstructionEventsListener.saveTask(): BEFORE PERSIST");
            System.out.println();

            Task task = new Task(taskReferenceNumber, TaskReferenceType.ETS, taskStatus, userActiveDirectoryId);
            taskRepository.persist(task);

            System.out.println();
            System.out.println("InstructionEventsListener.saveTask(): AFTER PERSIST");
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this is the real event listener for a routed event
    @EventListener
    public void updateTask(ServiceInstructionRoutedEvent serviceInstructionRoutedEvent) {

        ServiceInstruction serviceInstruction = serviceInstructionRoutedEvent.getServiceInstruction();
        ServiceInstructionStatus serviceInstructionStatus = serviceInstructionRoutedEvent.getServiceInstructionStatus();
        UserActiveDirectoryId targetUser = serviceInstructionRoutedEvent.getUserActiveDirectoryId();

        // use the SI id as the task reference number
        TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(serviceInstruction.getServiceInstructionId().toString());

        // set the value of task status field
        TaskStatus taskStatus = null;
        if (serviceInstructionStatus != null) {
            taskStatus = TaskStatus.valueOf(serviceInstructionStatus.toString());
        }

        Task savedTask = taskRepository.load(taskReferenceNumber);

        // if a reference already exists, update the task
        if (savedTask != null ) {

            if (taskStatus != null) {
                savedTask.updateStatus(taskStatus, targetUser);

                // if the eTS was approved, we need to set the status of the trade service to pending
                if(serviceInstructionStatus == ServiceInstructionStatus.APPROVED) {

                    TaskReferenceNumber tsReferenceNumber = new TaskReferenceNumber(serviceInstructionRoutedEvent.getTradeServiceId().toString());

                    Task tsTask = taskRepository.load(tsReferenceNumber);
                    tsTask.updateStatus(TaskStatus.PENDING, targetUser);

                    taskRepository.merge(tsTask);

                }

            }

            taskRepository.merge(savedTask);

        } else {

            // otherwise, create it
            Task task = new Task(taskReferenceNumber, TaskReferenceType.ETS, taskStatus, targetUser);
            taskRepository.persist(task);

        }

    }

    @EventListener
    public void updateTask(ServiceInstructionUpdatedEvent serviceInstructionUpdatedEvent) {

        try {

            ServiceInstruction serviceInstruction = serviceInstructionUpdatedEvent.getServiceInstruction();
            ServiceInstructionStatus serviceInstructionStatus = serviceInstructionUpdatedEvent.getServiceInstructionStatus();
            UserActiveDirectoryId userActiveDirectoryId = serviceInstructionUpdatedEvent.getUserActiveDirectoryId();

            // Persist task
            TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(serviceInstruction.getServiceInstructionId().toString());
            TaskStatus taskStatus = null;

            if (serviceInstructionStatus != null) {
                if (serviceInstructionStatus.equals(ServiceInstructionStatus.DRAFT)) {
                    taskStatus = TaskStatus.PENDING;
                } else {
                    taskStatus = TaskStatus.valueOf(serviceInstructionStatus.toString());
                }
            }

            System.out.println();
            System.out.println("InstructionEventsListener.updateTask(): BEFORE PERSIST");
            System.out.println();

            Task savedTask = taskRepository.load(taskReferenceNumber);

            if (savedTask != null ) {

                if (taskStatus != null) {
                    savedTask.updateStatus(taskStatus, userActiveDirectoryId);
                }

                taskRepository.merge(savedTask);
            }

            System.out.println();
            System.out.println("InstructionEventsListener.updateTask(): AFTER PERSIST");
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    @EventListener
    public void removeTaskForReversedEts(ServiceInstructionReversedEvent serviceInstructionReversedEvent) {

        // delete both tradeService and serviceInstruction for reversed eTS
        try {
            taskRepository.delete(new TaskReferenceNumber(serviceInstructionReversedEvent.getServiceInstructionId().toString()));
            taskRepository.delete(new TaskReferenceNumber(serviceInstructionReversedEvent.getTradeServiceId().toString()));
        }
        catch(Exception e) {
            // do nothing, if this was approved after the task is already gone
        }
    }
*/


    @EventListener
    public void handleEtsMarkedForReversal(ServiceInstructionMarkedForReversalEvent serviceInstructionMarkedForReversalEvent) {

        TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(serviceInstructionMarkedForReversalEvent.getTradeServiceId().toString());

        Task task = taskRepository.load(taskReferenceNumber);

        task.updateStatus(TaskStatus.FOR_REVERSAL, task.getUserActiveDirectoryId());

        taskRepository.merge(task);

    }

    @EventListener
    public void handleEtsReversalUnmarked(ServiceInstructionReversalUnmarkedEvent serviceInstructionReversalUnmarkedEvent) {

        TaskReferenceNumber taskReferenceNumber = new TaskReferenceNumber(serviceInstructionReversalUnmarkedEvent.getTradeServiceId().toString());

        Task task = taskRepository.load(taskReferenceNumber);

        task.updateStatus(serviceInstructionReversalUnmarkedEvent.getOriginalStatus(), task.getUserActiveDirectoryId());

        taskRepository.merge(task);

    }
}

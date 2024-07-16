package com.ucpb.tfs.domain.task;

import com.ipc.rbac.domain.UserActiveDirectoryId;
import com.ucpb.tfs.domain.task.enumTypes.TaskReferenceType;
import com.ucpb.tfs.domain.task.enumTypes.TaskStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * User: IPCVal
 * Date: 8/13/12
 */
public class Task implements Serializable {

    private TaskReferenceNumber taskReferenceNumber;

    private TaskReferenceType taskReferenceType;

    private TaskStatus taskStatus;

    private UserActiveDirectoryId userActiveDirectoryId;

    private String unitCode;

    private Date createdDate;

    private Date modifiedDate;

    public Task() {}

    public Task(TaskReferenceNumber taskReferenceNumber, TaskReferenceType taskReferenceType, TaskStatus taskStatus, UserActiveDirectoryId userActiveDirectoryId) {
        this.taskReferenceNumber = taskReferenceNumber;
        this.taskReferenceType = taskReferenceType;
        this.taskStatus = taskStatus;
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.createdDate = new Date();
        this.modifiedDate = createdDate;
    }

    public Task(TaskReferenceNumber taskReferenceNumber, TaskReferenceType taskReferenceType, TaskStatus taskStatus, UserActiveDirectoryId userActiveDirectoryId, String unitCode) {
        this.taskReferenceNumber = taskReferenceNumber;
        this.taskReferenceType = taskReferenceType;
        this.taskStatus = taskStatus;
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.createdDate = new Date();
        this.modifiedDate = createdDate;
        this.unitCode = unitCode;
    }

    public TaskReferenceNumber getTaskReferenceNumber() {
        return this.taskReferenceNumber;
    }

    public void updateStatus(TaskStatus taskStatus, UserActiveDirectoryId userActiveDirectoryId) {
        this.taskStatus = taskStatus;
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.modifiedDate = new Date();
    }

    public void updateStatus(TaskStatus taskStatus, UserActiveDirectoryId userActiveDirectoryId, String unitCode) {
        this.taskStatus = taskStatus;
        this.userActiveDirectoryId = userActiveDirectoryId;
        this.modifiedDate = new Date();
        this.unitCode = unitCode;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public UserActiveDirectoryId getUserActiveDirectoryId() {
        return userActiveDirectoryId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUserActiveDirectoryId(UserActiveDirectoryId userActiveDirectoryId) {
        this.userActiveDirectoryId = userActiveDirectoryId;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public TaskReferenceType getTaskReferenceType() {
        return taskReferenceType;
    }

    public void setTaskReferenceNumber(TaskReferenceNumber taskReferenceNumber) {
        this.taskReferenceNumber = taskReferenceNumber;
    }
}

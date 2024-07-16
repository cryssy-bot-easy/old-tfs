package com.ucpb.tfs.domain.task;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 8/13/12
 */
public class TaskReferenceNumber implements Serializable {

    private String taskReferenceNumber;

    public TaskReferenceNumber() {}

    public TaskReferenceNumber(final String taskReferenceNumber) {
        Validate.notNull(taskReferenceNumber);
        this.taskReferenceNumber = taskReferenceNumber;
    }

    @Override
    public String toString() {
        return taskReferenceNumber;
    }

}

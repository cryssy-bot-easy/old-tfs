package com.ucpb.tfs.domain.task;

/**
 * User: IPCVal
 * Date: 8/13/12
 */
public interface TaskRepository {

    public void persist(Task task);

    public void update(Task task);

    public void merge(Task task);

    public Task load(TaskReferenceNumber taskReferenceNumber);

    public void delete(TaskReferenceNumber taskReferenceNumber);
}

package com.ucpb.tfs.domain.task.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.task.Task;
import com.ucpb.tfs.domain.task.TaskReferenceNumber;
import com.ucpb.tfs.domain.task.TaskRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: IPCVal
 * Date: 8/13/12
 */
@Component
@Repository
@Transactional
public class HibernateTaskRepository implements TaskRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    public void persist(Task task) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(task);
    }

    public void update(Task task) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(task);
    }

    public void merge(Task task) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(task);
    }

    public Task load(TaskReferenceNumber taskReferenceNumber) {
        return (Task) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.task.Task where taskReferenceNumber = :taskReferenceNumber").setParameter("taskReferenceNumber", taskReferenceNumber).uniqueResult();
    }

    public void delete(TaskReferenceNumber taskReferenceNumber) {

        Session session = this.mySessionFactory.getCurrentSession();

        Task task = (Task) session.createCriteria(Task.class)
                .add(Restrictions.eq("taskReferenceNumber", taskReferenceNumber)).uniqueResult();

        session.delete(task);

    }
}

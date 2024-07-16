package com.ucpb.tfs.domain.attach.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.attach.Attachment;
import com.ucpb.tfs.domain.attach.AttachmentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * User: IPCVal
 */
@Repository
@Component
public class HibernateAttachmentRepository implements AttachmentRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    @Transactional
    public Map<String, Object> getAttachmentDetailsMap(Long id) {

        Session session = this.mySessionFactory.getCurrentSession();

        Attachment attachment = (Attachment)session.createQuery("from com.ucpb.tfs.domain.attach.Attachment where id = :id").setParameter("id", id).uniqueResult();

        Gson gson = new Gson();
        String result = gson.toJson(attachment);
        Map<String, Object> returnClass = gson.fromJson(result, Map.class);

        return returnClass;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, readOnly = false)
    public int delete(Long id) {
        Session session = this.mySessionFactory.getCurrentSession();
        return session.createSQLQuery("delete from attachment where id = :id").setParameter("id", id).executeUpdate();
    }
}

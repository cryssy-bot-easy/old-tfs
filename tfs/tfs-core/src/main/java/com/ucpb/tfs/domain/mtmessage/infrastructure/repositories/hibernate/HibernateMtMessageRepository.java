package com.ucpb.tfs.domain.mtmessage.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.mtmessage.MtMessage;
import com.ucpb.tfs.domain.mtmessage.MtMessageRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.service.TradeServiceReferenceNumber;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Marv
 * Date: 10/10/12
 */

@Repository
@Component
public class HibernateMtMessageRepository implements MtMessageRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    @Transactional
    public void persist(MtMessage mtMessage) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(mtMessage);
    }

    @Override
    @Transactional
    public void merge(MtMessage mtMessage) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(mtMessage);
    }

    @Override
    @Transactional
    public void update(MtMessage mtMessage) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(mtMessage);
    }

    @Override
    @Transactional
    public MtMessage load(Long id) {
        return (MtMessage) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.mtmessage.MtMessage where id = ?").setParameter(0, id).uniqueResult();
    }

    @Override
    public MtMessage load(TradeServiceReferenceNumber tradeServiceReferenceNumber) {
        return (MtMessage) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.mtmessage.MtMessage where tradeServiceReferenceNumber = ?")
                .setParameter(0, tradeServiceReferenceNumber).uniqueResult();
    }

}

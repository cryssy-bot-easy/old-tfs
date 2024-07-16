package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.TransactionLog;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Component
@Transactional
public class HibernateTransactionLogRepository implements TransactionLogRepository{

	@Autowired(required=true)
	private SessionFactory sessionFactory;

	@Override
	public TransactionLog getAuditLog(Long id) {
		return (TransactionLog) sessionFactory.getCurrentSession()
				.get(TransactionLog.class, id);
	}

	@Override
	public void persist(TransactionLog auditLog) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(auditLog);
	}

	@Override
	public TransactionLog getAuditLogByReferenceNumber(String referenceNumber) {
		return (TransactionLog) sessionFactory.getCurrentSession().
				createQuery("from com.ucpb.tfs.domain.audit.TransactionLog where txnReferenceNumber = ?").setParameter(0, referenceNumber).uniqueResult();
	}

    @Override
    public void delete(TradeServiceId tradeServiceId) {
        List<TransactionLog> transactionLogList = (List<TransactionLog>)sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.audit.TransactionLog where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).list();
        if (transactionLogList != null && !transactionLogList.isEmpty()) {
            sessionFactory.getCurrentSession().createQuery("delete from com.ucpb.tfs.domain.audit.TransactionLog where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).executeUpdate();
        }
    }
	
	@Override
	public void deleteBatchFlag(int flag) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().createQuery("UPDATE com.ucpb.tfs.domain.audit.TransactionLog set batchFlag = 0 where batchFlag = :batchFlag").setParameter("batchFlag", flag).executeUpdate();
	}

}

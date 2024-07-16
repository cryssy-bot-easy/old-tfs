package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.AccountLog;
import com.ucpb.tfs.domain.service.TradeServiceId;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Transactional
public class HibernateAccountLogRepository implements AccountLogRepository {

	@Autowired(required=true)
	private SessionFactory sessionFactory;
	
	@Override
	public AccountLog getAccountLogById(Long id) {
		return (AccountLog)sessionFactory.getCurrentSession().get(AccountLog.class, id);
	}

	@Override
	public void persist(AccountLog accountLog) {
		sessionFactory.getCurrentSession().persist(accountLog);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccountLog> getAccountLogsByOpeningDate(Date date) {
		return sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.audit.AccountLog where openingDate = ?")
				.setParameter(0, date).list();
	}

	@Override
	public AccountLog getAccountLogByAccountNumber(String accountNumber) {
		return (AccountLog) sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.audit.AccountLog where accountNumber = ?")
				.setParameter(0, accountNumber).uniqueResult();
	}

    @Override
    public List<AccountLog> getAccountLogsByCdtClosingDate(Date from, Date to) {

        // Closing date is remittance date

        Session session  = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(AccountLog.class);

        crit.add(Restrictions.eq("accountTag", "CDT"));
        crit.add(Restrictions.between("closingDate", getMinDate(from), getMaxDate(to)));

        return crit.list();
    }

    @Override
    public void delete(TradeServiceId tradeServiceId) {
        List<AccountLog> accountLogList = (List<AccountLog>) sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.audit.AccountLog where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).list();
        if (accountLogList != null && !accountLogList.isEmpty()) {
            sessionFactory.getCurrentSession().createQuery("delete from com.ucpb.tfs.domain.audit.AccountLog where tradeServiceId = :tradeServiceId").setParameter("tradeServiceId", tradeServiceId).executeUpdate();
        }
    }

    private Date getMinDate(Date dateToGet) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(dateToGet);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private Date getMaxDate(Date dateToGet) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(dateToGet);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //calendar.add(Calendar.DATE, 1);
        Calendar tomorrow = calendar;
        tomorrow.add(Calendar.DATE, 1);

        tomorrow.add(Calendar.MILLISECOND, -1);

        return tomorrow.getTime();
    }
	
	 public void deleteBatchFlag(int flag) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().createQuery("UPDATE com.ucpb.tfs.domain.audit.AccountLog set batchFlag = 0 where batchFlag = :batchFlag").setParameter("batchFlag", flag).executeUpdate();
	}

}

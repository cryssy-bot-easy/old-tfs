package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ucpb.tfs.domain.product.LetterOfCredit;
import com.ucpb.tfs.domain.product.LetterOfCreditRepository;
import com.ucpb.tfs.domain.product.enums.LCType;
import com.ucpb.tfs.domain.product.enums.TradeProductStatus;

@Transactional
public class HibernateLetterOfCreditRepository implements LetterOfCreditRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;

	@Override
	public List<LetterOfCredit> getLcsWithEarmarking(Date date) {
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));

		return this.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from com.ucpb.tfs.domain.product.LetterOfCredit " +
						"where type in ('REGULAR', 'STANDBY') and (status in ('OPEN','REINSTATED') " +
						"or (status = :closedStatus and month(dateClosed) = :month and year(dateClosed) = :year) " +
						"or (status = :cancelledStatus and month(cancellationDate) = :month and year(cancellationDate) = :year) " +
						"or (status = :expiredStatus and month(expiryDate) = :month and year(expiryDate) = :year))")
				.setParameter("closedStatus", TradeProductStatus.CLOSED)
				.setParameter("cancelledStatus", TradeProductStatus.CANCELLED)
				.setParameter("expiredStatus", TradeProductStatus.EXPIRED)
				.setParameter("month", month).setParameter("year", year).list();
	}

}

package com.ucpb.tfs.domain.product.infrastructure.repositories.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ucpb.tfs.domain.product.BookingSettlement;
import com.ucpb.tfs.domain.product.BookingSettlementRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.ExportAdvancePayment;

public class HibernateBookingSettlementRepository implements
		BookingSettlementRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

	@Override
	public void persist(BookingSettlement bookingSettlement) {
		Session session = this.mySessionFactory.getCurrentSession();
        session.persist(bookingSettlement);
	}

	@Override
	public void update(BookingSettlement bookingSettlement) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(bookingSettlement);
    }

	@Override
	public void merge(BookingSettlement bookingSettlement) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(bookingSettlement);
    }

	@Override
	public BookingSettlement load(DocumentNumber documentNumber) {
		return (BookingSettlement) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.product.BookingSettlement where documentNumber = :documentNumber").setParameter("documentNumber", documentNumber).uniqueResult();
	}

}

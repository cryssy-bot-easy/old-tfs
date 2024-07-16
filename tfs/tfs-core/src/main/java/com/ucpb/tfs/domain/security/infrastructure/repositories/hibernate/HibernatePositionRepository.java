package com.ucpb.tfs.domain.security.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.security.Position;
import com.ucpb.tfs.domain.security.PositionCode;
import com.ucpb.tfs.domain.security.PositionRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 11/28/13
 * Time: 4:02 PM
 * To change this template use File | Settings | File Templates.
 */

@Transactional
public class HibernatePositionRepository implements PositionRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Position loadPosition(PositionCode code) {
        return (Position) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.security.Position where code = :code").setParameter("code", code).uniqueResult();
    }

    @Override
    public List<Position> getAllPositions() {
        Session session  = this.sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Position.class);

        return criteria.list();
    }

    @Override
    public void merge(Position position) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(position);
    }

    @Override
    public List<Position> getAllPositionsMatching(String positionName, BigDecimal signingLimitFrom, BigDecimal signingLimitTo) {
        Session session  = this.sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Position.class);

        if (positionName != null) {
            criteria.add(Restrictions.ilike("positionName", positionName, MatchMode.ANYWHERE));
        }

        if (signingLimitFrom != null) {
            criteria.add(Restrictions.ge("signingLimit", signingLimitFrom));
        }

        if (signingLimitTo != null) {
            criteria.add(Restrictions.le("signingLimit", signingLimitTo));
        }

        return criteria.list();
    }
}

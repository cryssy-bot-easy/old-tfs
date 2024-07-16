package com.ucpb.tfs.domain.routing.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.routing.Route;
import com.ucpb.tfs.domain.routing.RouteRepository;
import com.ucpb.tfs.domain.routing.RoutingInformationId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: IPCVal
 * Date: 10/7/13
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateRouteRepository implements RouteRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public List<Route> getAllRoutes(RoutingInformationId routingInformationId) {
        Session session = this.sessionFactory.getCurrentSession();
        return (List<Route>) session.createQuery("from com.ucpb.tfs.domain.routing.Route where routingInformationId = ?").setParameter(0, routingInformationId.toString()).list();
    }
}

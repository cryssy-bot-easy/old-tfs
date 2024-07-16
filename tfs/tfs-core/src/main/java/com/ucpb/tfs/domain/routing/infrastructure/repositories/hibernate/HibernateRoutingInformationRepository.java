package com.ucpb.tfs.domain.routing.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.routing.Route;
import com.ucpb.tfs.domain.routing.RoutingInformation;
import com.ucpb.tfs.domain.routing.RoutingInformationId;
import com.ucpb.tfs.domain.routing.RoutingInformationRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@Component
@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateRoutingInformationRepository implements RoutingInformationRepository{

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void addRoutingInformation(Route route,RoutingInformationId id) {
      RoutingInformation routingInformation = (RoutingInformation) sessionFactory.getCurrentSession().get(RoutingInformation.class,id);
      if(routingInformation == null){
          routingInformation = new RoutingInformation(id);
      }
      routingInformation.addRoute(route);
      sessionFactory.getCurrentSession().saveOrUpdate(route);
      sessionFactory.getCurrentSession().saveOrUpdate(routingInformation);
    }

    @Override
    public RoutingInformation getRoutingInformation(RoutingInformationId routingInformationId) {
        return (RoutingInformation) sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.routing.RoutingInformation where routingInformationId = ?")
                .setParameter(0,routingInformationId).uniqueResult();
    }
}

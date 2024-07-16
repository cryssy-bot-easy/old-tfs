package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.ProductReference;
import com.ucpb.tfs.domain.reference.ProductServiceReference;
import com.ucpb.tfs.domain.reference.ProductServiceReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * User: IPCJon
 * Date: 1/21/13
 */
@Transactional
public class HibernateProductServiceReferenceRepository implements ProductServiceReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void persist(ProductServiceReference refProductService) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(refProductService);
    }

    @Override
    public void merge(ProductServiceReference refProductService) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(refProductService);
    }

    @Override
    public void update(ProductServiceReference refProductService) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(refProductService);
    }

    @Override
    public ProductServiceReference getProductService(Long productServiceId) {

        return (ProductServiceReference) this.sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.reference.ProductServiceReference where id = ?").setParameter(0, productServiceId).uniqueResult();
    }

    @Override
    public Map getProductServiceById(Long productServiceId) {

        ProductServiceReference refProductService = getProductService(productServiceId);

        // eagerly load all references
        // Hibernate.initialize(refBank);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(refProductService);
        Map returnClass = gson.fromJson(result, Map.class);

        return returnClass;
    }

    @Override
    public List<ProductServiceReference> getRequestsMatching(String productId, String serviceType) {

        Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ProductServiceReference.class);

        if(productId != null && !productId.trim().isEmpty()) {
            crit.add(Restrictions.ilike("productId.productId", productId.toString(), MatchMode.ANYWHERE));
        }

        // Handle Enum ServiceType
        if(serviceType != null&& !serviceType.trim().isEmpty()) {
            crit.add(Restrictions.sqlRestriction("lcase(serviceType) like ?", "%" + serviceType.toLowerCase() + "%", StringType.INSTANCE));
            //crit.add(Restrictions.ilike("serviceType.serviceType",serviceType.toString(), MatchMode.ANYWHERE));
        }

        List<ProductServiceReference> refProductServices = crit.list();

        return refProductServices;
    }

    @Override
    public ProductServiceReference getProductService(ProductId productId, ServiceType serviceType) {

        return (ProductServiceReference) this.sessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.reference.ProductServiceReference where productId = ? and  serviceType = ?").setParameter(0, productId).setParameter(1, serviceType).uniqueResult();
    }
}

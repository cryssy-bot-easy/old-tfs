package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.ProductReference;
import com.ucpb.tfs.domain.reference.ProductReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateProductReferenceRepository implements ProductReferenceRepository{

    @Autowired
    private SessionFactory sessionFactory;

    public HibernateProductReferenceRepository(){

    }

    @Override
    @Transactional
    public void save(ProductReference productReference) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(productReference);
    }

    @Override
    @Transactional
    public ProductReference find(DocumentClass documentClass, DocumentType documentType, DocumentSubType1 documentSubType1, DocumentSubType2 documentSubType2) {

       Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(ProductReference.class);
        c.add(documentClass == null ? Restrictions.isNull("documentClass") : Restrictions.eq("documentClass", documentClass));
        c.add(documentType == null ? Restrictions.isNull("documentType"):  Restrictions.eq("documentType", documentType));
        c.add(documentSubType1 == null ? Restrictions.isNull("documentSubType1") : Restrictions.eq("documentSubType1", documentSubType1));
        c.add(documentSubType2 == null ? Restrictions.isNull("documentSubType2") : Restrictions.eq("documentSubType2", documentSubType2));
        c.add(Restrictions.isNull("documentSubType3"));
        c.setMaxResults(1);

        //System.out.println("finding marching product .... ");

        List<ProductReference> results = c.list();

        if(results.size() == 1) {
            return results.get(0);
        }
        else {
            for(ProductReference prodRef : results){
                System.out.println("Product ID"+prodRef.getProductId());
            }
            return null;
        }

    }

    @Override
    @Transactional
    public Long getCount() {

        Session session = this.sessionFactory.getCurrentSession();

        return ( (Long) session.createQuery("select count(*) from com.ucpb.tfs.domain.reference.ProductReference").iterate().next() ).longValue();
    }


    @Override
    @Transactional
    public ProductReference find(ProductId productId) {

        Session session = this.sessionFactory.getCurrentSession();

        // use criteria since we may have null values for some fields
        Criteria c = session.createCriteria(ProductReference.class);
        c.add(productId == null ? Restrictions.isNull("productId") : Restrictions.eq("productId", productId));
        c.setMaxResults(1);

        //System.out.println("finding marching product .... ");

        List<ProductReference> results = c.list();

        if(results.size() == 1) {
            return results.get(0);
        }
        else {
            for(ProductReference prodRef : results){
                System.out.println("Product ID"+prodRef.getProductId());
            }
            return null;
        }

    }
    
    @Override
    @Transactional
    public String getUCPBProdID(String productId) {

        Session session = this.sessionFactory.getCurrentSession();

        return session.createQuery("select ucpbProductId from com.ucpb.tfs.domain.reference.ProductReference where PRODUCTID=?")
        		.setString(0,productId.toString()).uniqueResult().toString();
    }


}

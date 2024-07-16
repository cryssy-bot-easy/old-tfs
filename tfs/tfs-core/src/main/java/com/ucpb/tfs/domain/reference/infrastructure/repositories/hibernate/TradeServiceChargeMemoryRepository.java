package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.TradeServiceChargeReference;
import com.ucpb.tfs.domain.reference.TradeServiceChargeReferenceRepository;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Jett
 * Date: 7/24/12
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class TradeServiceChargeMemoryRepository implements TradeServiceChargeReferenceRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    // this is a singleton implementation of this repository
    private static final TradeServiceChargeMemoryRepository instance = new TradeServiceChargeMemoryRepository();

    public static TradeServiceChargeMemoryRepository getInstance() {
        return instance;
    }

    private List<TradeServiceChargeReference> tradeServiceCharges;

    private TradeServiceChargeMemoryRepository() {
        tradeServiceCharges = new ArrayList<TradeServiceChargeReference>();
    };

    // since this is a dummy implementation, provide a method to add some test data to this class
    // (should be refactored to use Spring)
    public void initAdd(TradeServiceChargeReference tradeServiceCharge) {
        tradeServiceCharges.add(tradeServiceCharge);
    }

    @Override
    public List<TradeServiceChargeReference> getCharges(ProductId productId, ServiceType serviceType) {
        List<TradeServiceChargeReference> matchingCharges = new ArrayList<TradeServiceChargeReference>();

        // iterate through all our charges and find the ones that are charges for this product
        // and service type
        for(TradeServiceChargeReference c : tradeServiceCharges) {
            if(c.isChargeFor(productId, serviceType)) {
                matchingCharges.add(c);
            }
        }

        return matchingCharges;

    }

    public void save(TradeServiceChargeReference tradeServiceCharge) {
        this.sessionFactory.getCurrentSession().persist(tradeServiceCharge);
    }

    @Override
    public Long getCount() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void clearCharges(){
        //Empties tradeServiceCharges
        //Used by unit test
        tradeServiceCharges = new ArrayList<TradeServiceChargeReference>();
    }
}

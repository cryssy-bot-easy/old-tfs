package com.ucpb.tfs.domain.settlementaccount.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.settlementaccount.MarginalDeposit;
import com.ucpb.tfs.domain.settlementaccount.MarginalDepositRepository;
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@Repository
@Transactional
public class HibernateMarginalDepositRepository implements MarginalDepositRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public MarginalDeposit load(SettlementAccountNumber settlementAccountNumber) {
        return (MarginalDeposit) this.sessionFactory.getCurrentSession().createQuery(
        "from com.ucpb.tfs.domain.settlementaccount.MarginalDeposit where settlementAccountNumber = :settlementAccountNumber").setParameter("settlementAccountNumber", settlementAccountNumber).uniqueResult();
    }

    @Override
    public void persist(MarginalDeposit md) {
        this.sessionFactory.getCurrentSession().persist(md);
    }

    @Override
    public void update(MarginalDeposit md) {
        this.sessionFactory.getCurrentSession().update(md);
    }

    @Override
    public Map loadToMap(SettlementAccountNumber settlementAccountNumber) {
        MarginalDeposit marginalDeposit = (MarginalDeposit) this.load(settlementAccountNumber);

        if(marginalDeposit != null) {
            // eagerly load all references
            Hibernate.initialize(marginalDeposit);

            // we cannot return the list so we use gson to serialize then deserialize back to a list
            Gson gson = new Gson();
            Map returnClass = gson.fromJson(gson.toJson(marginalDeposit), Map.class);

            return returnClass;
        }
        else {
            return null;
        }
    }
}

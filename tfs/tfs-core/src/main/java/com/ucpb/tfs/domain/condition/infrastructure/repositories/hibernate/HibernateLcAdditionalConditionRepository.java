package com.ucpb.tfs.domain.condition.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.condition.ConditionCode;
import com.ucpb.tfs.domain.condition.LcAdditionalCondition;
import com.ucpb.tfs.domain.condition.LcAdditionalConditionRepository;
import com.ucpb.tfs.domain.condition.enumTypes.ConditionType;
import com.ucpb.tfs.domain.product.DocumentNumber;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * User: Marv
 * Date: 11/4/12
 */
@Transactional
public class HibernateLcAdditionalConditionRepository implements LcAdditionalConditionRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public void persist(LcAdditionalCondition lcAdditionalCondition) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(lcAdditionalCondition);
    }

    @Override
    public void merge(LcAdditionalCondition lcAdditionalCondition) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(lcAdditionalCondition);
    }

    @Override
    public void update(LcAdditionalCondition lcAdditionalCondition) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(lcAdditionalCondition);
    }

    @Override
    public LcAdditionalCondition load(ConditionCode conditionCode) {
        return (LcAdditionalCondition) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.condition.LcAdditionalCondition where conditionCode = ?").setParameter(0, conditionCode).uniqueResult();
    }

    @Override
    public Set<LcAdditionalCondition> load(DocumentNumber documentNumber) {

        Session session = this.mySessionFactory.getCurrentSession();

        // id is intentionally left out of the select
        String queryStatement = new String("select conditionCode, cast(condition as varchar(6500)) as condition, conditionType from LcAdditionalCondition where documentNumber = ?");
        Query query = session.createSQLQuery(queryStatement.toString()).setParameter(0, documentNumber.toString());

        Set<LcAdditionalCondition> lcAdditionalConditionSet = new HashSet<LcAdditionalCondition>();

        Iterator it = query.list().iterator();

        while(it.hasNext()) {

            Object[] obj = (Object[]) it.next();

            LcAdditionalCondition lcAdditionalCondition = new LcAdditionalCondition();

            if (obj[0] != null) {
                ConditionCode conditionCode = new ConditionCode((String)obj[0]);
                lcAdditionalCondition.setConditionCode(conditionCode);
            }

            lcAdditionalCondition.setCondition((String) obj[1]);

            ConditionType conditionType = ConditionType.valueOf((String) obj[2]);
            lcAdditionalCondition.setConditionType(conditionType);

            lcAdditionalConditionSet.add(lcAdditionalCondition);
        }

        return lcAdditionalConditionSet;
    }

}

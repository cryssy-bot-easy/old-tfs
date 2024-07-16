package com.ucpb.tfs.domain.corresCharges.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.corresCharges.CorresChargeAdvance;
import com.ucpb.tfs.domain.corresCharges.CorresChargeAdvanceRepository;
import com.ucpb.tfs.domain.product.DocumentNumber;
import com.ucpb.tfs.domain.product.enums.ProductType;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: IPCVal
 * Date: 11/4/12
 */

@Transactional
public class HibernateCorresChargeAdvanceRepository implements CorresChargeAdvanceRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public CorresChargeAdvance load(Long id) {
        return (CorresChargeAdvance) this.mySessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.corresCharges.CorresChargeAdvance where id = :id").setParameter("id", id).uniqueResult();
    }

    @Override
    public void save(CorresChargeAdvance corresChargeAdvance) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.save(corresChargeAdvance);
    }

    @Override
    public void merge(CorresChargeAdvance corresChargeAdvance) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(corresChargeAdvance);
    }

    @Override
    public List<CorresChargeAdvance> getAllByDocumentNumber(DocumentNumber documentNumber) {

        Session session = this.mySessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(CorresChargeAdvance.class);

        if(documentNumber != null && !documentNumber.toString().isEmpty()) {
            crit.add(Restrictions.eq("documentNumber", documentNumber));
        }

        List corresChargesAdvance = crit.list();

        return corresChargesAdvance;
    }

    @Override
    public List<Map<String, Object>> findAllByDocumentNumber(String documentNumber, String unitCode, String unitcode) {
        Session session = this.mySessionFactory.getCurrentSession();

        StringBuilder queryStatement = new StringBuilder("select sum(cca.amount) as totalAdvancedAmount, sum(cca.coveredAmount) as totalCoveredAmount, ");
        queryStatement.append("cca.documentNumber, tp.cifName, tp.cifNumber, tp.accountOfficer, tp.ccbdBranchUnitCode, cca.createdDate, ");
        queryStatement.append("tp.longName, tp.address1, tp.address2 ");
        queryStatement.append("from CorresChargeAdvance cca ");
        queryStatement.append("inner join TradeProduct tp on tp.documentNumber = cca.documentNumber ");
        queryStatement.append("where tp.productType = 'LC'");
//      queryStatement.append("where (cca.amount-cca.coveredAmount) > 0 ");
        
        if (documentNumber != null && !documentNumber.isEmpty()) {
            queryStatement.append("and cca.documentNumber like '%" + documentNumber + "%' ");
            if (unitCode != null) {
            	queryStatement.append("and tp.ccbdBranchUnitCode = '" + unitCode + "' ");
            } else if (unitcode != null && !unitcode.isEmpty() && !unitcode.equals("909")) {
            	queryStatement.append("and tp.ccbdBranchUnitCode = '" + unitcode + "' ");
            }
        } else if (unitCode != null) {
        	queryStatement.append("and tp.ccbdBranchUnitCode = '" + unitCode + "' ");
        } else if (unitcode != null && !unitcode.isEmpty() && !unitcode.equals("909")) {
        	queryStatement.append("and tp.ccbdBranchUnitCode = '" + unitcode + "' ");
        }

        queryStatement.append("group by cca.documentNumber, tp.cifName, tp.cifNumber, tp.accountOfficer, tp.ccbdBranchUnitCode, cca.createdDate , tp.longName, tp.address1, tp.address2");
        
        Query query = session.createSQLQuery(queryStatement.toString());

        List<Map<String, Object>> corresChargeList = new ArrayList<Map<String, Object>>();

        
        
        Iterator it = query.list().iterator();

        while(it.hasNext()) {
            Object[] obj = (Object[]) it.next();
        
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("totalAdvancedAmount", obj[0]);
            map.put("totalCoveredAmount", obj[1]);
            map.put("documentNumber", obj[2]);
            map.put("cifName", obj[3]);
            map.put("cifNumber", obj[4]);
            map.put("accountOfficer", obj[5]);
            map.put("ccbdBranchUnitCode", obj[6]);
            map.put("createdDate", obj[7]);
            map.put("longName", obj[8]);
            map.put("address1", obj[9]);
            map.put("address2", obj[10]);
            corresChargeList.add(map);
        }

        return corresChargeList;
    }
}

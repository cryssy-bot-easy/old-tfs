/**
 * 
 */
package com.ucpb.tfs.domain.instruction.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.instruction.ServiceInstruction;
import com.ucpb.tfs.domain.instruction.ServiceInstructionId;
import com.ucpb.tfs.domain.instruction.ServiceInstructionRepository;
import com.ucpb.tfs.domain.service.TradeService;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Val
 *
 */
@Repository
@Component
@Transactional
public class HibernateServiceInstructionRepository implements ServiceInstructionRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

	@Override
    public void persist(ServiceInstruction serviceInstruction) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.persist(serviceInstruction);
	}

    @Override
    public void merge(ServiceInstruction serviceInstruction) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.merge(serviceInstruction);
    }

    @Override
    public void update(ServiceInstruction serviceInstruction) {
        Session session = this.mySessionFactory.getCurrentSession();
        session.update(serviceInstruction);
	}

	@Override
    @Transactional
    public ServiceInstruction load(ServiceInstructionId serviceInstructionId) {
        return (ServiceInstruction) mySessionFactory.getCurrentSession().
                createQuery("from com.ucpb.tfs.domain.instruction.ServiceInstruction where serviceInstructionId = ?").setParameter(0, serviceInstructionId).uniqueResult();
	}

    private List findServiceInstructionBy(Criteria criteria) {

        List tradeServices = criteria.list();

        // eagerly load all references
        Hibernate.initialize(tradeServices);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(tradeServices);
        List returnMap = gson.fromJson(result, List.class);

        return returnMap;
    }

    @Override
    public List<ServiceInstruction> getAllServiceInstruction() {

        Session session  = this.mySessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(ServiceInstruction.class);

        return findServiceInstructionBy(criteria);

    }

    @Override
    public Map getServiceInstructionBy(ServiceInstructionId serviceInstructionId) {

        ServiceInstruction tradeService = load(serviceInstructionId);

        // eagerly load all references
        Hibernate.initialize(tradeService);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(tradeService);
        Map returnClass = gson.fromJson(result, Map.class);

        return returnClass;
    }

    public List<ServiceInstruction> findActiveServiceInstructions(List<ServiceInstructionId> serviceInstructionIdList) {
        Session session  = this.mySessionFactory.getCurrentSession();

        if (serviceInstructionIdList.size() > 0) {
            Criteria criteria = session.createCriteria(ServiceInstruction.class);

            criteria.add(Restrictions.in("serviceInstructionId", serviceInstructionIdList));

            return criteria.list();
        } else {
            return new ArrayList<ServiceInstruction>();
        }
    }

    public Integer getReversal(String serviceInstructionId, String serviceType) {
        System.out.println("getReversal");
        Session session  = this.mySessionFactory.getCurrentSession();

        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("select count(*) from ServiceInstruction si ");
        sqlStatement.append("inner join TradeService ts on ts.serviceInstructionId = si.serviceInstructionId ");
        sqlStatement.append("where si.details like '%" + serviceInstructionId + "%' ");


        sqlStatement.append("and ts.serviceType like '%"+serviceType+"_REVERSAL' and si.status in ('PENDING', 'PREPARED', 'CHECKED', 'APPROVED')");
        //sqlStatement.append("and ts.status !='APPROVED' ");

        System.out.println("angol angol getReversalQuery:"+sqlStatement.toString());
        return (Integer) session.createSQLQuery(sqlStatement.toString()).uniqueResult();
    }

    @Override
    public List<Map<String, Object>> getNextBranchApprovers(String roleId, String unitCode, String lastUser, String currentOwner) {
        Session session  = this.mySessionFactory.getCurrentSession();

        StringBuilder sqlStatement = new StringBuilder();
        // sqlStatement.append("select e.id, e.lastname || ', ' || e.firstname as ename, e.limit, e.level from sec_employee e, sec_user_roles ur ");
        // sqlStatement.append("select distinct e.id, e.lastname || ', ' || e.firstname as ename, e.limit, e.level from sec_employee e, sec_user_roles ur, sec_role r "); // added this
        sqlStatement.append("select distinct e.id, (case when (e.firstname is null or length(e.firstname) = 0) then e.lastname else e.lastname || ', ' || e.firstname end) as ename, e.limit, e.level from sec_employee e, sec_user_roles ur, sec_role r ");
        sqlStatement.append("where lcase(e.id) = lcase(ur.userid) ");
        sqlStatement.append("and ur.roleid = r.id "); // added this
        sqlStatement.append("and (r.id = '" + roleId + "' OR r.parent = '"+ roleId +"') ");
        // sqlStatement.append("and roleid = '" + roleId + "' ");

        sqlStatement.append("and unitcode = '" + unitCode + "' ");
        sqlStatement.append("and lcase(e.id) not in ('" + lastUser.toLowerCase() + "', '" + currentOwner.toLowerCase() + "') ");
        sqlStatement.append("order by level asc");

        Query query = session.createSQLQuery(sqlStatement.toString());

        Iterator it = query.list().iterator();

        List<Map<String, Object>> nextBranchApprovers = new ArrayList<Map<String, Object>>();

        while(it.hasNext()) {
            Object[] obj = (Object[]) it.next();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", obj[0]);
            map.put("ename", obj[1]);
            map.put("limit", obj[2]);
            map.put("level", obj[3]);

            nextBranchApprovers.add(map);
        }

        return nextBranchApprovers;
    }
    
    @Override
    public List<ServiceInstruction> getUnapprovedServiceInstructions() {
        return mySessionFactory.getCurrentSession().createQuery("from com.ucpb.tfs.domain.instruction.ServiceInstruction " +
                "where STATUS = 'CHECKED' OR STATUS = 'PREPARED'").list();
    }
}

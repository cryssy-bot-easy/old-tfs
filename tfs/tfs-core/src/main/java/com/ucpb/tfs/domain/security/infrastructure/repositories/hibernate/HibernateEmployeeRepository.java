package com.ucpb.tfs.domain.security.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.security.Employee;
import com.ucpb.tfs.domain.security.EmployeeRepository;
import com.ucpb.tfs.domain.security.UserId;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
public class HibernateEmployeeRepository implements EmployeeRepository{

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Employee employee) {
        sessionFactory.getCurrentSession().merge(employee);
    }
    

    @Override
    public Employee getEmployee(UserId userId) {
        // return (Employee) this.sessionFactory.getCurrentSession().createQuery(
        //         "from com.ucpb.tfs.domain.security.Employee where userId = :id").setParameter("id", userId).uniqueResult();

        Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(Employee.class);
        crit.add(Restrictions.eq("userId.id", userId).ignoreCase());

        Employee employee = null;
        Object empObj = crit.uniqueResult();
        if (empObj != null && empObj instanceof Employee) {
            employee = (Employee)empObj;
        }

        return employee;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<Map<String,Object>> getEmployeesMatching(String userId, String name) {
        Session session  = this.sessionFactory.getCurrentSession();

        List<Map<String, Object>> employeeList = new ArrayList<Map<String, Object>>();

        StringBuilder queryStatement = new StringBuilder();
        
        queryStatement.append("select emp.id, emp.fullname, user.last_login from sec_employee emp left join sec_user user on emp.id=user.id ");
        
        if (userId != "" || name != ""){
        	queryStatement.append("where ");
        }
        
        if (userId != ""){
        	queryStatement.append("upper(emp.id) like upper('%" + userId + "%') ");
        } 
        
        if (userId != "" && name != ""){
        	queryStatement.append("and ");
        }
        
        if (name != ""){
        	queryStatement.append("upper(emp.fullname) like upper('%" + name + "%')");
        }
        
        Query query = session.createSQLQuery(queryStatement.toString());
        
        Iterator it = query.list().iterator();
        
    	while (it.hasNext()) {
            Object[] obj = (Object[]) it.next();

            Map<String, Object> employeeMap = new HashMap<String, Object>();
            employeeMap.put("userId", obj[0]);
            employeeMap.put("fullName", obj[1]);
            employeeMap.put("lastLogin", obj[2]);

            employeeList.add(employeeMap);
        }
        
        return employeeList;
    }

    @Override
    public Map getEmployeeById(UserId userId) {
        Employee employee = getEmployee(userId);

        // eagerly load all references
        Hibernate.initialize(employee);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(employee);
        Map returnClass = gson.fromJson(result, Map.class);

        returnClass.put("truncatedTellerId", employee.getTruncatedTellerId());

        return returnClass;
    }
}

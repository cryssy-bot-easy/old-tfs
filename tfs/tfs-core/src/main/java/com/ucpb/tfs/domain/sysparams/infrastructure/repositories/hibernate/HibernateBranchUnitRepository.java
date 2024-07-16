package com.ucpb.tfs.domain.sysparams.infrastructure.repositories.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ucpb.tfs.domain.sysparams.BranchUnit;
import com.ucpb.tfs.domain.sysparams.BranchUnitRepository;

@Transactional
public class HibernateBranchUnitRepository implements BranchUnitRepository{
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Override
	public BranchUnit getBranchUnit(String unitCode){
        Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(BranchUnit.class);
        crit.add(Restrictions.eq("unitCode", unitCode).ignoreCase());

        BranchUnit branchUnit = null;
        Object branchUnitObj = crit.uniqueResult();
        
        if (branchUnitObj != null && branchUnitObj instanceof BranchUnit) {
        	branchUnit = (BranchUnit)branchUnitObj;
        }

        return branchUnit;
	}
	
	
	@Override
	public List<Map<String, Object>> getBranchUnit(String unitCode, String unitName) {
		
		Session session  = this.sessionFactory.getCurrentSession();
		
		StringBuilder queryStatement = new StringBuilder();
		
		queryStatement.append("SELECT * FROM REF_BRANCH_UNIT ");
		
		if(!unitCode.equalsIgnoreCase("") || !unitName.equalsIgnoreCase("")) {
			queryStatement.append("WHERE ");
		}
		
		if(!unitCode.equalsIgnoreCase("")) {
			queryStatement.append("UNIT_CODE = '"+ unitCode +"' ");
		}
		
		if(!unitCode.equalsIgnoreCase("") && !unitName.equalsIgnoreCase("")) {
			queryStatement.append("AND ");
		}
		
		if(!unitName.equalsIgnoreCase("")) {
			queryStatement.append("BRANCH_NAME like '%"+ unitName +"%' ");
		}
		
		Query query = session.createSQLQuery(queryStatement.toString());
	        
		List<Map<String, Object>> branchUnitList = new ArrayList<Map<String, Object>>();
	        
	    Iterator it = query.list().iterator();
	    
	    while (it.hasNext()) {
            Object[] obj = (Object[]) it.next();
            Map<String, Object> detailsMap = new HashMap<String, Object>();
            detailsMap.put("unitCode", obj[1] != null ? obj[1] : "");
            detailsMap.put("unitName", obj[2] != null ? obj[2] : "");
            detailsMap.put("address", obj[3] != null ? obj[3] : "");
            detailsMap.put("branchType", obj[4] != null ? obj[4] : "");
            detailsMap.put("swiftStatus", obj[5] != null ? obj[5] : "");
            branchUnitList.add(detailsMap);
	    }
		return branchUnitList;
	}

	@Override
	public void saveOrUpdate(BranchUnit branchUnit) {
        sessionFactory.getCurrentSession().saveOrUpdate(branchUnit);
	}

}

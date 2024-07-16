package com.ucpb.tfs.domain.sysparams.infrastructure.repositories.hibernate;

import com.google.gson.Gson;
import com.ucpb.tfs.domain.sysparams.RefBank;
import com.ucpb.tfs.domain.sysparams.RefBankRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 1/21/13
 */
@Transactional
public class HibernateRefBankRepository implements RefBankRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void persist(RefBank refBank) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(refBank);
    }

    @Override
    public void merge(RefBank refBank) {
        Session session = this.sessionFactory.getCurrentSession();
        session.merge(refBank);
    }

    @Override
    public void update(RefBank refBank) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(refBank);
    }

	@Override
	public List<RefBank> getBanks(String bicCode) {
		Query query = this.sessionFactory.getCurrentSession().createQuery(
				"from com.ucpb.tfs.domain.sysparams.RefBank where bic = ?").setParameter(0, bicCode);
		
		@SuppressWarnings("unchecked")
		List<RefBank> refBanks = query.list();
		
		return refBanks;
	}
	
	@Override
	public List<RefBank> getNullRmaBanks() {
		Query query = this.sessionFactory.getCurrentSession().createQuery(
				"from com.ucpb.tfs.domain.sysparams.RefBank where rmaFlag is null");
		
		@SuppressWarnings("unchecked")
		List<RefBank> refBanks = query.list();
		
		return refBanks;
	}
	
    @Override
    public RefBank getBank(String bic, String branchCode) {

        return (RefBank) this.sessionFactory.getCurrentSession().createQuery(
                "from com.ucpb.tfs.domain.sysparams.RefBank where bic = ? and branchCode = ?").setParameter(0, bic).setParameter(1, branchCode).uniqueResult();
    }

    @Override
    public Map getBankByBicAndBranch(String bic, String branchCode) {

        RefBank refBank = getBank(bic, branchCode);

        // eagerly load all references
        // Hibernate.initialize(refBank);

        // we cannot return the list so we use gson to serialize then deserialize back to a list
        Gson gson = new Gson();

        String result = gson.toJson(refBank);
        Map returnClass = gson.fromJson(result, Map.class);

        return returnClass;
    }

    @Override
    public List<RefBank> getRequestsMatching(String bic, String branchCode, String institutionName, String depositoryFlag) {

        Session session = this.sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(RefBank.class);

        // Query query = session.createQuery("from com.ucpb.tfs.domain.sysparams.RefBank");

        // add criteria if parameter was specified
/*
        if(swiftCode != null && !swiftCode.trim().isEmpty()) {

            String substrBic = null;
            String substrBranchCode = null;

            if (swiftCode.length() > 8) {
                substrBic = swiftCode.substring(0, 8);
                substrBranchCode = swiftCode.substring(8);
            } else if (swiftCode.length() <= 8) {
                substrBic = swiftCode;
            }

            System.out.println("\nsubstrBic = " + substrBic);
            System.out.println("substrBranchCode = " + substrBranchCode + "\n");

            crit.add(Restrictions.eq("bic", substrBic));
            crit.add(Restrictions.eq("branchCode", substrBranchCode));
        }
*/

        if(bic != null && !bic.trim().isEmpty()) {
            // crit.add(Restrictions.eq("bic", bic));
            crit.add(Restrictions.ilike("bic", bic, MatchMode.ANYWHERE));
        }

        if(branchCode != null && !branchCode.trim().isEmpty()) {
            // crit.add(Restrictions.eq("branchCode", branchCode));
            crit.add(Restrictions.ilike("branchCode", branchCode, MatchMode.ANYWHERE));
        }

        if(institutionName != null && !institutionName.trim().isEmpty()) {
            // crit.add(Restrictions.like("institutionName", "%" + institutionName + "%"));
            crit.add(Restrictions.ilike("institutionName", institutionName, MatchMode.ANYWHERE));
        }

        if(depositoryFlag != null && !depositoryFlag.trim().isEmpty()) {
            crit.add(Restrictions.eq("depositoryFlag", depositoryFlag));
        }

        crit.add(Restrictions.or(Restrictions.isNull("deleteFlag"),Restrictions.eq("deleteFlag", "N")));

        
        List refBanks = crit.list();

        return refBanks;
    }

    @Override
    public String getGlCode(String type, String account) {

        String query;
        if(type.equalsIgnoreCase("RBU")){
            query = " select glCodeRbu from com.ucpb.tfs.domain.sysparams.RefBank where rbuAccount = ? ";
        } else {
            query = " select glCodeFcdu from com.ucpb.tfs.domain.sysparams.RefBank where fcduAccount = ? ";
        }

        try {

            //return (String) this.sessionFactory.getCurrentSession().createQuery(query).setParameter(0, account).uniqueResult();
            return (String) this.sessionFactory.getCurrentSession().createQuery(query).setParameter(0, account).uniqueResult();
        } catch (Exception e){
            //e.printStackTrace();

            return (String) this.sessionFactory.getCurrentSession().createQuery(query).setParameter(0, account).list().get(0);
        }

    }
}

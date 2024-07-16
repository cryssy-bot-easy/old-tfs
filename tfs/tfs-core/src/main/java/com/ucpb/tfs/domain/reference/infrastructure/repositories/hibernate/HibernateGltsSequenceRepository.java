package com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.reference.GltsSequenceRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import org.hibernate.Query;
/**
 * User: Giancarlo
 * Date: 2/8/13
 * Time: 4:17 PM
 */

@Transactional
public class HibernateGltsSequenceRepository implements GltsSequenceRepository {

    @Autowired(required = true)
    private SessionFactory sessionFactory;

    @Override
    public String getGltsSequence() {
        Object sequence = sessionFactory.getCurrentSession().createSQLQuery("SELECT SEQUENCE FROM GLTS_SEQUENCE ")
                .uniqueResult();
        String temp = sequence.toString();
        temp = String.format("%6s", temp).replace(' ', '0');
        return temp != null ? temp.toString() : null;
    }

    @Override
    public void incrementGltsSequence() {

        sessionFactory.getCurrentSession().createSQLQuery("UPDATE GLTS_SEQUENCE SET SEQUENCE = (SEQUENCE + 1) ").executeUpdate();
    }

    @Override
    public List<Map<String, Object>> getGetMigrationCif() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("");
        List<Map<String, Object>> cifList = new ArrayList<Map<String, Object>>();

        Iterator it = query.list().iterator();

        while(it.hasNext()) {
            Object[] obj = (Object[]) it.next();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", obj[0]);
            map.put("cifNumber", obj[1]);
            map.put("ccbdBranchUnitCode", obj[2]);
            cifList.add(map);
        }

        return cifList;


    }
}

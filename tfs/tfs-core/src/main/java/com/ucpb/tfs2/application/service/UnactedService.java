package com.ucpb.tfs2.application.service;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Transactional(readOnly = true)
public class UnactedService {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    public List queryUnacted() {

        Session session = this.sessionFactory.getCurrentSession();

        String sql = "SELECT ts.tradeserviceid, t.taskstatus, t.taskowner, ts.tradeservicereferencenumber, " +
                "ts.documentclass || ' ' ||  ts.servicetype transaction_type " +
                " FROM task t, tradeservice ts " +
                "WHERE t.taskreferencenumber = ts.tradeserviceid " +
                "  AND t.taskreferencetype = 'DATA_ENTRY' " +
                "  AND ts.documentClass not in ('LC', 'DA', 'DP', 'DR', 'OA', 'INDEMNITY', 'IMPORT_ADVANCE')" +
                "  AND (t.taskStatus != 'APPROVED' and t.taskStatus != 'MARV')" +
                "  AND t.taskowner = 'jett'";

        Query query=session.createSQLQuery(sql);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String,Object>> results = query.list();

        // List results = session.createSQLQuery("SELECT * FROM Task").list();

        System.out.println(results.size());

        return results;
    }

}

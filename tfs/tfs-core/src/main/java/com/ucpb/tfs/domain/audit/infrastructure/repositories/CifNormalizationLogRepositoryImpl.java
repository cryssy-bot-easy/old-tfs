package com.ucpb.tfs.domain.audit.infrastructure.repositories;

import com.ucpb.tfs.domain.audit.CifNormalizationLog;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/22/14
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */

@Transactional
public class CifNormalizationLogRepositoryImpl implements CifNormalizationLogRepository {

    @Autowired(required=true)
    private SessionFactory sessionFactory;

    @Override
    public void persist(CifNormalizationLog cifNormalizationLog) {
        Session session = this.sessionFactory.getCurrentSession();
        session.persist(cifNormalizationLog);
    }

    @Override
    public List<CifNormalizationLog> getLogMatching(String oldCifNumber,
                                                    String oldCifName,
                                                    String newCifNumber,
                                                    String newCifName,
                                                    String oldMainCifNumber,
                                                    String oldMainCifName,
                                                    String newMainCifNumber,
                                                    String newMainCifName,
                                                    BigDecimal oldFacilityId,
                                                    BigDecimal newFacilityId,
                                                    Date normalizationDate) {

        Session session = this.sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(CifNormalizationLog.class);

        if (oldCifNumber != null && StringUtils.isNotBlank(oldCifNumber)) {
            criteria.add(Restrictions.eq("oldCifNumber", oldCifNumber));
        }

        if (oldCifName != null && StringUtils.isNotBlank(oldCifName)) {
            criteria.add(Restrictions.eq("oldCifName", oldCifName));
        }

        if (newCifNumber != null && StringUtils.isNotBlank(newCifNumber)) {
            criteria.add(Restrictions.eq("newCifNumber", newCifNumber));
        }

        if (newCifName != null && StringUtils.isNotBlank(newCifName)) {
            criteria.add(Restrictions.eq("newCifName", newCifName));
        }

        if (oldMainCifNumber != null && StringUtils.isNotBlank(oldMainCifNumber)) {
            criteria.add(Restrictions.eq("oldMainCifNumber", oldMainCifNumber));
        }

        if (oldMainCifName != null && StringUtils.isNotBlank(oldMainCifName)) {
            criteria.add(Restrictions.eq("oldMainCifName", oldMainCifName));
        }

        if (newMainCifNumber != null && StringUtils.isNotBlank(newMainCifNumber)) {
            criteria.add(Restrictions.eq("newMainCifNumber", newMainCifNumber));
        }

        if (newMainCifName != null && StringUtils.isNotBlank(newMainCifName)) {
            criteria.add(Restrictions.eq("newMainCifName", newMainCifName));
        }

        if (oldFacilityId != null) {
            criteria.add(Restrictions.eq("oldFacilityId", oldFacilityId));
        }

        if (newFacilityId != null) {
            criteria.add(Restrictions.eq("newFacilityId", newFacilityId));
        }

        if (normalizationDate != null) {
            Map<String, Date> minMaxDate = getMinMaxDate(normalizationDate);

            criteria.add(Restrictions.between("normalizationDate", minMaxDate.get("minDate"), minMaxDate.get("maxDate")));
        }

        return criteria.list();

    }

    private Map<String, Date> getMinMaxDate(Date dateToMinMax) {
        Map<String, Date> minMaxDate = new HashMap<String, Date>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateToMinMax);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        minMaxDate.put("minDate", calendar.getTime());

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(calendar.getTime());

        tomorrow.add(Calendar.DATE, 1);
        tomorrow.add(Calendar.MILLISECOND, -1);

        minMaxDate.put("maxDate", tomorrow.getTime());

        return minMaxDate;
    }

    @Override
    public List<CifNormalizationLog> getAllLogs() {
        Session session = this.sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(CifNormalizationLog.class);

        return criteria.list();
    }
}

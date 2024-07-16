package com.ucpb.tfs.domain.service.infrastructure.repositories.hibernate;

import com.ucpb.tfs.domain.service.ChargesParameter;
import com.ucpb.tfs.domain.service.ChargesParameterRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *    Created by: Cedrick C. Nungay
 *    Details: Implentation of ChargesParameterRepository.
 *    Date created: 02/01/2018
*/
@Transactional
public class HibernateChargesParameterRepository implements ChargesParameterRepository {

    @Autowired(required=true)
    private SessionFactory mySessionFactory;

    @Override
    public List<ChargesParameter> load(Map<String, Object> extendedPropertiesMap) {
        String documentType = (String) extendedPropertiesMap.get("documentType");
        String documentClass = (String) extendedPropertiesMap.get("documentClass");
        String documentSubType1 = (String) extendedPropertiesMap.get("documentSubType1");
        String documentSubType2 = (String) extendedPropertiesMap.get("documentSubType2");
        String serviceType = (String) extendedPropertiesMap.get("serviceType");
        return this.mySessionFactory.getCurrentSession().createQuery(
                "FROM com.ucpb.tfs.domain.service.ChargesParameter "
                        + "WHERE PARAM_DOC_TYPE = :PARAM_DOC_TYPE"
                        + "    AND PARAM_DOC_CLASS = :PARAM_DOC_CLASS"
                        + "    AND PARAM_DOC_SUBTYPE1 = :PARAM_DOC_SUBTYPE1"
                        + "    AND PARAM_DOC_SUBTYPE2 = :PARAM_DOC_SUBTYPE2"
                        + "    AND PARAM_SERVICE_TYPE = :PARAM_SERVICE_TYPE ")
                        .setParameter("PARAM_DOC_TYPE", (documentType == null) ? "" : documentType.toString())
                        .setParameter("PARAM_DOC_CLASS", (documentClass == null) ? "" : documentClass.toString())
                        .setParameter("PARAM_DOC_SUBTYPE1", (documentSubType1 == null) ? "" : documentSubType1.toString())
                        .setParameter("PARAM_DOC_SUBTYPE2", (documentSubType2 == null) ? "" : documentSubType2.toString())
                        .setParameter("PARAM_SERVICE_TYPE", (serviceType == null) ? "" : serviceType.toString())
                        .list();
    }

    @Override
    public Map<String, String> getParameters(Map<String, Object> extendedPropertiesMap) {
        List<ChargesParameter> records = load(extendedPropertiesMap);
        Map<String, String> parameters = new HashMap<String, String>();
        for (ChargesParameter cp : records) {
            parameters.put(cp.getParameterName(), cp.getParameterValue());
        }
        return parameters;
    }
}

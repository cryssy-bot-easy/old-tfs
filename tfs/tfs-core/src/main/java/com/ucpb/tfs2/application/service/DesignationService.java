package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.security.Designation;
import com.ucpb.tfs.domain.security.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 12/27/13
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */

@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class DesignationService {

    @Autowired
    DesignationRepository designationRepository;

    public void saveDesignation(Map parameter) {
        Designation designation = new Designation((String) parameter.get("description"),
                new Long((String) parameter.get("level")));

        designationRepository.merge(designation);
    }

    public List<Designation> searchDesignation(Map parameter) {

        List<Designation> designationList = designationRepository.getAllDesignationMatching((String) parameter.get("description"));

        return designationList;
    }

}

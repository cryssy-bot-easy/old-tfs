package com.ucpb.tfs.domain.security;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 12/27/13
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DesignationRepository {

    public List<Designation> getAllDesignationMatching(String description);

    public Designation load(Long id);

    public void merge(Designation designation);

}

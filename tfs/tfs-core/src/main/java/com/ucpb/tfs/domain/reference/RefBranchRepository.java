package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.reference.Branch;

/**
 */
public interface RefBranchRepository {


    public Branch getBranchById(Long id);

    public Branch getBranchByUnitCode(String unitCode);

    public void persist(Branch branch);

}

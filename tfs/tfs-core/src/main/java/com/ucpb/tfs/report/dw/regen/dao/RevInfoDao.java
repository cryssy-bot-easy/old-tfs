package com.ucpb.tfs.report.dw.regen.dao;

import com.ucpb.tfs.report.dw.regen.RevInfo;

/**
 * User: IPCVal
 */
public interface RevInfoDao {

    int insert(RevInfo record);

    Integer selectMaxRev();
}

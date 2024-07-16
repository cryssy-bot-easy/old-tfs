package com.ucpb.tfs.batch.report.dw.service;

import com.ucpb.tfs.batch.report.dw.MasterFileRecord;

import java.util.List;

/**
 */
public interface MasterFileService {

//    public List<MasterFileRecord> getMasterFiles();

    public List<MasterFileRecord> getMasterFiles(String appDate);
    
    public List<MasterFileRecord> getMasterFilesException(String appDate);
}

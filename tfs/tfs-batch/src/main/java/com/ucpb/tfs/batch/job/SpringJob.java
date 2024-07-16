package com.ucpb.tfs.batch.job;

import java.io.Serializable;

/**
 */
public interface SpringJob extends Serializable {

    public void execute() throws Exception;

    public void execute(String reportDate) throws Exception;

}

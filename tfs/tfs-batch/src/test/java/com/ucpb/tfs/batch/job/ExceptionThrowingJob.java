package com.ucpb.tfs.batch.job;

/**
 */
public class ExceptionThrowingJob implements SpringJob {

    @Override
    public void execute() {
        throw new RuntimeException("An exception was intentionally thrown (obviously)");
    }

    @Override
    public void execute(String reportDate) {
        //TODO
        throw new RuntimeException("An exception was intentionally thrown (obviously)!"+reportDate);
    }
}

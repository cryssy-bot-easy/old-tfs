package com.ucpb.tfs.domain.cdt;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/23/14
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CDTReportControlReferenceRepository {

    public void persist(CDTReportControlReference cdtReportControlReference);

    public void merge(CDTReportControlReference cdtReportControlReference);

    public CDTReportControlReference load(String unitCode);

}

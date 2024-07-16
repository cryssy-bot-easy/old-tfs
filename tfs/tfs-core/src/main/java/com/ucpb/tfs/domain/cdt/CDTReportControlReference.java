package com.ucpb.tfs.domain.cdt;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/23/14
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class CDTReportControlReference implements Serializable {

    private Long id;

    private String firstInitial;

    private String unitCode;

    private String shortCode;


    public CDTReportControlReference() {}

    private CDTReportControlReference(String unitCode, String firstInitial, String shortCode) {
        this.unitCode = unitCode;
        this.firstInitial = firstInitial;
        this.shortCode = shortCode;
    }

    public String getFirstInitial() {
        return firstInitial;
    }

    public String getShortCode() {
        return shortCode;
    }

}

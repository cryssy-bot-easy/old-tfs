package com.ucpb.tfs.domain.audit;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/22/14
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class CifDetails {

    private String oldCifNumber;
    private String oldCifName;

    private String newCifNumber;
    private String newCifName;

    public CifDetails() {}

    public CifDetails(String oldCifNumber, String oldCifName, String newCifNumber, String newCifName) {
        this.oldCifNumber = oldCifNumber;
        this.oldCifName = oldCifName;

        this.newCifNumber = newCifNumber;
        this.newCifName = newCifName;
    }

    public String getOldCifNumber() {
        return oldCifNumber;
    }

    public String getOldCifName() {
        return oldCifName;
    }

    public String getNewCifNumber() {
        return newCifNumber;
    }

    public String getNewCifName() {
        return newCifName;
    }
}

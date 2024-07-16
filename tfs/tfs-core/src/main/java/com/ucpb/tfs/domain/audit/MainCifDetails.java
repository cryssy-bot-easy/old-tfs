package com.ucpb.tfs.domain.audit;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/22/14
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainCifDetails {

    private String oldMainCifNumber;
    private String oldMainCifName;

    private String newMainCifNumber;
    private String newMainCifName;

    public MainCifDetails() {}

    public MainCifDetails(String oldMainCifNumber, String oldMainCifName, String newMainCifNumber, String newMainCifName) {
        this.oldMainCifNumber = oldMainCifNumber;
        this.oldMainCifName = oldMainCifName;

        this.newMainCifNumber = newMainCifNumber;
        this.newMainCifName = newMainCifName;
    }

    public String getOldMainCifNumber() {
        return oldMainCifNumber;
    }

    public String getOldMainCifName() {
        return oldMainCifName;
    }

    public String getNewMainCifNumber() {
        return newMainCifNumber;
    }

    public String getNewMainCifName() {
        return newMainCifName;
    }
    
}

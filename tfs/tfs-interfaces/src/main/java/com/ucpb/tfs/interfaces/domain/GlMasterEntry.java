package com.ucpb.tfs.interfaces.domain;

/**
 * User: Giancarlo
 * Date: 2/11/13
 * Time: 4:50 PM
 */
public class GlMasterEntry {

    String acctNo; //ACCTNO
    String gmcTyp; //GMCTYP
    String bookCode; //BOOKCD
    String title; //TITLE
    String shortT; //SHORTT
    String acType; //ACTYPE
    String branch; //BRANCH
    String valPst; //VALPST

    public GlMasterEntry() {

    }

    public GlMasterEntry(String acctNo, String gmcTyp, String bookCode, String title, String shortT, String acType, String branch, String valPst) {
        this.acctNo = acctNo;
        this.gmcTyp = gmcTyp;
        this.bookCode = bookCode;
        this.title = title;
        this.shortT = shortT;
        this.acType = acType;
        this.branch = branch;
        this.valPst = valPst;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getGmcTyp() {
        return gmcTyp;
    }

    public void setGmcTyp(String gmcTyp) {
        this.gmcTyp = gmcTyp;
    }

    public String getBookCode() {
        return bookCode;
    }

    public void setBookCode(String bookCode) {
        this.bookCode = bookCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortT() {
        return shortT;
    }

    public void setShortT(String shortT) {
        this.shortT = shortT;
    }

    public String getAcType() {
        return acType;
    }

    public void setAcType(String acType) {
        this.acType = acType;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getValPst() {
        return valPst;
    }

    public void setValPst(String valPst) {
        this.valPst = valPst;
    }
}

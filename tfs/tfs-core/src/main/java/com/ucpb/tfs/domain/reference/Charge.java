package com.ucpb.tfs.domain.reference;

import java.io.Serializable;

/**
 * User: Jett
 * Date: 7/12/12
 */
public class Charge implements Serializable {

    private ChargeId chargeId;

    private String description;

    private String link;

    private String displayName;

    private String cilexFlag;

    private String formulaName;

    public Charge() {
    }

    public Charge(ChargeId chargeId, String description, String link, String displayName, String cilexFlag) {
        this.chargeId = chargeId;
        this.description = description;
        this.link = link;
        this.displayName = displayName;
        this.cilexFlag = cilexFlag;
    }

    public Charge(String chargeId, String description) {

        this.chargeId = new ChargeId(chargeId);
        this.description = description;
        this.link = "";
        this.displayName = "";

    }

    public ChargeId getChargeId() {
        return chargeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCilexFlag() {
        return cilexFlag;
    }

    public void setCilexFlag(String cilexFlag) {
        this.cilexFlag = cilexFlag;
    }

    public String getFormulaName() {
        return formulaName;
    }

    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
    }
}

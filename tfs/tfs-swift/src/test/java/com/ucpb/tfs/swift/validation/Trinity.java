package com.ucpb.tfs.swift.validation;

/**
 */
@DependentField.List(
        {
        @DependentField(dependentField = "son", targetFields = {"father","holySpirit"}),
        @DependentField(dependentField = "father", targetFields = {"son","holySpirit"})
        }

)
public class Trinity {

    private String father;
    private String son;
    private String holySpirit;

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getSon() {
        return son;
    }

    public void setSon(String son) {
        this.son = son;
    }

    public String getHolySpirit() {
        return holySpirit;
    }

    public void setHolySpirit(String holySpirit) {
        this.holySpirit = holySpirit;
    }
}

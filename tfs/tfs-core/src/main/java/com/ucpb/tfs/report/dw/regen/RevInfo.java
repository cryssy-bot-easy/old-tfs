package com.ucpb.tfs.report.dw.regen;

import java.io.Serializable;

/**
 * User: IPCVal
 */
public class RevInfo implements Serializable {

    private Integer rev;
    private Long revtstmp;

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    public Long getRevtstmp() {
        return revtstmp;
    }

    public void setRevtstmp(Long revtstmp) {
        this.revtstmp = revtstmp;
    }
}

package com.ucpb.tfs.domain.audit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/22/14
 * Time: 4:02 PM
 * To change this template use File | Settings | File Templates.
 */

public class CifNormalizationLog implements Serializable {

    private Long id;

    private CifDetails cifDetails;

    private MainCifDetails mainCifDetails;

    private BigDecimal oldFacilityId;
    private BigDecimal newFacilityId;

    private Date normalizationDate;

    public CifNormalizationLog() {}

    public CifNormalizationLog(Date normalizationDate, CifDetails cifDetails, MainCifDetails mainCifDetails,
                               BigDecimal oldFacilityId, BigDecimal newFacilityId) {
        this.cifDetails = cifDetails;
        this.mainCifDetails = mainCifDetails;

        this.oldFacilityId = oldFacilityId;
        this.newFacilityId = newFacilityId;

        this.normalizationDate = normalizationDate;
    }

    public CifDetails getCifDetails() {
        return cifDetails;
    }

    public MainCifDetails getMainCifDetails() {
        return mainCifDetails;
    }

    public BigDecimal getOldFacilityId() {
        return oldFacilityId;
    }

    public BigDecimal getNewFacilityId() {
        return newFacilityId;
    }

    public Date getNormalizationDate() {
        return normalizationDate;
    }
}

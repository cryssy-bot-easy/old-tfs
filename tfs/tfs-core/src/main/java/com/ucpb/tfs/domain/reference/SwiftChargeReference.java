package com.ucpb.tfs.domain.reference;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: Marv
 * Date: 11/28/12
 */

public class SwiftChargeReference implements Serializable {

    private Long id;

    private String code;
    
    private String description;

    public SwiftChargeReference() {}
    
    public SwiftChargeReference(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

}

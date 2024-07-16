package com.ucpb.tfs.domain.reference;

import org.apache.commons.lang.Validate;

/**
 * User: Marv
 * Date: 11/10/12
 */

public class FormatCode {

    private String formatCode;

    public FormatCode() {}

    public FormatCode(final String formatCode) {
        Validate.notNull(formatCode);
        this.formatCode = formatCode;
    }

    @Override
    public String toString() {
        return formatCode;
    }

}

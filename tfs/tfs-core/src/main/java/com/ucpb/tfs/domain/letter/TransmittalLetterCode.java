
package com.ucpb.tfs.domain.letter;

import java.io.Serializable;
import org.apache.commons.lang.Validate;


/**
 * User: Marv
 * Date: 11/7/12
 */

public class TransmittalLetterCode implements Serializable {

    private String transmittalLetterCode;

    public TransmittalLetterCode() {}

    public TransmittalLetterCode(final String documentCode) {
        Validate.notNull(documentCode);
        this.transmittalLetterCode = documentCode;
    }

    @Override
    public String toString() {
        return transmittalLetterCode;
    }

}

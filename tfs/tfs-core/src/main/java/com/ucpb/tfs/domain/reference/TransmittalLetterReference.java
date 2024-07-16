package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.letter.TransmittalLetterCode;

import java.io.Serializable;

/**
 * User: Marv
 * Date: 11/7/12
 */

public class TransmittalLetterReference implements Serializable {
    
    private Long id;
    
    private TransmittalLetterCode transmittalLetterCode;
    
    private String letterDescription;
    
    public TransmittalLetterReference() {}
    
    public TransmittalLetterReference(TransmittalLetterCode transmittalLetterCode, String letterDescription) {
        this.transmittalLetterCode = transmittalLetterCode;
        this.letterDescription = letterDescription;
    }
    
}

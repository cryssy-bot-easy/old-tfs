package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.letter.TransmittalLetterCode;

import java.util.List;

/**
 * User: Marv
 * Date: 11/7/12
 */

public interface TransmittalLetterReferenceRepository {

    public void save(TransmittalLetterReference transmittalLetterReference);

    public TransmittalLetterReference load(TransmittalLetterCode transmittalLetterCode);

    public List<TransmittalLetterReference> getTransmittalLetter();

    public void clear();

}

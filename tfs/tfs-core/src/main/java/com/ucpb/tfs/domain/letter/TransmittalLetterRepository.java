package com.ucpb.tfs.domain.letter;

/**
 * User: Marv
 * Date: 11/7/12
 */

public interface TransmittalLetterRepository {

    public void persist(TransmittalLetter transmittalLetter);

    public void merge(TransmittalLetter transmittalLetter);

    public void update(TransmittalLetter transmittalLetter);

    public TransmittalLetter load(TransmittalLetterCode transmittalLetterCode);

}

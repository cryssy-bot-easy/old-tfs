package com.ucpb.tfs.interfaces.services.impl;

import com.ucpb.tfs.interfaces.domain.Sequence;
import com.ucpb.tfs.interfaces.repositories.SequenceRepository;
import com.ucpb.tfs.interfaces.services.SequenceService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class SequenceServiceImpl implements SequenceService{


    private static final String LOAN = "LOAN";
    private static final String BALANCE = "BALANCE";
    private SequenceRepository sequenceRepository;

    //TODO: perform sequence resetting logic here
    @Override
    public long getLoanSequence() {
        return getSequence(LOAN);
    }

    @Override
    public long getFacilityBalanceSequence() {
        return getSequence(BALANCE);
    }


    public void setSequenceRepository(SequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    private long getSequence(String type){
        Sequence sequence = sequenceRepository.getSequence(type);
        Assert.notNull(sequence,"Sequence type: " + type + " has not been defined in the database!");

        sequenceRepository.incrementSequence(type);
        return sequence.getSequenceNumber();
    }
}

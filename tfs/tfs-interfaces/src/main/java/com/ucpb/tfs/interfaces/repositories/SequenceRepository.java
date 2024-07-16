package com.ucpb.tfs.interfaces.repositories;

import com.ucpb.tfs.interfaces.domain.Sequence;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 */
public interface SequenceRepository {


    public Sequence getSequence(@Param("sequenceType")String sequenceType);

    public void updateLoanSequence(@Param("sequence")Sequence sequence);

    public void incrementSequence(@Param("sequenceType")String sequenceType);

    public void resetSequence(@Param("sequenceType")String sequenceType);

}

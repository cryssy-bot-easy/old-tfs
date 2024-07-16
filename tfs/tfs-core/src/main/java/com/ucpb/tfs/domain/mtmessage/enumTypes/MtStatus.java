package com.ucpb.tfs.domain.mtmessage.enumTypes;

/**
 * User: Marv
 * Date: 10/10/12
 */

public enum MtStatus {

    NEW, // new MT
    UPDATED, // updated MT
    PREPARED, // routed MT
    DONE, // closed MT
    RETURNED,
    TRANSMITTED, // returned to RSD-Cable Section
    DISCARDED;

}

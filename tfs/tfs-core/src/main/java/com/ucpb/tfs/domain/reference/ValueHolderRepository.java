package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1;
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType2;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;

/**
 * User: giancarlo
 * Date: 10/10/12
 * Time: 1:20 PM
 */
public interface ValueHolderRepository {

    public void save(ValueHolder valueHolder);

    public ValueHolder find(String token);

    public Long getCount();

    public void clear();

}

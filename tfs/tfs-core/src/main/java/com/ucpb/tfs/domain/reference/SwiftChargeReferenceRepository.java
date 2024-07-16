package com.ucpb.tfs.domain.reference;

import java.util.List;

/**
 * User: Marv
 * Date: 11/28/12
 */

public interface SwiftChargeReferenceRepository {

    public void save(SwiftChargeReference swiftChargeReference);

    public SwiftChargeReference load(String code);

    public List<SwiftChargeReference> getSwiftChargeReferences();

    public void clear();

}

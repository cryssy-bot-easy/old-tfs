package com.ucpb.tfs.domain.reference;

import java.util.List;
import java.util.Map;

/**
 * User: giancarlo
 * Date: 2/8/13
 * Time: 4:10 PM
 */
public interface GltsSequenceRepository {
    public String getGltsSequence();

    public void incrementGltsSequence();

    public List<Map<String, Object>> getGetMigrationCif();
}

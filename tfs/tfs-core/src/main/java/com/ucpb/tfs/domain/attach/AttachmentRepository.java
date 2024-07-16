package com.ucpb.tfs.domain.attach;

import java.util.Map;

/**
 * User: IPCVal
 */
public interface AttachmentRepository {

    public Map<String, Object> getAttachmentDetailsMap(Long id);

    public int delete(Long id);
}

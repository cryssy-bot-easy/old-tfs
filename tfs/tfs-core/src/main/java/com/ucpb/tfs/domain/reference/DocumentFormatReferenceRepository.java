package com.ucpb.tfs.domain.reference;

/**
 * User: Marv
 * Date: 11/10/12
 */

public interface DocumentFormatReferenceRepository {

    public void save(DocumentFormatReference documentFormatReference);

    public DocumentFormatReference load(FormatCode formatCode);

}

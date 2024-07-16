package com.ucpb.tfs.application.query.reference;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: Marv
 * Date: 11/10/12
 */

public interface IDocumentFormatReferenceFinder {

    List<Map<String, Object>> findAllDocumentFormat();
    
    List<Map<String, Object>> findAllDocumentFormatByTagging(@Param("tagging") String tagging);
    
    Map<String, Object> findAllDocumentFormatByFormatCode(@Param("formatCode") String formatCode);

}

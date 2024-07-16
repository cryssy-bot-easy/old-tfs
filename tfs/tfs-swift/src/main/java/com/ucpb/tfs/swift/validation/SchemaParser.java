package com.ucpb.tfs.swift.validation;

import java.io.IOException;
import java.util.Map;

/**
 */
public interface SchemaParser {

    public Map<String,Constraint> parseSchema(String url) throws IOException;

}

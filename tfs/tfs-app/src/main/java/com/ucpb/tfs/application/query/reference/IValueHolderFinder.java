package com.ucpb.tfs.application.query.reference;

import java.util.Map;

/**
 * User: giancarlo
 * Date: 10/10/12
 * Time: 1:29 PM
 */
public interface IValueHolderFinder {

    public Map<String, ?> getValue(String token);

}

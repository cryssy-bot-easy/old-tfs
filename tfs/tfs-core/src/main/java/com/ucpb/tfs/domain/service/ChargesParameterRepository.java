package com.ucpb.tfs.domain.service;

import com.ucpb.tfs.domain.service.ChargesParameter;
import java.util.List;
import java.util.Map;

/**
 * Created by: Cedrick C. Nungay
 * Details: Repository for Charges Parameter.
 * Date created: 02/01/2018
*/
public interface ChargesParameterRepository {
    public List<ChargesParameter> load(Map<String, Object> extendedPropertiesMap);

    public Map<String, String> getParameters(Map<String, Object> extendedPropertiesMap);
}

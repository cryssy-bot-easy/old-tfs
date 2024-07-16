package com.ucpb.tfs.application.query.routing;

import com.incuventure.cqrs.query.Finder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 */
@Finder
public interface IRoutingInformationFinder {

    public List<Map<String,?>> findRoutesByRoutingInformationId(@Param("routingInformationId")String routingInformationId);
    public List<Map<String,?>> findNextRouteTarget(@Param("roleId") String roleId, @Param("unitCode") String unitCode);
    public List<Map<String,?>> findProductServiceRoute(@Param("documentClass") String documentClass, @Param("documentType") String documentType, @Param("documentSubType1") String documentSubtype1, @Param("documentSubType2") String documentSubType2, @Param("groupName") String groupName, @Param("serviceType") String serviceType);
    public List<Map<String,?>> findRoutableTo(@Param("documentClass") String documentClass, @Param("documentType") String documentType, @Param("documentSubType1") String documentSubtype1, @Param("documentSubType2") String documentSubType2, @Param("serviceType") String serviceType, @Param("unitCode") String unitCode, @Param("exceptions") String[] exceptions);
    public List<Map<String,?>> getProductServiceAttributes(@Param("documentClass") String documentClass, @Param("documentType") String documentType, @Param("documentSubType1") String documentSubtype1, @Param("documentSubType2") String documentSubType2, @Param("serviceType") String serviceType);
    public Map<String,?> getUser(@Param("username") String username, @Param("unitCode") String unitCode);

    public List<Map<String,Object>> getAllTsdMakers();
    
    public List<Map<String,Object>> getHigherUserHierarchySize(@Param("level") Double level);
    
    public List<Map<String,?>> findNextBranchRouteTarget(@Param("username") String username);
}

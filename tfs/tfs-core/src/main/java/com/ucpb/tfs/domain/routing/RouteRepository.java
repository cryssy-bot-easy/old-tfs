package com.ucpb.tfs.domain.routing;

import java.util.List;

/**
 * User: IPCVal
 * Date: 10/7/13
 */
public interface RouteRepository {

    public List<Route> getAllRoutes(RoutingInformationId routingInformationId);
}

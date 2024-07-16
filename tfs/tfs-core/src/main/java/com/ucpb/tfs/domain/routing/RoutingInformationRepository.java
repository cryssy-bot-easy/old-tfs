package com.ucpb.tfs.domain.routing;

/**
 */
public interface RoutingInformationRepository {

    public void addRoutingInformation(Route route,RoutingInformationId id);

    public RoutingInformation getRoutingInformation(RoutingInformationId routingInformationId);
}

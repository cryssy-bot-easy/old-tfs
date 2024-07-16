package com.ucpb.tfs.domain.routing;

import com.incuventure.ddd.domain.annotations.DomainAggregateRoot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Jett
 * Date: 6/28/12
 */
@DomainAggregateRoot
public class RoutingInformation implements Serializable {

    private RoutingInformationId routingInformationId;

    private List<Route> routes = new ArrayList<Route>();


    public RoutingInformation(){

    }

    public RoutingInformation(RoutingInformationId routingInformationId){
        this.routingInformationId = routingInformationId;
    }

    public RoutingInformationId getRoutingInformationId() {
        return routingInformationId;
    }

    public void setRoutingInformationId(RoutingInformationId routingInformationId) {
        this.routingInformationId = routingInformationId;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void addRoute(Route route){
        this.routes.add(route);
    }

}

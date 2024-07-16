package com.ucpb.tfs.domain.routing;

import org.apache.commons.lang.Validate;

import java.io.Serializable;

/**
 * User: IPCVal
 * Date: 8/10/12
 */
public class RoutingInformationId implements Serializable {

    private String routingInformationId;

    public RoutingInformationId() {}

    public RoutingInformationId(final String routingInformationId) {
        Validate.notNull(routingInformationId);
        this.routingInformationId = routingInformationId;
    }

    public String getRoutingInformationId() {
        return routingInformationId;
    }

    @Override
    public String toString() {
        return routingInformationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoutingInformationId that = (RoutingInformationId) o;

        if (routingInformationId != null ? !routingInformationId.equals(that.routingInformationId) : that.routingInformationId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return routingInformationId != null ? routingInformationId.hashCode() : 0;
    }
}

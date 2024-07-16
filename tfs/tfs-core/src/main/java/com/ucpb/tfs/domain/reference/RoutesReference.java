package com.ucpb.tfs.domain.reference;


import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

public class RoutesReference {

    private Long id;

    private ProductId productId;
    private ServiceType serviceType;

    private String group;
    private String route;

    public RoutesReference() {
    }

}

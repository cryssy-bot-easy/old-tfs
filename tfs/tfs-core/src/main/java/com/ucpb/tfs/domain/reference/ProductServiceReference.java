package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.reference.enumTypes.PostApprovalRequirement;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.io.Serializable;

public class ProductServiceReference implements Serializable {

    private Long productServiceId;

    // UCPB Product Id
    private ProductId productId;

    private ServiceType serviceType;

    private Boolean financial;

    private Integer branchApprovalRequiredCount;

    private PostApprovalRequirement postApprovalRequirement;

    private String documentCode;

    public ProductServiceReference() {
    }

    public void setProductId(ProductId productId) {
        this.productId = productId;
    }

    public ProductId getProductId() {
        return productId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }
}

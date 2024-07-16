package com.ucpb.tfs2.application.service;

import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.ProductServiceReference;
import com.ucpb.tfs.domain.reference.ProductServiceReferenceRepository;
import com.ucpb.tfs.utils.UtilSetFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * User: IPCJon
 * Date: 01/24/13
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class RefProductServiceService {

    @Autowired
    ProductServiceReferenceRepository refProductServiceRepository;

    public void saveRefProductServiceDetail(Map parameters) throws Exception {

        System.out.println("productServiceId: " + parameters.get("productServiceId"));
        System.out.println("productId: " + parameters.get("productId"));
        System.out.println("serviceType: " + parameters.get("serviceType"));
        System.out.println("financial: " + parameters.get("financial"));
        System.out.println("branchApprovalRequiredCount: " + parameters.get("branchApprovalRequiredCount"));
        System.out.println("postApprovalRequirement: " + parameters.get("postApprovalRequirement"));

        ProductServiceReference refProductService = new ProductServiceReference();
        UtilSetFields.copyMapToObject(refProductService, (HashMap<String, Object>) parameters);

        // Set productId separately
        String productIdStr = (String)parameters.get("productId");
        ProductId productId = new ProductId(productIdStr);
        refProductService.setProductId(productId);

        refProductServiceRepository.merge(refProductService);
    }
}
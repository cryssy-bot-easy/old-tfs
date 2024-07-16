package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.service.enumTypes.ServiceType;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: IPC Jon
 * Date: 1/25/13
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProductServiceReferenceRepository {

    public void persist(ProductServiceReference refProductService);

    public void merge(ProductServiceReference refProductService);

    public void update(ProductServiceReference refProductService);

    public ProductServiceReference getProductService(Long productServiceId);

    public ProductServiceReference getProductService(ProductId productId, ServiceType serviceType);

    public Map getProductServiceById(Long productServiceId);

    public List<ProductServiceReference> getRequestsMatching(String productId, String serviceType);
}
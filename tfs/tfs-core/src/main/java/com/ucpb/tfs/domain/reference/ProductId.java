package com.ucpb.tfs.domain.reference;

import org.apache.commons.lang.Validate;
import org.hibernate.type.StringType;

import java.io.Serializable;

/**
 * User: Jett
 * Date: 7/24/12
 */
public class ProductId extends StringType implements Serializable {

    private String productId;

    public ProductId() {}

    public ProductId(final String productId) {
        Validate.notNull(productId);
        this.productId = productId;
    }

    @Override
    public String toString() {
        return this.productId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductId that = (ProductId) o;

        if (productId != null ? !productId.equals(that.productId) : that.productId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }
}

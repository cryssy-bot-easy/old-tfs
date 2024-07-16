package com.ucpb.tfs.batch.report.dw;

import com.ancientprogramming.fixedformat4j.annotation.Field;
import com.ancientprogramming.fixedformat4j.annotation.Record;

/**
 */
@Record
public class DWParameterRecord {

    private Long id;

//    private Integer field;
    private String field;

    private String productId;

    private String description;

    private String type;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    @Field(offset = 1, length = 30)
//    public Integer getField() {
//        return field;
//    }
//
//    public void setField(Integer field) {
//        this.field = field;
//    }

    @Field(offset = 1, length = 30)
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Field(offset = 31, length = 15)
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Field(offset = 46, length = 30)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

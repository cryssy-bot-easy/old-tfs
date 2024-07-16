package com.ucpb.tfs.domain.security;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 12/27/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Designation implements Serializable {

    private Long id;

    private String description;

    private Long level;

    public Designation() {}

    public Designation(String description, Long level) {
        this.description = description;
        this.level = level;
    }

    public Long getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

}

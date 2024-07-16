package com.ucpb.tfs.utils;

import org.springframework.beans.factory.config.AbstractFactoryBean;

public class MyFactoryBean extends AbstractFactoryBean {

    private Class targetClass;

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    protected Object createInstance() throws Exception {
        return targetClass.newInstance();
    }

    @Override
    public Class getObjectType() {
        return targetClass;
    }

}

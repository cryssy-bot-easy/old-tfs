package org.apache;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static junit.framework.Assert.assertEquals;

/**
 */
public class BeanUtilsTest {


    @Test
    public void successfullyGetWidthProperty() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth("123");

        assertEquals("123", BeanUtils.getProperty(rectangle,"width"));

    }


}

package com.ucpb.tfs.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Jett
 * Date: 8/16/12
 */
public class DomainFieldUtils {

    public static Map<String, Object> getFieldChoices(Class clazz) {

        Method method;

        Map<String, Object> lookups = new HashMap<String, Object>();

        // get a reference to all the fields of this class
        Field[] fields = clazz.getDeclaredFields();

        for(Field field : fields) {
            String fieldName = field.getName();
            Class fieldClass = field.getType();

            // only return value lists of Enum Types
            if(fieldClass.isEnum()) {

                Map<String, String> enumValues = new HashMap<String, String>();

                // check if our enum has a getDisplayText method, if there is, remember this method
                // so we can use it to get the display value for the Enum
                try {
                    method =  fieldClass.getMethod("getDisplayText");
                }
                catch(NoSuchMethodException nsme) {
                    method = null;
                }

                for(Object constant :  fieldClass.getEnumConstants()) {

                    String displayKey = constant.toString();
                    String displayValue = "";

                    // if getDisplayText exists for this Enum, use it, otherwise use the Enum constant as the value
                    if(method == null) {
                        displayValue = constant.toString();
                    }
                    else {

                        try {

                            Object enumInstance = Enum.valueOf(fieldClass, displayKey);
                            displayValue = (String) method.invoke(enumInstance);
                        }
                        catch(IllegalAccessException iae) {
                            // do nothing we default to the enum constant
                        }
                        catch(InvocationTargetException ite) {
                            // do nothing we default to the enum constant
                        }
                    }

//                    System.out.println(displayKey + " : " + displayValue);
                    enumValues.put(displayKey, displayValue);
                }

                lookups.put(fieldName, enumValues);

            }
        }

        return lookups;
    }

}

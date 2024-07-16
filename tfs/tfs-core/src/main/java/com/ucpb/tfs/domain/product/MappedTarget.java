package com.ucpb.tfs.domain.product;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * User: Jett
 * Date: 7/17/12
 */
public class MappedTarget {


    public void transferFromMap(HashMap<String, Object> hashMap) {
        System.out.println(this.getClass().getName());

        Field[] fields = this.getClass().getDeclaredFields();

        for(Field f : fields) {
            System.out.println(f.getName());

            if(hashMap.containsKey(f.getName())) {
                f.setAccessible(true);
//                f.set(this, "aa");
            }
        }

    }
}

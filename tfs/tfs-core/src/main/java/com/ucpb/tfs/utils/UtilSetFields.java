package com.ucpb.tfs.utils;

import com.ucpb.tfs.domain.product.ICNumber;
import com.ucpb.tfs.utils.enumTypes.BooleanField;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Jett
 * Date: 7/17/12
 * @author Jett Gamboa
 */
public class UtilSetFields {

    public static void copyMapToObject(Object obj, Map<String,Object> hashMap) {

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Map<String,Object> tempHashMap = new HashMap<String,Object>();
        tempHashMap.putAll(hashMap);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Field[] fields = obj.getClass().getDeclaredFields();
        // System.out.println("fields.length = " + fields.length);

        Field[] fieldsSc = obj.getClass().getSuperclass().getDeclaredFields();
        // System.out.println("fieldsSc.length = " + fieldsSc.length);

        ArrayList<Field> fieldsList = new ArrayList<Field>(Arrays.asList(fields));
        ArrayList<Field> fieldsScList = new ArrayList<Field>(Arrays.asList(fieldsSc));

        fieldsScList.addAll(fieldsList);

        // iterate through our fields and check if our hashmap has a corresponding entry
        // for(Field field : fields) {
        for(Field field : fieldsScList) {

            String fieldName = field.getName();
            Class fieldClass = field.getType();

            if(hashMap.containsKey(fieldName)) {

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                tempHashMap.remove(fieldName);
                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                Boolean accessible = field.isAccessible();

                // set field to accessible if it is not
                if(!accessible) {
                    field.setAccessible(true);
                }

                try {

                    Object fieldContent = hashMap.get(fieldName);

                    if (fieldContent != null)  {

                        // check if this field is of the same type as the value in the hash, if it is then set it to the value
                        // this will automatically work for strings
                        if(field.getType().equals(fieldContent.getClass())) {
                            field.set(obj, hashMap.get(fieldName));
                        }

                        // check for Enums
                        else if(fieldClass.isEnum()) {

                            // get a list of all the constants for this enum
                            List<Object> enumConstants = Arrays.asList(fieldClass.getEnumConstants());

                            String enumValue = (String) fieldContent;

                            if (enumValue != null && !enumValue.trim().equals("")) {
                                // check if the String value matches one of the enums, if it does
                                // set the field to the matching enum value
                                if(enumConstants.contains(Enum.valueOf(fieldClass, enumValue.toUpperCase().replaceAll(" ", "_")))) {  // Expect that our enums always have upper case values
                                    field.set(obj, Enum.valueOf(fieldClass, enumValue.toUpperCase().replaceAll(" ", "_")));
                                }
                            }

//                        System.out.println("field: " + fieldName + " type " + field.getType()+ " not matching " +hashMap.get(fieldName).getClass());
                        }
                        else if(fieldClass.equals(Long.class)) {

                            // if our content is of type string, return a
                            if(fieldContent.getClass().equals(String.class) && !((String)fieldContent).equals("")) {

                                field.set(obj, Long.valueOf((String) fieldContent));
                            }
                        }
                        else if(fieldClass.equals(Integer.class)) {

                            // if our content is of type string, return a
                            if(fieldContent.getClass().equals(String.class) && !((String)fieldContent).equals("")) {
                                field.set(obj, Integer.valueOf((String) fieldContent));

                            }
                        }
                        else if(fieldClass.equals(Boolean.class)) {
                            // if our content is of type string, return a
                            if(fieldContent.getClass().equals(String.class) && !((String)fieldContent).equals("")) {
                                if (fieldContent.equals(BooleanField.Y.toString())) {
                                    field.set(obj, Boolean.TRUE);
                                } else if (fieldContent.equals(BooleanField.N.toString())) {
                                    field.set(obj, Boolean.FALSE);
                                } else if ("1".equals((String) fieldContent)) {
                                    field.set(obj, Boolean.TRUE);
                                } else if ("0".equals((String) fieldContent)) {
                                    field.set(obj, Boolean.FALSE);
                                } else {
                                    field.set(obj, Boolean.valueOf((String) fieldContent));
                                }
                            }
                        }
                        else if(fieldClass.equals(BigInteger.class)) {

                            // if our content is of type string, return a
                            if(fieldContent.getClass().equals(String.class) && !((String)fieldContent).equals("")) {
                                field.set(obj, new BigInteger((String) fieldContent.toString().replace(",","")));
                            }
                        }
                        else if(fieldClass.equals(BigDecimal.class)) {

                            // if our content is of type string, return a
                            if(fieldContent.getClass().equals(String.class) && !((String)fieldContent).equals("")) {
                                field.set(obj, new BigDecimal((String) fieldContent.toString().replace(",","")));
                            }
                        }
                        else if(fieldClass.equals(Currency.class)) {

                            // if our content is of type string, return a
                            if(fieldContent.getClass().equals(String.class) && !((String)fieldContent).equals("")) {

                                field.set(obj, Currency.getInstance(((String)fieldContent.toString()).toUpperCase()));
                            }
                        }
                        else if(fieldClass.equals(java.util.Date.class)) {

                            // if our content is of type string, return a
                            if(fieldContent.getClass().equals(String.class) && !((String)fieldContent).equals("")) {

                                try {

                                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

                                    // include time if the passed value include time (used in cdt upload)
                                    // format used ex: 01/01/2013 01:30am
                                    String regex = "(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/((19|20)\\d\\d)\\s(0?[1-9]|1[012]):(0?[0-9]|[1-5][0-9])([ap]m)";

                                    Pattern pattern = Pattern.compile(regex);
                                    Matcher matcher = pattern.matcher(fieldContent.toString().trim());

                                    if (matcher.matches()) {
                                        df.applyPattern("MM/dd/yyyy hh:mma");
                                        System.out.println("regex matches");
                                    }

                                    field.set(obj, (java.util.Date)df.parse(fieldContent.toString()));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else if (fieldClass.equals(ICNumber.class)) {
                            if(fieldContent.getClass().equals(String.class) && !((String)fieldContent).equals("")) {
                                field.set(obj, new ICNumber(fieldContent.toString()));
                            }
                        }
                        else
                        {
                            System.out.println(fieldClass + " is not handled.");
                        }
                    }

                } catch(IllegalAccessException iae) {

                    // this should never happen
                    iae.printStackTrace();

                }
                finally {

                    // revert to original accessibility setting
                    if(!accessible) {
                        field.setAccessible(false);
                    }
                }
            }
        }

//        System.out.println("\n========================================");
//        System.out.println("PARAMETERS NOT COPIED:");
//        System.out.println("========================================");
//        for (Map.Entry<String, Object> entry : tempHashMap.entrySet()) {
//            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
//        }
//        System.out.println("========================================\n");

    }

    public static List<Map<String, Object>> stringOfListMapToListMap(String stringList) {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        System.out.println("stringList " + stringList);
        for (String item : stringList.split("],")) {
            item = item.toString().trim().replaceAll("\\[", "").replaceAll("\\]", "");

            Map<String, Object> map = new HashMap<String, Object>();
            System.out.println("item " + item);
            for (String itemPiece : item.trim().split(",")) {
                String[] mapParts = itemPiece.split(":");
                System.out.println("mapParts " + mapParts);
                if (!"id".equals(mapParts[0].toString())) {
                	try{
                		map.put(mapParts[0].toString().trim(), mapParts[1].toString());
                	}catch(ArrayIndexOutOfBoundsException aiobe){
                		map.put(mapParts[0].toString().trim(), "");
                	}
                    
                }
            }

            returnList.add(map);
        }

        return returnList;
    }

    public static List<Map<String, Object>> stringOfListMapToListMapInstructions(String stringList) {
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();

        for (String item : stringList.split("],")) {
            item = item.toString().trim().replaceAll("\\[", "").replaceAll("\\]", "");

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", item.substring(3,item.indexOf(",")));
            map.put("instruction", item.split("instruction:")[1]);

            returnList.add(map);
        }

        return returnList;
    }
}

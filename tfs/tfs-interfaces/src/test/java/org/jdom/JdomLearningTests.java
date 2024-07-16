package org.jdom;

import com.ucpb.tfs.swift.validation.Constraint;
import org.jdom2.*;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Ignore
public class JdomLearningTests {

    private static final String SIMPLE_TYPE = "simpleType";
    private static final String SCHEMA_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
    private static final String PATH = "tfs-interfaces/src/test/resources/swift/validation/simpleTypes.xsd";

    @Test
    public void successfullyGetAllElements(){
        Namespace namespace = Namespace.getNamespace(SCHEMA_NAMESPACE_URI);
        SAXBuilder builder = new SAXBuilder();
        Map<String,Constraint> constraints = new HashMap<String,Constraint>();
        try {
            org.jdom2.Document document = builder.build( new File(PATH));
            org.jdom2.Element root = document.getRootElement();
            for(Element element : root.getChildren()){
                System.out.println("********* NAME: " + element.getName());
                System.out.println("********* NAMESPACE: " + element.getNamespace().toString());
            }

            for(Element element : root.getChildren("simpleType",namespace)){
                System.out.println(" ______ NAME: " + element.getName());
                for(Attribute attribute : element.getAttributes()){
                    System.out.println("----------- ATTRIBUTE: " + attribute.getName());
                    System.out.println("----------- ATTRIBUTE VALUE: " + attribute.getValue());

                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}

package com.ucpb.tfs.application;

import com.incuventure.cqrs.infrastructure.QueryException;
import com.incuventure.cqrs.query.QueryBus;
import com.incuventure.cqrs.query.QueryItem;
import com.incuventure.cqrs.token.TokenProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * User: Jett
 * Date: 8/16/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FieldProviderTest {

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    QueryBus queryBus;

    @Test
    public void TestFieldLists() throws QueryException {

        try {
            QueryItem qi = new QueryItem("data", com.ucpb.tfs.application.query.SelectBoxDataProvider.class, "getAllProductSelectData");
            List<QueryItem> qis = new ArrayList<QueryItem>();
            qis.add(qi);

            HashMap<String, Object> returnValues = queryBus.dispatch(qis);

            System.out.println(returnValues);

//            System.out.println(returnValues.get("token"));

//            List mylist = (List) returnValues.get("token");

//            System.out.println(returnValues.get("token"));
//            System.out.println("string value from list is: " + mylist.get(0));

        } catch(NoSuchMethodException nsme) {
            nsme.printStackTrace();
        }

    }

}

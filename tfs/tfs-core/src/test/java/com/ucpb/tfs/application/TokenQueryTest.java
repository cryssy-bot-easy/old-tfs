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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: Jett
 * Date: 8/4/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:unitTestContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TokenQueryTest {

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    QueryBus queryBus;

    @Test
    public void TestTokenQuery() throws QueryException {


        tokenProvider.addTokenForId("mytoken", "mytokenvalue");

        try {
            QueryItem qi = new QueryItem("token", com.incuventure.cqrs.token.TokenProvider.class, "getIdForToken", "mytoken");
            List<QueryItem> qis = new ArrayList<QueryItem>();
            qis.add(qi);

            HashMap<String, Object> returnValues = queryBus.dispatch(qis);

            System.out.println(returnValues.get("token"));

            List mylist = (List) returnValues.get("token");

            System.out.println(returnValues.get("token"));
            System.out.println("string value from list is: " + mylist.get(0));

        } catch(NoSuchMethodException nsme) {
            nsme.printStackTrace();
        }

    }
}

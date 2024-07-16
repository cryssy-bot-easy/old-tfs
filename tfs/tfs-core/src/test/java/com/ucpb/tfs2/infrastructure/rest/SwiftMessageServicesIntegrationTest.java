package com.ucpb.tfs2.infrastructure.rest;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.jboss.resteasy.mock.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;

/**
 */
@Ignore
public class SwiftMessageServicesIntegrationTest {

    private Dispatcher dispatcher;

    @Before
    public void setup(){
        dispatcher = MockDispatcherFactory.createDispatcher();
        POJOResourceFactory noDefaults = new POJOResourceFactory(SwiftMessageServices.class);
        dispatcher.getRegistry().addResourceFactory(noDefaults);
//        dispatcher.getRegistry().add

    }

    @Test
    public void successfullyValidateExistingSwiftMessage() throws URISyntaxException {
        MockHttpRequest request = MockHttpRequest.post("/swift/validate");
        request.setAttribute("tradeServiceId","dummyTradeServiceId");
        request.setAttribute("messageType","700");

        MockHttpResponse response = new MockHttpResponse();

        dispatcher.invoke(request, response);


//        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("basic", response.getErrorMessage());
    }

}

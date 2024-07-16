package com.ucpb.tfs2.webservices
import com.google.gson.Gson
import com.ucpb.tfs2.application.service.OutgoingMTService
import org.jboss.resteasy.client.ClientRequest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.core.MediaType

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:postlc-unitTestContext.xml")
@Transactional
class OutgoingMTServicesTest {

    @Autowired
    OutgoingMTService outgoingMTService;

    @Test
    @Rollback(false)
    public void testMTServiceSave() {

        Map details = [
            mtfield : [
                    field4 : "field 4 data",
                    field5 : "field 5 data",
                    field6 : "field 6 data",
                    field7 : "field 7 data"
            ]
        ]

        outgoingMTService.saveOutgoingMT("mcueto", "MT799", "ABC1234", details)

    }

    @Test
    public void testWSOutgoingMTSave() {

        Gson gson = new Gson()
        ClientRequest request = new ClientRequest("http://localhost:9090/tfs-core/api/outgoingMT/save");

        // test data
        Map testData =  [
            username: "mcueto",
            destinationBank: "ABC1234",
            messageType: "MT799",
            mtfield : [
                field4 : "field 4 data",
                field5 : "field 5 data",
                field6 : "field 6 data",
                field7 : "field 7 data"
            ]
        ]

        // convert test map data to JSON
        String jsontext = gson.toJson(testData)

        // request.accept("application/xml").pathParameter("id", 1).body( MediaType.APPLICATION_XML, xmltext);
        request.accept(MediaType.APPLICATION_JSON_TYPE).body(MediaType.APPLICATION_JSON_TYPE, jsontext)

        String response = request.postTarget(String.class)

        Map responseMap = gson.fromJson(response, Map.class)

        println "the service returned: " + responseMap
    }

}

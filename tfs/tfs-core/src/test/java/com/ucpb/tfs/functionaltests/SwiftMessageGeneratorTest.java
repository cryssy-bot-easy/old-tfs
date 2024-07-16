package com.ucpb.tfs.functionaltests;

import com.ucpb.tfs.domain.service.TradeService;
import com.ucpb.tfs.domain.service.TradeServiceId;
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.swift.message.RawSwiftMessage;
import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder;
import com.ucpb.tfs.swift.message.writer.DefaultSwiftMessageWriter;
import com.ucpb.tfs.swift.message.writer.JaxbXmlSwiftMessageWriter;
import com.ucpb.tfs.swift.message.writer.SwiftMessageWriter;
import com.ucpb.tfs.swift.validator.SwiftValidator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is not a test case. This is a convenience class for generating swift messages
 * using tradeServiceIds as input.
 */
@Ignore("Should not be run as part of the build process. DO NOT commit this with @Ignore uncommented")
@TransactionConfiguration
@Transactional
@ContextConfiguration(locations = {"classpath*:amla-report-integration-tests.xml","classpath*:swift/message-builder.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({TransactionalTestExecutionListener.class})
public class SwiftMessageGeneratorTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private TradeServiceRepository tradeServiceRepository;

    private SwiftMessageWriter writer = new DefaultSwiftMessageWriter();

    @Autowired
    private SwiftMessageBuilder builder;


    private DataSource dataSource;

    @Resource(name = "myDataSource")
    public void setDataSource(final DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Ignore
    @Test
    public void generateSwiftMessage(){

        TradeService tradeService = tradeServiceRepository.load(new TradeServiceId("2b6bfff2-1fdd-4b22-9e0b-de29ee35cab4"));

        List<RawSwiftMessage> messages = builder.build("410",tradeService);
        RawSwiftMessage message = messages.get(0);

        System.out.println("******************* MESSAGE : **********************");
        System.out.println(writer.write(message));
        System.out.println("********************* END ***************************");

    }

}

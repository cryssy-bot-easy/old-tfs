package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.condition.AdditionalCondition
import com.ucpb.tfs.domain.documents.RequiredDocument
import com.ucpb.tfs.domain.product.DocumentNumber
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs.domain.service.TradeServiceId
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.interfaces.services.SwiftMessageService
import com.ucpb.tfs.swift.message.RawSwiftMessage
import com.ucpb.tfs.swift.message.builder.SwiftMessageBuilder
import com.ucpb.tfs.swift.message.writer.DefaultSwiftMessageWriter
import com.ucpb.tfs.swift.message.writer.SwiftMessageWriter
import com.ucpb.tfs.swift.validator.SwiftValidator
import com.ucpb.tfs.swift.validator.ValidationError
import com.ucpb.tfs.utils.SwiftMessageFactory
import com.ucpb.tfs2.utils.TradeServiceUtils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.Assert

import javax.annotation.Resource
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

import static com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus.APPROVED
import static com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus.POST_APPROVED;
import static com.ucpb.tfs.domain.service.enumTypes.TradeServiceStatus.POSTED;

/**
 */
@Component
@Path("/swift")
class SwiftMessageServices {

    @Autowired
    private TradeServiceRepository tradeServiceRepository;

    private SwiftMessageWriter writer = new DefaultSwiftMessageWriter();

    @Resource(name = "swiftValidators")
    private List<SwiftValidator> messageValidators;

    @Autowired
    private SwiftMessageService swiftService;

    @Autowired
    private SwiftMessageFactory swiftMessageFactory;

    @Autowired
    private SwiftMessageBuilder builder;

    private static final Gson GSON = new Gson();


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/generate")
    public Response getSwiftMessage(@Context UriInfo allUri, String postRequestBody) {
        Map formDetails = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(formDetails["tradeServiceId"], "Trade Service Id must not be null!");
        Assert.notNull(formDetails["messageType"], "Swift Message Type must not be null!");

        Map returnMap = new HashMap();
        returnMap.put("status", "ok");
        List<RawSwiftMessage> messages = generateSwiftMessage(formDetails["tradeServiceId"], formDetails["messageType"]);
        returnMap.put("message", writer.write(messages.get(0)));

        return Response.status(200).entity(GSON.toJson(returnMap)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/validate")
    public Response validateSwiftMessage(@Context UriInfo allUri, String postRequestBody) {
        Map formDetails = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(formDetails["tradeServiceId"], "Trade Service Id must not be null!");
        Assert.notNull(formDetails["messageType"], "Swift Message Type must not be null!");
        println "validateSwiftMessage"
        def defaultSequenceNumber = 0

        if (formDetails["sequenceNumber"]?.matches(/\d+/))
            defaultSequenceNumber = formDetails["sequenceNumber"].toInteger()
			
        List<RawSwiftMessage> messages = generateSwiftMessage(formDetails["tradeServiceId"], formDetails["messageType"], defaultSequenceNumber);
		
		List<String> errors = validate(messages.get(0));

        println "errors:"+errors
        println "messages:"+messages
        Map returnMap = new HashMap();
        returnMap.put("message", writer.write(messages.get(0)));
        returnMap.put("status", "ok");
        returnMap.put("isValid", errors.isEmpty());
        returnMap.put("errors", errors);

        return Response.status(200).entity(GSON.toJson(returnMap)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/validateMultiMessages")
    public Response validateSwiftMessageList(@Context UriInfo allUri, String postRequestBody) {
        Map formDetails = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(formDetails["tradeServiceId"], "Trade Service Id must not be null!");
        Assert.notNull(formDetails["messageType"], "Swift Message Type must not be null!");

        List<RawSwiftMessage> messages = generateSwiftMessage(formDetails["tradeServiceId"], formDetails["messageType"]);
        List<String> formattedMessages = new ArrayList<String>();
        List<String> messageErrors = new ArrayList<String>();

        if (messages.size() > 1) {
            messages.remove(0);
            for (int ctr = 0; ctr < messages.size(); ctr++) {
                messageErrors.addAll(validate(messages.get(ctr)));
                formattedMessages.add(writer.write(messages.get(ctr)));
            }
        }
		
        Map returnMap = new HashMap();
        returnMap.put("messages", formattedMessages);
        returnMap.put("status", "ok");
        returnMap.put("isValid", messageErrors.isEmpty());
        returnMap.put("errors", messageErrors);

        return Response.status(200).entity(GSON.toJson(returnMap)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/generate")
    public Response generateSwiftMessages(@Context UriInfo allUri, String postRequestBody) {
        Map formDetails = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(formDetails["tradeServiceId"], "Trade Service Id must not be null!");

        TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(formDetails["tradeServiceId"]));
        int messagesSent = 0;
        if(tradeService != null && (APPROVED.equals(tradeService.getStatus()) || POST_APPROVED.equals(tradeService.getStatus()) || POSTED.equals(tradeService.getStatus()))) {
            messagesSent = generateAndSend(tradeService);
        }

        return Response.status(200).entity(GSON.toJson([messagesSent : messagesSent])).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/generateAll")
    public Response generateAllSwiftMessages(@Context UriInfo allUri) {
        int messagesSent = 0;
        for(TradeService tradeService : tradeServiceRepository.list()){
            if(tradeService != null && (APPROVED.equals(tradeService.getStatus()) || POST_APPROVED.equals(tradeService.getStatus()) || POSTED.equals(tradeService.getStatus()))) {
                messagesSent += generateAndSend(tradeService);
            }
        }
        return Response.status(200).entity(GSON.toJson([messagesSent : messagesSent])).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/generateByDocumentNumber")
    public Response generateSwiftMessagesByDocumentNumber(@Context UriInfo allUri) {
        int messagesSent = 0;
        Assert.notNull(allUri?.getQueryParameters()?.getFirst("documentNumber"),"Document Number must not be null!");
        StringBuilder filesGenerated = new StringBuilder();

        for(TradeService tradeService : tradeServiceRepository.load(new DocumentNumber(allUri?.getQueryParameters()?.getFirst("documentNumber")))){
            if(tradeService != null && (APPROVED.equals(tradeService.getStatus()) || POST_APPROVED.equals(tradeService.getStatus()) || POSTED.equals(tradeService.getStatus()))) {
                List<RawSwiftMessage> messagesToSend = swiftMessageFactory.generateSwiftMessages(tradeService);
                for (RawSwiftMessage messageToSend : messagesToSend){
                    filesGenerated.append(swiftService.sendMessage(messageToSend) + ",");
                }
            }
        }
        return Response.status(200).entity(GSON.toJson([messagesSent : messagesSent, filesGenerated : filesGenerated.toString()])).build();
    }


    private int generateAndSend(TradeService tradeService){
        List<RawSwiftMessage> messagesToSend = swiftMessageFactory.generateSwiftMessages(tradeService);
        for (RawSwiftMessage messageToSend : messagesToSend){
            swiftService.sendMessage(messageToSend);
        }
        return messagesToSend.size();
    }





    private List<RawSwiftMessage> generateSwiftMessage(String tradeServiceId, String messageType, Integer sequenceNumber) {
        TradeService tradeService = tradeServiceRepository.load(new TradeServiceId(tradeServiceId));
		if(sequenceNumber != null){
			tradeService = TradeServiceUtils.getSplittedValues(tradeService, sequenceNumber);
			
			
		}
        def m =  builder.build(messageType, tradeService);
		m[0].messageBlock.update("27", tradeService.details.get('sequenceOrder')?: "1/1")
		return m
    }

    private List<String> validate(RawSwiftMessage message) {
        List<ValidationError> errorList = new ArrayList<ValidationError>();
        List<String> errorMessages = new ArrayList<String>();
		println "before swiftValidator validate"
        for (SwiftValidator validator : messageValidators) {
			errorList.addAll(validator.validate(message));
        }
		println "after swiftValidator validate"
        for (ValidationError validationError : errorList) {						
			if (!validationError.getMessage().toString().equalsIgnoreCase("Invalid content found around field72Z, field77J is expected.")) {
				errorMessages.add(validationError.getMessage());
			} 
        }

        return errorMessages;
    }
	
}

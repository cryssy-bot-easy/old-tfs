/* Modified by : Rafael Ski Poblete
 * Date : 7/27/18
 * Description : Added Charges Narrative to mtmessage and added condition to handle the modified field "72Z"
 * 				 and the old field "72"  that other module uses.
 * */
package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.domain.mtmessage.MtMessage
import com.ucpb.tfs.domain.mtmessage.MtMessageRepository
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.swift.message.MessageBlock
import com.ucpb.tfs.swift.message.RawSwiftMessage
import com.ucpb.tfs.swift.message.parser.SwiftFormattedMessageParser
import com.ucpb.tfs.swift.message.parser.SwiftMessageParser
import com.ucpb.tfs.utils.DateUtil
import com.ucpb.tfs2.application.service.TradeServiceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo
import java.util.regex.Matcher
import java.util.regex.Pattern

@Path("/mtMessage")
@Component
class MtMessageRestServices {

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    MtMessageRepository mtMessageRepository;

    @Autowired
    TradeServiceService tradeServiceService;

    @Autowired
    SwiftMessageParser simpleSwiftMessageParser;
    // SimpleSwiftMessageParser simpleSwiftMessageParser;

    @Autowired
    SwiftFormattedMessageParser swiftFormattedMessageParser;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/generateTradeService")
    public Response generateTradeSeviceFromMt(@Context UriInfo allUri, String postRequestBody) {

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();

        Map jsonParams = gson.fromJson(postRequestBody, Map.class);

        MtMessage mtMessage = mtMessageRepository.load(new Long(jsonParams.get("id")));
		try {
			
		
        if (mtMessage) {
            RawSwiftMessage rawSwiftMessage = swiftFormattedMessageParser.parse(mtMessage?.getMessage());
            MessageBlock messageBlock = rawSwiftMessage?.getMessageBlock();

            Map parsedLcFromMt = new HashMap();

            String lcType = (mtMessage?.getMtType().equals("700") || mtMessage?.getMtType().equals("710")) ? "REGULAR" : "STANDBY";
            String lcTenor = messageBlock?.getTagValue("42C")?.contains("SIGHT") ? "SIGHT" : "USANCE"

            String currencyAmount = messageBlock?.getTagValue("32B");

            String amount = currencyAmount?.substring(3, currencyAmount?.length())?.replace(",", ".");

            if (amount?.endsWith(".")) {
                amount += "00"
            }

            String expiryDate;
            String issueDate;
            String[] reimbursingBankCode;

            if("700".equalsIgnoreCase(rawSwiftMessage.getMessageType()) || "710".equalsIgnoreCase(rawSwiftMessage.getMessageType())){
                expiryDate = DateUtil.convertDateFormat(messageBlock?.getTagValue("31D"),"yyMMdd","MM/dd/yyyy");
                issueDate = DateUtil.convertDateFormat(messageBlock?.getTagValue("31C"),"yyMMdd","MM/dd/yyyy")
                // check if 53A is required
                if (messageBlock?.getTagValue("53A")) {
                    reimbursingBankCode = messageBlock?.getTagValue("53A").split("\n");
                } else if (messageBlock?.getTagValue("53D")) {
                    reimbursingBankCode = messageBlock?.getTagValue("53D").split("\n");
                }

            }else if("760".equalsIgnoreCase(rawSwiftMessage.getMessageType())){
                if(StringUtils.hasText(messageBlock?.getTagValue("30"))){
                    issueDate = DateUtil.convertDateFormat(messageBlock?.getTagValue("30"),"yyMMdd","MM/dd/yyyy")
                }
            }
			
			String exporterName = ""
			String exporterAddress = ""
			
			if (messageBlock?.getTagValue("59")){
	            def exporterParts = messageBlock?.getTagValue("59")?.split("\n")
	            exporterName = exporterParts[0]
	
	            exporterParts.eachWithIndex { ep, idx ->
	                if (idx > 0) {
	                    exporterAddress += ep?.trim()
	
	                    if (idx+1 < exporterParts.size()) {
	                        exporterAddress += "\n"
	                    }
	                }
	            }
			}
			
			String importerName = ""
			String importerAddress = ""
			String issuingBank710 = ""
			
			if (messageBlock?.getTagValue("50")){
				def importerParts = messageBlock?.getTagValue("50").split("\n")
				importerName = importerParts[0]
	
				importerParts.eachWithIndex { ep, idx ->
					if (idx > 0) {
						importerAddress += ep?.trim()
	
						if (idx+1 < importerParts.size()) {
							importerAddress += "\n"
						}
					}
				}
			}
			
			if (messageBlock?.getTagValue("52A")){
				def issuingBank710Parts = messageBlock?.getTagValue("52A").split("\n")
				issuingBank710 = issuingBank710Parts[0]
			}
            parsedLcFromMt << ["lcNumber" : ("710".equalsIgnoreCase(rawSwiftMessage.getMessageType())) ? messageBlock?.getTagValue("21") : messageBlock?.getTagValue("20"),
                    "exporterName" : exporterName,
                    "exporterAddress" : exporterAddress,
                    "importerName" : importerName,
                    "importerAddress" : importerAddress,
                    "lcType" : lcType,
                    "lcIssueDate" : issueDate,
                    "lcTenor" : lcTenor,
                    "lcCurrency" : messageBlock?.getTagValue("32B")?.substring(0,3),
                    "lcAmount" : amount,
                    "lcExpiryDate" : expiryDate,
                    "issuingBank" : ("710".equalsIgnoreCase(rawSwiftMessage.getMessageType())) ? issuingBank710 : rawSwiftMessage?.getMessageSender(),
                    "reimbursingBank" : (reimbursingBankCode != null) ? (reimbursingBankCode[1] ?: reimbursingBankCode[0]) : null,
					"senderToReceiverInformation" : ("730".equalsIgnoreCase(rawSwiftMessage.getMessageType())) ? messageBlock?.getTagValue("72Z") : messageBlock?.getTagValue("72"),
					"chargesNarrative" : messageBlock?.getTagValue("71D"),
					"sequenceOfTotal" : messageBlock?.getTagValue("27"),
					"formOfDocumentaryCredit" : messageBlock?.getTagValue("40A"),
					"documentaryCreditNumber" : ("710".equalsIgnoreCase(rawSwiftMessage.getMessageType())) ? messageBlock?.getTagValue("21") : messageBlock?.getTagValue("20"),
					"referenceToPreAdvice" : messageBlock?.getTagValue("23"),
					"dateOfIssue" : messageBlock?.getTagValue("31C"),
					"applicableRules" : messageBlock?.getTagValue("40E"),
					"dateAndPlaceOfExpiry" : messageBlock?.getTagValue("31D"),
					"applicantBank" : messageBlock?.getTagValue("51"),
					"applicant" : messageBlock?.getTagValue("50"),
					"beneficiary" : messageBlock?.getTagValue("59"),
					"percentageCreditAmountTolerance" : messageBlock?.getTagValue("39A"),
					"maximumCreditAmount" : messageBlock?.getTagValue("39B"),
					"additionalAmountsCovered" : messageBlock?.getTagValue("39C"),
					"availableWithBy" : messageBlock?.getTagValue("41"),
					"draftsAt" : messageBlock?.getTagValue("42C"),
					"drawee" : messageBlock?.getTagValue("42"),
					"mixedPaymentDetails" : messageBlock?.getTagValue("42M"),
					"deferredPaymentDetails" : messageBlock?.getTagValue("42P"),
					"partialShipments" : messageBlock?.getTagValue("43P"),
					"transshipment" : messageBlock?.getTagValue("43T"),
					"placeOfTakingInCharge" : messageBlock?.getTagValue("44A"),
					"portOfLoading" : messageBlock?.getTagValue("44E"),
					"placeOfFinalDestination" : messageBlock?.getTagValue("44B"),
					"latestDateOfShipment" : messageBlock?.getTagValue("44C"),
					"shipmentPeriod" : messageBlock?.getTagValue("44D"),
					"descriptionOfGoodsAndOrServices" : messageBlock?.getTagValue("45A"),
					"documentsRequired" : messageBlock?.getTagValue("46A"),
					"additionalConditions" : messageBlock?.getTagValue("47A"),
					"charges" : messageBlock?.getTagValue("71B"),
					"periodForPresentation" : messageBlock?.getTagValue("48"),
					"confirmationInstructions" : messageBlock?.getTagValue("49"),
					"reimbursingBank" : messageBlock?.getTagValue("53"),
					"instructionsToThePayingBank" : messageBlock?.getTagValue("78"),
					"adviseThroughBank" : messageBlock?.getTagValue("57"),
					"date" : messageBlock?.getTagValue("30"),
					"applicableRules" : messageBlock?.getTagValue("40C"),
					"detailsOfGuarantee" : messageBlock?.getTagValue("77C")];

            if ("USANCE".equals(lcTenor) && messageBlock?.getTagValue("42C")) {
				def usanceParts = messageBlock?.getTagValue("42C").split("\n")
				String usanceTerm = ""
				usanceParts.eachWithIndex { up, idx ->
					if (idx > -1) {
						usanceTerm += up?.trim()
							
						if (idx+1 < usanceParts.size()) {
							usanceTerm += "\n"
						}
					}
				}
                parsedLcFromMt << ["usanceTerm" : usanceTerm]
            }
            println "mtMessageId:"+mtMessage.getId().toString()
            parsedLcFromMt.put('mtMessageId',mtMessage.getId().toString())

            returnMap.put("details", parsedLcFromMt);
            returnMap.put("status", "ok");
        } else {
            returnMap.put("details", "mt message not existing")
            returnMap.put("status", "error")
        }
		
		}catch(Exception e){
			e.printStackTrace()
			Map parsedLcFromMt = new HashMap();
			parsedLcFromMt = [:]
			returnMap.put("details", parsedLcFromMt);
			returnMap.put("status", "ok");
		}

        // format return data as json
        result = gson.toJson(returnMap);

        return Response.status(200).entity(result).build();

    }

}
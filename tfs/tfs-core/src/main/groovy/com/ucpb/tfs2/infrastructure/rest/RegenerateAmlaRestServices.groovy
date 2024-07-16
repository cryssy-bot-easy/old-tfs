package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.ucpb.tfs.application.service.TradeProductService
import com.ucpb.tfs.domain.accounting.AccountingEntryActual
import com.ucpb.tfs.domain.accounting.AccountingEntryActualRepository
import com.ucpb.tfs.domain.audit.infrastructure.repositories.AccountLogRepository
import com.ucpb.tfs.domain.audit.infrastructure.repositories.CustomerAccountLogRepository
import com.ucpb.tfs.domain.audit.infrastructure.repositories.CustomerLogRepository
import com.ucpb.tfs.domain.audit.infrastructure.repositories.TransactionLogRepository
import com.ucpb.tfs.domain.product.DocumentNumber
import com.ucpb.tfs.domain.product.LetterOfCredit
import com.ucpb.tfs.domain.product.TradeProductRepository
import com.ucpb.tfs.domain.product.event.LCAdjustedEvent
import com.ucpb.tfs.domain.product.event.LCAmendedEvent
import com.ucpb.tfs.domain.product.event.LCNegotiationCreatedEvent
import com.ucpb.tfs.domain.product.event.LetterOfCreditCreatedEvent
import com.ucpb.tfs.domain.service.TradeProductNumber
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.domain.service.enumTypes.*
import com.ucpb.tfs.domain.service.event.AmlaInformationLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*
import java.text.SimpleDateFormat

/**
 * Created with IntelliJ IDEA.
 * User: IPCVal
 * Date: 12/27/13
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/regenAmla")
@Component
public class RegenerateAmlaRestServices {

    @Autowired
    TradeServiceRepository tradeServiceRepository;

    @Autowired
    AccountingEntryActualRepository accountingEntryActualRepository;

    @Autowired
    TradeProductRepository tradeProductRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Autowired
    private AccountLogRepository accountLogRepository;

    @Autowired
    private CustomerAccountLogRepository customerAccountLogRepository;

    @Autowired
    private CustomerLogRepository customerLogRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/execute")
    public Response execute(@Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {

            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            // 1. Get all transactions approved on the passed date.
            // 2. Iterate on the transactions; query the GLTS number from INT_ACCENTRYACTUAL using the
            //    TradeServiceId.
            // 3. Load the TradeService object from repository, and determine its type.
            // 4. Based on its type, generate the Event object. Initially put in a List.
            // 5. Delete all entries in the AMLA tables for the passed date.
            // 6. Iterate the List created in Step 4, then call the appropriate Listener method from
            //    AmlaInformationLogger to re-insert the entries, passing the GLTS number obtained in Step 2.

            String passedDate = ((String)jsonParams.get("date")).trim();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
            Date date = dateFormatter.parse(passedDate);

            List<TradeService> tradeServices = tradeServiceRepository.getAllApprovedTradeServiceByDate(date);

            AmlaInformationLogger amla = new AmlaInformationLogger();

            println "\n---START---\n"
            for (TradeService tradeService : tradeServices) {

                AccountingEntryActual entry = accountingEntryActualRepository.getEntryFirstResultAmla(tradeService);
                String glts = entry.getGltsNumber();

                DocumentClass documentClass = tradeService.getDocumentClass();
                DocumentType documentType = tradeService.getDocumentType();
                DocumentSubType1 documentSubType1 = tradeService.getDocumentSubType1();
                DocumentSubType2 documentSubType2 = tradeService.getDocumentSubType2();
                ServiceType serviceType = tradeService.getServiceType();

                println "----------------"
                println "glts             = ${glts}"
                println "documentClass    = ${documentClass}"
                println "documentType     = ${documentType}"
                println "documentSubType1 = ${documentSubType1}"
                println "documentSubType2 = ${documentSubType2}"
                println "serviceType      = ${serviceType}"
                println "----------------\n"

                // LC
                if (documentClass.equals(DocumentClass.LC)) {

                    TradeProductNumber tradeProductNumber = tradeService.getTradeProductNumber();
                    // DocumentNumber documentNumber = tradeService.getDocumentNumber();

                    // LetterOfCredit letterOfCredit = (LetterOfCredit)tradeProductRepository.load(new DocumentNumber(tradeProductNumber.toString()));
                    LetterOfCredit letterOfCredit = TradeProductService.createLetterOfCredit(new DocumentNumber(tradeProductNumber.toString()), tradeService.getDetails());

                    if (serviceType.equals(ServiceType.OPENING)) {

                        // LetterOfCreditCreatedEvent
                        LetterOfCreditCreatedEvent letterOfCreditCreatedEvent = new LetterOfCreditCreatedEvent(tradeService, letterOfCredit, glts);
                        // amla.logLcCreatedEvent(letterOfCreditCreatedEvent);

                    } else if (serviceType.equals(ServiceType.NEGOTIATION)) {

                        // LCNegotiationCreatedEvent
                        // LetterOfCredit lcNegotiation = (LetterOfCredit) tradeProductRepository.load(new DocumentNumber((String)tradeService.getDetails().get("lcNumber")));
                        LCNegotiationCreatedEvent lcNegotiationCreatedEvent = new LCNegotiationCreatedEvent(tradeService, letterOfCredit, glts);
                        // amla.logLcNegotiationEvent(lcNegotiationCreatedEvent);

                    } else if (serviceType.equals(ServiceType.AMENDMENT)) {

                        // LCAmendedEvent
                        // Only amendedLc is used, so can use letterOfCredit
                        LCAmendedEvent lcAmendedEvent = new LCAmendedEvent(tradeService, letterOfCredit, letterOfCredit, glts);
                        // amla.logLcAmendedEvent(lcAmendedEvent);

                    } else if (serviceType.equals(ServiceType.ADJUSTMENT)) {

                        // LCAdjustedEvent
                        LCAdjustedEvent lcAdjustedEvent = new LCAdjustedEvent(tradeService, letterOfCredit, glts);

                    } else if (serviceType.equals(ServiceType.CANCELLATION)) {

                        // LCCancelledEvent
                    }

                // Non-LC
                } else if (documentClass.equals(DocumentClass.DA) || documentClass.equals(DocumentClass.DP) ||
                           documentClass.equals(DocumentClass.OA) || documentClass.equals(DocumentClass.DR)) {

                }

            }
            println "\n---END---\n"

            returnMap.put("status", "ok");
            returnMap.put("details", "success");

        } catch(Exception e) {

            e.printStackTrace();

            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        // format return data as json
        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
}

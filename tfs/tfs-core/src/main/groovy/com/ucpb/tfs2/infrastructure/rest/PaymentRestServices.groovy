package com.ucpb.tfs2.infrastructure.rest

import com.google.gson.Gson
import com.incuventure.ddd.domain.DomainEventPublisher
import com.ucpb.tfs.application.service.ConversionService
import com.ucpb.tfs.domain.casa.services.RefCasaAccountService
import com.ucpb.tfs.domain.cdt.CDTPaymentRequest
import com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository
import com.ucpb.tfs.domain.cdt.event.CDTPaidEvent
import com.ucpb.tfs.domain.cdt.event.CDTUnpaidEvent
import com.ucpb.tfs.domain.payment.*
import com.ucpb.tfs.domain.payment.casa.CasaAccount
import com.ucpb.tfs.domain.payment.casa.parser.exception.InvalidAccountNumberFormatException
import com.ucpb.tfs.domain.payment.modes.Loan
import com.ucpb.tfs.domain.reference.RefBranchRepository
import com.ucpb.tfs.domain.security.EmployeeRepository
import com.ucpb.tfs.domain.service.ChargeType
import com.ucpb.tfs.domain.service.TradeService
import com.ucpb.tfs.domain.service.TradeServiceId
import com.ucpb.tfs.domain.service.TradeServiceRepository
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass
import com.ucpb.tfs.domain.service.enumTypes.DocumentSubType1
import com.ucpb.tfs.domain.service.enumTypes.ServiceType
import com.ucpb.tfs.interfaces.domain.Facility
import com.ucpb.tfs.interfaces.services.CustomerInformationFileService
import com.ucpb.tfs.interfaces.services.FacilityService
import com.ucpb.tfs.interfaces.services.HolidayService
import com.ucpb.tfs.interfaces.services.RatesService
import com.ucpb.tfs2.application.service.PaymentService
import com.ucpb.tfs2.application.service.casa.CasaService
import com.ucpb.tfs2.application.service.casa.exception.CasaServiceException
import com.ucpb.tfs2.application.service.casa.exception.CreditLimitNotSetException
import org.codehaus.plexus.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.Assert

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

import static com.ucpb.tfs.domain.payment.PaymentInstrumentType.CASA
import static com.ucpb.tfs.domain.payment.PaymentInstrumentType.MD
import static com.ucpb.tfs.domain.payment.PaymentInstrumentType.AP
import static com.ucpb.tfs.domain.payment.enumTypes.PaymentStatus.PROCESSING

/**
 * <pre>
 * Program_id    : PaymentRestServices
 * Program_name  : PaymentRestServices
 * SCR_Number    : IBD-12-0502-01
 * Process_Mode  : WEB
 * Frequency     : Daily
 * Input         : N/A
 * Output        : N/A
 * Description   : Web Sphere Rest Service that handles Payment-related requests
 * </pre>
 * @author Robbie Anonuevo
 * @author Marvin Volante
 * @author Arvin Patrick Guiam
 *
 */
 
 
//  PROLOGUE:
//* 	(revision)
//   SCR/ER Number:20160415-062
//   SCR/ER Description: a transaction with debti from account mode of payment was paid, but amount debited was deducted from account thrice...
//   [Revised by:] Allan Comboy Jr.
//   [Date Deployed:] 04/19/2016
//   Program [Revision] Details: Catch any key events on keyboard while paying CASA. 
//	 Any key press attempt while loading will result to an ALERT msg(Transaction on load. Please wait). (Available in CASA payment only)
//   PROJECT: CORE
//   MEMBER TYPE  : GROOVY
//   Project Name: PaymentRestServices.groovy


//  PROLOGUE:
//* 	(revision)
//   SCR/ER Number: 20160726-088
//   SCR/ER Description: Unable to pay all transaction, resulting to TFS STOP/START
//   [Revised by:] Allan Comboy Jr.
//   [Date Deployed:] 07/26/2016
//   Program [Revision] Details: Global Variable re-initialize into false when error occur. to allow payment processing again.
//   PROJECT: CORE
//   MEMBER TYPE  : GROOVY
//   Project Name: PaymentRestServices.groovy

@Path("/payment")
@Component
class PaymentRestServices {

	/**
	* Definition and Autowiring of the PaymentRepository.java
	* @see com.ucpb.tfs.domain.payment.PaymentRepository
	* @see com.ucpb.tfs.domain.payment.infrastructure.repositories.hibernate.HibernatePaymentRepository
	*/
    @Autowired
    PaymentRepository paymentRepository;

	/**
	* Definition and Autowiring of the LoanService.java
	* @see com.ucpb.tfs.domain.payment.LoanService
	*/
    @Autowired
    private LoanService loanService;
    
	/**
	* Definition and Autowiring of the FacilityService.java
	* @see com.ucpb.tfs.interfaces.services.FacilityService
	* @see com.ucpb.tfs.interfaces.services.impl.FacilityServiceImpl
	*/
    @Autowired
    private FacilityService facilityService;
    
	/**
	* Definition and Autowiring of the RatesService.java
	* @see com.ucpb.tfs.interfaces.services.RatesService
	* @see com.ucpb.tfs.interfaces.services.impl.RatesServiceImpl
	*/
    @Autowired
    private RatesService ratesService;

	/**
	* Definition and Autowiring of the TradeServiceRepository.java
	* @see com.ucpb.tfs.domain.service.TradeServiceRepository
	* @see com.ucpb.tfs.domain.service.infrastructure.repositories.hibernate.HibernateTradeServiceRepository
	*/
    @Inject
    TradeServiceRepository tradeServiceRepository;

    /**
     * Definition and Autowiring of the DomainEventPublisher.java
     * @see com.incuventure.ddd.domain.DomainEventPublisher
     */
    @Autowired
    DomainEventPublisher eventPublisher;

	/**
	* Definition and Autowiring of the CasaService.java
	* @see com.ucpb.tfs2.application.service.casa.CasaService
	*/
    @Autowired
    private CasaService casaService;

    
    /**
     * Definition and Autowiring of the PaymentService.java
     * @see com.ucpb.tfs2.application.service.PaymentService
     */
    @Autowired
    private PaymentService paymentService;

	/**
	* Definition and Autowiring of the RefBranchRepository.java
	* @see com.ucpb.tfs.domain.reference.RefBranchRepository
	*/
    @Autowired
    private RefBranchRepository refBranchRepository;

	/**
	* Definition and Autowiring of the ConversionService.java
	* @see com.ucpb.tfs.application.service.ConversionService
	*/
    @Autowired
    private ConversionService conversionService;

	/**
	* Definition and Autowiring of the EmployeeRepository.java
	* @see com.ucpb.tfs.domain.security.EmployeeRepository
	* @see com.ucpb.tfs.domain.security.infrastructure.repositories.hibernate.HibernateEmployeeRepository
	*/
    @Autowired
    private EmployeeRepository employeeRepository;
	
	/**
	* Definition and Autowiring of the HolidayService.java
	* @see com.ucpb.tfs.interfaces.services.HolidayService
	* @see com.ucpb.tfs.interfaces.services.impl.HolidayServiceImpl
	*/
	@Autowired
	private HolidayService holidayService;
	
	/**
	* Definition and Autowiring of the CustomerInformationFileService.java
	* @see com.ucpb.tfs.interfaces.services.CustomerInformationFileService
	* @see com.ucpb.tfs.interfaces.services.impl.CustomerInformationFileServiceImpl
	*/
	@Autowired
	private CustomerInformationFileService customerInformationFileService;

	
	/**
	 * Definition and Autowiring of the CDTPaymentRequestRepository.java
	 * @see com.ucpb.tfs.domain.cdt.CDTPaymentRequestRepository
	 * @see com.ucpb.tfs.domain.cdt.infrastructure.repositories.hibernate.HibernateCDTPaymentRequestRepository
	 */
    @Autowired
    private CDTPaymentRequestRepository cdtPaymentRequestRepository;

	/**
	* Definition and Autowiring of the RefCasaAccountService.java
	* @see com.ucpb.tfs.domain.casa.services.RefCasaAccountService
	* @see com.ucpb.tfs.domain.casa.services.RefCasaAccountServiceImpl
	*/
    @Autowired
    private RefCasaAccountService refCasaAccountService

    private static final String ERROR_MESSAGE = "ERRDSC";
    private static final String PAYMENT_TERM_CODE = "paymentTerm";
    private static final String PAYMENT_TERM = "paymentTerm";
    private static final Gson GSON = new Gson();


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/details")
    public Response getPaymentDetails(@Context UriInfo allUri) {
        println "inside getPaymentDetails rest service"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            // if ETS number was specified ...
            if (jsonParams.get("tradeServiceId") != null) {
                List payment = paymentRepository.getPaymentBy(new TradeServiceId(jsonParams.get("tradeServiceId").toString()));
                returnMap.put("details", payment)
            }

            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/validateCasaTransactionAmount")
    public Response validateCasaAmount(@Context UriInfo allUri, String postRequestBody){
        println "inside validateCasaTransactionAmount rest service"
        Map formDetails = GSON.fromJson(postRequestBody, Map.class);
//        Assert.notNull(formDetails["unitCode"], "Unit Code must not be null!");
        Assert.notNull(formDetails["userId"], "User ID must not be null!");
        Assert.notNull(formDetails["amount"], "Amount must not be null!");
        Assert.notNull(formDetails["currency"], "Currency must not be null!");

        String errorMessage;
        BigDecimal transactionLimit;
        BigDecimal amount = new BigDecimal(formDetails["amount"].toString().replaceAll(",", ""));
        boolean requiresValidation = false;
        try{
//            transactionLimit = casaService.getCasaTransactionLimit(formDetails["unitCode"]);
            transactionLimit = casaService.getCasaTransactionLimit(formDetails["userId"]);

            if(!"PHP".equals(formDetails["currency"])){
                BigDecimal phpTransactionAmount = conversionService.convertToPhpUsingUrr(Currency.getInstance(formDetails["currency"]),
                        new BigDecimal(formDetails["amount"]));

                if(phpTransactionAmount.compareTo(transactionLimit) > 0){
                    errorMessage = "Transaction requires an override. Credit limit is: " + transactionLimit;
                    requiresValidation = true;
                }
            } else {
                if(amount.compareTo(transactionLimit) > 0){
                    errorMessage = "Transaction requires an override. Credit limit is: " + transactionLimit;
                    requiresValidation = true;
                }
            }
        }catch(CreditLimitNotSetException e){
            errorMessage = e.getMessage() //"Unit code '"+formDetails["unitCode"]+"' does not exist.";
        }
        return Response.status(200).entity(GSON.toJson([success : StringUtils.isEmpty(errorMessage),
                errorMessage : errorMessage, limit : amount, requiresValidation : requiresValidation])).build();

    }

    /**
     * Initiates a request to obtain the status of the evaluation of the Loan Payment Request in SIBS
     * @param allUri the url containing the passed parameters from the user interface
     * @param postRequestBody the post parameters sent from the user interface needed to process the request
     * @return the status of the Loan Payment in SIBS and the errors in the Loan Payment (if there is any)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getLoanStatus")
    public Response getLoanStatus(@Context UriInfo allUri, String postRequestBody){
        println "inside getLoanStatus rest service"
        Map formDetails = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(formDetails["paymentDetailId"], "Payment Detail Id must not be null!");
        Long paymentDetailId = Long.valueOf(formDetails["paymentDetailId"]);

        List<String> loanErrors = loanService.synchronizeDetails(paymentDetailId,formDetails["reversalDENumber"]);
        PaymentDetail detail = paymentRepository.getPaymentDetail(paymentDetailId);

        Map returnMap = new HashMap();
        returnMap.put("success", loanErrors.isEmpty());
        returnMap.put("errors", loanErrors);
        returnMap.put("isProcessing",PROCESSING.equals(detail.getStatus()));
        return Response.status(200).entity(GSON.toJson(returnMap)).build();
    }

    /**
     * Initiates a request to obtain all errors of Loan Payment Request in SIBS
     * @param allUri the url containing the passed parameters from the user interface
     * @param postRequestBody the post parameters sent from the user interface needed to process the request
     * @return the errors in the Loan Payment
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getLoanErrors")
    public Response getLoanErrors(@Context UriInfo allUri, String postRequestBody) {
        println "inside getLoanErrors rest service"
        Map formDetails = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(formDetails["sequenceNumber"], "Sequence Number must not be null!");

        Map returnMap = new HashMap();
        returnMap.put("status", "ok");
        returnMap.put("errors", toErrorMessageList(loanService.getLoanErrorRecord(Long.valueOf(formDetails["sequenceNumber"]))));
        return Response.status(200).entity(GSON.toJson(returnMap)).build();
    }

    /**
     * Initiates a settlement request of specific non-loan payments in TFS
     * If the module of the payments if a Regular/Standby LC Negotiation, 
     * then this will also initiate the unearmarking of the corresponding contingent facility
     * in SIBS 
     * @param allUri the url containing the passed parameters from the user interface
     * @param postRequestBody the post parameters sent from the user interface needed to process the request
     * @return the result of the settlement request
     */
	
	//removed global variable
	def onGoingPayment = false
    @POST    
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payItem")
    public Response payTransaction(@Context UriInfo allUri, String postRequestBody) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
 
        println "inside payItem rest service ###time" + dateFormat.format(date) + "time###";
        Map<String, Object> parameterMap = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(parameterMap["userId"], "User Id must not be null!");
        Assert.notNull(parameterMap["paymentDetailId"], "Payment Detail Id must not be null!");
		def currentUser = parameterMap["userId"]
        Long paymentDetailId = Long.valueOf(parameterMap["paymentDetailId"]);
        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        PaymentDetail paymentDetail = payment.getPaymentDetail(paymentDetailId);
        TradeService tradeService = tradeServiceRepository.load(payment.getTradeServiceId());
		println "##Payment by: " + currentUser;
        String referenceNumber;
        String errorMessage;
		def responseMap = [:]
		

        //I would've preferred it if the client were to call the different webService methods instead of the server deciding which
        //one to use (e.g. client will call payViaCasaAccount for casa accounts and payTransaction for other paymentTypes.
        //Unfortunately, i'm afraid that implementing this will entail a lot of changes on the client side.
        //This design is to accommodate the front end.
		
		
		println onGoingPayment.toString() + " ## On Going Status";
		if(!onGoingPayment){
			
			try{
	
		onGoingPayment = true;
		
        if (CASA.equals(paymentDetail.getPaymentInstrumentType())) {
            println "\n\n"
            println "######################"
            println "# CASA PAYMENT START #"
            println "######################"

            println "transacting user is " + parameterMap["userId"]

            try{
                referenceNumber =  paymentService.payViaCasaAccount(payment,paymentDetailId, parameterMap["userId"], tradeService,parameterMap["supervisorId"]);
            }catch (CasaServiceException e){
				if(e.getErrorCode().equalsIgnoreCase("DEADMA") || e.getErrorCode().equalsIgnoreCase("DUPLICATE")){
					errorMessage = e.getMessage()
					responseMap << [deadma:true]
				}else{
					errorMessage = e.getErrorCode() + " : "  + e.getCasaErrorMessage();
				}
//				CasaService.resetDuplicateFilterVariables();
            }
            println "####################"
            println "# CASA PAYMENT END #"
            println "####################"
            println "\n\n"

        } else if (paymentDetail.getPaymentInstrumentType()?.isLoan()) {
            println "\n\n"
            println "######################"
            println "# LOAN PAYMENT START #"
            println "######################"
            referenceNumber = String.valueOf(paymentService.payByLoan(paymentDetailId, mapParametersToLoan(parameterMap), parameterMap["userId"], tradeService));
            println "####################"
            println "# LOAN PAYMENT END #"
            println "####################"
            println "\n\n"
        } else if (MD.equals(paymentDetail.getPaymentInstrumentType())) {
            paymentService.payTransaction(payment,paymentDetailId, tradeService, tradeService.getTradeProductNumber().toString());
        } else if (AP.equals(paymentDetail.getPaymentInstrumentType())) {
        	paymentService.payTransaction(payment,paymentDetailId, tradeService, tradeService.getTradeProductNumber().toString(), parameterMap["referenceId"]);
        } else {
            paymentService.payTransaction(payment,paymentDetailId, tradeService);
        }

        //Taken directly from the payCommandHandler. Will look for a place to refactor this
        // TODO: this should not be here but until we fix this payment event sequence thing, it needs to be here for now
        // this is specific to CDT only

        if (DocumentClass.CDT.equals(tradeService.getDocumentClass()) &&
                ServiceType.PAYMENT.equals(tradeService.getServiceType())) {

            println "i am CDT"

            String iedieirdNumber = (String) tradeService.getTradeServiceReferenceNumber().toString();
            String processingUnitCode = tradeService.getDetails().get("processingUnitCode") ?
                                        (String) tradeService.getDetails().get("processingUnitCode").toString() :
                                        (String) tradeService.getDetails().get("unitCode").toString();

            CDTPaidEvent cdtPaidEvent = new CDTPaidEvent(iedieirdNumber, payment, processingUnitCode, tradeService)
            eventPublisher.publish(cdtPaidEvent)

            CDTPaymentRequest cdtPaymentRequest = cdtPaymentRequestRepository.load(iedieirdNumber);
//            if (tradeService.getPaymentStatus() == PaymentStatus.PAID) {
//                PaymentRequestPaidEvent paymentRequestPaidEvent = new PaymentRequestPaidEvent(iedieirdNumber, processingUnitCode);
//                eventPublisher.publish(paymentRequestPaidEvent);
//            }
            if (cdtPaymentRequest.getPaymentReferenceNumber()) {
                responseMap << [paymentReferenceNumber: cdtPaymentRequest.getPaymentReferenceNumber()]
            }
        }
		
		if (StringUtils.isEmpty(errorMessage) && DocumentClass.LC == tradeService.documentClass &&
			ServiceType.NEGOTIATION.equals(tradeService.serviceType) &&
			(DocumentSubType1.REGULAR.equals(tradeService.documentSubType1) ||
			DocumentSubType1.STANDBY.equals(tradeService.documentSubType1)) &&
			payment.chargeType == ChargeType.PRODUCT) {
			
			println "--------------------Earmarking--------------------"
			Currency currency = Currency.getInstance(tradeService?.details?.get("currency") ?: tradeService?.details?.get("negotiationCurrency"));
			println "currency: " + currency
			
			BigDecimal amount = new BigDecimal(tradeService?.details?.get("negotiationAmount"))
			BigDecimal outstandingAmount = new BigDecimal(tradeService?.details?.get("outstandingBalance"))
			println "amount: " + amount
			println "outstandingAmount: " + outstandingAmount
			println "amountInLcCurrency: " + paymentDetail.amountInLcCurrency
			
			Boolean isReinstated = Boolean.FALSE;
			if (tradeService.isForReinstatement()) {
				isReinstated = Boolean.TRUE;
			}
			
			facilityService.updateAvailmentAmountUnearmark(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), paymentDetail.amountInLcCurrency, outstandingAmount, isReinstated, amount);
		}

        responseMap << [success: StringUtils.isEmpty(errorMessage),
                referenceNumber : referenceNumber, errorMessage : errorMessage]

		onGoingPayment = false;
        println 'responseMap ' + responseMap
		println onGoingPayment.toString() + " ## End On Going Status ";
        return Response.status(200).entity(GSON.toJson(responseMap)).build();
				
		
			}catch(Exception e){
				onGoingPayment = false;
				println onGoingPayment.toString() + " ### End On Going Status ";
				}finally{
					onGoingPayment = false;
					println onGoingPayment.toString() + " #### End On Going Status ";
					}
		}
		else{

			println("Payment is on going!!!");		
		
		}
		

    }

    /**
     * Initiates a settlement reversal request of specific paid non-CASA payments in TFS
     * If the type of payment is loan, it will also initiate a Loan Payment Reversal in SIBS
     * Also, if the module of the payments if a Regular/Standby LC Negotiation, 
     * then this will also initiate the reearmarking of the corresponding contingent facility
     * in SIBS 
     * @param allUri the url containing the passed parameters from the user interface
     * @param postRequestBody the post parameters sent from the user interface needed to process the request
     * @return the result of the settlement reversal request
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/reverseItem")
    public Response reverseTransaction(@Context UriInfo allUri, String postRequestBody) {
        println "inside reverseItem rest service"
        Map<String, Object> parameterMap = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(parameterMap["paymentDetailId"], "Payment Detail Id must not be null!");
        Long paymentDetailId = Long.valueOf(parameterMap["paymentDetailId"]);
        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        TradeService tradeService = tradeServiceRepository.load(payment.getTradeServiceId());
        PaymentDetail paymentDetail = payment.getPaymentDetail(paymentDetailId);
        String errorMessage;
        boolean result = false;
        String referenceNumber;
        TradeServiceId reversalTradeServiceId;

        if(!StringUtils.isEmpty(parameterMap["reversalDENumber"])){
            reversalTradeServiceId = new TradeServiceId(parameterMap["reversalDENumber"]);
        }

        if(CASA.equals(paymentDetail.getPaymentInstrumentType())){
            println "\n\n"
            println "######################"
            println "# CASA REVERSE START #"
            println "######################"
            try{
                result = paymentService.reverseCasaPayment(paymentDetailId,parameterMap["userId"],tradeService,reversalTradeServiceId, parameterMap["supervisorId"]);
            }catch(CasaServiceException e){
                errorMessage = e.getMessage();
            }
            println "####################"
            println "# CASA REVERSE END #"
            println "####################"
            println "\n\n"
        }else if(paymentDetail.getPaymentInstrumentType()?.isLoan()){
            referenceNumber = String.valueOf(paymentService.reverseLoan(paymentDetailId,parameterMap["userId"]));
        }else if (paymentDetail.getPaymentInstrumentType().equals(PaymentInstrumentType.MD)){
            result = paymentService.reversePayment(paymentDetailId,tradeService,reversalTradeServiceId, tradeService.getTradeProductNumber().toString());
        }else{
            result = paymentService.reversePayment(paymentDetailId,tradeService,reversalTradeServiceId);
        }

        //Taken directly from the payCommandHandler. Will look for a place to refactor this
        // TODO: this should not be here but until we fix this payment event sequence thing, it needs to be here for now
        // this is specific to CDT only
        if(DocumentClass.CDT.equals(tradeService.getDocumentClass()) &&
                ServiceType.PAYMENT.equals(tradeService.getServiceType())) {

            String iedieirdNumber = (String) tradeService.getTradeServiceReferenceNumber().toString();

//            CDTUnpaidEvent cdtUnpaidEvent = new CDTUnpaidEvent(iedieirdNumber, tradeService)
            CDTUnpaidEvent cdtUnpaidEvent = new CDTUnpaidEvent(iedieirdNumber, tradeService.getTradeServiceId())
            eventPublisher.publish(cdtUnpaidEvent);

//            if(PaymentStatus.UNPAID.equals(tradeService.getPaymentStatus())) {
//                PaymentRequestUnpaidEvent paymentRequestUnpaidEvent = new PaymentRequestUnpaidEvent(iedieirdNumber);
//                eventPublisher.publish(paymentRequestUnpaidEvent);
//            }
        }
		
		if (result && DocumentClass.LC == tradeService.documentClass &&
			ServiceType.NEGOTIATION.equals(tradeService.serviceType) &&
			(DocumentSubType1.REGULAR.equals(tradeService.documentSubType1) ||
			DocumentSubType1.STANDBY.equals(tradeService.documentSubType1)) &&
			payment.chargeType == ChargeType.PRODUCT) {
			
			println "--------------------Unearmarking--------------------"
			Currency currency = Currency.getInstance(tradeService?.details?.get("currency") ?: tradeService?.details?.get("negotiationCurrency"));
			println "currency: " + currency
			
			BigDecimal outstandingAmount = new BigDecimal(tradeService?.details?.get("outstandingBalance"))
			println "outstandingAmount: " + outstandingAmount
			println "amountInLcCurrency: " + paymentDetail.amountInLcCurrency
			
			Boolean isReinstated = Boolean.FALSE;
			if (tradeService.isForReinstatement()) {
				isReinstated = Boolean.TRUE;
			}
			
			facilityService.updateAvailmentAmountEarmark(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), paymentDetail.amountInLcCurrency, outstandingAmount, isReinstated);
		}

        return Response.status(200).entity(GSON.toJson([success: result, errorMessage : errorMessage, referenceNumber : referenceNumber])).build();
    }

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/errorCorrectPayment")
	public Response errorCorrectPayment(@Context UriInfo allUri, String postRequestBody) {
		println "inside errorCorrectPayment rest service"
		Map<String, Object> parameterMap = GSON.fromJson(postRequestBody, Map.class);
		Assert.notNull(parameterMap["paymentDetailId"], "Payment Detail Id must not be null!");
		Long paymentDetailId = Long.valueOf(parameterMap["paymentDetailId"]);
		Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
		TradeService tradeService = tradeServiceRepository.load(payment.getTradeServiceId());
		PaymentDetail paymentDetail = payment.getPaymentDetail(paymentDetailId);
		String errorMessage="";
		boolean result = false;
		TradeServiceId reversalTradeServiceId;

		if(!StringUtils.isEmpty(parameterMap["reversalDENumber"])){
			reversalTradeServiceId = new TradeServiceId(parameterMap["reversalDENumber"]);
		}

		try{
			result = paymentService.reversePayment(paymentDetailId,tradeService,reversalTradeServiceId);
		}catch(Exception e){
			errorMessage = e.getMessage();
		}

		if(DocumentClass.CDT.equals(tradeService.getDocumentClass()) &&
				ServiceType.PAYMENT.equals(tradeService.getServiceType())) {
			String iedieirdNumber = (String) tradeService.getTradeServiceReferenceNumber().toString();

			CDTUnpaidEvent cdtUnpaidEvent = new CDTUnpaidEvent(iedieirdNumber, tradeService.getTradeServiceId())
			eventPublisher.publish(cdtUnpaidEvent);
		}
		
		if (result && DocumentClass.LC == tradeService.documentClass &&
			ServiceType.NEGOTIATION.equals(tradeService.serviceType) &&
			(DocumentSubType1.REGULAR.equals(tradeService.documentSubType1) ||
			DocumentSubType1.STANDBY.equals(tradeService.documentSubType1)) &&
			payment.chargeType == ChargeType.PRODUCT) {
			
			println "--------------------Unearmarking--------------------"
			Currency currency = Currency.getInstance(tradeService?.details?.get("currency") ?: tradeService?.details?.get("negotiationCurrency"));
			println "currency: " + currency
			
			BigDecimal outstandingAmount = new BigDecimal(tradeService?.details?.get("outstandingBalance"))
			println "outstandingAmount: " + outstandingAmount
			println "amountInLcCurrency: " + paymentDetail.amountInLcCurrency
			
			Boolean isReinstated = Boolean.FALSE;
			if (tradeService.isForReinstatement()) {
				isReinstated = Boolean.TRUE;
			}
			
			facilityService.updateAvailmentAmountEarmark(tradeService.getTradeProductNumber().toString(), currency.getCurrencyCode(), paymentDetail.amountInLcCurrency, outstandingAmount, isReinstated);
		}

		return Response.status(200).entity(GSON.toJson([success: result, errorMessage : errorMessage])).build();
	}

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payViaCasa")
    public Response payViaCasaAccount(@Context UriInfo allUri, String postRequestBody) {
        println "inside payViaCasa rest service"
        Map<String, Object> parameterMap = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(parameterMap["userId"], "User Id must not be null!");
        Assert.notNull(parameterMap["paymentDetailId"], "Payment Detail Id must not be null!");

        Long paymentDetailId = Long.valueOf(parameterMap["paymentDetailId"]);
        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        TradeService tradeService = tradeServiceRepository.load(payment.getTradeServiceId());
        String errorMessage;
        String transactionNumber;
        try{
            transactionNumber =  paymentService.payViaCasaAccount(payment,paymentDetailId, parameterMap["userId"], tradeService);
        }catch(CasaServiceException e){
            errorMessage = e.getCasaErrorMessage();
        }
        return Response.status(200).entity(GSON.toJson([success: !StringUtils.isEmpty(transactionNumber), transactionNumber : transactionNumber, errorMessage : errorMessage])).build();
    }

	
    /**
     * Initiates a settlement request of specific loan payments in TFS and in SIBS
     * @deprecated current system initiates payment of loans via the CreateLoanCommandHandler.java
     * @see com.ucpb.tfs.application.commandHandler.CreateLoanCommandHandler
     * @param allUri the url containing the passed parameters from the user interface
     * @param postRequestBody the post parameters sent from the user interface needed to process the request
     * @return the result of the settlement request and the sequence number of the loan payment
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/payViaLoan")
    public Response payViaLoan(@Context UriInfo allUri, String postRequestBody) {
        println "inside payViaLoan rest service"
        Map<String, Object> parameterMap = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(parameterMap["userId"], "User Id must not be null!");
        Assert.notNull(parameterMap["paymentDetailId"], "Payment Detail Id must not be null!");

        Long paymentDetailId = Long.valueOf(parameterMap["paymentDetailId"]);
        Payment payment = paymentRepository.getPaymentByPaymentDetailId(paymentDetailId);
        TradeService tradeService = tradeServiceRepository.load(payment.getTradeServiceId());

        Long sequenceNumber =  paymentService.payByLoan(paymentDetailId, mapParametersToLoan(parameterMap), parameterMap["userId"], tradeService);

        return Response.status(200).entity(GSON.toJson([success: true, sequenceNumber : sequenceNumber])).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getCasaAccountStatus")
    public Response getAccountStatus(@Context UriInfo allUri, String postRequestBody) {
        println "inside getCasaAccountStatus rest service"
        Map<String, Object> parameterMap = GSON.fromJson(postRequestBody, Map.class);
        Assert.notNull(parameterMap["userId"], "User Id must not be null!");
        Assert.notNull(parameterMap["accountNumber"], "Account Number must not be null!");
        Assert.notNull(parameterMap["currency"], "Currency must not be null!");
        String errorMessage;
        CasaAccount account;

        def response = [:]

		
        try{
//            account = paymentService.getAccountDetails(parameterMap["accountNumber"],parameterMap["userId"]);
            // MARV: added currency code for message string
            account = paymentService.getAccountDetails(parameterMap["accountNumber"],
                                                       parameterMap["userId"],
                                                       Currency.getInstance(parameterMap["currency"]));

			String accountName = "";
//			try{
//				List<Map<String,Object>> accounts = customerInformationFileService.getCifFullNameByCifName('%'+account?.getAccountName().trim()+'%');
//				if (accounts.size() > 0){
//					for(Map<String,Object> accountDetail : accounts){
//						println "accountDetail.toString(): "+accountDetail.toString()
//						if (accountDetail.get("ACCOUNT_FULL_NAME") != null && !accountDetail.get("ACCOUNT_FULL_NAME")?.toString()?.trim().isEmpty() && !accountDetail.get("ACCOUNT_FULL_NAME")?.toString()?.trim()?.equals("")){
//							accountName = accountDetail.get("ACCOUNT_FULL_NAME").toString().trim();
//						}
//					}
//				} else {
//					accountName = account?.getAccountName();
//				}
//				/*List<Map<String,Object>> accounts = customerInformationFileService.getCasaAccountsByNumberAndCurrency(account?.getAccountNumber(), account?.getCurrency()?.getName());
//				if (accounts.size() > 0){
//					for(Map<String,Object> accountDetail : accounts){
//						if (accountDetail.get("ACCOUNT_NAME").toString().trim().contains(account?.getAccountName()) || account?.getAccountName().contains(accountDetail.get("ACCOUNT_NAME").toString().trim())){
//							if (!accountDetail.get("ACCOUNT_FULL_NAME").equals(null) && !accountDetail.get("ACCOUNT_FULL_NAME").toString().trim().isEmpty() && !accountDetail.get("ACCOUNT_FULL_NAME").toString().trim().equals("")){
//								accountName = accountDetail.get("ACCOUNT_FULL_NAME").toString().trim();
//							} else {
//								accountName = accountDetail.get("ACCOUNT_NAME").toString().trim();
//							}
//						} else {
//							accountName = account?.getAccountName();
//						}
//					}
//				} else {
//					accountName = account?.getAccountName();
//				}*/
//			}catch (Exception e){
				accountName = account?.getAccountName();
//			}finally{
            response = [success: StringUtils.isEmpty(errorMessage),
                    accountNumber : account?.getAccountNumber(),
                    status : account?.getAccountStatus(),
                    accountName : accountName,
                    currency : account?.getCurrency()?.getName(),
                    accountType : account?.getAccountType()];
//			}
        }catch(CasaServiceException e){
            errorMessage = e.getCasaErrorMessage();

            response.put("status", "error");
            response.put("error", errorMessage);
        } catch(InvalidAccountNumberFormatException ianf) {
            errorMessage = ianf.getMessage();

            response.put("status", "error");
            response.put("error", errorMessage);
        }

        return Response.status(200).entity(GSON.toJson(response)).build();
    }
	
	/**
	 * Initiates a request to verify in SIBS if the specified date is a holiday or not a business day for the specific branch.
	 * @param allUri the url containing the passed parameters from the user interface
	 * @param postRequestBody the post parameters sent from the user interface needed to process the request
	 * @return the validation result of the date
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getHoliday")
	public Response isHolidayOrIsNotABusinessDay(@Context UriInfo allUri, String postRequestBody) {
		Map<String, Object> parameterMap = GSON.fromJson(postRequestBody, Map.class);
		Assert.notNull(parameterMap["date"], "Date must not be null!");
		Assert.notNull(parameterMap["branchCode"], "Branch Code must not be null!");
		
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date date;
		Boolean holiday = null;
		boolean success = true;
		def returnMap = [:]
		try{
			date = df.parse(parameterMap["date"])
			holiday = holidayService.isHolidayOrIsNotABusinessDay(date, parameterMap["branchCode"].toString());
			if(holiday == null){
				success = false;
			}
 
		}catch (ParseException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		returnMap.put("success", success);
		returnMap.put("result", holiday)
		
		return Response.status(200).entity(GSON.toJson(returnMap)).build();
	}

    /**
     * Internal method that formats the data into a Loan Object
     * @param parameterMap the raw data to be formatted
     * @return the formatted Loan object
     */
    private Loan mapParametersToLoan(Map<String, Object> parameterMap) {
        Facility facility = new Facility();
        facility.setFacilityId(parameterMap["facilityId"]);
        facility.setFacilityReferenceNumber(parameterMap["facilityReferenceNumber"]);
        facility.setFacilityType(parameterMap["facilityType"]);

        Boolean withCramApproval = parameterMap["withCramApproval"] != null ? Boolean.valueOf(parameterMap["withCramApproval"]) : null;

        Loan loan = new Loan();
        loan.setFacility(facility);
        loan.setPaymentCode(Integer.valueOf(parameterMap["paymentCode"]));
        loan.setApprovedByCram(withCramApproval);
        loan.setMaturityDate(parameterMap["maturityDate"]);

        if(parameterMap["loanTerm"] != null){
            loan.setLoanTerm(Integer.valueOf(parameterMap["loanTerm"]));
        }
        return loan;
    }
	
    /**
     * Internal method that obtains the error messages from the source
     * @param sourceList the complete list of record of the errors 
     * @return the list of error messages
     */
    private List<String> toErrorMessageList(List<Map<String, Object>> sourceList) {
        List<String> result = new ArrayList<String>();
        for (Map<String, Object> row : sourceList) {
            result.add(StringUtils.trim(row.get(ERROR_MESSAGE)));
        }
        return result;
    }


    /**
     * loads ua loan payment
     * @param allUri the url containing the passed parameters from the user interface
     * @return the ua loan payment
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getUaLoanPayment")
    public Response getUaLoanPayment(@Context UriInfo allUri) {
        println "inside getUaLoanPayment rest service"
        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            def uaLoanPayment = paymentRepository.getUaLoanPayment((String) jsonParams.get("referenceNumber"));

            println "uaLoanPayment > " + uaLoanPayment;

            returnMap.put("details", uaLoanPayment);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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

    /**
     * Validation if there is at least one settled payment in a specific transaction module
     * @param allUri the url containing the passed parameters from the user interface
     * @return the result of validation
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/checkPaymentStatusOfPayments")
    public Response checkPaymentStatusOfPayments(@Context UriInfo allUri) {
        println "inside checkPaymentStatusOfPayments"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            def hasPaidPayment = paymentRepository.checkIfHasPaidPayment(new TradeServiceId((String) jsonParams.get("tradeServiceId")))

            println "hasPaidPayment > " + hasPaidPayment;

            returnMap.put("details", hasPaidPayment);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getSavedProductPayments")
    public Response getSavedProductPayments(@Context UriInfo allUri) {
        println "inside getSavedProductPayments"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            def savedProductPayments = paymentRepository.getSavedProductPayments(new TradeServiceId((String) jsonParams.get("tradeServiceId")))

            returnMap.put("response", savedProductPayments);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getCasaAccounts")
    public Response getCasaAccounts(@Context UriInfo allUri) {
        println "inside getCasaAccounts"

        Gson gson = new Gson();

        String result = "";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for (String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {

            refCasaAccountService.testMethod()
//            returnMap.put("response", savedProductPayments);
            returnMap.put("status", "ok");


        } catch (Exception e) {

            Map errorDetails = new HashMap();

            e.printStackTrace();

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
	
	/**
	* Validation if there is at least one unsettled payment in a specific transaction module
	* @param allUri the url containing the passed parameters from the user interface
	* @return the result of validation
	*/
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/checkUnpaidStatusOfPayments")
	public Response checkUnpaidPayments(@Context UriInfo allUri) {
		println "inside checkUnpaidPayments"

		Gson gson = new Gson();

		String result = "";
		Map returnMap = new HashMap();
		Map jsonParams = new HashMap<String, String>();

		// get all query parameters
		MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();
		println "allUri.getQueryParameters() :" + allUri.getQueryParameters()
		for (String key : mpAllQueParams.keySet()) {
			println 'key : ' + key
			// if there are multiple instances of the same param, we only use the first one
			jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
		}

		try {
			def hasUnpaidPayment = paymentRepository.checkIfHasUnpaidPayment(new TradeServiceId((String) jsonParams.get("tradeServiceId")))

			println "hasUnpaidPayment > " + hasUnpaidPayment;

			returnMap.put("details", hasUnpaidPayment);
			returnMap.put("status", "ok");


		} catch (Exception e) {

			Map errorDetails = new HashMap();

			e.printStackTrace();

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

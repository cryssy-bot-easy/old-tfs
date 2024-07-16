package com.ucpb.tfs2.infrastructure.rest
import com.google.gson.Gson
import com.ucpb.tfs.batch.report.dw.dao.TradeProductDao;
import com.ucpb.tfs.domain.product.DocumentNumber
import com.ucpb.tfs.domain.product.ExportBillsRepository
import com.ucpb.tfs.domain.product.TradeProductRepository
import com.ucpb.tfs.domain.service.TradeProductNumber
import com.ucpb.tfs.domain.service.TradeServiceRepository;
import com.ucpb.tfs.domain.service.enumTypes.DocumentClass;
import com.ucpb.tfs.domain.service.enumTypes.DocumentType;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.domain.settlementaccount.MarginalDepositRepository
import com.ucpb.tfs.domain.settlementaccount.SettlementAccountNumber

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.*

/**  PROLOGUE:
 * 	(revision)
	SCR/ER Number:
	SCR/ER Description: Redmine #4118 - If with outstanding EBC is tagged as Yes, the drop down lists of EBC document numbers
	are not complete. Example: Document number 909-11-307-17-00004-2 is not included in the list but it should be part of the
	drop down list since this is an approved EBC Nego and it is still outstanding.
	[Revised by:] John Patrick C. Bautista
	[Date Deployed:] 06/16/2017
	Program [Revision] Details: Added new method to query from Export Bills without the BP Currency restriction.
	PROJECT: CORE
	MEMBER TYPE  : Groovy
	Project Name: ProductsRestServices
 */
 
 /**
 * (revision)
 *	SCR/ER Number:
 *	SCR/ER Description: EBP Negotiation - Data Entry Inquiry (Redmine# 4152)
 *	[Revised by:] Brian Harold A. Aquino
 *	[Date revised:] 02/20/2017 (tfs Rev# 7258)
 *	[Date deployed:] 06/16/2017
 *	Program [Revision] Details: Created a new parameter that retrieves all the values in Export Bills.
 *	Member Type: Groovy
 *	Project: CORE
 *	Project Name: ProductsRestServices.groovy
 */

/**
 * PROLOGUE
 * SCR/ER Description: 20181217-064 - The program that generates MT 707 during LC Amendment extracts the expiry country code in field 31D instead of the expiry country name.
 *	[Revised by:] Jesse James Joson
 *	Program [Revision] Details: Modify the return for getting the LC detail to include the country name of the expiry country code.
 *	Date deployment: 12/18/2018
	 PROJECT: CORE
	 MEMBER TYPE  : Groovy
	 Project Name: ProductsRestServices
 */

@Path("/product")
@Component
class ProductsRestServices {

    @Autowired
    TradeProductRepository tradeProductRepository;

    @Autowired
    ExportBillsRepository exportBillsRepository;

    @Autowired
    MarginalDepositRepository marginalDepositRepository
	
	@Autowired
	TradeServiceRepository tradeServiceRepository
	
	@Autowired
	TradeProductDao tradeProductDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/lc/details/{documentNumber}")
    public Response getLetterOfCreditDetails(@PathParam("documentNumber") String documentNumber, @Context UriInfo allUri) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        // get all query parameters
        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

//        for(String key : mpAllQueParams.keySet()) {
//
//            // if there are multiple instances of the same param, we only use the first one
//            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
//        }

//        String documentNumber = jsonParams["documentNumber"]

        try {

            if((documentNumber != null) && (!documentNumber.equalsIgnoreCase(""))) {

                Map tradeProduct = tradeProductRepository.loadToMap(new DocumentNumber(documentNumber))
				String expiryCountryName = "";
				if(tradeProduct.get("expiryCountryCode") != null && !tradeProduct.get("expiryCountryCode").toString().equalsIgnoreCase("")){
					println "expiryCountryCode" + tradeProduct.get("expiryCountryCode").toString();
					expiryCountryName = tradeProductDao.getCountryName(tradeProduct.get("expiryCountryCode").toString());
				}
				
				if(expiryCountryName != null && !expiryCountryName.equalsIgnoreCase("")){
					tradeProduct.put("expiryCountryName", expiryCountryName);
					println "expiryCountryName" + expiryCountryName
				}

                if (tradeProduct != null) {

                    returnMap.put("status", "ok")
                    returnMap.put("details", tradeProduct)

                } else {
                    returnMap.put("status", "error");
                    returnMap.put("details", "document does not exist")
                }

            }
            else {
                returnMap.put("status", "error");
                returnMap.put("details", "missing documentNumber")
            }

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/exportAdvising/details/{documentNumber}")
    public Response getExportAdvisingDetails(@PathParam("documentNumber") String documentNumber) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            if((documentNumber != null) && (!documentNumber.equalsIgnoreCase(""))) {

                Map tradeProduct = tradeProductRepository.loadToMapExportAdvising(new DocumentNumber(documentNumber))

                if (tradeProduct != null) {

                    returnMap.put("status", "ok")
                    returnMap.put("details", tradeProduct)

                } else {
                    returnMap.put("status", "error");
                    returnMap.put("details", "document does not exist")
                }

            }
            else {
                returnMap.put("status", "error");
                returnMap.put("details", "missing documentNumber")
            }

        } catch(Exception e) {

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/exportBills/details/{documentNumber}")
    public Response getExportBillsDetails(@PathParam("documentNumber") String documentNumber) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            if((documentNumber != null) && (!documentNumber.equalsIgnoreCase(""))) {

                Map tradeProduct = tradeProductRepository.loadToMapExportBills(new DocumentNumber(documentNumber))

				ServiceType servType = "NEGOTIATION"
				DocumentType docType = "FOREIGN"
				DocumentClass docClass = "BC"
				
				
                if (tradeProduct != null) {


                    List exportBills = exportBillsRepository.getAllExportBillsByNegotiationNumber(new DocumentNumber(documentNumber))

					Map tradeService = tradeServiceRepository.getTradeServiceBy(new TradeProductNumber(documentNumber), servType, docType, docClass)
					
                    /*BigDecimal bpAmount = BigDecimal.ZERO
                    Currency bpCurrency = null

                    exportBills.each {
                        bpAmount = bpAmount.add(it.bpAmount)
                        bpCurrency = it.bpCurrency
                    }

                    tradeProduct.put("bpAmount", bpAmount)
                    tradeProduct.put("bpCurrency",bpCurrency)*/

                    returnMap.put("status", "ok")
                    returnMap.put("details", tradeProduct)
					returnMap.put("exportDetails", tradeService)
//					returnMap.put("tradeService", tradeService)

                } else {
                    returnMap.put("status", "error");
                    returnMap.put("details", "document does not exist")
                }

            }
            else {
                returnMap.put("status", "error");
                returnMap.put("details", "missing documentNumber")
            }

        } catch(Exception e) {

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/rebate/details/{documentNumber}")
    public Response getRebateDetails(@PathParam("documentNumber") String documentNumber) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            if((documentNumber != null) && (!documentNumber.equalsIgnoreCase(""))) {

                Map tradeProduct = tradeProductRepository.loadToMapRebate(new DocumentNumber(documentNumber))

                if (tradeProduct != null) {

                    returnMap.put("status", "ok")
                    returnMap.put("details", tradeProduct)

                } else {
                    returnMap.put("status", "error");
                    returnMap.put("details", "document does not exist")
                }

            }
            else {
                returnMap.put("status", "error");
                returnMap.put("details", "missing documentNumber")
            }

        } catch(Exception e) {

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/marginalDeposit/details/{documentNumber}")
    public Response loadMarginalDeposit(@PathParam("documentNumber") String documentNumber) {

        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();

        try {

            def marginalDeposit = marginalDepositRepository.loadToMap(new SettlementAccountNumber(documentNumber))

            returnMap.put("status", "ok");
            returnMap.put("details", marginalDeposit)

        } catch (Exception e) {
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/importProducts/search")
    public Response searchAllLetterOfCredit(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            List lcList = tradeProductRepository.searchAllImportProducts(
                    jsonParams.get("documentNumber") ? (String) jsonParams.get("documentNumber") : null,
                    jsonParams.get("cifName") ? (String) jsonParams.get("cifName") : null,
                    jsonParams.get("cifNumber") ? (String) jsonParams.get("cifNumber") : null,
                    jsonParams.get("unitcode") ? (String) jsonParams.get("unitcode") : null)

            returnMap.put("status", "ok");
            returnMap.put("details", lcList)

        } catch (Exception e) {
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allimports/search")
    public Response searchAllImports(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            List importsList = tradeProductRepository.findAllImportProducts(
                    jsonParams.get("documentNumber") ? (String) jsonParams.get("documentNumber") : null,
					jsonParams.get("productType") ? (String) jsonParams.get("productType") : null,
                    jsonParams.get("cifName") ? (String) jsonParams.get("cifName") : null,
                    jsonParams.get("cifNumber") ? (String) jsonParams.get("cifNumber") : null,
                    jsonParams.get("unitCode") ? (String) jsonParams.get("unitCode") : null,
                    jsonParams.get("unitcode") ? (String) jsonParams.get("unitcode") : null)

            returnMap.put("status", "ok");
            returnMap.put("details", importsList)

        } catch (Exception e) {
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/import/get/{documentNumber}")
    public Response getImport(@PathParam("documentNumber") String documentNumber, @Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            Map importItem = tradeProductRepository.getImport(documentNumber)

            returnMap.put("status", "ok");
            returnMap.put("details", importItem)

        } catch (Exception e) {
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allexports/search")
    public Response searchAllExports(@Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            List exportsList = tradeProductRepository.findAllExportProducts(
            		jsonParams.get("documentNumber") ? (String) jsonParams.get("documentNumber") : null,
                    jsonParams.get("cifName") ? (String) jsonParams.get("cifName") : null,
                    jsonParams.get("importersName") ? (String) jsonParams.get("importersName") : null,
                    jsonParams.get("exportersName") ? (String) jsonParams.get("exportersName") : null,
                    jsonParams.get("transaction") ? (String) jsonParams.get("transaction") : null,
					jsonParams.get("unitCode") ? (String) jsonParams.get("unitCode") : null,
					jsonParams.get("unitcode") ? (String) jsonParams.get("unitcode") : null)

            returnMap.put("status", "ok");
            returnMap.put("details", exportsList)

        } catch (Exception e) {
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/export/get/{documentNumber}")
    public Response getExport(@PathParam("documentNumber") String documentNumber, @Context UriInfo allUri) {
        Gson gson = new Gson();

        String result="";
        Map returnMap = new HashMap();
        Map jsonParams = new HashMap<String, String>();

        MultivaluedMap<String, String> mpAllQueParams = allUri.getQueryParameters();

        for(String key : mpAllQueParams.keySet()) {
            // if there are multiple instances of the same param, we only use the first one
            jsonParams.put(key, mpAllQueParams.getFirst(key).toString());
        }

        try {
            Map exportItem = tradeProductRepository.getExport(documentNumber)

            returnMap.put("status", "ok");
            returnMap.put("details", exportItem)

        } catch (Exception e) {
            Map errorDetails = new HashMap();

            errorDetails.put("code", e.getMessage());
            errorDetails.put("description", e.toString());

            returnMap.put("status", "error");
            returnMap.put("error", errorDetails);
        }

        result = gson.toJson(returnMap);

        // todo: we should probably return the appropriate HTTP error codes instead of always returning 200
        return Response.status(200).entity(result).build();
    }
}

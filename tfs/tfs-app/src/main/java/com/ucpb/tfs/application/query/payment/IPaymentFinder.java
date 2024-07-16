package com.ucpb.tfs.application.query.payment;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * User: IPCVal
 * Date: 9/6/12
 */
public interface IPaymentFinder {

	List<Map<String,?>> findPaymentPNNumberByDocumentNumber(@Param("documentNumber")String documentNumber);
	
    List<Map<String,?>> findServiceChargesEtsPaymentDetail(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findServiceChargesPaymentDetail(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findProductChargeEtsPaymentDetail(@Param("tradeServiceId") String tradeServiceId);
    
    List<Map<String,?>> findProductChargePaymentDetail(@Param("tradeServiceId") String tradeServiceId);
    
    List<Map<String,?>> findProceedsEtsPaymentDetail(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findProceedsPaymentDetail(@Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> getAllProductPayments(@Param("documentNumber") String documentNumber, @Param("tradeServiceId") String tradeServiceId);

    List<Map<String,?>> findRefundablePayments(@Param("documentNumber") String documentNumber);
    
    List<Map<String,?>> findPaymentServiceCharge(@Param("tradeServiceId") String tradeServiceId);
	
    List<Map<String,?>> findAllPaymentProductForAmla(@Param("tradeServiceId") String tradeServiceId);
    
    List<Map<String,?>> findAllPaymentProductForExports(@Param("tradeServiceId") String tradeServiceId);
}

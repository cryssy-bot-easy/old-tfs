package com.ucpb.tfs.domain.accounting;

import com.ucpb.tfs.application.service.AccountingService;
import com.ucpb.tfs.domain.accounting.enumTypes.*;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.utils.CalculatorUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import javax.persistence.EnumType;
import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;


/**
 * User: giancarlo
 * Date: 9/29/12
 * Time: 3:27 PM
 */

public class AccountingEntry implements Serializable {

	
	
	
	private long id;

    //Properties that define it
    private ProductId productId;
    private ServiceType serviceType;
    private AccountingEntryType accountingEntryType;
    private AccountingEventTransactionId accountingEventTransactionId; //For tagging Purposes
    private BookCurrency lcCurrency;
    private BookCurrency settlementCurrency;


    private String unitCode;
    private String respondingUnitCode;
    private BookCode bookCode;
    private BookCurrency bookCurrency;
    private EntryType entryType;
    private String accountingCode; //GL Code

    private String formulaValue;
    private String formulaPesoValue;
    private String formulaParticulars;
    private String formulaAccountingCode;

    private Serializable compiledExpressionValue;
    private Serializable compiledExpressionPesoValue;
    private ParserContext pctx;
    
    public AccountingEntry() {
    }

    public AccountingEntry(String unitCode,
                           String respondingUnitCode,
                           BookCode bookCode,
                           BookCurrency bookCurrency,
                           EntryType entryType,
                           String accountingCode,
                           String formulaValue,
                           String formulaPesoValue,
                           String formulaParticulars,
                           String formulaAccountingCode,
                           ProductId productId,
                           ServiceType serviceType,
                           AccountingEventTransactionId accountingEventTransactionId,
                           BookCurrency lcCurrency,
                           BookCurrency settlementCurrency,
                           AccountingEntryType accountingEntryType
    ) {

        this.respondingUnitCode = respondingUnitCode;
        this.productId = productId;
        this.serviceType = serviceType;
        this.accountingEntryType = accountingEntryType;
        this.accountingEventTransactionId = accountingEventTransactionId;
        this.lcCurrency = lcCurrency;
        this.settlementCurrency = settlementCurrency;

        this.unitCode = unitCode;
        this.bookCode = bookCode;
        this.bookCurrency = bookCurrency;
        this.entryType = entryType;
        this.accountingCode = accountingCode;
        this.formulaParticulars = formulaParticulars;
        this.formulaAccountingCode = formulaAccountingCode;
        this.formulaValue = formulaValue;
        this.formulaPesoValue = formulaPesoValue;

        pctx = ParserContext.create();

        // add a reference to our Calculator Utility class and BigDecimal
        pctx.addImport("CalculatorUtils", CalculatorUtils.class);
        pctx.addImport(BigDecimal.class);
        pctx.addImport(String.class);

        // compile the expression and keep the context
        compiledExpressionValue = MVEL.compileExpression(this.formulaValue, pctx);
        compiledExpressionPesoValue = MVEL.compileExpression(this.formulaPesoValue, pctx);

    }

    private void initParserContext() {
        pctx = ParserContext.create();

        // add a reference to our Calculator Utility class and BigDecimal
        pctx.addImport("CalculatorUtils", CalculatorUtils.class);
        pctx.addImport(BigDecimal.class);
        pctx.addImport(String.class);

        // compile the expression and keep the context
        compiledExpressionValue = MVEL.compileExpression(this.formulaValue, pctx);
        compiledExpressionPesoValue = MVEL.compileExpression(this.formulaPesoValue, pctx);

    }

    // todo: remove this after testing
    @Override
    public String toString() {
        return Long.toString(this.getId());
    }


    public Boolean isAccountingEntryFor(ProductId productId, ServiceType serviceType, AccountingEventTransactionId accountingEventTransactionId) {

        return ((this.getProductId().toString().equals(productId.toString())) && (this.getServiceType() == serviceType) && (this.getAccountingEventTransactionId() == accountingEventTransactionId));
    }

    public Boolean isAccountingEntryFor(ProductId productId, ServiceType serviceType) {

        return (((this.getProductId().toString().equals(productId.toString())) && (this.getServiceType() == serviceType)));
    }

    public BigDecimal computeValue(Map<String, Object> parameters) throws Exception {
        Boolean valid = true;

        // get a map of inputs to this formula
        if (pctx == null) {
            System.out.print("PCTX is null!!");
            System.out.print("PCTX is being initialized!!");
            initParserContext();
        } else {
            System.out.print("PCTX !!");
        }

        System.out.println("formulaValue:"+formulaValue);
        Map<String, Class> inputs = pctx.getInputs();

        // iterate through the list of inputs checking if the parameters passed contain all required
        Iterator it = inputs.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println("errol "+pairs.toString());

 
            // if we can't find the parameter, say goodbye
            if (!parameters.containsKey(pairs.getKey())) {
                System.out.println("missing parameter, " + pairs.getKey() + " cannot execute");
                parameters.put(pairs.getKey().toString(), null); //Added handling that accepts null and defaults to a value, thus throwing the exception is unnecessary.
            }

        }

        // throw an Exception if we have missing paramters
        if (!valid) {
            throw new Exception("blah blah AccountingEntry  compute value is not valid ");
        }

        Object obj = MVEL.executeExpression(compiledExpressionValue, parameters);
        System.out.println("errol errol errol >> "+obj);
        BigDecimal parsedBigDecimal;
        DecimalFormat df = new DecimalFormat();
        df.setParseBigDecimal(true);
        
        try {
	        if(obj == null) {
	        	parsedBigDecimal = BigDecimal.ZERO;
	        	System.out.println("parsedBigDecimal null is "+ parsedBigDecimal);
	        }else {
	        	parsedBigDecimal = (BigDecimal) df.parse(obj.toString());
	        	System.out.println("parsedBigDecimal is "+ parsedBigDecimal);
	        }   
  
        } catch (Exception e) {
       	 
        	if (obj instanceof String) {
        		 System.out.println("errol errol errol errol "+obj.toString());
        	}else{
        		parsedBigDecimal = BigDecimal.ZERO;
        	}
        	
            e.printStackTrace();
            throw new Exception("blah blah AccountingEntry parsedBigDecimal",e);
        }
        return parsedBigDecimal;

    }

    public BigDecimal computePesoValue(Map<String, Object> parameters) throws Exception {
        Boolean valid = true;

        // get a map of inputs to this formula
        if (pctx == null) {
            System.out.print("PCTX is null!! ");
            System.out.print("PCTX is being initialized!! ");
            initParserContext();
        } else {
            System.out.print("PCTX !!");
        }

        Map<String, Class> inputs = pctx.getInputs();
        System.out.println("formulaPesoValue:"+formulaPesoValue);

        // iterate through the list of inputs checking if the parameters passed contain all required
        Iterator it = inputs.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pairs = (Map.Entry) it.next();


            // if we can't find the parameter, say goodbye
            if (!parameters.containsKey(pairs.getKey())) {
                System.out.println("missing parameter, " + pairs.getKey() + " cannot execute");
                parameters.put(pairs.getKey().toString(), null); //Added handling that accepts null and defaults to a value, thus throwing the exception is unnecessary.
            }

        }

        // throw an Exception if we have missing paramters
        if (!valid) {
            throw new Exception("blah blah AccountingEntry computePesoValue is not valid");
        }

        Object obj = MVEL.executeExpression(compiledExpressionPesoValue, parameters);

        BigDecimal parsedBigDecimal;
        DecimalFormat df = new DecimalFormat();
        df.setParseBigDecimal(true);

        // removed try catch block since the change in dddcqrs throws exception all the way
        // if there is an exception thrown, thus the assigning of parsedBigDecimal to BigDecimal.ZERO
        // will not occur

//        try {
//            parsedBigDecimal = (BigDecimal) df.parse(obj.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            parsedBigDecimal = BigDecimal.ZERO;
//        }

        // work-around: set the parsedBigDecimal to BigDecimal.ZERO by default
        parsedBigDecimal = BigDecimal.ZERO;
        // and checkes obj if not null, then assign the parsed obj to parsedBigDecimal
        if (obj != null) {
            parsedBigDecimal = (BigDecimal) df.parse(obj.toString());
        }

        return parsedBigDecimal;

    }


    public long getId() {
        return id;
    }

    public ProductId getProductId() {
        return productId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public AccountingEventTransactionId getAccountingEventTransactionId() {
        return accountingEventTransactionId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public BookCode getBookCode() {
        return bookCode;
    }

    public BookCurrency getBookCurrency() {
        return bookCurrency;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public String getAccountingCode() {
        return accountingCode;
    }

    public String getRespondingUnitCode() {
        return respondingUnitCode;
    }

    public String getParticulars() {
        return formulaParticulars;
    }

    public BookCurrency getLcCurrency() {
        return lcCurrency;
    }

    public BookCurrency getSettlementCurrency() {
        return settlementCurrency;
    }

    public AccountingEntryType getAccountingEntryType() {
        return accountingEntryType;
    }

    public void setProductId(ProductId productId) {
        this.productId = productId;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public void setAccountingEntryType(AccountingEntryType accountingEntryType) {
        this.accountingEntryType = accountingEntryType;
    }

    public void setAccountingEventTransactionId(AccountingEventTransactionId accountingEventTransactionId) {
        this.accountingEventTransactionId = accountingEventTransactionId;
    }

    public void setLcCurrency(BookCurrency lcCurrency) {
        this.lcCurrency = lcCurrency;
    }

    public void setSettlementCurrency(BookCurrency settlementCurrency) {
        this.settlementCurrency = settlementCurrency;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public void setRespondingUnitCode(String respondingUnitCode) {
        this.respondingUnitCode = respondingUnitCode;
    }

    public void setBookCode(BookCode bookCode) {
        this.bookCode = bookCode;
    }

    public void setBookCurrency(BookCurrency bookCurrency) {
        this.bookCurrency = bookCurrency;
    }

    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    public void setFormulaValue(String formulaValue) {
        this.formulaValue = formulaValue;
    }

    public void setFormulaPesoValue(String formulaPesoValue) {
        this.formulaPesoValue = formulaPesoValue;
    }

    public void setFormulaParticulars(String formulaParticulars) {
        this.formulaParticulars = formulaParticulars;
    }

    public void setFormulaAccountingCode(String formulaAccountingCode) {
        this.formulaAccountingCode = formulaAccountingCode;
    }

    public void setCompiledExpressionValue(Serializable compiledExpressionValue) {
        this.compiledExpressionValue = compiledExpressionValue;
    }

    public void setCompiledExpressionPesoValue(Serializable compiledExpressionPesoValue) {
        this.compiledExpressionPesoValue = compiledExpressionPesoValue;
    }

    public void setPctx(ParserContext pctx) {
        this.pctx = pctx;
    }

}

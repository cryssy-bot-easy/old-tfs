package com.ucpb.tfs.domain.reference;

import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.utils.CalculatorUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Jett
 * Date: 7/24/12
 */
public class TradeServiceChargeReference implements Serializable {

//    private String id;
    private long id;

    private ChargeId chargeId;

    private ProductId productId;
    private ServiceType serviceType;


    private String formula;

    private Serializable compiledExpression;
    private ParserContext pctx;

    public TradeServiceChargeReference() {
    }

    public TradeServiceChargeReference(ChargeId chargeId, ProductId productId, ServiceType serviceType, String formula) {

        this.chargeId = chargeId;
        this.serviceType = serviceType;
        this.productId = productId;
        this.formula = formula;

        pctx = ParserContext.create();

        // add a reference to our Calculator Utility class and BigDecimal
        pctx.addImport("CalculatorUtils", CalculatorUtils.class);
        pctx.addImport(BigDecimal.class);
        pctx.addImport(String.class);

        // compile the expression and keep the context
        compiledExpression = MVEL.compileExpression(formula, pctx);

    }

    private void initParserContext() {

        pctx = ParserContext.create();

        // add a reference to our Calculator Utility class and BigDecimal
        pctx.addImport("CalculatorUtils", CalculatorUtils.class);
        pctx.addImport(BigDecimal.class);
        pctx.addImport(String.class);

        // compile the expression and keep the context
        compiledExpression = MVEL.compileExpression(formula, pctx);
    }

    public TradeServiceChargeReference(ChargeId chargeId, ProductId productId, ServiceType serviceType, String formula, Map<String, Class<?>> classTypeMap) {

        this.chargeId = chargeId;
        this.serviceType = serviceType;
        this.productId = productId;
        this.formula = formula;

        pctx = ParserContext.create();

        // add a reference to our Calculator Utility class and BigDecimal
        pctx.addImport("CalculatorUtils", CalculatorUtils.class);
        pctx.addImport(BigDecimal.class);
        pctx.addImport(String.class);


        // compile the expression and keep the context
        compiledExpression = MVEL.compileExpression(formula, pctx);

    }

    // todo: remove this after testing
    @Override
    public String toString() {
        return this.chargeId.toString();
    }

    public Boolean isChargeFor(ProductId productId, ServiceType serviceType) {

        return (this.productId.toString() == productId.toString() && this.serviceType == serviceType) ? true : false;
    }

    public BigDecimal compute(Map<String, Object> parameters) throws Exception {

        Boolean valid = true;

        // get a map of inputs to this formula
        if (pctx == null) {
            System.out.println("PCTX is null!!");
            System.out.println("PCTX is being initialized!!");
            initParserContext();
        } else {
            System.out.println("PCTX !!");
        }

        Map<String, Class> inputs = pctx.getInputs();
        System.out.println("inputs.size():" + inputs.size());
        System.out.println("----------------------------------------------------------------------------------------------");
        for (String keyed : inputs.keySet()) {
            Object ob = inputs.get(keyed);
        }
        System.out.println("----------------------------------------------------------------------------------------------");


        // iterate through the list of inputs checking if the parameters passed contain all required
        Iterator it = inputs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();

            // if we can't find the parameter, say goodbye
            if (!parameters.containsKey(pairs.getKey())) {
                System.out.println("missing parameter, " + pairs.getKey() + " cannot execute");
                System.out.println();
                //parameters.put(pairs.getKey().toString(),new BigDecimal(0)); //Added handling that accepts null and defaults to a value, thus throwing the exception is unnecessary.
                valid = false;
            }

        }

        System.out.println("FORMULA: " + formula);

        // throw an Exception if we have missing parameters
        if (!valid) {
            throw new Exception("blah blah");
        }


        Object obbb = MVEL.executeExpression(compiledExpression, parameters);
        System.out.println("obbb:"+obbb.toString());
        return new BigDecimal(obbb.toString());
    }

    public ChargeId getChargeId() {
        return chargeId;
    }
}
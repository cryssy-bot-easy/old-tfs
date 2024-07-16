package com.ucpb.tfs.charges;

import com.ucpb.tfs.domain.reference.Charge;
import com.ucpb.tfs.domain.reference.ChargeId;
import com.ucpb.tfs.domain.reference.ProductId;
import com.ucpb.tfs.domain.reference.TradeServiceChargeReference;
import com.ucpb.tfs.domain.reference.infrastructure.repositories.hibernate.TradeServiceChargeMemoryRepository;
import com.ucpb.tfs.domain.service.enumTypes.ServiceType;
import com.ucpb.tfs.utils.CalculatorUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Jett
 * Date: 7/24/12
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ChargesTest {

    List<Charge> charges = new ArrayList<Charge>();

    //TODO change advising tag to advanceCorresChargesFlag
    //TODO change corres tag to advanceCorresChargesFlag
    //TODO change confirming tag to advanceCorresChargesFlag

    @Before
    public void setup() {

        //Clear Repository before each test
        TradeServiceChargeMemoryRepository.getInstance().clearCharges();

        charges.add(new Charge("BC", "Bank Commission"));
        charges.add(new Charge("CF", "Commitment Fee"));
        charges.add(new Charge("DOCSTAMPS", "Documentary Stamps"));
        charges.add(new Charge("CABLE", "Cable Fee"));
        charges.add(new Charge("SUP", "Supplies"));
        charges.add(new Charge("CILEX", "CILEX"));
        charges.add(new Charge("NOTARIAL", "NOTARIAL"));
        charges.add(new Charge("INTEREST", "INTEREST"));
        charges.add(new Charge("BOOKING", "BOOKING"));
        charges.add(new Charge("CORRES-CONFIRMING", "CORRES-CONFIRMING"));
        charges.add(new Charge("CORRES-ADVISING", "CORRES-ADVISING"));
        charges.add(new Charge("CANCEL", "CANCEL"));

        setupFX();
        setupDM();

    }

    public void setupFX(){
        //Start of FXLC Opening
        //Used for FX LC Cash Opening
        String formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(bankCommissionNumerator==null) {bankCommissionNumerator = new BigDecimal(1);};"+
                "if(bankCommissionDenominator==null) {bankCommissionDenominator = new BigDecimal(8);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(bankCommissionNumerator, bankCommissionDenominator) * 0.01B * amount) * months;" +
                "tmp < 1000.00 ? BigDecimal.valueOf(1000.00B) : new BigDecimal (tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-FOREIGN-CASH"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "BigDecimal.valueOf(0.30B * CalculatorUtils.divideUp(amount, 200B)) ";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-FOREIGN-CASH"), ServiceType.OPENING, formula));

        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);}; cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-FOREIGN-CASH"), ServiceType.OPENING, formula));

        formula = "if(suppliesAmount==null) {suppliesAmount = new BigDecimal(50);};suppliesAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("SUP"), new ProductId("LC-FOREIGN-CASH"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(cilexNumerator==null) {cilexNumerator = new BigDecimal(1);};"+
                "if(cilexDenominator==null) {cilexDenominator = new BigDecimal(8);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("LC-FOREIGN-CASH"), ServiceType.OPENING, formula));

        formula ="if(usdToPHPRate==null) {usdToPHPRate = new BigDecimal(1);};"+
                "if(advisingFee==null) {advisingFee = new BigDecimal(0);};"+
                "var fiftyDollarMinimum = usdToPHPRate * 50.0B;" +
                "var tmp = advisingFee < fiftyDollarMinimum ? BigDecimal.valueOf(fiftyDollarMinimum) : advisingFee;" +
                "advising == 'Y' ? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-ADVISING"), new ProductId("LC-FOREIGN-CASH"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(confirmingFeeNumerator==null) {confirmingFeeNumerator = new BigDecimal(1);};"+
                "if(confirmingFeeDenominator==null) {confirmingFeeDenominator = new BigDecimal(4);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(confirmingFeeNumerator,confirmingFeeDenominator) * 0.01B * amount) * months;" +
                " (corres == 'Y'  && confirm == 'Y' )? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-CONFIRMING"), new ProductId("LC-FOREIGN-CASH"), ServiceType.OPENING, formula));

        //Used for FX LC Standby Opening
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(bankCommissionNumerator==null) {bankCommissionNumerator = new BigDecimal(1);};"+
                "if(bankCommissionDenominator==null) {bankCommissionDenominator = new BigDecimal(8);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(bankCommissionNumerator,bankCommissionDenominator) * new BigDecimal(0.01) * amount) * months;" +
                "tmp < 1000.00B ? BigDecimal.valueOf(1000.00B) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-FOREIGN-STANDBY"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(commitmentFeeNumerator==null) {commitmentFeeNumerator = new BigDecimal(1);};"+
                "if(commitmentFeeDenominator==null) {commitmentFeeDenominator = new BigDecimal(1);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(commitmentFeeNumerator, commitmentFeeDenominator) * 0.01 * amount) * months;" +
                "tmp < 500.00B ? BigDecimal.valueOf(500.00B) : BigDecimal.valueOf(tmp)";//TODO
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CF"), new ProductId("LC-FOREIGN-STANDBY"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "BigDecimal.valueOf(0.30B * CalculatorUtils.divideUp(amount, 200B)) ";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-FOREIGN-STANDBY"), ServiceType.OPENING, formula));

        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-FOREIGN-STANDBY"), ServiceType.OPENING, formula));

        formula = "if(suppliesAmount==null) {suppliesAmount = new BigDecimal(50);};suppliesAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("SUP"), new ProductId("LC-FOREIGN-STANDBY"), ServiceType.OPENING, formula));

        formula = "var fiftyDollarMinimum = usdToPHPRate * 50.0B;" +
                "if(advisingFee==null){advisingFee=new BigDecimal(0)};"+
                "var tmp = advisingFee < fiftyDollarMinimum ? BigDecimal.valueOf(fiftyDollarMinimum) : advisingFee;" +
                "advising == 'Y' ? advisingFee : new BigDecimal(0)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-ADVISING"), new ProductId("LC-FOREIGN-STANDBY"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(confirmingFeeNumerator==null) {confirmingFeeNumerator = new BigDecimal(1);};"+
                "if(confirmingFeeDenominator==null) {confirmingFeeDenominator = new BigDecimal(4);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(confirmingFeeNumerator,confirmingFeeDenominator) * 0.01B * amount) * months;" +
                " (corres == 'Y'  && confirm == 'Y' )? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-CONFIRMING"), new ProductId("LC-FOREIGN-STANDBY"), ServiceType.OPENING, formula));

        //Used for FX LC REGULAR SIGHT Opening
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(bankCommissionNumerator==null) {bankCommissionNumerator = new BigDecimal(1);};"+
                "if(bankCommissionDenominator==null) {bankCommissionDenominator = new BigDecimal(8);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(bankCommissionNumerator,bankCommissionDenominator) * 0.01B * amount) * months;" +
                "tmp < 1000.00B ? new BigDecimal(1000.00) : new BigDecimal(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "BigDecimal.valueOf(0.30B * CalculatorUtils.divideUp(amount, 200B)); ";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "if(suppliesAmount==null) {suppliesAmount = new BigDecimal(50);};suppliesAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("SUP"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "var fiftyDollarMinimum = usdToPHPRate * 50.0B;" +
                "if(advisingFee==null) {advisingFee = new BigDecimal(0);};advisingFee ;"+
                "var tmp = advisingFee < fiftyDollarMinimum ? BigDecimal.valueOf(fiftyDollarMinimum) : advisingFee;" +
                "advising == 'Y' ? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-ADVISING"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(confirmingFeeNumerator==null) {confirmingFeeNumerator = new BigDecimal(1);};"+
                "if(confirmingFeeDenominator==null) {confirmingFeeDenominator = new BigDecimal(4);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(confirmingFeeNumerator,confirmingFeeDenominator) * 0.01B * amount) * months;" +
                " (corres == 'Y'  && confirm == 'Y' )? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-CONFIRMING"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.OPENING, formula));


        //Used for FX LC REGULAR USANCE Opening
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(bankCommissionNumerator==null) {bankCommissionNumerator = new BigDecimal(1);};"+
                "if(bankCommissionDenominator==null) {bankCommissionDenominator = new BigDecimal(8);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(bankCommissionNumerator,bankCommissionDenominator) * 0.01B * amount) * months;" +
                "tmp < 1000.00B ? BigDecimal.valueOf(1000.00B) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsOf(usancePeriod);" +
                "if(commitmentFeeNumerator==null) {commitmentFeeNumerator = new BigDecimal(1);};"+
                "if(commitmentFeeDenominator==null) {commitmentFeeDenominator = new BigDecimal(4);};"+
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(commitmentFeeNumerator,commitmentFeeDenominator) * 0.01B * amount) * months;" +
                "tmp < 500.00B ? BigDecimal.valueOf(500.00B) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CF"), new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "BigDecimal.valueOf(0.30B * CalculatorUtils.divideUp(amount, 200B)) ";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "if(suppliesAmount==null) {suppliesAmount = new BigDecimal(50);};suppliesAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("SUP"), new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "var fiftyDollarMinimum = usdToPHPRate * 50.0B;" +
                "if(advisingFee==null) {advisingFee= new BigDecimal(0);};"+
                "var tmp = advisingFee < fiftyDollarMinimum ? BigDecimal.valueOf(fiftyDollarMinimum) : advisingFee;" +
                "advising == 'Y' ? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-ADVISING"), new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(confirmingFeeNumerator==null) {confirmingFeeNumerator= new BigDecimal(1);};"+
                "if(confirmingFeeDenominator==null) {confirmingFeeDenominator= new BigDecimal(4);};"+
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(confirmingFeeNumerator,confirmingFeeDenominator) * 0.01B * amount) * months;" +
                " (corres == 'Y'  && confirm == 'Y' )? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-CONFIRMING"), new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.OPENING, formula));

        //End of FXLC Opening


        //Start of FXLC Negotiation

        //Used for FX LC Cash Negotiation
        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-FOREIGN-CASH"), ServiceType.NEGOTIATION, formula));

        formula = "if(notarialAmount==null) {notarialAmount= new BigDecimal(50);};notarialAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("NOTARIAL"), new ProductId("LC-FOREIGN-CASH"), ServiceType.NEGOTIATION, formula));

        //Used for FX LC STANDBY Negotiation
        formula = "cableAmount==null? BigDecimal.valueOf(500B):BigDecimal.valueOf(cableAmount)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-FOREIGN-STANDBY"), ServiceType.NEGOTIATION, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "if(cilexNumerator==null) {cilexNumerator= new BigDecimal(0);};"+
                "if(cilexDenominator==null) {cilexDenominator= new BigDecimal(8);};"+
                "if(usdToPHPRate==null) {cilexDenominator= new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : new BigDecimal(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("LC-FOREIGN-STANDBY"), ServiceType.NEGOTIATION, formula));

        //Used for FX LC REGULAR SIGHT Negotiation
        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.NEGOTIATION, formula));

        formula = "if(notarialAmount==null) {notarialAmount= new BigDecimal(50);};notarialAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("NOTARIAL"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.NEGOTIATION, formula));

        formula ="if(amount==null) {amount= new BigDecimal(0);};"+
                "if(oldAmount==null) {oldAmount= new BigDecimal(0);};"+
                "CalculatorUtils.getDocStampsAmount_FXLC_Nego(amount,oldAmount) "; //TODO Missing less amount of Documentary Stamps collected during fxlc opening
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.NEGOTIATION, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "if(cilexNumerator==null) {cilexNumerator= new BigDecimal(1);};"+
                "if(cilexDenominator==null) {cilexDenominator= new BigDecimal(8);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.NEGOTIATION, formula));

        //Used for FX LC REGULAR USANCE Negotiation
        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.NEGOTIATION, formula));

        formula = "if(notarialAmount==null) {notarialAmount= new BigDecimal(50);};notarialAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("NOTARIAL"), new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.NEGOTIATION, formula));

        //End of FXLC Negotiation

        //Start of FXLC Adjustment
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "if(cilexNumerator==null) {cilexNumerator= new BigDecimal(1);};"+
                "if(cilexDenominator==null) {cilexDenominator= new BigDecimal(8);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("LC-FOREIGN-CASH"), ServiceType.ADJUSTMENT, formula));
        //End of FXLC Adjustment


        //Start of UA Loan Adjustment
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "if(cilexNumerator==null) {cilexNumerator= new BigDecimal(1);};"+
                "if(cilexDenominator==null) {cilexDenominator= new BigDecimal(8);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("UA-FOREIGN"), ServiceType.UA_LOAN_SETTLEMENT, formula));

        formula = "BigDecimal.valueOf(CalculatorUtils.docStampsAmount(amount, fxlcOpeningDocStamps)) "; //TODO Missing less amount of Documentary Stamps collected during fxlc opening
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("UA-FOREIGN"), ServiceType.UA_LOAN_SETTLEMENT, formula));
        //End of UA Loan Adjustment

        //TODO Create Test
        //Start of BG Issuance
        formula = "if(bankCommissionAmount==null) {bankCommissionAmount= new BigDecimal(500);};bankCommissionAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("BG-FOREIGN"), ServiceType.ISSUANCE, formula));

        formula = "if(docstampsAmount==null){ new BigDecimal(37.50);};docstampsAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("BG-FOREIGN"), ServiceType.ISSUANCE, formula));
        //END of BG Issuance

        //TODO Create Test
        //Start of BE Issuance
        formula = "if(bankCommissionAmount==null) {bankCommissionAmount= new BigDecimal(500);};bankCommissionAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("BE-FOREIGN"), ServiceType.ISSUANCE, formula));
        //END of BE Issuance

        //TODO Create Test
        //Start of BG Cancellation
        formula = "if(bgCancellationFee==null) {bgCancellationFee= new BigDecimal(300);};bgCancellationFee;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BG"), new ProductId("BG-FOREIGN"), ServiceType.CANCELLATION, formula));
        //End of BG Cancellation

        //Start of FXLC Amendment
        formula = "CalculatorUtils.getBankCommission_FXLC_Amendment( tenorSwitch,  amountSwitch,  lcAmountFlagDisplay,\n" +
                "                                                expiryDateSwitchDisplay,  expiryDateFlagDisplay,  confirmationInstructionsFlagSwitch,  narrativeSwitchDisplay,\n" +
                "                                                bankCommissionNumerator,  bankCommissionDenominator,\n" +
                "                                                outstandingBalance ,  amountTo,\n" +
                "                                                expiryDateModifiedDays, expiryDate) ";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-FOREIGN"), ServiceType.AMENDMENT, formula));

        formula = "CalculatorUtils.getCableFeeFxlcAmendment( tenorCheck,  lcAmountCheck,  expiryDateCheck,  changeInConfirmationCheck,  narrativesCheck) ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-FOREIGN"), ServiceType.AMENDMENT, formula));

        formula = "CalculatorUtils.getAdvisingFeeFxlcAmendment(tenorCheck, lcAmountCheck, lcAmountFlag, changeInConfirmationCheck, usdToPHPSpecialRate) ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-ADVISING"), new ProductId("LC-FOREIGN"), ServiceType.AMENDMENT, formula));

        formula = " \"if(amountFrom==null) {amount= new BigDecimal(0);};\"+\n" +
                "                \"if(amountTo==null) {amount= new BigDecimal(0);};\"+\n" +
                "                \"CalculatorUtils.getDocStampsFxlcAmendment(lcAmountCheck,lcAmountFlag,amountFrom,amountTo)\";";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-FOREIGN"), ServiceType.AMENDMENT, formula));

        formula ="CalculatorUtils.getCommitmentFeeAmendment(tenorCheck,\n" +
                "                lcAmountCheck, lcAmountFlag,\n" +
                "                amountFrom, amountTo,\n" +
                "                usancePeriod, expiryDate,\n" +
                "                documentSubType1, documentSubType2, commitmentFeeNumerator, commitmentFeeDenominator)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CF"), new ProductId("LC-FOREIGN"), ServiceType.AMENDMENT, formula));


        //End of FXLC Amendment
    }

    public void setupDM(){
        //Start of DMLC Opening
        //Used for DM LC Cash Opening
        String formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(bankCommissionNumerator==null) {bankCommissionNumerator = new BigDecimal(1);};"+
                "if(bankCommissionDenominator==null) {bankCommissionDenominator = new BigDecimal(8);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(bankCommissionNumerator, bankCommissionDenominator) * 0.01B * amount) * months;" +
                "tmp < 1000.00 ? BigDecimal.valueOf(1000.00B) : new BigDecimal (tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "BigDecimal.valueOf(0.30B * CalculatorUtils.divideUp(amount, 200B)) ";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.OPENING, formula));

        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);}; cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.OPENING, formula));

        formula = "if(suppliesAmount==null) {suppliesAmount = new BigDecimal(50);};suppliesAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("SUP"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(cilexNumerator==null) {cilexNumerator = new BigDecimal(1);};"+
                "if(cilexDenominator==null) {cilexDenominator = new BigDecimal(8);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.OPENING, formula));

        formula ="if(usdToPHPRate==null) {usdToPHPRate = new BigDecimal(1);};"+
                "if(advisingFee==null) {advisingFee = new BigDecimal(0);};"+
                "var fiftyDollarMinimum = usdToPHPRate * 50.0B;" +
                "var tmp = advisingFee < fiftyDollarMinimum ? BigDecimal.valueOf(fiftyDollarMinimum) : advisingFee;" +
                "advising == 'Y' ? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-ADVISING"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(confirmingFeeNumerator==null) {confirmingFeeNumerator = new BigDecimal(1);};"+
                "if(confirmingFeeDenominator==null) {confirmingFeeDenominator = new BigDecimal(4);};"+
                "var tmp = (CalculatorUtils.divideUp(confirmingFeeNumerator,confirmingFeeDenominator) * 0.01B * amount) * months;" +
                " (corres == 'Y'  && confirm == 'Y' )? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-CONFIRMING"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.OPENING, formula));

        //Used for DM LC Standby Opening
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(bankCommissionNumerator==null) {bankCommissionNumerator = new BigDecimal(1);};"+
                "if(bankCommissionDenominator==null) {bankCommissionDenominator = new BigDecimal(8);};"+
                "var tmp = (CalculatorUtils.divideUp(bankCommissionNumerator,bankCommissionDenominator) * new BigDecimal(0.01) * amount) * months;" +
                "tmp < 1000.00B ? BigDecimal.valueOf(1000.00B) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-DOMESTIC-STANDBY"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(commitmentFeeNumerator==null) {commitmentFeeNumerator = new BigDecimal(1);};"+
                "if(commitmentFeeDenominator==null) {commitmentFeeDenominator = new BigDecimal(4);};"+
                "var tmp = (CalculatorUtils.divideUp(commitmentFeeNumerator, commitmentFeeDenominator) * 0.01 * amount) * months;" +
                "tmp < 500.00B ? BigDecimal.valueOf(500.00B) : BigDecimal.valueOf(tmp)";//TODO
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CF"), new ProductId("LC-DOMESTIC-STANDBY"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "BigDecimal.valueOf(0.30B * CalculatorUtils.divideUp(amount, 200B)) ";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-DOMESTIC-STANDBY"), ServiceType.OPENING, formula));

        formula = "cableAmount==null? BigDecimal.valueOf(800B):BigDecimal.valueOf(cableAmount)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC-STANDBY"), ServiceType.OPENING, formula));

        formula = "if(suppliesAmount==null) {suppliesAmount = new BigDecimal(50);};suppliesAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("SUP"), new ProductId("LC-DOMESTIC-STANDBY"), ServiceType.OPENING, formula));

        formula = "var fiftyDollarMinimum = usdToPHPRate * 50.0B;" +
                "if(advisingFee==null){advisingFee=new BigDecimal(0)};"+
                "var tmp = advisingFee < fiftyDollarMinimum ? BigDecimal.valueOf(fiftyDollarMinimum) : advisingFee;" +
                "advising == 'Y' ? advisingFee : new BigDecimal(0)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-ADVISING"), new ProductId("LC-DOMESTIC-STANDBY"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(confirmingFeeNumerator==null) {confirmingFeeNumerator = new BigDecimal(1);};"+
                "if(confirmingFeeDenominator==null) {confirmingFeeDenominator = new BigDecimal(4);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(confirmingFeeNumerator,confirmingFeeDenominator) * 0.01B * amount) * months;" +
                " (corres == 'Y'  && confirm == 'Y' )? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-CONFIRMING"), new ProductId("LC-DOMESTIC-STANDBY"), ServiceType.OPENING, formula));

        //Used for DM LC REGULAR SIGHT Opening
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(bankCommissionNumerator==null) {bankCommissionNumerator = new BigDecimal(1);};"+
                "if(bankCommissionDenominator==null) {bankCommissionDenominator = new BigDecimal(8);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(bankCommissionNumerator,bankCommissionDenominator) * 0.01B * amount) * months;" +
                "tmp < 1000.00B ? new BigDecimal(1000.00) : new BigDecimal(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "BigDecimal.valueOf(0.30B * CalculatorUtils.divideUp(amount, 200B)); ";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "if(suppliesAmount==null) {suppliesAmount = new BigDecimal(50);};suppliesAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("SUP"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "var fiftyDollarMinimum = usdToPHPRate * 50.0B;" +
                "if(advisingFee==null) {advisingFee = new BigDecimal(0);};advisingFee ;"+
                "var tmp = advisingFee < fiftyDollarMinimum ? BigDecimal.valueOf(fiftyDollarMinimum) : advisingFee;" +
                "advising == 'Y' ? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-ADVISING"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(confirmingFeeNumerator==null) {confirmingFeeNumerator = new BigDecimal(1);};"+
                "if(confirmingFeeDenominator==null) {confirmingFeeDenominator = new BigDecimal(4);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(confirmingFeeNumerator,confirmingFeeDenominator) * 0.01B * amount) * months;" +
                " (corres == 'Y'  && confirm == 'Y' )? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-CONFIRMING"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.OPENING, formula));


        //Used for DM LC REGULAR USANCE Opening
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(bankCommissionNumerator==null) {bankCommissionNumerator = new BigDecimal(1);};"+
                "if(bankCommissionDenominator==null) {bankCommissionDenominator = new BigDecimal(8);};"+
                "if(amount==null) {amount = new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(bankCommissionNumerator,bankCommissionDenominator) * 0.01B * amount) * months;" +
                "tmp < 1000.00B ? BigDecimal.valueOf(1000.00B) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-DOMESTIC-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsOf(usancePeriod);" +
                "if(commitmentFeeNumerator==null) {commitmentFeeNumerator = new BigDecimal(1);};"+
                "if(commitmentFeeDenominator==null) {commitmentFeeDenominator = new BigDecimal(4);};"+
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(commitmentFeeNumerator,commitmentFeeDenominator) * 0.01B * amount) * months;" +
                "tmp < 500.00B ? BigDecimal.valueOf(500.00B) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CF"), new ProductId("LC-DOMESTIC-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "BigDecimal.valueOf(0.30B * CalculatorUtils.divideUp(amount, 200B)) ";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-DOMESTIC-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "if(suppliesAmount==null) {suppliesAmount = new BigDecimal(50);};suppliesAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("SUP"), new ProductId("LC-DOMESTIC-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "var fiftyDollarMinimum = usdToPHPRate * 50.0B;" +
                "if(advisingFee==null) {advisingFee= new BigDecimal(0);};"+
                "var tmp = advisingFee < fiftyDollarMinimum ? BigDecimal.valueOf(fiftyDollarMinimum) : advisingFee;" +
                "advising == 'Y' ? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-ADVISING"), new ProductId("LC-DOMESTIC-REGULAR-USANCE"), ServiceType.OPENING, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(confirmingFeeNumerator==null) {confirmingFeeNumerator= new BigDecimal(1);};"+
                "if(confirmingFeeDenominator==null) {confirmingFeeDenominator= new BigDecimal(4);};"+
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(confirmingFeeNumerator,confirmingFeeDenominator) * 0.01B * amount) * months;" +
                " (corres == 'Y'  && confirm == 'Y' )? BigDecimal.valueOf(tmp) : BigDecimal.valueOf(0.00B)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CORRES-CONFIRMING"), new ProductId("LC-DOMESTIC-REGULAR-USANCE"), ServiceType.OPENING, formula));

        //End of DMLC Opening


        //Start of DMLC Negotiation

        //Used for DM LC Cash Negotiation
        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.NEGOTIATION, formula));

        formula = "if(notarialAmount==null) {notarialAmount= new BigDecimal(50);};notarialAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("NOTARIAL"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.NEGOTIATION, formula));

        //Used for DM LC STANDBY Negotiation
        formula = "cableAmount==null? BigDecimal.valueOf(500B):BigDecimal.valueOf(cableAmount)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC-STANDBY"), ServiceType.NEGOTIATION, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "if(cilexNumerator==null) {cilexNumerator= new BigDecimal(0\1);};"+
                "if(cilexDenominator==null) {cilexDenominator= new BigDecimal(8);};"+
                "if(usdToPHPRate==null) {cilexDenominator= new BigDecimal(0);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : new BigDecimal(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("LC-DOMESTIC-STANDBY"), ServiceType.NEGOTIATION, formula));

        //Used for DM LC REGULAR SIGHT Negotiation
        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.NEGOTIATION, formula));

        formula = "if(notarialAmount==null) {notarialAmount= new BigDecimal(50);};notarialAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("NOTARIAL"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.NEGOTIATION, formula));

        formula ="if(amount==null) {amount= new BigDecimal(0);};"+
                "if(oldAmount==null) {oldAmount= new BigDecimal(0);};"+
                "CalculatorUtils.getDocStampsAmount_FXLC_Nego(amount,oldAmount) "; //TODO Missing less amount of Documentary Stamps collected during DMlc opening
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.NEGOTIATION, formula));

        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "if(cilexNumerator==null) {cilexNumerator= new BigDecimal(1);};"+
                "if(cilexDenominator==null) {cilexDenominator= new BigDecimal(8);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("LC-DOMESTIC-REGULAR-SIGHT"), ServiceType.NEGOTIATION, formula));

        //Used for DM LC REGULAR USANCE Negotiation
        formula = "if(cableAmount==null) {cableAmount = new BigDecimal(800);};cableAmount ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC-REGULAR-USANCE"), ServiceType.NEGOTIATION, formula));

        formula = "if(notarialAmount==null) {notarialAmount= new BigDecimal(50);};notarialAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("NOTARIAL"), new ProductId("LC-DOMESTIC-REGULAR-USANCE"), ServiceType.NEGOTIATION, formula));

        //End of DMLC Negotiation

        //Start of DMLC Adjustment
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "if(cilexNumerator==null) {cilexNumerator= new BigDecimal(1);};"+
                "if(cilexDenominator==null) {cilexDenominator= new BigDecimal(8);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("LC-DOMESTIC-CASH"), ServiceType.ADJUSTMENT, formula));
        //End of DMLC Adjustment


        //Start of UA Loan Adjustment
        formula = "var months=CalculatorUtils.getMonthsTill(expiryDate);" +
                "if(amount==null) {amount= new BigDecimal(0);};"+
                "if(cilexNumerator==null) {cilexNumerator= new BigDecimal(1);};"+
                "if(cilexDenominator==null) {cilexDenominator= new BigDecimal(8);};"+
                "var tmp = (CalculatorUtils.divideUp(cilexNumerator, cilexDenominator) * 0.01B * amount) * months;" +
                "var twentyDollarMinimum = usdToPHPRate * 20B;" +
                "tmp < twentyDollarMinimum ? BigDecimal.valueOf(twentyDollarMinimum) : BigDecimal.valueOf(tmp)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CILEX"), new ProductId("UA-DOMESTIC"), ServiceType.UA_LOAN_SETTLEMENT, formula));

        formula = "BigDecimal.valueOf(CalculatorUtils.docStampsAmount(amount)) "; //TODO Missing less amount of Documentary Stamps collected during DMlc opening
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("UA-DOMESTIC"), ServiceType.UA_LOAN_SETTLEMENT, formula));
        //End of UA Loan Adjustment

        //TODO Create Test
        //Start of BG Issuance
        formula = "if(bankCommissionAmount==null) {bankCommissionAmount= new BigDecimal(500);};bankCommissionAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("BG-DOMESTIC"), ServiceType.ISSUANCE, formula));

        formula = "if(docstampsAmount==null){ new BigDecimal(37.50);};docstampsAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("DOCSTAMPS"), new ProductId("BG-DOMESTIC"), ServiceType.ISSUANCE, formula));
        //END of BG Issuance

        //TODO Create Test
        //Start of BE Issuance
        formula = "if(bankCommissionAmount==null) {bankCommissionAmount= new BigDecimal(500);};bankCommissionAmount;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("BE-DOMESTIC"), ServiceType.ISSUANCE, formula));
        //END of BE Issuance

        //TODO Create Test
        //Start of BG Cancellation
        formula = "if(bgCancellationFee==null) {bgCancellationFee= new BigDecimal(300);};bgCancellationFee;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BG"), new ProductId("BG-DOMESTIC"), ServiceType.CANCELLATION, formula));
        //End of BG Cancellation

        //Start of DMLC Amendment
        formula = "CalculatorUtils.getBankCommission_FXLC_Amendment( tenorCheck,  lcAmountCheck,  lcAmountFlag,\n" +
                "                                                expiryDateCheck,  expiryDateFlag,  changeInConfirmationCheck,  narrativesCheck,\n" +
                "                                                bankCommissionNumerator,  bankCommissionDenominator,\n" +
                "                                                amountFrom,  amountTo,\n" +
                "                                                expiryDateModifiedDays, expiryDate)";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("BC"), new ProductId("LC-DOMESTIC"), ServiceType.AMENDMENT, formula));

        formula = "CalculatorUtils.getCableFeeFxlcAmendment( tenorCheck,  lcAmountCheck,  expiryDateCheck,  changeInConfirmationCheck,  narrativesCheck) ;";
        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC"), ServiceType.AMENDMENT, formula));


        formula = "CalculatorUtils.getConfirmingFeeFxlcAmendment( lcAmountCheck,  lcAmountFlag,\n" +
                "                 expiryDateCheck,  expiryDateFlag,\n" +
                "                 changeInConfirmationCheck,\n" +
                "                 advanceCorresChargesFlag,\n" +
                "                 amountFrom,  amountTo,\n" +
                "                 expiryDateModifiedDays,\n" +
                "                 confirmingFeeNumerator,  confirmingFeeDenominator,  expiryDate,  processingDate )";

        TradeServiceChargeMemoryRepository.getInstance().initAdd(new TradeServiceChargeReference(new ChargeId("CABLE"), new ProductId("LC-DOMESTIC"), ServiceType.AMENDMENT, formula));

        //End of DMLC Amendment


    }

//    @Test
    public void testFX_LC_CASH_Opening_Formula() {
        System.out.println("testFX_LC_CASH_Opening_Formula");

        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("100000"));
//        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionNumerator", null);
//        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("bankCommissionDenominator", null);
        vars.put("cilexNumerator", null);
//        vars.put("cilexNumerator", new BigDecimal("1"));
        vars.put("cilexDenominator", null);
//        vars.put("cilexDenominator", new BigDecimal("8"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("advisingFee", new BigDecimal("800"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("urr", new BigDecimal("41"));
        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", new BigDecimal(50));


        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN-CASH"), ServiceType.OPENING);
        System.out.println("Size of charges: " + charges.size());

        for (TradeServiceChargeReference charge : charges) {
            try {

                BigDecimal temp = charge.compute(vars);

                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());
//
//                if (vars.get("settlementCurrency").toString() != "PHP" && charge.getChargeId().toString() == "CILEX") {
//                    temp = charge.compute(vars);
//                    System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());
//                    Assert.assertTrue(temp.equals(new BigDecimal(820.00)));
//
//                } else {
//                    temp = charge.compute(vars);
//                    System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());
//
//                    if (charge.getChargeId().toString() == "BC") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(1000.00)));
//                    }
//
//                    if (charge.getChargeId().toString() == "DOCSTAMPS") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(150.00)));
//                    }
//
//                    if (charge.getChargeId().toString() == "CABLE") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(800.00)));
//                    }
//
//                    if (charge.getChargeId().toString() == "SUP") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(50.00)));
//                    }
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Test
    public void testFX_LC_STANDBY_Opening_Formula() {
        System.out.println("testFX_LC_STANDBY_Formula");
        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("1000000"));
        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("commitmentFeeNumerator", new BigDecimal("1"));
        vars.put("commitmentFeeDenominator", new BigDecimal("4"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("advisingFee", new BigDecimal("8"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("urr", new BigDecimal("41"));
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("cableAmount", null);
//        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", null);
//        vars.put("suppliesAmount", new BigDecimal(50));

        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN-STANDBY"), ServiceType.OPENING);

        System.out.println("Size of charges: " + charges.size());
        for (TradeServiceChargeReference charge : charges) {
            try {

                BigDecimal temp;

                temp = charge.compute(vars);
                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());
//
//                if (charge.getChargeId().toString() == "BC") {
//                    Assert.assertTrue(temp.equals(new BigDecimal(3750.00)));
//                }
//
//                if (charge.getChargeId().toString() == "CF") {
//                    Assert.assertTrue(temp.equals(new BigDecimal(7500.00)));
//                }
//
//                if (charge.getChargeId().toString() == "DOCSTAMPS") {
//                    Assert.assertTrue(temp.equals(new BigDecimal(1500.00)));
//                }
//
//                if (charge.getChargeId().toString() == "CABLE") {
//                    Assert.assertTrue(temp.equals(new BigDecimal(800.00)));
//                }
//
//                if (charge.getChargeId().toString() == "SUP") {
//                    Assert.assertTrue(temp.equals(new BigDecimal(50.00)));
//                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Test
    public void testFX_LC_REGULAR_SIGHT_Opening_Formula() {
        System.out.println("testFX_LC_REGULAR_SIGHT_Opening_Formula");

        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("100000"));
        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("advisingFee", new BigDecimal("8"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", new BigDecimal(50));


        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.OPENING);
        System.out.println("Size of charges: " + charges.size());

        for (TradeServiceChargeReference charge : charges) {
            try {

                BigDecimal temp;
                temp = charge.compute(vars);
                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());

//                if (charge.getChargeId().toString() == "BC") {
//                    Assert.assertTrue(temp.equals(new BigDecimal(1000.00)));
//                }
//
//                if (charge.getChargeId().toString() == "DOCSTAMPS") {
//                    Assert.assertTrue(temp.equals(new BigDecimal(150.00)));
//                }
//
//                if (charge.getChargeId().toString() == "CABLE") {
//                    Assert.assertTrue(temp.equals(new BigDecimal(800.00)));
//                }
//
//                if (charge.getChargeId().toString() == "SUP") {
//                    Assert.assertTrue(temp.equals(new BigDecimal(50.00)));
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Test
    public void testFX_LC_REGULAR_USANCE_Opening_Formula() {
        System.out.println("testFX_LC_REGULAR_USANCE_Opening_Formula");

        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("100000"));
        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("commitmentFeeNumerator", new BigDecimal("1"));
        vars.put("commitmentFeeDenominator", new BigDecimal("4"));
        vars.put("advisingFee", new BigDecimal("4"));
        vars.put("usancePeriod", new BigDecimal("4"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", new BigDecimal(50));


        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.OPENING);
        System.out.println("Size of charges: " + charges.size());

        for (TradeServiceChargeReference charge : charges) {
            try {

                BigDecimal temp = charge.compute(vars);
                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());


//                BigDecimal temp;
//                if (vars.get("settlementCurrency").toString() != "PHP" && charge.getChargeId().toString() == "CILEX") {
//                    temp = charge.compute(vars);
//                    System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());
//                    Assert.assertTrue(temp.equals(new BigDecimal(820.00)));
//
//                } else {
//                    temp = charge.compute(vars);
//                    System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());
//
//                    if (charge.getChargeId().toString() == "BC") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(1000.00)));
//                    }
//
//                    if (charge.getChargeId().toString() == "DOCSTAMPS") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(150.00)));
//                    }
//
//                    if (charge.getChargeId().toString() == "CABLE") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(800.00)));
//                    }
//
//                    if (charge.getChargeId().toString() == "SUP") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(50.00)));
//                    }
//
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Test
    public void testFX_LC_CASH_Negotiation_Formula() {
        System.out.println("testFX_LC_CASH_Negotiation_Formula");

        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("100000"));
        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("cilexNumerator", new BigDecimal("1"));
        vars.put("cilexDenominator", new BigDecimal("8"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("advisingFee", new BigDecimal("800"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("urr", new BigDecimal("41"));
        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", new BigDecimal(50));
        vars.put("notarialAmount", new BigDecimal(50));


        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN-CASH"), ServiceType.NEGOTIATION);
        System.out.println("Size of charges: " + charges.size());

        for (TradeServiceChargeReference charge : charges) {
            try {

                BigDecimal temp;
                temp = charge.compute(vars);
                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Test
    public void testFX_LC_STANDBY_Negotiation_Formula() {
        System.out.println("testFX_LC_STANDBY_Negotiation_Formula");

        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("100000"));
        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("cilexNumerator", new BigDecimal("1"));
        vars.put("cilexDenominator", new BigDecimal("8"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("advisingFee", new BigDecimal("800"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("urr", new BigDecimal("41"));
        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", new BigDecimal(50));


        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN-STANDBY"), ServiceType.NEGOTIATION);
        System.out.println("Size of charges: " + charges.size());

        for (TradeServiceChargeReference charge : charges) {
            try {

                BigDecimal temp;
                temp = charge.compute(vars);
                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Test
    public void testFX_LC_REGULAR_SIGHT_Negotiation_Formula() {
        System.out.println("testFX_LC_REGULAR_SIGHT_Negotiation_Formula");

        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("100000"));
        vars.put("oldAmount", new BigDecimal("100000"));
        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("cilexNumerator", new BigDecimal("1"));
        vars.put("cilexDenominator", new BigDecimal("8"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("advisingFee", new BigDecimal("800"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("urr", new BigDecimal("41"));
        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", new BigDecimal(50));
        vars.put("notarialAmount", new BigDecimal(50));


        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN-REGULAR-SIGHT"), ServiceType.NEGOTIATION);
        System.out.println("Size of charges: " + charges.size());

        for (TradeServiceChargeReference charge : charges) {
            try {

                BigDecimal temp;
                temp = charge.compute(vars);
                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Test
    public void testFX_LC_REGULAR_USANCE_Negotiation_Formula() {
        System.out.println("testFX_LC_REGULAR_USANCE_Negotiation_Formula");

        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("100000"));
        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("cilexNumerator", new BigDecimal("1"));
        vars.put("cilexDenominator", new BigDecimal("8"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("advisingFee", new BigDecimal("800"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("urr", new BigDecimal("41"));
        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", new BigDecimal(50));
        vars.put("notarialAmount", new BigDecimal(50));


        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN-REGULAR-USANCE"), ServiceType.NEGOTIATION);
        System.out.println("Size of charges: " + charges.size());

        for (TradeServiceChargeReference charge : charges) {
            try {

                BigDecimal temp;
                temp = charge.compute(vars);
                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @Test
    public void testFX_LC_CASH_Adjustment_Formula() {
        System.out.println("testFX_LC_CASH_Adjustment_Formula");

        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("100000"));
        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("cilexNumerator", new BigDecimal("1"));
        vars.put("cilexDenominator", new BigDecimal("8"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("advisingFee", new BigDecimal("800"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("urr", new BigDecimal("41"));
        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", new BigDecimal(50));


        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN-CASH"), ServiceType.ADJUSTMENT);
        System.out.println("Size of charges: " + charges.size());


        for (TradeServiceChargeReference charge : charges) {
            try {

                BigDecimal temp = charge.compute(vars);
                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());

//
//                BigDecimal temp;
//
//                if (vars.get("settlementCurrency").toString() != "PHP" && charge.getChargeId().toString() == "CILEX") {
//                    temp = charge.compute(vars);
//                    System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());
//                    Assert.assertTrue(temp.equals(new BigDecimal(820.00)));
//
//                } else {
//                    temp = charge.compute(vars);
//                    System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());
//
//                    if (charge.getChargeId().toString() == "BC") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(1000.00)));
//                    }
//
//                    if (charge.getChargeId().toString() == "DOCSTAMPS") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(150.00)));
//                    }
//
//                    if (charge.getChargeId().toString() == "CABLE") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(800.00)));
//                    }
//
//                    if (charge.getChargeId().toString() == "SUP") {
//                        Assert.assertTrue(temp.equals(new BigDecimal(50.00)));
//                    }
//
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testFX_LC_CASH_Amendment_Formula() {
        System.out.println("testFX_LC_CASH_Amendment_Formula");

        String formulaExpression = "amount * factor";

        Map vars = new HashMap();
        vars.put("amount", new BigDecimal("100000"));
        vars.put("bankCommissionNumerator", new BigDecimal("1"));
        vars.put("bankCommissionDenominator", new BigDecimal("8"));
        vars.put("cilexNumerator", new BigDecimal("1"));
        vars.put("cilexDenominator", new BigDecimal("8"));
        vars.put("confirmingFeeNumerator", new BigDecimal("1"));
        vars.put("confirmingFeeDenominator", new BigDecimal("8"));
        vars.put("advisingFee", new BigDecimal("800"));
        vars.put("corres", "Y"); //TODO Ask marvin what to check
        vars.put("confirm", "Y"); //TODO Ask marvin what to check
        vars.put("advising", "Y"); //TODO Ask marvin what to check
        vars.put("expiryDate", "12/15/2012");
        vars.put("settlementCurrency", "USD");
        vars.put("thirdToUSDRate", new BigDecimal("1.3"));
        vars.put("usdToPHPRate", new BigDecimal("41"));
        vars.put("thirdToPHPRate", new BigDecimal("54.1"));
        vars.put("urr", new BigDecimal("41"));
        vars.put("cableAmount", new BigDecimal(800));
        vars.put("suppliesAmount", new BigDecimal(50));

        vars.put("tenorCheck", "Y");
        vars.put("lcAmountCheck", "Y");
        vars.put("lcAmountFlag", "I");
        vars.put("expiryDateCheck", "Y");
        vars.put("expiryDateFlag", "E");
        vars.put("expiryDateModifiedDays", new BigDecimal(1));
        vars.put("changeInConfirmationCheck", "Y");
        vars.put("narrativesCheck", "Y");
        vars.put("amountFrom", new BigDecimal(100000));
        vars.put("amountTo", new BigDecimal(200000));


        List<TradeServiceChargeReference> charges = TradeServiceChargeMemoryRepository.getInstance().getCharges(new ProductId("LC-FOREIGN"), ServiceType.AMENDMENT);
        System.out.println("Size of charges: " + charges.size());
        BigDecimal temp;

        for (TradeServiceChargeReference charge : charges) {
            try {

                temp = charge.compute(vars);
                System.out.println("result is: " + temp.toString() + "  ChargeId:" + charge.getChargeId());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//@Test
    public void test360Compute() {
        System.out.println("another is " + CalculatorUtils.getMonthsTill("9/15/2012"));
    }




}


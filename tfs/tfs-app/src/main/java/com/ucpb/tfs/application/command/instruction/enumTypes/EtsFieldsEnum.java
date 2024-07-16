/**
 * 
 */
package com.ucpb.tfs.application.command.instruction.enumTypes;

/**
 * @author Val
 *
 * Covered transactions:
 * 1. FXLC (including Reversals)
 *    - Opening Cash, Opening Regular, Opening Standby, Adjustment, Amendment,
 *      Cancellation, Indemnity/BG-BI, Negotiation, UA Loan Settlement
 * 2. DMLC
 *    -  
 */
public enum EtsFieldsEnum {
	
	// from Basic Details
	cifNumber,
	cifName,
    mainCifNumber,
	mainCifName,
	accountOfficer,
	ccbdBranchUnitCode,
	processingUnitCode,
	fxlcIssueDate,
	dmlcIssueDate, // DMLC
	fxlcType,
	dmlcType, // DMLC
	confirmationFlag,
	facilityType,
	facilityId,
	fxlcCurrency,
	dmlcCurrency,  // DMLC
	fxlcExpiryDate,
	dmlcExpiryDate,  // DMLC
	fxlcAmount,
	dmlcAmount, // DMLC
	cramFlag,
	otherPriceTerm,
	priceTerm,
	marineInsurance,
	cwtFlag,
	tenor,
	tenorOfDraft,
	usancePeriod,
	tenorOfDraftNarrative,  // DMLC
	advanceCorresChargesFlag,
	generalDescriptionOfGoods, 

	// from Adjustment
	cifNumberCheckBox,
	cifNumberFrom,
	cifNumberTo,
	mainCifNumberCheckBox,
	mainCifNumberFrom,
	mainCifNumberTo,
	partialCashSettlementCheckBox,
	facilityTypeFrom,
	facilityTypeTo,
	facilityIdFrom,
	facilityIdTo,

	// from Cancellation
	outstandingLcAmount,
	reasonForCancellation,
	originalLcSubmitted,

	// from BG-BI/Indemnity Issuance
	originalLcAmount,
	transportMedium,
	shipmentAmount,
	typeOfBiPresented,
	shipmentCurrency,
	indemnityIssueDate,
	shipmentSequenceNumber,
	trLine,

	// from Amendment
	lcAmountCheck,
	lcAmountRadio,
	lcAmountFrom,
	lcAmountTo,
	expiryDateCheck,
	expiryDateRadio,
	expiryDateFrom,
	expiryDateTo,
	tenorCheck,
	tenorFrom,
	tenorTo,
	changeInConfirmationCheck,
	changeInConfirmationFrom,
	changeInConfirmationTo,
	specifyAdvisingBank,

	// from Negotiation
	lcNumber,
	negotiationAmount,
	negotiationCurrency,
	negotiationValueDate,
	valueDate,
	reimbursingBankSpecialRate,
	mdAmount,
	mdCurrency,
	reimbursingCurrency,
	negotiatingAmountReimbursingCurrency,

	// from UA Loan Settlement
	negoNumber,
	withBeneficiarysConformity,
	fxlcUaLoanAmount,
	fxlcUaLoanCurrency,
	osMdBalance,
	settlementCurrencyUaLoanSettlement,
	amountSettlementPayment1,
	amountSettlementCurrency1,
	amountSettlementPayment2,
	amountSettlementCurrency2,
	amountFxlcPayment1,
	amountFxlcCurrency1,
	amountFxlcPayment2,
	amountFxlcCurrency2,
	bookingCurrencyPopup,
	interestRateTextPopup,
	interestRateRadioPopup,
	repricingTermTextPopup,
	repricingTermRadioPopup,
	loanTermTextPopup,
	loanTermRadioPopup,
	loanMaturityDatePopup,
	docStampTaggingPopup,
	modeOfPaymentUaLoanPopup,
	availableAccountBalancePopup,
	apRemittancePopup,
	tradeSuspense,

	// from UA Loan Maturity Adjustment
	uaLoanMaturityDateFrom,
	uaLoanMaturityDateTo,

	// from Charges Popup
	bankCommissionPopup,
	bankCommissionPercentageNPopup,
	bankCommissionPercentageDPopup,
	bankCommissionPercentageRPopup,
	bankCommissionNumberMonthsPopup,
	bankCommissionNetCankComPopup,
	bankCommissionCwtPopup,
	bankCommissionGrossBankComPopup,
	bankCommissionLcAmountPopup,
	bankCommissionRatePopup,

	// from Charges Popup
	cableFeePopup,

	// from Charges Popup
	commitmentFeePopup,
	commitmentFeePercentageNPopup,
	commitmentFeePercentageDPopup,
	commitmentFeePercentageRPopup,

	// from Charges Popup
	cilexPercentageNPopup,
	cilexPercentageDPopup,
	cilexPercentageRPopup,
	cilexNumberOfMonthsPopup,
	cilexNetBankComPopup,
	cilexCwtPopup,
	cilexGrossBankComPopup,
	cilexLcAmountPopup,
	cilexRatePopup,

	// from Charges Popup
	confirmingFeePercentageNPopup,
	confirmingFeePercentageDPopup,
	confirmingFeePercentageRPopup,
	confirmingFeeNumberMonthsPopup,
	confirmFeePopup,
	confirmingFeeLcAmountPopup,
	confirmingFeeRatePopup,

	// from Charges Popup
	documentaryStampPopup,
	documentaryStampCentavosPopup,
	documentaryStampForEvery200Popup,
	docStampPopup,
	documentaryStampLcAmountPopup,
	documentaryStampRatePopup,

	// from Charges Popup
	suppliesFeePopup,

	// from Charges Popup
	advisingFeePopup,

	// from Charges Tab
	settlementCurrency,

	// from Charges Tab
	bankCommission,
	commitmentFee,
	cilex,
	documentaryStamp,
	cableFee,
	supplies,
	advisingFee,
	confirmingFee,
	totalAmountChargesDue,

	totalAmtDue,
	totalAmtDueCurrency,
	remainingBalanceAmount,
	remainingBalanceCurrency,
	amountOfPaymentChargesSettlement,
	amountOfPaymentChargesSettlementCurrency,
	totalPaymentChargesSettlement,
	excessAmountChargesSettlement,
	modeOfPaymentCharges,
	accountNumberCharges,
	accountNameCharges,
	availableAccountBalanceCharges,
	accountBalance,

	// from Amendment
	cashLcCurrency,
	cashLcAmount,
	cashCurrencyBalance,
	cashLcAmountBalance,
	settlementCurrencyCashFxlc,
	passOnRateConfirmCash,
	cashLcAmountCurrency1,
	cashLcAmount1,
	cashLcAmountCurrency2,
	cashLcAmount2,
	cashLcAmountCurrencyFxlc1,
	cashLcAmountFxlc1,
	cashLcAmountCurrencyFxlc2,
	cashLcAmountFxlc2,
	totalPaymentChargesCashFxlc,
	excessAmountCashFxlc,

	docType,
	addComment,
	addInstruction,
	
	// from Marginal Deposit
	;
}

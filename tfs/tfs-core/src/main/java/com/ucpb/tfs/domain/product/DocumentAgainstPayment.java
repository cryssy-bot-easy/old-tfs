package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.product.enums.DCDocumentType;
import com.ucpb.tfs.domain.product.enums.ProductType;
import com.ucpb.tfs.domain.product.enums.TradeProductStatus;
import com.ucpb.tfs.domain.service.TradeServiceReferenceNumber;
import com.ucpb.tfs.utils.UtilSetFields;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Audited
public class DocumentAgainstPayment extends TradeProduct {
	
	private DCDocumentType documentType;

	private TradeServiceReferenceNumber tsNumber;
	
	private String remittingBank;
    private String reimbursingBank;
	
	private Date processDate;
	
	private String remittingBankReferenceNumber;
	
	private Currency currency;
	
	private BigDecimal amount;
	
	private BigDecimal outstandingAmount;
	
	private Date dateOfBlAirwayBill;
	
	private Date maturityDate;
	
	private String importerCifNumber;
	
	private String originalPort;
	
	private String importerCbCode;
	
	private String importerName;
	
	private String importerAddress;
	
	private String senderToReceiverInformation;
	
	private String beneficiaryName;
	
	private String beneficiaryAddress;
	
	private String lastTransaction;
	
	private Date cancelledDate;
	
	private Date settledDate;
	
	private String processingUnitCode;

	public DocumentAgainstPayment(){
		
	}
	public DocumentAgainstPayment(DocumentNumber documentNumber, Map<String, Object> details) {
		super(documentNumber, ProductType.DA);
		
		this.updateDetails(details);

		this.remittingBank = (String)details.get("remittingBank");
        this.reimbursingBank = (String)details.get("reimbursingBank");
		
		this.processDate = new Date((String)details.get("processDate"));
		
		this.remittingBankReferenceNumber = (String)details.get("remittingBankReferenceNumber");
		
		this.currency = Currency.getInstance((String)details.get("currency"));
		
		this.amount = new BigDecimal((String)details.get("amount"));
		
		this.outstandingAmount = new BigDecimal((String)details.get("amount"));
		
		this.dateOfBlAirwayBill = ((details.get("dateOfBlAirwayBill") != null) && (!details.get("dateOfBlAirwayBill").toString().isEmpty())) ? new Date((String)details.get("dateOfBlAirwayBill")) : null;
		
		this.maturityDate = ((details.get("maturityDate") != null) && (!details.get("maturityDate").toString().isEmpty())) ? new Date((String)details.get("maturityDate")) : null;
		
		this.importerCifNumber = (String)details.get("importerCifNumber");
		
		this.originalPort = (String)details.get("originalPort");
		
		this.importerCbCode = (String)details.get("importerCbCode");
		
		this.importerName = (String)details.get("importerName");
		
		this.importerAddress = (String)details.get("importerAddress");
		
		this.senderToReceiverInformation = (String)details.get("senderToReceiverInformation");
		
		this.beneficiaryName = (String)details.get("beneficiaryName");
		
		this.beneficiaryAddress = (String)details.get("beneficiaryAddress");
		
		if(((String)details.get("documentType")).equals("FOREIGN")){
			this.documentType = DCDocumentType.FOREIGN;
		} else if(((String)details.get("documentType")).equals("DOMESTIC")){
			this.documentType = DCDocumentType.DOMESTIC;
		}
		
		this.processingUnitCode = (String)details.get("processingUnitCode");
	}
	
	public void updateDetails(Map<String, Object> details) {
        System.out.println("\nDocumentAgainstPayment.updatePaymentDetails() ===========\n");
        UtilSetFields.copyMapToObject(this, (HashMap)details);
    }

    public void updateLastTransaction(String lastTransaction) {
        this.lastTransaction = lastTransaction;
    }
	
	@Override
    public void updateStatus(TradeProductStatus tradeProductStatus) {
		System.out.println("DA Status Updated");
        super.updateStatus(tradeProductStatus);

        switch(tradeProductStatus) {
            case NEGOTIATED:
                this.processDate = new Date();
            break;
        }
    }
	
	public void cancelDp() {
		this.cancelledDate = new Date();
		this.status = TradeProductStatus.CANCELLED;
	}
	
//	public void settle(BigDecimal amount) {
	public void settle(Map<String, Object> details) {
		System.out.println("DP details: " + details);
		
		if(!StringUtils.isEmpty((String)details.get("cifNumber"))){
			this.cifNumber = (String)details.get("cifNumber");
		}
		
		if(!StringUtils.isEmpty((String)details.get("cifName"))){
			this.cifName = (String)details.get("cifName");
		}
		
		if(!StringUtils.isEmpty((String)details.get("mainCifNumber"))){
			this.mainCifNumber = (String)details.get("mainCifNumber");
		}
		
		if(!StringUtils.isEmpty((String)details.get("mainCifName"))){
			this.mainCifName = (String)details.get("mainCifName");
		}
		
		if(!StringUtils.isEmpty((String)details.get("accountOfficer"))){
			this.accountOfficer = (String)details.get("accountOfficer");
		}
		
		if(!StringUtils.isEmpty((String)details.get("ccbdBranchUnitCode"))){
			this.ccbdBranchUnitCode = (String)details.get("ccbdBranchUnitCode");
		}
		
		if(!StringUtils.isEmpty((String)details.get("remittingBank"))){
			this.remittingBank = (String)details.get("remittingBank");
		}
		
		if(!StringUtils.isEmpty((String)details.get("remittingBank"))){
            this.reimbursingBank = (String)details.get("reimbursingBank");
        }
		
		if(!StringUtils.isEmpty((String)details.get("remittingBankReferenceNumber"))){
			this.remittingBankReferenceNumber = (String)details.get("remittingBankReferenceNumber");
		}
		
//		if(this.importerCifNumber.isEmpty() && !this.importerCbCode.isEmpty() && !((String)details.get("importerCifNumber")).isEmpty()){
//			this.importerCifNumber = (String)details.get("importerCifNumber");
//			this.importerCbCode = null;
//		} else if(this.importerCbCode.isEmpty() && !this.importerCifNumber.isEmpty() && !((String)details.get("importerCbCode")).isEmpty()){
//			this.importerCbCode = (String)details.get("importerCbCode");
//			this.importerCifNumber = null;
//		}
        if (!StringUtils.isEmpty((String)details.get("importerCifNumber"))) {
            this.importerCifNumber = (String)details.get("importerCifNumber");
            this.importerCbCode = null;
        }

        if (!StringUtils.isEmpty((String)details.get("importerCbCode"))) {
            this.importerCbCode = (String)details.get("importerCbCode");
            this.importerCifNumber = null;
        }
		
        if(!StringUtils.isEmpty((String)details.get("importerName"))){
			this.importerName = (String)details.get("importerName");
		}
		
        if(!StringUtils.isEmpty((String)details.get("importerAddress"))){
			this.importerAddress = (String)details.get("importerAddress");
		}
        
        if(!StringUtils.isEmpty((String)details.get("beneficiaryName"))){
        	this.beneficiaryName = (String)details.get("beneficiaryName");
        }
        
        if(!StringUtils.isEmpty((String)details.get("beneficiaryAddress"))){
        	this.beneficiaryAddress = (String)details.get("beneficiaryAddress");
        }
		
//		this.outstandingAmount = this.outstandingAmount.subtract(amount);
		this.outstandingAmount = this.outstandingAmount.subtract(new BigDecimal((String)details.get("productAmount")));
		if (this.outstandingAmount.compareTo(BigDecimal.ZERO) != 1){
			this.status = TradeProductStatus.CLOSED;
		}
		this.settledDate = new Date();
	}

    public Date getMaturityDate() {
        return maturityDate;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public String getOriginalPort() {
        return originalPort;
    }

    public String getRemittingBank() {
        return remittingBank;
    }

    public String getBeneficiaryAddress() {
        return beneficiaryAddress;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Date getDateOfBlAirwayBill() {
        return dateOfBlAirwayBill;
    }

    public DCDocumentType getDocumentType() {
        return documentType;
    }

    public String getImporterAddress() {
        return importerAddress;
    }

    public String getImporterCbCode() {
        return importerCbCode;
    }

    public String getImporterCifNumber() {
        return importerCifNumber;
    }

    public String getImporterName() {
        return importerName;
    }

    public String getLastTransaction() {
        return lastTransaction;
    }

    public String getProcessingUnitCode() {
        return processingUnitCode;
    }

    public String getReimbursingBank() {
        return reimbursingBank;
    }

    public String getRemittingBankReferenceNumber() {
        return remittingBankReferenceNumber;
    }

    public String getSenderToReceiverInformation() {
        return senderToReceiverInformation;
    }

    public Date getSettledDate() {
        return settledDate;
    }

    public TradeServiceReferenceNumber getTsNumber() {
        return tsNumber;
    }
}

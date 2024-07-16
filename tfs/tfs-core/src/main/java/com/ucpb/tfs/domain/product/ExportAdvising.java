package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.product.enums.*;
import com.ucpb.tfs.utils.UtilSetFields;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 2/19/13
 * Time: 7:07 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Modified by: Rafael Ski Poblete
 * Date: 7/26/18
 * added charge narrative with getter and setter.
 */
@Audited
public class ExportAdvising extends TradeProduct implements Serializable {

    private AdvisingBankType advisingBankType;

    private Date processDate;
    private String exporterCbCode;
    private String exporterName;
    private String exporterAddress;
    private String importerName;
    private String importerAddress;
    private LCType lcType;
    private DocumentNumber lcNumber;
    private Date lcIssueDate;
    private LCTenor lcTenor;
    private String usanceTerm;
    private Currency lcCurrency;
    private BigDecimal lcAmount;
    private Date lcExpiryDate;
    private Boolean confirmedFlag;
    private String issuingBank;
    private String issuingBankName;

	private String issuingBankAddress;
    private String reimbursingBank;
    private String receivingBank;
    private String advisingBank;
    private String advisingBankAddress;

    private BigDecimal totalBankCharges;

    private Integer numberOfAmendments;
    private Date lastAmendmentDate;
    private String senderToReceiver;
    private String senderToReceiverInformation;

    private Boolean withMt730;
    private Boolean withMt799;

    private String narrative;
    private String relatedReference;
    private Date cancellationDate;

    private String lastTransaction;

    private Boolean cwtFlag;
    
    private String chargeNarrative;

    public ExportAdvising() {}

    public ExportAdvising(DocumentNumber documentNumber, Map<String, Object> details, AdvisingBankType advisingBankType) {
        super(documentNumber, ProductType.EXPORT_ADVISING);

        super.setAmount(new BigDecimal((String) details.get("lcAmount")));
        super.setCurrency(Currency.getInstance((String) details.get("lcCurrency")));

        super.updateStatus(TradeProductStatus.OPEN);

        this.advisingBankType = advisingBankType;

        this.lcNumber = new DocumentNumber((String) details.get("lcNumber"));

        if (details.get("confirmedFlag").toString().equals("1")) {
            this.confirmedFlag = Boolean.TRUE;
        } else {
            this.confirmedFlag = Boolean.FALSE;
        }

        if (details.get("cwtFlag").toString().equals("1")) {
            this.confirmedFlag = Boolean.TRUE;
        } else {
            this.confirmedFlag = Boolean.FALSE;
        }

        this.updateDetails(details);

        this.numberOfAmendments = 0;

        this.lastTransaction = "Opening";
    }

    public String getIssuingBankName() {
		return issuingBankName;
	}

	public void setIssuingBankName(String issuingBankName) {
		this.issuingBankName = issuingBankName;
	}
    
    public void amendExportAdvising(BigDecimal amount, Currency currency) {
        super.setAmount(amount);
        super.setCurrency(currency);
    }

    public void updateDetails(Map<String, Object> details) {
        UtilSetFields.copyMapToObject(this, (HashMap<String, Object>) details);
    }

    public DocumentNumber getDocumentNumber() {
        return super.getDocumentNumber();
    }

    public DocumentNumber getLcNumber() {
        return lcNumber;
    }

    public void setWithMt730(Boolean withMt730) {
        this.withMt730 = withMt730;
    }

    public void setWithMt799(Boolean withMt799) {
        this.withMt799 = withMt799;
    }

    public Boolean getWithMt730() {
        return withMt730;
    }

    public Boolean getWithMt799() {
        return withMt799;
    }

    // for amendment
    public void amendExportAdvising(String newExporterName, String newExporterAddress, Date lastAmendmentDate) {
        if(this.numberOfAmendments == null) {
            this.numberOfAmendments = 0;
        }
        this.lastAmendmentDate = lastAmendmentDate;

        this.exporterName = newExporterName;
        this.exporterAddress = newExporterAddress;

        this.lastTransaction = "Amendment";
    }

    public void decreaseAmendmentCount() {
        if(this.numberOfAmendments == null) {
            this.numberOfAmendments = 0;
        }else {
            this.numberOfAmendments -= 1;
        }
    }

    // for cancellation
    public void cancelExportAdvising(Date cancellationDate) {
        super.status = TradeProductStatus.CANCELLED;

        this.cancellationDate = cancellationDate;

        this.lastTransaction = "Cancellation";
    }

    public Date getLcIssueDate() {
        return lcIssueDate;
    }

    public LCType getLcType() {
        return lcType;
    }

    public LCTenor getLcTenor() {
        return lcTenor;
    }

    public String getUsanceTerm() {
        return usanceTerm;
    }

    public Currency getLcCurrency() {
        return lcCurrency;
    }

    public BigDecimal getLcAmount() {
        return lcAmount;
    }

    public Date getLcExpiryDate() {
        return lcExpiryDate;
    }

    public String getIssuingBank() {
        return issuingBank;
    }

    public String getIssuingBankAddress() {
        return issuingBankAddress;
    }

    public String getReimbursingBank() {
        return reimbursingBank;
    }
    
    public String getImporterName() {
    	return importerName;
    }
    
    public String getImporterAddress() {
    	return importerAddress;
    }
    
    public Date getProcessDate() {
    	return processDate;
    }
    
    public Date getLastAmendmentDate() {
    	return lastAmendmentDate;
    }

    public String getExporterName() {
        return exporterName;
    }

    public String getExporterCbCode() {
        return exporterCbCode;
    }

	public String getChargeNarrative() {
		return chargeNarrative;
	}

	public void setChargeNarrative(String chargeNarrative) {
		this.chargeNarrative = chargeNarrative;
	}
}

package com.ucpb.tfs.domain.product;

import com.ucpb.tfs.domain.product.enums.IndemnityType;
import com.ucpb.tfs.domain.product.enums.ProductType;
import com.ucpb.tfs.domain.product.enums.TransportMedium;
import com.ucpb.tfs.utils.UtilSetFields;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Marv
 * Date: 9/26/12
 */
@Audited
public class Indemnity extends TradeProduct {

    private DocumentNumber indemnityNumber;
    private IndemnityType indemnityType;

    private TransportMedium transportMedium;

    private Integer shipmentSequenceNumber; // increments (unique count for every LC)

    private String shipmentCurrency;
    private BigDecimal shipmentAmount;

    // this is also shipment issue date  and issuance booking date
    // todo: validate this
    private Date indemnityIssueDate;

    private Boolean cwtFlag;

    // tr line  (equal to Facility Id
    private String facilityId;


    // Bill of Lading or Airway Bill number
    private String blAirwayBillNumber; // former blabNumber

    private Date processDate;

    private String blPresented;

    // used in cancellation
    private Date cancellationDate;
    private Date documentReleaseDate;   // date shipping documents were released to client

    private DocumentNumber referenceNumber; // reference to the LC number
    
    public Indemnity() {}

    public Indemnity(DocumentNumber indemnityNumber, IndemnityType indemnityType, DocumentNumber referenceNumber) {
        super(indemnityNumber, ProductType.INDEMNITY);

        this.indemnityNumber = indemnityNumber;
        this.indemnityType = indemnityType;
        this.referenceNumber = referenceNumber;
    }
    
    public void updateDetails(Map<String, Object> details) {
        UtilSetFields.copyMapToObject(this, (HashMap) details);
    }

}

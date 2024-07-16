package com.ucpb.tfs.swift.message;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.*;

/**
 */
@XmlRootElement(name =  "swift_address",namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
@XmlType(propOrder={"bankIdentifierCode","branchCode"})
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SwiftAddress {

    private static final int BIC_LENGTH = 8;
    private static final int BRANCH_CODE_LENGTH = 3;
    private static final String NO_BRANCH_CODE = "XXX";
    private static final int SWIFT_ADDRESS_LENGTH = 11;
    private static final int SWIFT_ADDRESS_LENGTH_WITH_LT_PAD = 12;
    private static final String DEFAULT_LT_SEPARATOR = "X";

    private String bankIdentifierCode;

    private String branchCode;

    private String ltSeparator = DEFAULT_LT_SEPARATOR;

    @XmlElement(name = "bank_identifier_code", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getBankIdentifierCode() {
        return bankIdentifierCode;
    }

    public void setBankIdentifierCode(String bankIdentifierCode) {
        this.bankIdentifierCode = bankIdentifierCode;
    }

    @XmlElement(name = "branch_code", namespace = SwiftMessageSchemas.SWIFT_MESSAGE)
    public String getBranchCode() {
        return branchCode != null ? branchCode : NO_BRANCH_CODE;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    @XmlTransient
    public String getLtSeparator() {
        return ltSeparator;
    }

    public void setLtSeparator(String ltSeparator) {
        this.ltSeparator = ltSeparator;
    }

    public void setCompleteAddress(String swiftAddress){
        this.bankIdentifierCode = StringUtils.left(swiftAddress, BIC_LENGTH);
        if(swiftAddress.length() == SWIFT_ADDRESS_LENGTH || swiftAddress.length() == SWIFT_ADDRESS_LENGTH_WITH_LT_PAD){
            this.branchCode = StringUtils.right(swiftAddress, BRANCH_CODE_LENGTH);
        }
        if(swiftAddress.length() == SWIFT_ADDRESS_LENGTH_WITH_LT_PAD){
            this.ltSeparator = String.valueOf(swiftAddress.charAt(8));
        }
    }

    @XmlTransient
    public String getCompleteAddress(){
        return bankIdentifierCode + getBranchCode();
    }

    @XmlTransient
    public String getAddressWithLtPadding(){
        if(bankIdentifierCode != null){
            return bankIdentifierCode + ltSeparator + (branchCode != null ? branchCode : NO_BRANCH_CODE);
        }
        return null;
    }

}

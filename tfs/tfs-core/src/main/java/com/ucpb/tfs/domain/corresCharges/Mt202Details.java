package com.ucpb.tfs.domain.corresCharges;

import com.ucpb.tfs.domain.product.DocumentNumber;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 7/16/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */

public class Mt202Details {

    private DocumentNumber mtDcumentNumber;

    private String remittingBankReferenceNumber;

    private String timeIndicationCode;
    private String timeIndication;

    private Date transactionDate;
    private Currency settlementCurrency;
    private BigDecimal settlementAmount;

    private String orderingBankFlag;
    private String orderingBankCode;
    private String orderingBankNameAndAddress;

    private String sendersCorrespondentFlag;
    private String sendersCorrespondentCode;
    private String sendersCorrespondentPartyIdentifier;
    private String sendersCorrespondentNameAndAddress;

    private String receiversCorrespondentFlag;
    private String receiversCorrespondentCode;
    private String receiversCorrespondentPartyIdentifier;
    private String recieversCorrespondentLocation;
    private String receiversCorrespondentNameAndAddress;

    private String intermediaryFlag;
    private String intermediaryCode;
    private String intermediaryNameAndAddress;

    private String accountWithBankFlag;
    private String accountWithBankCode;
    private String accountWithBankPartyIdentifier;
    private String accountWithBankLocation;
    private String accountWithBankNameAndAddress;

    private String beneficiaryBankFlag;
    private String beneficiaryBankCode;
    private String beneficiaryBankNameAndAddress;

    private String senderToReceiverInformation;

    public Mt202Details() {}

    public Mt202Details(Map<String, Object> details) {
//        this.mtDcumentNumber = new Documentt  Number((String) details.get("documentNumberMT103"));
        // temp fix
        if (details.get("documentNumberMT103") != null){
            this.mtDcumentNumber = new DocumentNumber((String) details.get("documentNumberMT103"));
        }

        if (details.get("documentNumberMT202") != null){
            this.mtDcumentNumber = new DocumentNumber((String) details.get("documentNumberMT202"));
        }

        this.remittingBankReferenceNumber = (String) details.get("remittingBankReferenceNumber");

        if (details.get("timeIndicationMt") != null) {
            this.timeIndicationCode = (String) details.get("timeIndicationMt");
        }

        if (details.get("timeIndicationMt202") != null) {
            this.timeIndicationCode = (String) details.get("timeIndicationMt202");
        }


        if (details.get("timeIndicationFieldMt") != null) {
            this.timeIndication = (String) details.get("timeIndicationFieldMt");
        }

        if (details.get("timeIndicationFieldMt202") != null) {
            this.timeIndication = (String) details.get("timeIndicationFieldMt202");
        }

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            if (details.get("valueDateMt202") != null) {
                this.transactionDate = dateFormat.parse((String) details.get("valueDateMt202"));
            }

            if (details.get("valueDateMt") != null) {
                this.transactionDate = dateFormat.parse((String) details.get("valueDateMt"));
            }

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        if (details.get("lcCurrencyMt") != null) {
            this.settlementCurrency = Currency.getInstance((String) details.get("lcCurrencyMt"));
        }

        if (details.get("lcCurrencyMt202") != null) {
            this.settlementCurrency = Currency.getInstance((String) details.get("lcCurrencyMt202"));
        }


        if (details.get("netAmountMt") != null) {
            this.settlementAmount = new BigDecimal(details.get("netAmountMt").toString().replaceAll(",", ""));
        }

        if (details.get("netAmountMt202") != null) {
            this.settlementAmount = new BigDecimal(details.get("netAmountMt202").toString().replaceAll(",", ""));
        }


        if (details.get("orderingBankFlagMt") != null) {
            this.orderingBankFlag = (String) details.get("orderingBankFlagMt");
        }

        if (details.get("orderingBankFlagMt202") != null) {
            this.orderingBankFlag = (String) details.get("orderingBankFlagMt202");
        }


        if (details.get("bankIdentifierCodeMt") != null) {
            this.orderingBankCode = (String) details.get("bankIdentifierCodeMt");
        }

        if (details.get("bankIdentifierCodeMt202") != null) {
            this.orderingBankCode = (String) details.get("bankIdentifierCodeMt202");
        }


        if (details.get("bankNameAndAddressMt") != null) {
            this.orderingBankNameAndAddress = (String) details.get("bankNameAndAddressMt");
        }

        if (details.get("bankNameAndAddressMt202") != null) {
            this.orderingBankNameAndAddress = (String) details.get("bankNameAndAddressMt202");
        }


        if (details.get("sendersCorrespondentFlagMt") != null) {
            this.sendersCorrespondentFlag = (String) details.get("sendersCorrespondentFlagMt");
        }

        if (details.get("sendersCorrespondentFlagMt202") != null) {
            this.sendersCorrespondentFlag = (String) details.get("sendersCorrespondentFlagMt202");
        }


        if (details.get("senderIdentifierCodeMt") != null) {
            this.sendersCorrespondentCode = (String) details.get("senderIdentifierCodeMt");
        }

        if (details.get("senderIdentifierCodeMt202") != null) {
            this.sendersCorrespondentCode = (String) details.get("senderIdentifierCodeMt202");
        }


        if (details.get("senderPartyIdentifierMt") != null) {
            this.sendersCorrespondentPartyIdentifier = (String) details.get("senderPartyIdentifierMt");
        }

        if (details.get("senderPartyIdentifierMt202") != null) {
            this.sendersCorrespondentPartyIdentifier = (String) details.get("senderPartyIdentifierMt202");
        }


        if (details.get("senderNameAndAddressMt") != null) {
            this.sendersCorrespondentNameAndAddress = (String) details.get("senderNameAndAddressMt");
        }

        if (details.get("senderNameAndAddressMt202") != null) {
            this.sendersCorrespondentNameAndAddress = (String) details.get("senderNameAndAddressMt202");
        }


        if (details.get("receiversCorrespondentFlagMt") != null) {
            this.receiversCorrespondentFlag = (String) details.get("receiversCorrespondentFlagMt");
        }

        if (details.get("receiversCorrespondentFlagMt202") != null) {
            this.receiversCorrespondentFlag = (String) details.get("receiversCorrespondentFlagMt202");
        }


        if (details.get("receiverIdentifierCodeMt") != null) {
            this.receiversCorrespondentCode = (String) details.get("receiverIdentifierCodeMt");
        }

        if (details.get("receiverIdentifierCodeMt202") != null) {
            this.receiversCorrespondentCode = (String) details.get("receiverIdentifierCodeMt202");
        }


        if (details.get("receiverPartyIdentifierMt") != null) {
            this.receiversCorrespondentPartyIdentifier = (String) details.get("receiverPartyIdentifierMt");
        }

        if (details.get("receiverPartyIdentifierMt202") != null) {
            this.receiversCorrespondentPartyIdentifier = (String) details.get("receiverPartyIdentifierMt202");
        }


        if (details.get("receiverLocationMt") != null) {
            this.recieversCorrespondentLocation = (String) details.get("receiverLocationMt");
        }

        if (details.get("receiverLocationMt202") != null) {
            this.recieversCorrespondentLocation = (String) details.get("receiverLocationMt202");
        }


        if (details.get("receiverNameAndAddressMt") != null) {
            this.receiversCorrespondentNameAndAddress = (String) details.get("receiverNameAndAddressMt");
        }

        if (details.get("receiverNameAndAddressMt202") != null) {
            this.receiversCorrespondentNameAndAddress = (String) details.get("receiverNameAndAddressMt202");
        }


        if (details.get("intermediaryFlagMt") != null) {
            this.intermediaryFlag = (String) details.get("intermediaryFlagMt");
        }

        if (details.get("intermediaryFlagMt202") != null) {
            this.intermediaryFlag = (String) details.get("intermediaryFlagMt202");
        }


        if (details.get("intermediaryIdentifierCodeMt") != null) {
            this.intermediaryCode = (String) details.get("intermediaryIdentifierCodeMt");
        }

        if (details.get("intermediaryIdentifierCodeMt202") != null) {
            this.intermediaryCode = (String) details.get("intermediaryIdentifierCodeMt202");
        }


        if (details.get("intermediaryNameAndAddressMt") != null) {
            this.intermediaryNameAndAddress = (String) details.get("intermediaryNameAndAddressMt");
        }

        if (details.get("intermediaryNameAndAddressMt202") != null) {
            this.intermediaryNameAndAddress = (String) details.get("intermediaryNameAndAddressMt202");
        }


        if (details.get("accountWithBankFlagMt") != null) {
            this.accountWithBankFlag = (String) details.get("accountWithBankFlagMt");
        }

        if (details.get("accountWithBankFlagMt202") != null) {
            this.accountWithBankFlag = (String) details.get("accountWithBankFlagMt202");
        }


        if (details.get("accountIdentifierCodeMt") != null) {
            this.accountWithBankCode = (String) details.get("accountIdentifierCodeMt");
        }

        if (details.get("accountIdentifierCodeMt202") != null) {
            this.accountWithBankCode = (String) details.get("accountIdentifierCodeMt202");
        }


        if (details.get("accountWithBankIdentifierMt") != null) {
            this.accountWithBankPartyIdentifier = (String) details.get("accountWithBankIdentifierMt");
        }

        if (details.get("accountWithBankIdentifierMt202") != null) {
            this.accountWithBankPartyIdentifier = (String) details.get("accountWithBankIdentifierMt202");
        }


        if (details.get("accountWithBankLocationMt") != null) {
            this.accountWithBankLocation = (String) details.get("accountWithBankLocationMt");
        }

        if (details.get("accountWithBankLocationMt202") != null) {
            this.accountWithBankLocation = (String) details.get("accountWithBankLocationMt202");
        }


        if (details.get("accountNameAndAddressMt") != null) {
            this.accountWithBankNameAndAddress = (String) details.get("accountNameAndAddressMt");
        }

        if (details.get("accountNameAndAddressMt202") != null) {
            this.accountWithBankNameAndAddress = (String) details.get("accountNameAndAddressMt202");
        }


        if (details.get("beneficiaryBankFlagMt") != null) {
            this.beneficiaryBankFlag = (String) details.get("beneficiaryBankFlagMt");
        }

        if (details.get("beneficiaryBankFlagMt202") != null) {
            this.beneficiaryBankFlag = (String) details.get("beneficiaryBankFlagMt202");
        }


        if (details.get("beneficiaryIdentifierCodeMt") != null) {
            this.beneficiaryBankCode = (String) details.get("beneficiaryIdentifierCodeMt");
        }

        if (details.get("beneficiaryIdentifierCodeMt202") != null) {
            this.beneficiaryBankCode = (String) details.get("beneficiaryIdentifierCodeMt202");
        }


        if (details.get("beneficiaryNameAndAddressMt") != null) {
            this.beneficiaryBankNameAndAddress = (String) details.get("beneficiaryNameAndAddressMt");
        }

        if (details.get("beneficiaryNameAndAddressMt202") != null) {
            this.beneficiaryBankNameAndAddress = (String) details.get("beneficiaryNameAndAddressMt202");
        }


        if (details.get("senderToReceiverInformationMt") != null) {
            this.senderToReceiverInformation = (String) details.get("senderToReceiverInformationMt");
        }

        if (details.get("senderToReceiverInformationMt202") != null) {
            this.senderToReceiverInformation = (String) details.get("senderToReceiverInformationMt202");
        }

    }

}

package com.ucpb.tfs.swift.message;

/** Modified by: Rafael Ski Poblete
 *  Date : 7/26/18
 *  Description : Added Z_DATA_TYPE to handle Z characters.
 */
public final class SwiftFields {

    public static final String X_DATA_TYPE = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]";
    public static final String COMMENTS  = "(([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,65})){0,}";
    public static final String ADDRESS = "[a-zA-Z\\Q/-?:().,'+\\E\\s]{0,65}";
    public static final String PARTY_IDENTIFIER = "[a-zA-Z\\Q/-?:().,'+\\E\\s]{0,35}";
    public static final String ACCOUNT_IDENTIFICATION = "[a-zA-Z\\Q/-?:().,'+\\E\\s]{0,35}";
    public static final String FORM_OF_DOCUMENTARY_CREDIT = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,24}";
    public static final String DOCUMENT_NUMBER = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,16}";
    public static final String PRE_ADVICE = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,16}";
    public static final String IDENTIFICATION = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,16}";
    public static final String REFERENCE_NUMBER = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,16}";
    public static final String DATE_6 = "\\d{6}";
    public static final String MONEY = "[a-zA-Z]{3}[\\d,]{0,15}";
    public static final String NOTES = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,65}){0,100}";
    public static final String NARRATIVE = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,50}){0,35}";
    public static final String FREE_FORMAT_NARRATIVE = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,35}){0,50}";
    public static final String APPLICABLE_RULES = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,30}[/?][a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,35}";
    public static final String APPLICABLE_RULES_ONLY = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,30}";
    public static final String RULES_WITH_NARRATIVE = "[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,30}[/][a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,35}";
    public static final String CHARGES = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,35}){0,6}";
    public static final String SENDER_TO_RECEIVER_INFO = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,35}){0,6}";
    public static final String DISCREPANCIES = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,50}){0,70}";

    public static final String DATE_AND_PLACE = "\\d{6}" + X_DATA_TYPE + "{0,29}";

    public static final String DETAILS_OF_GUARANTEE = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,65}){0,150}";

    public static final String AMMENDMENT_DETAILS = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,65}){0,150}";

    public static final String TIME_INDICATION = "/[\\w]{0,8}[\\d]{4}[\\w][\\d]{4}";

    public static final String SEQUENCE_NUMBER = "[\\d]+[/][\\d]+";

    public static final String NUMBER_OF_AMENDMENT = "[\\d]{2}";

    public static final String PERCENTAGE = "\\d{2}[/]\\d{2}";

    public static final String MAX_CREDIT_AMOUNT = "[a-zA-Z0-9]{0,13}";

    public static final String ADDITIONAL_AMOUNTS_COVERED = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,35}){0,4}";

    public static final String SHIPMENT_PERIOD = "([a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,65}){0,6}";

    public static final String DATE_AND_AMOUNT = "[\\d]{6}[\\w]{3}[\\d]{15}";

    public static final String PARTY_IDENTIFIER_AND_CODE = "(/\\w)?(/[a-zA-Z0-9\\Q/-?:().,'+\\E\\s]{0,34}[\\s])?\\w{4}\\w{2}\\w{2}(\\w{3})?";

    public static final String PARTY_IDENTIFIER_AND_NAME_ADDRESS = "(/\\w)?(/[a-zA-Z0-9]{34}[\\s])?([a-zA-Z0-9]{0,35}){0,4}";

    public static final String PARTY_IDENTIFIER_AND_LOCATION ="(/\\w)?(/[a-zA-Z0-9]{34}[\\s])?[a-zA-Z0-9]{0,35}";

    public static final String Z_DATA_TYPE = "[a-zA-Z0-9\\Q/-?:().,'+=!\\.\"%&*<>;{@_#\\E\\s]";
    
    private SwiftFields(){
        //do not instantiate
    }
}

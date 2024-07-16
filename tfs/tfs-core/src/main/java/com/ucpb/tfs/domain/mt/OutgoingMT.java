package com.ucpb.tfs.domain.mt;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OutgoingMT {

    String id;

    String userId;
    Date createDate;

    String messageType;

    TransactionType transactionType;
    String destinationBankCode;

    private Map<String, Object> details;

    OutgoingMT() {
        this.details =  new HashMap<String, Object>();
        this.createDate = new Date();
    }

    public OutgoingMT(String userid, String messageType, String destinationBankCode) {
        this();

        this.userId = userid;
        this.destinationBankCode = destinationBankCode;
        this.messageType = messageType;
    }

    public OutgoingMT(String userid, String messageType, String destinationBankCode, Map details) {
        this();

        this.userId = userid;
        this.destinationBankCode = destinationBankCode;
        this.messageType = messageType;
        this.details = details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    // this is used to store details as JSON into our database
    // this is not meant to be called by anything other than by the persistence mechanism
    private void setMTDetails(String mtDetails) {

        Gson gson = new Gson();

        System.out.println("details: " + mtDetails);

        try {
        // use GSON to deserialize from JSON to our HashMap
        this.details = gson.fromJson(mtDetails, new TypeToken<HashMap<String, String>>() {}.getType());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    // this is used to retrieve details stored as JSON in the database to our HashMap
    // this is not meant to be called by anything other than by the persistence mechanism
    private String getMTDetails() {

        Gson gson = new Gson();

        // use GSON to serialize our HashMap to a JSON string that will be stored in the DB
        return gson.toJson(details);
    }

}

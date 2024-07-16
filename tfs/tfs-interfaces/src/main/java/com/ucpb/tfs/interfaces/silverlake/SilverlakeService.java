package com.ucpb.tfs.interfaces.silverlake;


import com.ucpb.tfs.interfaces.domain.CasaAccount;

public class SilverlakeService {

    private static final String VALID_OUTPUT = "000066666688888888                                                ";
    private static final String INVALID_OUTPUT = "131366666688888888                                                ";
    private static final String INVALID_OUTPUT1 = "0005005 Insufficient balance                                      ";
    private static final String INVALID_OUTPUT2 = "0092005 Refer to Officer w/IBT Agreemt                                ";
    private static final String INVALID_OUTPUT3 = "0034034 Org jrnl seq# not found                                   ";
    private static final String INVALID_OUTPUT4 = "0221221 Print passbook cover first                                ";
    private static final String INVALID_OUTPUT5 = "0221118 Mishandled Account 1570006997                                ";

    private static final String INQ_STATUS_CURRENT_FORMAT = "(.{8})(.{8})(.{3})(.{4})1101(.{4})([\\d]{12})([\\d]{15})";
    private static final String INQ_STATUS_SAVINGS_FORMAT = "(.{8})(.{8})(.{3})(.{4})2101(.{4})([\\d]{12})([\\d]{15})";

    public String process(String input){
		System.out.println("********* INPUT :" + input + " **************");
        String output = INVALID_OUTPUT;
        if(input.matches(INQ_STATUS_CURRENT_FORMAT)){
            System.out.println("*** RECEIVED STATUS INQUIRY - CURRENT");
            output = "00000MIGHTY CORP                                                  ";
        }else if(input.matches(INQ_STATUS_SAVINGS_FORMAT)){
            System.out.println("*** RECEIVED STATUS INQUIRY - SAVINGS");

            output =  "00000MIGHTY CORP                                                  ";
        }else if(input.matches("(.){66}")){
            System.out.println("*** RECEIVED VALID CASA REQUEST");

            output = VALID_OUTPUT;
//            output = INVALID_OUTPUT1;
//            output = INVALID_OUTPUT2;
//            output = INVALID_OUTPUT3;
//            output = INVALID_OUTPUT4;
//            output = INVALID_OUTPUT5;
        }

		return output;
	}
	
}

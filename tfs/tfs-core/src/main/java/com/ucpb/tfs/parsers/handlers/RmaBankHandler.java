package com.ucpb.tfs.parsers.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RmaBankHandler  extends DefaultHandler{

	private List<Map<String,Object>> banksToSave = new ArrayList<Map<String,Object>>();
	private String bicCode=StringUtils.EMPTY;
	private String nodeContent=StringUtils.EMPTY;
	
	private final String AUTHORIZED_BANK = "TLBPPHMM";
	private final String BIC_CODE_NODE = "Doc:Issr";
	private final String RMA_STATUS_NODE = "Sw:RMASts";
	private final String AUTHORIZED_BANK_NODE="Doc:Crspdt";
	private boolean isStatusEnabled=false;

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		//Note: This will only work if RMA STATUS NODE will be parsed first before BIC CODE 
		//and BIC CODE first before AUTHORIZED BANK CODE
		Map<String,Object> refBankObject = new HashMap<String,Object>();
		if(qName.equalsIgnoreCase(RMA_STATUS_NODE) && nodeContent.equalsIgnoreCase("Enabled")){
			isStatusEnabled=true;
		}else if(qName.equalsIgnoreCase(BIC_CODE_NODE)){
			bicCode=nodeContent;
		}else if(qName.equalsIgnoreCase(AUTHORIZED_BANK_NODE) && isStatusEnabled &&
				AUTHORIZED_BANK.equalsIgnoreCase(nodeContent)){
			refBankObject.put("bic", bicCode);
			refBankObject.put("rmaFlag", "Y");
			banksToSave.add(refBankObject);
			isStatusEnabled=false;			
		}else if(qName.equalsIgnoreCase(AUTHORIZED_BANK_NODE)){
			refBankObject.put("bic", bicCode);
			refBankObject.put("rmaFlag", "N");
			banksToSave.add(refBankObject);
			isStatusEnabled=false;						
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		nodeContent=String.copyValueOf(ch, start, length);
	}
	
	public List<Map<String, Object>> getBanksToSave() {
		return banksToSave;
	}
}
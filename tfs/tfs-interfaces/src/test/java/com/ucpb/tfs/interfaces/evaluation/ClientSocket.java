package com.ucpb.tfs.interfaces.evaluation;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

//import com.ibs.dao.transaction.TransactionDAO;

public class ClientSocket {
	
	public ClientSocket() {
		
	}

	public String sendTransactionToHost(String inputRequest) throws IOException {
		
	//	TransactionDAO transaction = new TransactionDAO();
		
	//	String traceNumber = transaction.generateRandomNumber();
		
	//	System.out.println("sendTransactionToHost start "+transaction.getCurrentDateTimestamp()+" "+traceNumber+".");
		
		String responseString = "";
		
		System.out.println("inputRequest "+inputRequest);
		
		int inputRequestLength = inputRequest.length();
		
		System.out.println("inputRequestLength "+inputRequestLength);
		
        Socket echoSocket = null;

        try {
            echoSocket = new Socket("130.130.0.225", 36113);
            
//            System.out.println("sendTransactionToHost echoSocket initialized "+transaction.getCurrentDateTimestamp()+" "+traceNumber+".");
            
        } catch (UnknownHostException e) {
        	System.out.println("UnknownHostException");
            System.exit(1);
        } catch (IOException e) {
        	System.out.println("IOException");
            System.exit(1);
        }

        DataOutputStream dataOutputStream = new DataOutputStream(echoSocket.getOutputStream());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(echoSocket.getInputStream());
        
//        System.out.println("sendTransactionToHost output and input stream initialized "+transaction.getCurrentDateTimestamp()+" "+traceNumber+".");
        
        byte[] messageHeader = new byte[] {
                (byte)(inputRequestLength >>> 24),
                (byte)(inputRequestLength >>> 16),
                (byte)(inputRequestLength >>> 8),
                (byte)inputRequestLength};
        
        byte[] inputRequestByteArray = inputRequest.getBytes("CP1047");
        
        if (echoSocket != null && bufferedInputStream != null && dataOutputStream != null) {
		
//        	System.out.println("sendTransactionToHost preparing to send input bytes "+transaction.getCurrentDateTimestamp()+" "+traceNumber+".");
			
			byte[] one = messageHeader; 
			byte[] two = inputRequestByteArray;
			byte[] combined = new byte[one.length + two.length];  
			System.arraycopy(one,0,combined,0,one.length); 
			System.arraycopy(two,0,combined,one.length,two.length); 
	           
			dataOutputStream.write(combined);
			dataOutputStream.flush();
									
//			System.out.println("sendTransactionToHost input bytes sent "+transaction.getCurrentDateTimestamp()+" "+traceNumber+".");			
			
//			for (int x = 0; x < inputRequest.length(); x++) {
//	        	System.out.println(x + " : " + inputRequest.charAt(x));
//	        }
			
	        byte[] responseBufferByteArray = new byte[10000]; 
	  
	        int responseBufferInteger = 0;
	        
//	        System.out.println("sendTransactionToHost preparing to recieve response bytes "+transaction.getCurrentDateTimestamp()+" "+traceNumber+".");
	
	        responseBufferInteger = bufferedInputStream.read(responseBufferByteArray);
	        
//	        System.out.println("sendTransactionToHost response bytes received "+transaction.getCurrentDateTimestamp()+" "+traceNumber+".");
	        
	        System.out.println("responseBufferInteger : " + responseBufferInteger);
	        System.out.println("responseBufferByteArrayLength "+responseBufferByteArray.length);
	        
	        responseString = new String(responseBufferByteArray, 4, responseBufferInteger - 4, "CP1047");
	        
//	        System.out.println("sendTransactionToHost response bytes converted to string "+transaction.getCurrentDateTimestamp()+" "+traceNumber+".");
	        
	        System.out.println("responseString "+responseString);
	        System.out.println("responseString.length() "+responseString.length());
	        

	        
        }
        else{
        	System.out.println("Null!");
        }

        System.out.println("finish");

        bufferedInputStream.close();
        dataOutputStream.close();
        
//        System.out.println("sendTransactionToHost output and input stream closed "+transaction.getCurrentDateTime()+" "+traceNumber+".");
        
        return responseString;
        
	}
	
}

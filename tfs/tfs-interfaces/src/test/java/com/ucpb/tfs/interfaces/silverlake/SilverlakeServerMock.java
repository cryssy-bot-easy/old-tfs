package com.ucpb.tfs.interfaces.silverlake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.lang.StringUtils;

import com.ucpb.tfs.interfaces.gateway.serializer.CasaSerializer;

public class SilverlakeServerMock extends Thread{

	public static final int DEFAULT_PORT = 9090;
	
	private int port;
	
	@Override
	public void run() {
		System.out.println("SERVER STARTING");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(getPort());
		} catch (IOException e) {
			System.out.println("COULD NOT LISTEN TO PORT: " + getPort());
			e.printStackTrace();
		}
		Socket clientSocket = null;
		try {
		    clientSocket = serverSocket.accept();
//		    System.out.println("CONNECTION OPENED");
//		    int messageLength = new CasaHeaderDeserializer().deserialize(clientSocket.getInputStream());
//		    System.out.println("RECEIVED MESSAGE LENGTH: " + messageLength);
//		    printInput(clientSocket.getInputStream(), 53);

		   InputStream input = clientSocket.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	        String msg = reader.readLine();
	        System.out.println(msg);
		    
		    OutputStream output = clientSocket.getOutputStream();
		    String response =StringUtils.rightPad("0000refNumtaskId01",48," ");
		    output.write(String.valueOf(response.length()).getBytes(CasaSerializer.DEFAULT_ENCODING));
		    output.write(response.getBytes(CasaSerializer.DEFAULT_ENCODING));


		} 
		catch (IOException e) {
		    System.out.println("Accept failed: ");
		    System.exit(-1);
		}
		
	}
	
	private void printInput(InputStream input, int messageLength) throws IOException{
		byte[] rawInput = new byte[1000];
		int totalBytesRead = 0;
		while(totalBytesRead < messageLength && (totalBytesRead = input.read(rawInput)) != -1){
			System.out.println(new String(rawInput,CasaSerializer.DEFAULT_ENCODING));
		}
	}
	
	public int getPort() {
		return port != 0 ? port : DEFAULT_PORT;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	private String buildDefaultResponse() {
		StringBuilder mockResponse = new StringBuilder();
		mockResponse.append("0000").append("refNum").append("taskId01")
				.append(StringUtils.leftPad("", 48, " "));
		return mockResponse.toString();
	}
}

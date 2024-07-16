package com.ucpb.tfs.interfaces.silverlake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.ucpb.tfs.interfaces.gateway.serializer.CasaHeaderDeserializer;
import com.ucpb.tfs.interfaces.gateway.serializer.CasaSerializer;
import com.ucpb.tfs.interfaces.util.SocketUtils;

public class MockSilverlakeServer extends Thread {

	public static final int DEFAULT_PORT = 9090;

	private CasaHeaderDeserializer casaHeaderDeserializer = new CasaHeaderDeserializer();

	private int port = 9090;

	private boolean stop = false;

	private String encoding;

	@Override
	public void run() {
		stop = false;
		System.out.println("**** STARTING SILVERLAKE SERVER *****");
		Socket socket = null;
		BufferedReader reader = null;
		OutputStream outputStream = null;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(getPort());
			socket = serverSocket.accept();
			System.out.println("PORT OPENED: " + socket.getLocalPort());
			while (!stop) {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),CasaSerializer.DEFAULT_ENCODING));
//				printInput(socket.getInputStream());
//				String input = reader.readLine();
				 if(reader.readLine() != null){
//					System.out.println("READ: " + input);
					outputStream = socket.getOutputStream();
					byte[] response = buildDefaultResponse().getBytes(getEncoding());
					outputStream.write(String.valueOf(response.length).getBytes(CasaSerializer.DEFAULT_ENCODING));
					outputStream.write(response);
					outputStream.flush();
				 }

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(outputStream);
			SocketUtils.closeQuietly(socket);
			SocketUtils.closeQuietly(serverSocket);
		}
	}
	
	private void printInput(BufferedReader reader) throws IOException,
			UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			System.out.println("READ: " + line);
			builder.append(line);
			line = reader.readLine();
		}
	}

	private void printInput(InputStream input) throws IOException {
		int messageLength = casaHeaderDeserializer.deserialize(input);
		int bytesRead = 0;
		System.out.println("MESSAGE LENGTH: " + messageLength);
		byte[] message = new byte[1000];
		while (bytesRead != messageLength) {
			bytesRead += input.read(message, 0, messageLength - bytesRead);
			System.out.println("**** SERVER READ: "
					+ new String(message, getEncoding()));
		}
	}

	public int getPort() {
		return port != 0 ? port : DEFAULT_PORT;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void stopServer() {
		System.out.println("**** STOPPING SERVER ****");
		this.stop = true;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return this.encoding != null ? encoding
				: CasaSerializer.DEFAULT_ENCODING;
	}

	private String buildDefaultResponse() {
		StringBuilder mockResponse = new StringBuilder();
		mockResponse.append("0000").append("refNum").append("taskId01")
				.append(StringUtils.leftPad("", 48, " "));
		return mockResponse.toString();
	}

}

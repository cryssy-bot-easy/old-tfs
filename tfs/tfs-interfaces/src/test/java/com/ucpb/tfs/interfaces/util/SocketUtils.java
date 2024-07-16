package com.ucpb.tfs.interfaces.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketUtils {

	private SocketUtils(){
		//do not instantiate!
	}
	
	public static void closeQuietly(Socket socket){
		try {
			if(socket!=null){
				socket.close();
			}
		} catch (IOException e) {
			//ignore exception
		} 
	}
	
	public static void closeQuietly(ServerSocket socket){
		try {
			if(socket != null){
				socket.close();
			}
		} catch (IOException e) {
			//ignore exception
		} 
	}

}

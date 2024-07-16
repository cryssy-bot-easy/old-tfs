package com.ucpb.tfs.interfaces.util;

import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import static org.junit.Assert.fail;


public class ServerUtils {

	public static void waitListening(AbstractServerConnectionFactory serverConnectionFactory) {
		int n = 0;
		while (!serverConnectionFactory.isListening()) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				throw new IllegalStateException(e1);
			}

			if (n++ > 100) {
//				fail("Server didn't begin listening.");
				throw new RuntimeException("Server did not start");
			}
		}
	}

}
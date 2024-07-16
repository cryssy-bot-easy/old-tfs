package com.ucpb.tfs.interfaces.gateway;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.ucpb.tfs.interfaces.gateway.CasaRequest;

@Ignore
public class CasaRequestTest {

	@Test
	public void successfullyConvertMessageToBytes() throws UnsupportedEncodingException{
		StringBuilder builder = new StringBuilder();
		builder.append("    user")
		.append("    pass")
		.append(" 341")
		.append(" uId")
		.append("1101")
		.append("  accountNum")
		.append("000000000018.12")
		.append("    ")
		.append("1234");

        FinRequest request = new FinRequest();
		request.setUsername("user");
		request.setPassword("pass");
		request.setBranchCode("341");
		request.setUserId("uId");
		request.setTransactionCode(TransactionCode.INQUIRE_STATUS_CURRENT);
		request.setAccountNumber("accountNum");
		request.setAmount(new BigDecimal("18.12"));

		assertEquals(builder.toString(),request.toRequestString());
		byte[] expected = builder.toString().getBytes("CP1047");
		byte[] actual = request.pack("CP1047");
		
		for(int ctr = 0; ctr < expected.length;ctr++){
			assertEquals(expected[ctr],actual[ctr]);
		}
		
	}

    @Test
    public void splitTest(){
        String amount = "18.12";
        String[] decimals = amount.split("\\.");
        assertEquals(2,decimals.length);
        assertEquals("18",decimals[0]);
        assertEquals("12",decimals[1]);
    }

 }

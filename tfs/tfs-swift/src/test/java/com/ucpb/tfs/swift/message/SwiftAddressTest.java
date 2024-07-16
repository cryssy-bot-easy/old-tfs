package com.ucpb.tfs.swift.message;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 */
public class SwiftAddressTest {


    @Test
    public void successfullyParseCompleteSwiftAddress(){
        SwiftAddress swiftAddress = new SwiftAddress();
        swiftAddress.setCompleteAddress("AAAAAAAADDD");
        assertEquals("AAAAAAAA",swiftAddress.getBankIdentifierCode());
        assertEquals("DDD",swiftAddress.getBranchCode());
        assertEquals("X",swiftAddress.getLtSeparator());
        assertEquals("AAAAAAAAXDDD",swiftAddress.getAddressWithLtPadding());
        assertEquals("AAAAAAAADDD",swiftAddress.getCompleteAddress());
    }

    @Test
    public void successfullyParseBICAddress(){
        SwiftAddress swiftAddress = new SwiftAddress();
        swiftAddress.setCompleteAddress("AAAAAAAA");
        assertEquals("AAAAAAAA",swiftAddress.getBankIdentifierCode());
        assertEquals("XXX",swiftAddress.getBranchCode());
        assertEquals("AAAAAAAAXXXX",swiftAddress.getAddressWithLtPadding());
        assertEquals("AAAAAAAAXXX",swiftAddress.getCompleteAddress());

    }

    @Test
    public void successfullyRetrieveLtSeparator(){
        SwiftAddress swiftAddress = new SwiftAddress();
        swiftAddress.setCompleteAddress("AAAAAAAABCCC");
        assertEquals("AAAAAAAA",swiftAddress.getBankIdentifierCode());
        assertEquals("CCC",swiftAddress.getBranchCode());
        assertEquals("B",swiftAddress.getLtSeparator());
        assertEquals("AAAAAAAABCCC",swiftAddress.getAddressWithLtPadding());
        assertEquals("AAAAAAAACCC",swiftAddress.getCompleteAddress());
    }
}

package com.ucpb.tfs.interfaces.util;

/**
 */
public class PackedDecimalConverter {


    public static double convertToDecimal(int packedDecimal) {
        int power = 0;
        double total = 0;
        int sign = getSign(packedDecimal);
        while (packedDecimal > 0) {
            packedDecimal = packedDecimal >> 4;
            total = total + ((packedDecimal & 0xF) * Math.pow(10,power));
            power++;
        }
        return sign * total;
    }

    public static int getSign(int packedDecimal){
        if((packedDecimal & 0xF) == 0xF){
            return 1;
        }
        return -1;
    }


}

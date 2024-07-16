package com.ucpb.tfs.batch.report.dw.formatter;

import com.ancientprogramming.fixedformat4j.annotation.Align;
import com.ancientprogramming.fixedformat4j.exception.FixedFormatException;
import com.ancientprogramming.fixedformat4j.format.FixedFormatter;
import com.ancientprogramming.fixedformat4j.format.FormatInstructions;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;

/**
 */
public class BigDecimalFormatter implements FixedFormatter<BigDecimal> {


    @Override
    public BigDecimal parse(String s, FormatInstructions formatInstructions) throws FixedFormatException {
        return new BigDecimal(s);
    }

    @Override
    public String format(BigDecimal bigDecimal, FormatInstructions formatInstructions) throws FixedFormatException {
        String output = "0";
        if(bigDecimal != null && BigDecimal.ZERO.compareTo(bigDecimal)!=0){
            output = bigDecimal.toString();
        }

        if(Align.RIGHT.equals(formatInstructions.getAlignment())){
            return StringUtils.leftPad(output,formatInstructions.getLength()," ");
        }
        return StringUtils.rightPad(output,formatInstructions.getLength()," ");
    }
}

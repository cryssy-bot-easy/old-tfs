package com.ucpb.tfs.interfaces.domain.transformer;

import au.com.bytecode.opencsv.CSVWriter;
import org.springframework.integration.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 */
public class StringArrayToCsvWriter {

    private static final String DEFAULT_FILENAME = "WIP.csv";

    private String filename;

    public File transform(Message<?>message) throws IOException {
        List<String[]> list = (List<String[]>)message.getPayload();
        File outputFile = new File(getFilename());
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile,true), '\t');
        writer.writeAll(list);
        return outputFile;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename != null ? filename : DEFAULT_FILENAME;
    }


}

package com.ucpb.tfs.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Marv
 * Date: 1/22/14
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class WriteFile {

    // http://www.homeandlearn.co.uk/java/write_to_textfile.html
    private String path;
    private boolean append_to_file = false;

    public WriteFile(String file_path) {
        path = file_path;
    }

    public WriteFile(String file_path, boolean append_value) {
        path = file_path;
        append_to_file = append_value;
    }

    public void writeToFile(String textLine) throws IOException {
        FileWriter write = new FileWriter(path, append_to_file);
        PrintWriter print_line = new PrintWriter(write);

        print_line.printf("%s" + "%n", textLine);

        print_line.close();
    }
}

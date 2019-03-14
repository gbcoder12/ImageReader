package com.gbcode.testing;
import com.gbcode.tools.ImageCrypter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class MainTest {
    private static final String NO_ARG_MSG = "",
            UNKNOWN_CMD_MSG = "Command unknown. Possible commands:\n" +
                    "\tencrypt [-f text_file_name | message] [img_file_name | img_file_source img_file_dst]\n" +
                    "\tdecrypt [img_file_source | img_file_source text_file_output]",
            IMG_NOT_FOUND_MSG = "Image source could not be found.";


    public static void main(String[] args) {
        try {
            ImageCrypter crypter;


        } catch(FileNotFoundException e) { System.out.println(IMG_NOT_FOUND_MSG); }
        catch (IOException e) { e.printStackTrace(); }
    }
}

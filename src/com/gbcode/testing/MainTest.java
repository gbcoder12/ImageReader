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


    public static void main(String[] args) throws IOException {
        try {
            switch (args.length) {
                case 2:
                    if (args[0].equals("decrypt")) { // decrypt filename
                        System.out.println(ImageCrypter.decrypt(args[1]));
                    } else
                        System.out.println(UNKNOWN_CMD_MSG);
                    break;

                case 3:
                    if (args[0].equals("encrypt")) // encrypt message filename
                        ImageCrypter.encrypt(args[1], args[2]);
                    else if (args[0].equals("decrypt")) { // decrypt filename_img filename_out
                        PrintStream out = new PrintStream(args[2]);
                        out.println(ImageCrypter.decrypt(args[1]));
                    } else
                        System.out.println(UNKNOWN_CMD_MSG);
                    break;

                case 4:
                    if (args[0].equals("encrypt")) { // encrypt -f filename_txt filename
                        if (args[1].equals("-f")) {
                            StringBuilder str = new StringBuilder();
                            Scanner in = new Scanner(args[2]);
                            while (in.hasNextLine())
                                str.append(in.nextLine());
                            ImageCrypter.encrypt(str.toString(), args[3]);
                            in.close();
                        } else // encrypt message filename_in filename_out
                            ImageCrypter.encrypt(args[1], args[2], args[3]);
                    } else
                        System.out.println(UNKNOWN_CMD_MSG);
                    break;

                case 5:
                    if (args[0].equals("encrypt")) { // encrypt -f filename_txt filename_in filename_out
                        if (args[1].equals("-f")) {
                            StringBuilder str = new StringBuilder();
                            Scanner in = new Scanner(args[2]);
                            while (in.hasNextLine())
                                str.append(in.nextLine());
                            ImageCrypter.encrypt(str.toString(), args[3], args[4]);
                        }
                    }
                    System.out.println(UNKNOWN_CMD_MSG);
                    break;
            }
        } catch(FileNotFoundException e) { System.out.println(IMG_NOT_FOUND_MSG); }
        catch (IOException e) { e.printStackTrace(); }
    }
}

package com.gbcode.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageCrypter {

    /**
     * Puts a message into one image.
     * @param message Message to encrypt.
     * @param imgName Name of the image file to encrypt into and write to.
     * @throws IOException When an error occurs in file management.
     * @throws IllegalArgumentException When the message passed in can't fit in the image.
     */
    public static void encrypt(String message, String imgName)
        throws IOException, IllegalArgumentException {
        encrypt(message, imgName, imgName, true);
    }

    /**
     * Puts a message into an image.
     * @param message Message to encrypt.
     * @param inputImgName Name of image file to encrypt into.
     * @param outputImgName Name of image file to output as (same file as input will overwrite it).
     * @param colored If the encryption should use multi-colored pixels (true), or black and white pixels (false).
     * @throws IOException When an error occurs in file management.
     * @throws IllegalArgumentException When the message passed in can't fit in the image.
     */
    public static void encrypt(String message, String inputImgName, String outputImgName, boolean colored)
            throws IOException, IllegalArgumentException {
        File inImg = new File(inputImgName);
        BufferedImage img;
        if(!inImg.exists()) {
            int size;
            if(!colored)
                size = (int) (Math.sqrt(message.length()*8)+1);
            else
                size = (int) (Math.sqrt(message.length())+1);
            img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        } else
            img = ImageIO.read(inImg);


        // change any possibly conflicting values for black and white colored pixels
        if(!colored) {
            for (int h = 0; h < img.getHeight(); h++) {
                for (int w = 0; w < img.getWidth(); w++) {
                    if (img.getRGB(w, h) == -16777216) // check if it's "black"
                        img.setRGB(w, h, img.getRGB(w, h) + 1); // add one to make it not "black"
                    else if (img.getRGB(w, h) == -1) // check if it's "white"
                        img.setRGB(w, h, img.getRGB(w, h) - 1); // subtract one to make it not "white"
                }
            }
        }


        // encrypts the message to bytes

        int imgSize = img.getWidth()*img.getHeight();

        // check if size of text is too big.
        if((!colored && message.length()*8 > imgSize) || (colored && message.length() > imgSize))
            throw new IllegalArgumentException("Length of message is too long for image provided.");

        // begin encryption (black and white)
        if(!colored) {
            byte[] encryption = new byte[imgSize];
            int index = 0;
            int spacing = imgSize / (message.length() * 8);
            byte[] bytes = message.getBytes();
            for (byte b : bytes) {
                byte temp = b;
                for (int i = 7; i >= 0; i--) {
                    int val = (int) (temp / Math.pow(2, i));
                    if (val == 0) encryption[index] = -1;
                    else {
                        encryption[index] = 1;
                        temp %= Math.pow(2, i);
                    }
                    index += spacing;
                }
            }

            index = 0;
            for (int h = 0; h < img.getHeight(); h++) {
                for (int w = 0; w < img.getWidth(); w++) {
                    if (encryption[index] == 1) img.setRGB(w, h, -16777216);
                    else if (encryption[index] == -1) img.setRGB(w, h, -1);
                    index++;
                }
            }
        }
        // begin encryption (colored)
        else {
            // todo implement
        }


        String ending = outputImgName.substring(outputImgName.length()-3);
        File outImg = new File(outputImgName);
        ImageIO.write(img, ending, outImg); // writes image to output image.
    }
    
    /**
     * Reads a message from an image.
     * @param imgName Name of image file to read from.
     * @return The message in the image.
     * @throws IOException When an error occurs in file management.
     */
    public static String decrypt(String imgName) throws IOException {
        File imgFile = new File(imgName);
        BufferedImage img = ImageIO.read(new FileInputStream(imgFile));
        byte[] bwPixels = new byte[img.getWidth()*img.getHeight()]; // stores black/white pixels

        // store the white and black pixels, others are stored as -1 to indicate unknown.
        int index = 0;
        for (int h = 0; h < img.getHeight(); h++) {
            for (int w = 0; w < img.getWidth(); w++) {
                // -16777216 is the black RGB color, -1 is white
                if(img.getRGB(w, h) == -16777216)
                    bwPixels[index] = 1;
                else if(img.getRGB(w, h) == -1)
                    bwPixels[index] = -1;
                index++;
            }
        }

        // read characters from sets of 8 pixels (bits)
        StringBuilder msg = new StringBuilder();
        int i = 7; // counts to 8 bits
        byte b = 0; // stores current byte
        for (byte pixel : bwPixels) {
            // add byte to string as character
            if(i == -1) {
                msg.append((char) b);
                b = 0;
                i = 7;
            }
            if(pixel == 1) {
                b += Math.pow(2, i)*pixel;
                i--;
            } else if(pixel == -1) i--;
        }
        // if any bits are leftover, print them out as decimal value
        if(b != 0) msg.append(b);

        return msg.toString();
    }

}

package com.gbcode.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageDecrypter {
    byte[] bwPixels;
    StringBuilder message = new StringBuilder();

    public ImageDecrypter(String fileName) throws IOException {
        this(new File(fileName));
    }

    public ImageDecrypter(File file) throws IOException {
        BufferedImage img = ImageIO.read(new FileInputStream(file));
        bwPixels = new byte[img.getWidth()*img.getHeight()]; // stores black/white pixels

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
        int i = 7; // counts to 8 bits
        byte b = 0; // stores current byte
        for (byte pixel : bwPixels) {
            // add byte to string as character
            if(i == -1) {
                addToString(b);
                b = 0;
                i = 7;
            }
            if(pixel == 1) {
                b += Math.pow(2, i)*pixel;
                i--;
            } else if(pixel == -1) i--;
        }
        // if any bits are leftover, print them out as decimal value
        if(b != 0) this.message.append(b);
    }

    private void addToString(byte x) {
        this.message.append((char) x);
    }

    public String getMessage() {
        return this.message.toString();
    }
}

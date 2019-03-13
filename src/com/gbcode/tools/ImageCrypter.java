package com.gbcode.tools;

import javax.imageio.ImageIO;
import java.awt.*;
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
        encrypt(message, imgName, imgName);
    }

    /**
     * Puts a message into an image.
     * @param message Message to encrypt.
     * @param inputImgName Name of image file to encrypt into.
     * @param outputImgName Name of image file to output as (same file as input will overwrite it).
     * @throws IOException When an error occurs in file management.
     * @throws IllegalArgumentException When the message passed in can't fit in the image.
     */
    public static void encrypt(String message, String inputImgName, String outputImgName)
            throws IOException, IllegalArgumentException {
        File inImg = new File(inputImgName);
        BufferedImage img;
        if (!inImg.exists()) {
            int size = (int) (Math.sqrt(message.length()) + 1);
            img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        } else
            img = ImageIO.read(inImg);


        // encrypts the message to bytes

        int imgSize = img.getWidth() * img.getHeight();

        // check if size of text is too big.
        if (message.length() > imgSize)
            throw new IllegalArgumentException("Length of message is too long for image provided.");

        // begin encryption
        Color[] newPixels = new Color[message.length() + 1]; // stores the last few bits (r 3, g 3, b 2) of the pixel
        int index = 0;
        byte[] bytes = message.getBytes();
        for (byte b : bytes) {
            byte rSub = (byte) (b >> 5), // bits 7:5 of 'b' being picked out (00000xxx)
                    gSub = (byte) ((b & 28) >> 2), // bits 4:2 of 'b' being picked out (000xxx00)
                    bSub = (byte) (b & 3); // bits 1:0 of 'b' being picked out (000000xx)
            newPixels[index++] = new Color(rSub, gSub, bSub);
        }
        newPixels[index] = new Color(0, 0, 3); // indicates end of the sentence

        index = 0;
        for (int h = 0; h < img.getHeight(); h++) {
            for (int w = 0; w < img.getWidth(); w++) {
                if (index >= newPixels.length) break;
                Color current = new Color(img.getRGB(w, h));
                newPixels[index] = new Color( // establishes the new color
                        (current.getRed() & 248) | newPixels[index].getRed(),
                        (current.getGreen() & 248) | newPixels[index].getGreen(),
                        (current.getBlue() & 252) | newPixels[index].getBlue()
                );
                img.setRGB(w, h, newPixels[index++].getRGB());
            }
            if(index >= newPixels.length) break;
        }

        String ending = outputImgName.substring(outputImgName.length() - 3);
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
        StringBuilder msg = new StringBuilder();

        // store colors in array
        for (int h = 0; h < img.getHeight(); h++) {
            for (int w = 0; w < img.getWidth(); w++) {
                Color current = new Color(img.getRGB(w, h));
                byte rSub = (byte) (current.getRed() << 5),
                        gSub = (byte) ((current.getGreen() & 7) << 2),
                        bSub = (byte) (current.getBlue() & 3);
                byte b = (byte) (rSub | gSub | bSub);
                if(b == 3) return msg.toString(); // 3 is ETX character (end of text)
                msg.append((char) b);
            }
        }
        return msg.toString();
    }

}

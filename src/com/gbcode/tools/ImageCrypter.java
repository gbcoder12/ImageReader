package com.gbcode.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageCrypter {

    private String message;
    private BufferedImage imgPre;
    private File imgPost;

    public ImageCrypter(String message, String imgName) throws IOException {
        this(message, imgName, imgName);
    }

    public ImageCrypter(String message, String srcImgName, String dstImgName) throws IOException {
        this.message = message;
        File srcImg = new File(srcImgName);

        // create blank image if it doesn't exist.
        if (!srcImg.exists()) {
            Logger.info("Creating blank image...");
            int size = (int) (Math.sqrt(message.length()) + 1);
            this.imgPre = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        // otherwise just use source image.
        } else
            Logger.info("Source image initialized.");
            this.imgPre= ImageIO.read(new FileInputStream(srcImg));

        if(srcImgName.equals(dstImgName)) this.imgPost = new File(srcImgName);
    }

    /**
     * Puts a message into an image.
     * @throws IOException When an error occurs in file management.
     * @throws IllegalArgumentException When the message passed in can't fit in the image.
     */
    public void encrypt() throws IOException, IllegalArgumentException {
        if(this.isOverflowing())
            throw new IllegalArgumentException("Length of message is too big for image.");

        // Stores color differentials to be added to each pixel on the image.
        Color[] newPixels = new Color[message.length() + 1]; // stores the last few bits (r 3, g 3, b 2) of the pixel
        int index = 0;
        byte[] bytes = message.getBytes();
        for (byte b : bytes) {
            byte rSub = (byte) (b >> 5), // bits 7:5 of 'b' being picked out (00000xxx)
                    gSub = (byte) ((b & 28) >> 2), // bits 4:2 of 'b' being picked out (000xxx00)
                    bSub = (byte) (b & 3); // bits 1:0 of 'b' being picked out (000000xx)
            newPixels[index++] = new Color(rSub, gSub, bSub);
        }
        newPixels[index] = new Color(0, 0, 3); // ETX char indicates end of the sentence

        // Adds each differential in 'newPixels' to the pixels in the image.
        index = 0;
        for (int h = 0; h < this.imgPre.getHeight(); h++) {
            for (int w = 0; w < this.imgPre.getWidth(); w++) {
                if (index >= newPixels.length) break; // done encrypting
                Color current = new Color(this.imgPre.getRGB(w, h));
                newPixels[index] = new Color( // establishes the new color
                        (current.getRed() & 248) | newPixels[index].getRed(),
                        (current.getGreen() & 248) | newPixels[index].getGreen(),
                        (current.getBlue() & 252) | newPixels[index].getBlue()
                );
                this.imgPre.setRGB(w, h, newPixels[index++].getRGB());
            }
            if(index >= newPixels.length) break; // done encrypting
        }

        // writes image to the destination image.
        ImageIO.write(this.imgPre, "png", this.imgPost);
    }

    /**
     * Reads a message from the resulting image.
     * @return The message in the image.
     * @throws IOException When an error occurs in file management.
     */
    public String decrypt() throws IOException {
        if(!this.imgPost.exists()) {
            Logger.err("Encrypted image did not exist when accessed!");
            return null;
        }

        BufferedImage img = ImageIO.read(new FileInputStream(this.imgPost));
        StringBuilder msg = new StringBuilder();

        // Reads each pixel and appends char to 'msg'
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

    /**
     * @return True if the message is too big for the picture, false if it will fit.
     */
    public boolean isOverflowing() {
        int imgSize = this.imgPre.getWidth() * this.imgPre.getHeight();
        return this.message.length() > imgSize;
    }

}

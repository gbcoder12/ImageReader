package com.gbcode.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageEncrypter {
    private File outFile;
    private BufferedImage img;
    private String message;
    private byte[] encryptedMsg;

    public ImageEncrypter(String message, String outFileName) throws IOException {
        this(message, outFileName, true);
    }

    public ImageEncrypter(String message, String outFileName, boolean secret) throws IOException {
        this.message = message;
        this.outFile = new File(outFileName);
        if(!this.outFile.exists()) {
            int size = (int) (Math.sqrt(this.message.length()*8)+1);
            this.img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        } else
            this.img = ImageIO.read(this.outFile);

        // change any possibly conflicting values
        for (int h = 0; h < this.img.getHeight(); h++) {
            for (int w = 0; w < this.img.getWidth(); w++) {
                if (this.img.getRGB(w, h) == -16777216) // check if it's "black"
                    this.img.setRGB(w, h, this.img.getRGB(w, h) + 1); // add one to make it not "black"
                else if (this.img.getRGB(w, h) == -1) // check if it's "white"
                    this.img.setRGB(w, h, this.img.getRGB(w, h) - 1); // subtract one to make it not "white"
            }
        }

        try {
            if (secret) this.encryptedMsg = encryptSecretlyToBytes(this.message, this.img);
            else this.encryptedMsg = encryptToBytes(this.message, this.img);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Message too big for " + this.outFile.getAbsolutePath());
        }
    }

    private byte[] encryptSecretlyToBytes(String message, BufferedImage image) {
        return new byte[0];
    }

    private byte[] encryptToBytes(String message, BufferedImage image) throws ArrayIndexOutOfBoundsException {
        byte[] encryption = new byte[image.getWidth()*image.getHeight()];
        int index = 0;

        byte[] bytes = message.getBytes();
        for (byte b : bytes) {
            byte temp = b;
            for (int i = 7; i >= 0; i--) {
                int val = (int) (temp / Math.pow(2, i));
                if(val == 0) encryption[index++] = -1;
                else {
                    encryption[index++] = 1;
                    temp %= Math.pow(2, i);
                }
            }
        }

        return encryption;
    }

    public boolean encryptImage() throws IOException {
        if(this.encryptedMsg == null) return false;
        int index = 0;
        for (int h = 0; h < this.img.getHeight(); h++) {
            for (int w = 0; w < this.img.getWidth(); w++) {
                if(this.encryptedMsg[index] == 1) this.img.setRGB(w, h, -16777216);
                else if(this.encryptedMsg[index] == -1) this.img.setRGB(w, h, -1);
                index++;
            }
        }
        String fileName = this.outFile.getName();
        String ending = fileName.substring(fileName.length()-3, fileName.length());

        ImageIO.write(this.img, ending, this.outFile);
        return true;
    }
}

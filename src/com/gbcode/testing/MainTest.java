package com.gbcode.testing;

import com.gbcode.tools.ImageCrypter;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

import static org.junit.Assert.fail;

public class MainTest {
    private ImageCrypter ic;

    @Test
    public void test_encrypt_message_into_nonexistent_image() {
        try {
            String msg = "This is a new message", file = "res/testing/test001.png";
            ic = new ImageCrypter(msg, file);
            ic.encrypt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

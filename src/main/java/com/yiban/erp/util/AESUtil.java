package com.yiban.erp.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AESUtil {
    public static final String PASSWORD = "ybjf_1234";

    public static String decrypt(String content) throws Exception {
        byte[] result = null;
        try
        {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");

            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");

            secureRandom.setSeed(PASSWORD.getBytes());

            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] encodeFormat = secretKey.getEncoded();

            SecretKeySpec key = new SecretKeySpec(encodeFormat, "AES");

            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, key);
            result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, "utf-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }
}

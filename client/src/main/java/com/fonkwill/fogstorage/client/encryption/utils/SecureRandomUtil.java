package com.fonkwill.fogstorage.client.encryption.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public class SecureRandomUtil {

    private static SecureRandom secureRandom;

    private static final Logger logger = LoggerFactory.getLogger(SecureRandomUtil.class);

    /**
     * Returns a random byte array (with a secure random)
     * @param i The size of the array
     * @return A random byte array
     */
    public static byte[] getBytes(int i) {
        byte[] bytes = new byte[i];
        getSecureRandomInstance().nextBytes(bytes);
        return bytes;
    }

    public static  SecureRandom getSecureRandomInstance(){
        if (secureRandom == null) {
            logger.debug("Setting up Secure Random");
            secureRandom = new SecureRandom();
            logger.debug("Secure Random set up");
        }
        return  secureRandom;
    }
}

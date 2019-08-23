package com.fonkwill.fogstorage.client.encryption.impl;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public abstract  class AbstractAesService {

    public static final int keyLengthBit = 256;
    public static final String algorithm = "AES";
    public final String algorithmAndPadding = "AES/CBC/PKCS5Padding";

    protected String key;

    protected SecretKeySpec secretKeySpec;

}

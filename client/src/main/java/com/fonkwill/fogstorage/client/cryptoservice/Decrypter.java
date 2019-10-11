package com.fonkwill.fogstorage.client.cryptoservice;

import com.fonkwill.fogstorage.client.cryptoservice.exception.EncryptionException;

public interface Decrypter {

    /**
     * Decrypts a encrypted byte array with the given key and returns a plain byte array of the same length
     * @param content The byte array to decrypt
     * @return A byte array with the decrypted data
     */
    public byte[] decrypt(byte[] content) throws EncryptionException;
}

package com.simroth.passwordstore.crypto;

public interface CryptoDecryptCallback {
    void onDecryptDone(PgpData pgpData);
}

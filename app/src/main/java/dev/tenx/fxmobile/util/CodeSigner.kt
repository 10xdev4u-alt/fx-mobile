package dev.tenx.fxmobile.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CodeSigner @Inject constructor(
    private val context: Context
) {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val alias = "fx_mobile_key"

    init {
        if (!keyStore.containsAlias(alias)) {
            generateKey()
        }
    }

    private fun generateKey() {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            "AndroidKeyStore"
        )
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        )
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .build()
        keyPairGenerator.initialize(spec)
        keyPairGenerator.generateKeyPair()
    }

    fun sign(data: ByteArray): ByteArray {
        val privateKey = keyStore.getKey(alias, null) as java.security.PrivateKey
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    fun getPublicKey(): String {
        val cert = keyStore.getCertificate(alias)
        return android.util.Base64.encodeToString(cert.publicKey.encoded, android.util.Base64.NO_WRAP)
    }

    fun verifySignature(data: ByteArray, signatureBytes: ByteArray): Boolean {
        val cert = keyStore.getCertificate(alias)
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initVerify(cert.publicKey)
        signature.update(data)
        return signature.verify(signatureBytes)
    }
}

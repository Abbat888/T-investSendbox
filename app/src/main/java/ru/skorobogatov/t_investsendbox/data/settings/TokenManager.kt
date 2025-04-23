package ru.skorobogatov.t_investsendbox.data.settings

import android.annotation.SuppressLint
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data_store")

class TokenManager @Inject constructor(
    private val context: Context
): TokenInterface {

    companion object {
        val ENCRYPTED_TOKEN = stringPreferencesKey("encrypted_token")

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: TokenManager? = null
        private val LOCK = Any()

        fun getInstance(context: Context): TokenManager {

            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }

                val tokenManager = TokenManager(context)

                INSTANCE = tokenManager
                return tokenManager
            }
        }
    }

    init {
        generateKey()
    }

    override fun saveToken(token: String) {
        val encrypted = encryptToken(token)
        runBlocking {
            context.dataStore.edit { prefs ->
                prefs[ENCRYPTED_TOKEN] = encrypted
            }
        }
    }

    override fun getToken(): String? {
        val prefs = runBlocking { context.dataStore.data.first()}
        val encrypted = prefs[ENCRYPTED_TOKEN]

        return if (encrypted != null) {
            decryptToken(encrypted)
        } else {
            null
        }
    }

    override fun checkToken(): Boolean {
        val prefs = runBlocking { context.dataStore.data.first() }
        val encrypted = prefs[ENCRYPTED_TOKEN]

        return encrypted != null
    }

    private fun generateKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias("token_key")) {
            val keyGen =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val spec = KeyGenParameterSpec.Builder(
                "token_key",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

            keyGen.init(spec)
            keyGen.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return (keyStore.getEntry("token_key", null) as KeyStore.SecretKeyEntry).secretKey
    }

    private fun encryptToken(plainText: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    private fun decryptToken(base64CipherText: String): String {
        val combined = Base64.decode(base64CipherText, Base64.DEFAULT)
        val iv = combined.sliceArray(0 until 12)
        val encrypted = combined.sliceArray(12 until combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}
package chela.kotlin.core

import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import chela.kotlin.android.ChApp
import chela.kotlin.android.ChShared
import java.math.BigInteger
import java.security.*
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAKeyGenParameterSpec.F4
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.security.auth.x500.X500Principal

object ChCrypto{
    @JvmStatic private val sha256 = MessageDigest.getInstance("SHA-256")
    @JvmStatic fun sha256(v:String):String
         = sha256.digest(v.toByteArray()).fold(""){str,it-> str+"%02x".format(it)}
    @JvmStatic fun rsaEncrypt(v:String) = Rsa.encrypt(v)
    @JvmStatic fun rsaDecrypt(v:String) = Rsa.decrypt(v)
    @JvmStatic fun generateRandomKey(lengthBits:Int) = with(KeyGenerator.getInstance("AES")){
        init(lengthBits, SecureRandom())
        generateKey().encoded
    }
    @JvmStatic fun permanentPw():String{
        var pw = ChShared.name("ch").s("dp")
        if(pw.isBlank()){
            val secretKey = ChCrypto.generateRandomKey(256)
            pw = String(Base64.encode(secretKey, Base64.DEFAULT))
            ChShared.name("ch").s("dp", ChCrypto.rsaEncrypt(pw))
            secretKey.fill(0, 0, secretKey.size - 1)
        }else pw = ChCrypto.rsaDecrypt(pw)
        return String(Base64.decode(pw, Base64.DEFAULT))
    }
    @JvmStatic fun rsaPublicKey() = Rsa.publicKey()
}
private object Rsa{
    private const val KEY_LENGTH_BIT = 2048
    private const val VALIDITY_YEARS = 25
    private const val KEY_PROVIDER_NAME = "AndroidKeyStore"
    private const val CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"
    private lateinit var keyEntry: KeyStore.Entry
    @Suppress("ObjectPropertyName")
    private var _isSupported = false

    val isSupported: Boolean
        get() = _isSupported

    private fun init() {
        if(isSupported) return
        val alias = "${ChApp.packName}.rsakeypairs"
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {load(null)}
        _isSupported = when{
            keyStore.containsAlias(alias) -> true
            Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 -> initM(alias)
            else -> initL(alias)
        }
        keyEntry = keyStore.getEntry(alias, null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initM(alias: String) = try{
        with(KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_PROVIDER_NAME)){
            val spec = KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(KEY_LENGTH_BIT, F4))
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setDigests(KeyProperties.DIGEST_SHA512,
                    KeyProperties.DIGEST_SHA384,
                    KeyProperties.DIGEST_SHA256)
                .setUserAuthenticationRequired(false)
                .build()
            initialize(spec)
            generateKeyPair()
        }
        true
    }catch(e:GeneralSecurityException){false}

    @Suppress("DEPRECATION")
    private fun initL(alias: String) = try{
        with(KeyPairGenerator.getInstance("RSA", KEY_PROVIDER_NAME)){
            val start = Calendar.getInstance(Locale.ENGLISH)
            val end = Calendar.getInstance(Locale.ENGLISH).apply { add(Calendar.YEAR, VALIDITY_YEARS) }
            val spec = KeyPairGeneratorSpec.Builder(ChApp.app)
                .setKeySize(KEY_LENGTH_BIT)
                .setAlias(alias)
                .setSubject(X500Principal("CN=francescojo.github.com, OU=Android dev, O=Francesco Jo, L=Chiyoda, ST=Tokyo, C=JP"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
            initialize(spec)
            generateKeyPair()
        }
        true
    }catch(e:GeneralSecurityException){false}
    fun publicKey():String{
        init()
        return String((keyEntry as KeyStore.PrivateKeyEntry).certificate.publicKey.encoded)
    }
    fun encrypt(plainText: String): String {
        init()
        if (!_isSupported) return plainText
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply{
            init(Cipher.ENCRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).certificate.publicKey)
        }
        val bytes = plainText.toByteArray(Charsets.UTF_8)
        val encryptedBytes = cipher.doFinal(bytes)
        val base64EncryptedBytes = Base64.encode(encryptedBytes, Base64.DEFAULT)
        return String(base64EncryptedBytes)
    }
    fun decrypt(base64EncryptedCipherText: String): String {
        init()
        if(!_isSupported) return base64EncryptedCipherText
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply{
            init(Cipher.DECRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).privateKey)
        }
        val base64EncryptedBytes = base64EncryptedCipherText.toByteArray(Charsets.UTF_8)
        val encryptedBytes = Base64.decode(base64EncryptedBytes, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}
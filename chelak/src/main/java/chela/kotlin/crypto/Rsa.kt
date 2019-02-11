@file:Suppress("DEPRECATION")

package chela.kotlin.crypto

import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import chela.kotlin.android.ChApp
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.spec.RSAKeyGenParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

internal object Rsa{
    private const val KEY_LENGTH_BIT = 2048
    private const val VALIDITY_YEARS = 25
    private const val KEY_PROVIDER_NAME = "AndroidKeyStore"
    private const val CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"
    private lateinit var keyEntry: KeyStore.Entry
    private var isInited = false

    private fun init(){
        if(isInited) return
        isInited = true
        val alias = "${ChApp.packName}.rsakeypairs"
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply{load(null)}
        if(!when{
            keyStore.containsAlias(alias) -> true
            Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 -> initM(alias)
            else -> initL(alias)
        }) throw Throwable("RSA not support!")
        keyEntry = keyStore.getEntry(alias, null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initM(alias: String) = try{
        with(
            KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                KEY_PROVIDER_NAME
            )
        ){
            val spec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setAlgorithmParameterSpec(
                    RSAKeyGenParameterSpec(
                        KEY_LENGTH_BIT,
                        RSAKeyGenParameterSpec.F4
                    )
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setDigests(
                    KeyProperties.DIGEST_SHA512,
                    KeyProperties.DIGEST_SHA384,
                    KeyProperties.DIGEST_SHA256
                )
                .setUserAuthenticationRequired(false)
                .build()
            initialize(spec)
            generateKeyPair()
        }
        true
    }catch(e: GeneralSecurityException){false}
    @Suppress("DEPRECATION")
    private fun initL(alias: String) = try{
        with(KeyPairGenerator.getInstance("RSA", KEY_PROVIDER_NAME)){
            val start = Calendar.getInstance(Locale.ENGLISH)
            val end = Calendar.getInstance(Locale.ENGLISH).apply { add(
                Calendar.YEAR,
                VALIDITY_YEARS
            ) }
            val spec = KeyPairGeneratorSpec.Builder(ChApp.app)
                .setKeySize(KEY_LENGTH_BIT)
                .setAlias(alias)
                .setSubject(X500Principal("CN=chela.com, OU=Android dev, O=chela, L=chela, ST=Seoul, C=KR"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
            initialize(spec)
            generateKeyPair()
        }
        true
    }catch(e: GeneralSecurityException){false}
    fun publicKey():String{
        init()
        return String(
            Base64.encode(
                (keyEntry as KeyStore.PrivateKeyEntry).certificate.publicKey.encoded,
                Base64.DEFAULT
            )
        ).replace("\n", "\\n")
    }
    fun encrypt(v:String, isBase64:Boolean = true):String{
        init()
        val encryptedBytes = Cipher.getInstance(CIPHER_ALGORITHM).apply{
            init(Cipher.ENCRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).certificate.publicKey)
        }.doFinal(v.toByteArray(Charsets.UTF_8))
        return String(if(isBase64) Base64.encode(encryptedBytes, Base64.DEFAULT) else encryptedBytes)
    }
    fun encrypt(v:ByteArray, publicKey: PublicKey):String{
        val encryptedBytes = Cipher.getInstance(CIPHER_ALGORITHM).apply{
            init(Cipher.ENCRYPT_MODE, publicKey)
        }.doFinal(v)
        return String(Base64.encode(encryptedBytes, Base64.DEFAULT))
    }
    fun decrypt(v:String, isBase64:Boolean = true):String{
        init()
        val bytes = v.toByteArray(Charsets.UTF_8)
        return String(Cipher.getInstance(CIPHER_ALGORITHM).apply{
            init(Cipher.DECRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).privateKey)
        }.doFinal(if(isBase64) Base64.decode(bytes, Base64.DEFAULT) else bytes))
    }
}
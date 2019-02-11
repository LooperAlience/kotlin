@file:Suppress("DEPRECATION")

package chela.kotlin.crypto

import chela.kotlin.android.ChShared
import java.security.MessageDigest
import java.security.PublicKey
import java.security.SecureRandom
import javax.crypto.KeyGenerator

object ChCrypto{
    @JvmStatic private val sha256 = MessageDigest.getInstance("SHA-256")
    @JvmStatic fun sha256(v:String):String
         = sha256.digest(v.toByteArray()).fold(""){ str, it-> str+"%02x".format(it)}

    @JvmStatic fun rsaPublicKey() = Rsa.publicKey()
    @JvmStatic fun rsaEncrypt(v:String) = Rsa.encrypt(v)
    @JvmStatic fun rsaEncrypt(v:ByteArray, publicKey:PublicKey)= Rsa.encrypt(v, publicKey)
    @JvmStatic fun rsaDecrypt(v:String) = Rsa.decrypt(v)

    @JvmStatic fun aesKey() = generateRandomKey(300).copyOfRange(0, 32)
    @JvmStatic fun aesEncryptByte(v:String, secretKey:ByteArray) = AES256.encryptByte(v, secretKey)
    @JvmStatic fun aesDecryptByte(v:ByteArray, secretKey:ByteArray) = AES256.decryptByte(v, secretKey)


    @JvmStatic fun aesEncrypt(v:ByteArray, secretKey:ByteArray) = AES256.encrypt(v, secretKey)
    @JvmStatic fun aesEncrypt(v:String, secretKey:ByteArray) = AES256.encrypt(v, secretKey)
    @JvmStatic fun aesDecrypt(v:ByteArray, secretKey:ByteArray) = AES256.decrypt(v, secretKey)
    @JvmStatic fun aesDecrypt(v:String, secretKey:ByteArray) = AES256.decrypt(v, secretKey)

    @JvmStatic fun generateRandomKey(lengthBits:Int) = with(KeyGenerator.getInstance("AES")){
        init(lengthBits, SecureRandom())
        generateKey().encoded
    }
    @JvmStatic fun permanentPw():String{
        var pw = ChShared.name("ch").s("dp")
        if(pw.isBlank()){
            pw = String(generateRandomKey(256))
            ChShared.name("ch").s("dp", rsaEncrypt(pw))
        }else pw = rsaDecrypt(pw)
        return pw
    }
}


@file:Suppress("DEPRECATION")

package chela.kotlin.crypto

import chela.kotlin.android.ChShared
import java.security.MessageDigest
import java.security.PublicKey
import java.security.SecureRandom
import javax.crypto.KeyGenerator
/*
App.akey = Ch.crypto.aesKey()
val a = "hika1234"
val e = Ch.crypto.aesEncrypt(a, App.akey!!)
Log.i("ch", "en:$e")
val d = Ch.crypto.aesDecrypt(e, App.akey!!)
Log.i("ch", "de:$d")
Log.i("ch", "rsa:${(Ch.crypto.rsaEncrypt(App.akey!!, App.pkey.second) + App.pkey.first).replace("\n", "\\n")}")
*/
object ChCrypto{
    private val sha256 = MessageDigest.getInstance("SHA-256")
    fun sha256(v:String):String
         = sha256.digest(v.toByteArray()).fold(""){ str, it-> str+"%02x".format(it)}

    fun rsaPublicKey() = Rsa.publicKey()
    fun rsaEncrypt(v:String) = Rsa.encrypt(v)
    fun rsaEncrypt(v:ByteArray, publicKey:PublicKey)= Rsa.encrypt(v, publicKey)
    fun rsaDecrypt(v:String) = Rsa.decrypt(v)

    fun aesKey() = generateRandomKey(300).copyOfRange(0, 32)
    fun aesEncryptByte(v:String, secretKey:ByteArray) = AES256.encryptByte(v, secretKey)
    fun aesDecryptByte(v:ByteArray, secretKey:ByteArray) = AES256.decryptByte(v, secretKey)


    fun aesEncrypt(v:ByteArray, secretKey:ByteArray) = AES256.encrypt(v, secretKey)
    fun aesEncrypt(v:String, secretKey:ByteArray) = AES256.encrypt(v, secretKey)
    fun aesDecrypt(v:ByteArray, secretKey:ByteArray) = AES256.decrypt(v, secretKey)
    fun aesDecrypt(v:String, secretKey:ByteArray) = AES256.decrypt(v, secretKey)

    fun generateRandomKey(lengthBits:Int) = with(KeyGenerator.getInstance("AES")){
        init(lengthBits, SecureRandom())
        generateKey().encoded
    }
    fun permanentPw():String{
        var pw = ChShared.name("ch").s("dp")
        if(pw.isBlank()){
            pw = String(generateRandomKey(256))
            ChShared.name("ch").s("dp", rsaEncrypt(pw))
        }else pw = rsaDecrypt(pw)
        return pw
    }
}


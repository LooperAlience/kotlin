package chela.kotlin.crypto

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object AES256{
    private fun cipher(opmode:Int, v:ByteArray): Cipher {
        val c = Cipher.getInstance("AES/CBC/PKCS7Padding")
        c.init(opmode,
            SecretKeySpec(v, "AES"),
            IvParameterSpec(v.copyOfRange(0, 16))
        )
        return c
    }
    fun encryptByte(str:String, secretKey:ByteArray) =
        cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8))
    fun decryptByte(v:ByteArray, secretKey:ByteArray) =
        String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(v), Charsets.UTF_8)



    fun encrypt(str:String, secretKey:ByteArray) =
        encrypt(str.toByteArray(Charsets.UTF_8), secretKey)
    fun decrypt(str:String, secretKey:ByteArray) =
        decrypt(str.toByteArray(Charsets.UTF_8), secretKey)
    fun encrypt(v:ByteArray, secretKey:ByteArray) =
        String(
            Base64.encode(
                cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(v),
                Base64.DEFAULT
            )
        )
    fun decrypt(v:ByteArray, secretKey:ByteArray) =
        String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(
            Base64.decode(
                v,
                Base64.DEFAULT
            )
        ))
}
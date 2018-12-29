package chela.kotlin.core

import java.security.MessageDigest

object ChCrypto{
    @JvmStatic private val sha256 = MessageDigest.getInstance("SHA-256")
    @JvmStatic fun sha256(v:String):String
         = sha256.digest(v.toByteArray()).fold(""){str,it-> str+"%02x".format(it)}
}

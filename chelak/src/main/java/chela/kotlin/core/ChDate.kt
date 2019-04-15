package chela.kotlin.core

import java.util.*

object ChDate{
    fun offset():Int{
        val longtime = Date().time
        val z1 = TimeZone.getDefault()
        val offset1 = z1.getOffset(longtime)
        val z2 = TimeZone.getTimeZone("UTC")
        val offset2 = z2.getOffset(longtime)
        return (offset2 - offset1) / 60000
    }
}
package chela.kotlin.core

object ChMath {
    fun rand(start:Int, end:Int):Int{
        val term = end - start + 1
        return start + (Math.random() * term.toDouble()).toInt()
    }
    fun rand(start:Double, end:Double):Double = start + Math.random() * (end - start + 1)
}

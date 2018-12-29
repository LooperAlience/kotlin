package chela.kotlin.core

object ChMath {
    @JvmStatic fun rand(start:Int, end:Int):Int{
        val term = end - start + 1
        return start + (Math.random() * term.toDouble()).toInt()
    }
    @JvmStatic fun rand(start:Double, end:Double):Double = start + Math.random() * (end - start + 1)
}

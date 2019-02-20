package chela.kotlin.looper

private const val PI = Math.PI
private const val HPI = Math.PI / 2

class ChItem{
    @JvmField var rate = 0.0
    @JvmField var current = 0.0
    @JvmField internal var start = 0.0
    @JvmField internal var end = 0.0
    @JvmField internal var term = 0.0
    @JvmField internal var isTurn = false
    @JvmField internal var loop = 1
    @JvmField internal var isPaused = false
    @JvmField internal var isInfinity = false

    @JvmField internal var block: ItemBlock = ItemDSL.empty
    @JvmField internal var ended: ItemBlock = ItemDSL.empty
    @JvmField internal var next: ChItem? = null
    @JvmField internal var isStop = false
    private var pauseStart = 0.0
    fun stop(){isStop = true}
    fun pause() {
        if(isPaused) return
        isPaused = true
        pauseStart = now()
    }
    fun resume() {
        if(!isPaused) return
        isPaused = false
        pauseStart = now() - pauseStart
        start += pauseStart
        end += pauseStart
        pauseStart = 0.0
    }
    fun linear(from: Double, to: Double): Double {
        return from + rate * (to - from)
    }
    fun backIn(from: Double, to: Double): Double {
        val b = to - from
        return b * rate * rate * (2.70158 * rate - 1.70158) + from
    }
    fun backOut(from: Double, to: Double): Double {
        val a = rate - 1
        val b = to - from
        return b * (a * a * (2.70158 * a + 1.70158) + 1) + from
    }
    fun backInOut(from: Double, to: Double): Double {
        var a = rate * 2
        val b = to - from
        if (1 > a)
            return .5 * b * a * a * (3.5949095 * a - 2.5949095) + from
        else {
            a -= 2.0
            return .5 * b * (a * a * (3.70158 * a + 2.70158) + 2) + from
        }
    }
    fun sineIn(from: Double, to: Double): Double {
        val b = to - from
        return -b * Math.cos(rate * HPI) + b + from
    }
    fun sineOut(from: Double, to: Double): Double {
        return (to - from) * Math.sin(rate * HPI) + from
    }
    fun sineInOut(from: Double, to: Double): Double {
        return .5 * -(to - from) * (Math.cos(PI * rate) - 1) + from
    }
    fun circleIn(from: Double, to: Double): Double {
        return -(to - from) * (Math.sqrt(1 - rate * rate) - 1) + from
    }
    fun circleOut(from: Double, to: Double): Double {
        val a = rate - 1
        return (to - from) * Math.sqrt(1 - a * a) + from
    }
    fun circleInOut(from: Double, to: Double): Double {
        var a = rate * 2
        val b = to - from
        if (1 > a)
            return .5 * -b * (Math.sqrt(1 - a * a) - 1) + from
        else {
            a -= 2.0
            return .5 * b * (Math.sqrt(1 - a * a) + 1) + from
        }
    }
    fun quadraticIn(from: Double, to: Double): Double {
        return (to - from) * rate * rate + from
    }
    fun quadraticOut(from: Double, to: Double): Double {
        return -(to - from) * rate * (rate - 2) + from
    }
    fun bounceOut(from: Double, to: Double): Double {
        var a = rate
        val b = to - from
        if (0.363636 > a)
            return 7.5625 * b * a * a + from
        else if (0.727272 > a) {
            a -= 0.545454
            return b * (7.5625 * a * a + 0.75) + from
        } else if (0.90909 > a) {
            a -= 0.818181
            return b * (7.5625 * a * a + 0.9375) + from
        } else {
            a -= 0.95454
            return b * (7.5625 * a * a + 0.984375) + from
        }
    }
}
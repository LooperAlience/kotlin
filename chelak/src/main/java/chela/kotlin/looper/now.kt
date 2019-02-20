package chela.kotlin.looper

import android.os.SystemClock

internal val now: Now = { SystemClock.uptimeMillis().toDouble()}
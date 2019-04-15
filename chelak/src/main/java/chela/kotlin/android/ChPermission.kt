package chela.kotlin.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

typealias resolve = (List<String>, Resolver)->Unit
typealias oknever = (List<String>)->Unit

object ChPermission{
    @SuppressLint("ObsoleteSdkInt")
    internal val dangers = with(mutableSetOf<String>()){
        addAll(listOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
        if(Build.VERSION.SDK_INT >= 26){
            add(Manifest.permission.READ_PHONE_NUMBERS)
            add(Manifest.permission.ANSWER_PHONE_CALLS)
            add(Manifest.permission.BODY_SENSORS)
        }
        if(Build.VERSION.SDK_INT >= 16){
            add(Manifest.permission.READ_CALL_LOG)
            add(Manifest.permission.WRITE_CALL_LOG)
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        this
    }
    internal val isGranted = PackageManager.PERMISSION_GRANTED
    private val instances = mutableMapOf<Int, Permission>()
    operator fun invoke(act:AppCompatActivity, code:Int):Permission{
        var v = instances[code]
        if(v == null || v.act !== act){
            v = Permission(act, code)
            instances[code] = v
        }
        return v
    }
    fun result(act:AppCompatActivity, code:Int, permission:Array<String>, granted:IntArray){
        val p = instances[code] ?: return
        val ok = mutableListOf<String>()
        val denied = mutableListOf<String>()
        val never = mutableListOf<String>()
        permission.forEachIndexed{ i, it->
            if(granted[i] == isGranted) ok.add(it)
            else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(act, it)) denied.add(it)
                else never.add(it)
            }
        }
        if(denied.isEmpty()) instances -= code
        if(ok.size == permission.size){
            p._ok?.let{it(ok)}
            return
        }
        if(denied.isNotEmpty()) p._denied?.let{it(denied, Resolver(p, false))}
        if(never.isNotEmpty()) p._never?.let {it(never)}
    }
}

class Resolver(private val per:Permission, private val isRequest:Boolean){
    fun request() = if(isRequest) per.ok() else per.request()
}
class Permission(internal val act: AppCompatActivity, private val code:Int){
    private val permissions = mutableSetOf<String>()
    private var _before:resolve? = null
    internal var _denied:resolve? = null
    internal var _ok:oknever? = null
    internal var _never:oknever? = null

    fun ok(f:oknever){_ok = f}
    fun neverAsk(f:oknever){_never = f}
    fun denied(f:resolve){_denied = f}
    fun before(f:resolve){_before = f}
    fun permissions(vararg arg: String) = arg.forEach {
        if(!ChPermission.dangers.contains(it)) throw Exception("invalid permission:$it")
        permissions.add(it)
    }
    fun request() {
        val notPermitted = mutableListOf<String>()
        if(
            if(permissions.isEmpty()) true
            else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) true
            else{
                var cnt = 0
                permissions.forEach{
                    if(ChPermission.isGranted == ContextCompat.checkSelfPermission(act, it)) cnt++
                    else notPermitted.add(it)
                }
                cnt == permissions.size
            }
        ){
            act.onRequestPermissionsResult(code, permissions.toTypedArray(), IntArray(permissions.size) {ChPermission.isGranted})
            return
        }else _before?.let {it(notPermitted, Resolver(this, true))} ?: ok()
    }
    internal fun ok() = ActivityCompat.requestPermissions(act, permissions.toTypedArray(), code)
}
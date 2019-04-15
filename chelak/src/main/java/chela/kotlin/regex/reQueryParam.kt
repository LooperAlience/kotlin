package chela.kotlin.regex

object reQueryParam: ChRegex("""@(?:([^@:]+)(?::([^@:]+))?)@"""){
    fun setItem(v:String, block:(k:String, v:String)->Unit):String = re.replace(v){
        val a = it.groupValues[2]
        block(it.groupValues[1], a)
        if(a.isBlank()) it.groupValues[0] else "?"
    }
}
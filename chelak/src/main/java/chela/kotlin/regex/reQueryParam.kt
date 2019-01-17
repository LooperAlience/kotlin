package chela.kotlin.regex

object reQueryParam: ChRegex("""@(?:([^@:]+)(?::([^@:]+))?)@"""){
    @JvmStatic fun setItem(v:String, block:(k:String, v:String)->Unit):String = re.replace(v){
        block(it.groupValues[1], it.groupValues[2])
        "?"
    }
}
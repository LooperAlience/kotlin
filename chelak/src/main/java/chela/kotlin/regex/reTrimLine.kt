package chela.kotlin.regex

object reTrimLine: ChRegex("""[\n\r]"""){
    fun trim(v:String):String = re.replace(v, " ").trim()
}
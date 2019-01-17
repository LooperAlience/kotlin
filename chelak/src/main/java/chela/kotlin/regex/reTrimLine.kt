package chela.kotlin.regex

object reTrimLine: ChRegex("""[\n\r]"""){
    @JvmStatic fun trim(v:String):String = re.replace(v, " ").trim()
}
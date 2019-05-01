package chela.kotlin.regex

object reCdata: ChRegex("""(?:[a-zA-Z0-9\/@]+=\[(?:(?:(?:@[a-zA-Z0-9\/]+=[a-zA-Z0-9\/]+|\*)&?),?)+\],?)+"""){
    private val key = """@?[0-z\/]+@[0-z\/]+""".toRegex()
    fun isValidKey(v:String) = re.matches(v)
    fun isValidJSONKey(v:String) = key.matches(v)
}
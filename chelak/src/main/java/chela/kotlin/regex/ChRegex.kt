package chela.kotlin.regex

abstract class ChRegex(r:String){
    private val re:Regex = r.toRegex()
    internal fun match(it: String):MatchResult? = re.find(it)
    internal fun cut(it:String):String = re.replaceFirst(it, "")
}


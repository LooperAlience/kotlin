package chela.kotlin.regex

object reParam: ChRegex("""\[(.+)\]"""){
    @JvmStatic val emptyArg = listOf<String>()
    @JvmStatic fun parse(v:String):Pair<String, List<String>>{
        val arg = re.find(v)?.let{it.groupValues[1].split(",").map{it.trim()}} ?: emptyArg
        val k = re.replace(v, "").trim().toLowerCase()
        return k to arg
    }
}
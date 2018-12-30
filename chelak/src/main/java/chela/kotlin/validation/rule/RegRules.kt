package chela.kotlin.validation.rule

import chela.kotlin.validation.ChRule

class RegRules:ChRule() {
    private val ip =
        """^(?:(?:[0-9]|(?:1\d{1,2})|(?:2[0-4]\d)|(?:25[0-5]))[.]){3}(?:[0-9]|[1-9][0-9]{1,2}|2[0-4]\d|25[0-5])$""".toRegex()
    private val url = """^https?://[a-zA-Z0-9.-]+(?:[.]+[A-Za-z]{2,4})+(?:[:]\d{2,4})?""".toRegex()
    private val email = """^[0-9a-zA-Z-_.]+@[0-9a-zA-Z-]+(?:[.]+[A-Za-z]{2,4})+$""".toRegex()
    private val korean = """^[ㄱ-힣]+$""".toRegex()
    private val japanese = """^[ぁ-んァ-ヶー一-龠！-ﾟ・～「」“”‘’｛｝〜−]+$""".toRegex()
    private val lower = """^[a-z]+$""".toRegex()
    private val upper = """^[A-Z]+$""".toRegex()
    private val num = """^(?:-?(?:0|[1-9]\d*)(?:\.\d+)(?:[eE][-+]?\d+)?)|(?:-?(?:0|[1-9]\d*))$""".toRegex()
    private val intnum = """^(?:-?(?:0|[1-9]\d*))$""".toRegex()
    private val doublenum = """^(?:-?(?:0|[1-9]\d*)(?:\.\d+)(?:[eE][-+]?\d+)?)$""".toRegex()
    private val lowernum = """^[a-z0-9]+$""".toRegex()
    private val uppernum = """^[A-Z0-9]+$""".toRegex()
    private val alphanum = """^[a-zA-Z0-9]+$""".toRegex()
    private val firstlower = """^[a-z]""".toRegex()
    private val firstUpper = """^[A-Z]""".toRegex()
    private val noblank = """\s""".toRegex()

    fun ip(v: Any, arg: List<String>) = if (v is String && ip.find(v) != null) v else this
    fun url(v: Any, arg: List<String>) = if (v is String && url.find(v) != null) v else this
    fun email(v: Any, arg: List<String>) = if (v is String && email.find(v) != null) v else this
    fun korean(v: Any, arg: List<String>) = if (v is String && korean.find(v) != null) v else this
    fun japanese(v: Any, arg: List<String>) = if (v is String && japanese.find(v) != null) v else this
    fun lower(v: Any, arg: List<String>) = if (v is String && lower.find(v) != null) v else this
    fun upper(v: Any, arg: List<String>) = if (v is String && upper.find(v) != null) v else this
    fun num(v: Any, arg: List<String>) = if (v is String && num.find(v) != null) v else this
    fun intnum(v: Any, arg: List<String>) = if (v is String && intnum.find(v) != null) v else this
    fun doublenum(v: Any, arg: List<String>) = if (v is String && doublenum.find(v) != null) v else this
    fun lowernum(v: Any, arg: List<String>) = if (v is String && lowernum.find(v) != null) v else this
    fun uppernum(v: Any, arg: List<String>) = if (v is String && uppernum.find(v) != null) v else this
    fun alphanum(v: Any, arg: List<String>) = if (v is String && alphanum.find(v) != null) v else this
    fun firstlower(v: Any, arg: List<String>) = if (v is String && firstlower.find(v) != null) v else this
    fun firstUpper(v: Any, arg: List<String>) = if (v is String && firstUpper.find(v) != null) v else this
    fun noblank(v: Any, arg: List<String>) = if (v is String && noblank.find(v) == null) v else this
}
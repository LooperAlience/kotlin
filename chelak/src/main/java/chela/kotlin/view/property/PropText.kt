package chela.kotlin.view.property

import android.os.Build
import android.text.Html
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import chela.kotlin.Ch.PxtoSp
import chela.kotlin.android.ChApp
import chela.kotlin.core._try
import chela.kotlin.view.ChStyle


object Type{
    val date = 0x14
    val dateTime = 0x4
    val none = 0x0
    val number = 0x2
    val numberDecimal = 0x2002
    val numberPassword = 0x12
    val numberSigned = 0x1002
    val phone = 0x3
    val text = 0x1
    val textAutoComplete = 0x10001
    val textAutoCorrect = 0x8001
    val textCapCharacters = 0x1001
    val textCapSentences = 0x4001
    val textCapWords = 0x2001
    val textEmailAddress = 0x21
    val textEmailSubject = 0x31
    val textFilter = 0xb1
    val textIMEMultiline = 0x40001
    val textLongMessage = 0x51
    val textMultiline = 0x20001
    val textNoSuggestions = 0x80001
    val textPassword = 0x81
    val textPersonName = 0x61
    val textPhonetic = 0xc1
    val textPostalAddress = 0x71
    val textShortMessage = 0x41
    val textUri = 0x11
    val textVisiblePassword = 0x91
    val textWebEditText = 0xa1
    val textWebEmailAddress = 0xd1
    val textWebPassword = 0xe1
    val time = 0x24
}
object Alignment{
    val center = 4
    val gravity = 1
    val inherit = 0
    val textend = 3
    val textstart = 2
    val viewend = 6
    val viewstart = 5
}
object PropText:Property(){
    val type = Type
    val alignment = Alignment
    fun text(view:View, v:Any){
        if(view !is TextView) return
        view.text = "$v"
    }
    fun fromHtml(view:View, v:Any){
        if(view !is TextView) return
        var spanned:Spanned
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(v as String,  Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            spanned = Html.fromHtml(v as String)
        }
        view.text = spanned
    }
    fun textSize(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.textSize = v.PxtoSp.toFloat()
    }
    fun textScaleX(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.textScaleX = v.toFloat()
    }
    fun lineSpacing(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.setLineSpacing(v.toFloat(), 1F)
    }
    fun textColor(view:View, v:Any){
        if(v !is String || view !is TextView) return
        view.setTextColor(ChProperty.color(v))
    }
    fun textAlignment(view:View, v:Any){
        if(view !is TextView) return
        if(v is Int) view.setTextAlignment(v)
        else if(v is String) {
            view.setTextAlignment(
                when (v.toLowerCase()) {
                    "center" -> 4
                    "gravity" -> 1
                    "inherit" -> 0
                    "textend" -> 3
                    "textstart" -> 2
                    "viewend" -> 6
                    "viewstart" -> 5
                    else -> 0
                }
            )
        }
    }
    fun hint(view:View, v:Any){
        if(v !is String || view !is TextView) return
        view.hint = v
    }
    fun hintColor(view:View, v:Any){
        if(v !is String || view !is TextView) return
        view.setHintTextColor(ChProperty.color(v))
    }
    fun maxLines(view:View, v:Any){
        if(view !is TextView || v !is Int) return
        view.maxLines = v
    }
    fun ellipsize(view:View, v:Any){
        if(view !is TextView) return
        view.ellipsize = when("$v") {
            "start" -> TextUtils.TruncateAt.START
            "middle" -> TextUtils.TruncateAt.MIDDLE
            "end" -> TextUtils.TruncateAt.END
            "marquee" -> TextUtils.TruncateAt.MARQUEE
            else->throw Throwable("invalid ellipsize $v")
        }
    }
    fun maxLength(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        val filters = view.filters?.toMutableList() ?: mutableListOf()
        filters.find {it is InputFilter.LengthFilter}?.let {filters.remove(it)}
        filters.add(InputFilter.LengthFilter(v.toInt()))
        view.filters = filters.toTypedArray()
    }
    fun allCaps(view:View, v:Any){
        if(v !is Boolean || view !is TextView) return
        view.isAllCaps = v
    }
    fun fontFamily(view:View, v:Any) = font(view, v)
    fun font(view:View, v:Any){
        if(view !is TextView) return
        when(v){
            is Number->view.typeface = ResourcesCompat.getFont(ChApp.app, v.toInt())
            is String->{
                if(!ChStyle.getFont(v){view.typeface = it}) _try{
                    ResourcesCompat.getFont(ChApp.app, ChApp.resFont(v))
                }
            }
        }
    }
    fun inputType(view:View, v:Any){
        if(view !is TextView) return
        if(v is Int) view.inputType = v
        else if(v is String){
            view.inputType = when(v.toLowerCase()){
                "date"->0x14
                "datetime"->0x4
                "none"->0x0
                "number"->0x2
                "numberdecimal"->0x2002
                "numberpassword"->0x12
                "numbersigned"->0x1002
                "phone"->0x3
                "text"->0x1
                "textautocomplete"->0x10001
                "textautocorrect"->0x8001
                "textcapcharacters"->0x1001
                "textcapsentences"->0x4001
                "textcapwords"->0x2001
                "textemailaddress"->0x21
                "textemailsubject"->0x31
                "textfilter"->0xb1
                "textimemultiline"->0x40001
                "textlongmessage"->0x51
                "textmultiline"->0x20001
                "textnosuggestions"->0x80001
                "textpassword"->0x81
                "textpersonname"->0x61
                "textphonetic"->0xc1
                "textpostaladdress"->0x71
                "textshortmessage"->0x41
                "texturi"->0x11
                "textvisiblepassword"->0x91
                "textwebedittext"->0xa1
                "textwebemailaddress"->0xd1
                "textwebpassword"->0xe1
                "time"->0x24
                else->0x0
            }
        }
    }
}
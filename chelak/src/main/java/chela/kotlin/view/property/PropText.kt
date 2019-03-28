package chela.kotlin.view.property

import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import chela.kotlin.Ch
import chela.kotlin.PxtoSp
import chela.kotlin.SptoPx
import chela.kotlin.android.ChApp
import chela.kotlin.core._try
import chela.kotlin.regex.reV
import chela.kotlin.view.ChStyle

object Type{
    @JvmStatic val date = 0x14
    @JvmStatic val dateTime = 0x4
    @JvmStatic val none = 0x0
    @JvmStatic val number = 0x2
    @JvmStatic val numberDecimal = 0x2002
    @JvmStatic val numberPassword = 0x12
    @JvmStatic val numberSigned = 0x1002
    @JvmStatic val phone = 0x3
    @JvmStatic val text = 0x1
    @JvmStatic val textAutoComplete = 0x10001
    @JvmStatic val textAutoCorrect = 0x8001
    @JvmStatic val textCapCharacters = 0x1001
    @JvmStatic val textCapSentences = 0x4001
    @JvmStatic val textCapWords = 0x2001
    @JvmStatic val textEmailAddress = 0x21
    @JvmStatic val textEmailSubject = 0x31
    @JvmStatic val textFilter = 0xb1
    @JvmStatic val textIMEMultiline = 0x40001
    @JvmStatic val textLongMessage = 0x51
    @JvmStatic val textMultiline = 0x20001
    @JvmStatic val textNoSuggestions = 0x80001
    @JvmStatic val textPassword = 0x81
    @JvmStatic val textPersonName = 0x61
    @JvmStatic val textPhonetic = 0xc1
    @JvmStatic val textPostalAddress = 0x71
    @JvmStatic val textShortMessage = 0x41
    @JvmStatic val textUri = 0x11
    @JvmStatic val textVisiblePassword = 0x91
    @JvmStatic val textWebEditText = 0xa1
    @JvmStatic val textWebEmailAddress = 0xd1
    @JvmStatic val textWebPassword = 0xe1
    @JvmStatic val time = 0x24
}
object Alignment{
    @JvmStatic val center = 4
    @JvmStatic val gravity = 1
    @JvmStatic val inherit = 0
    @JvmStatic val textend = 3
    @JvmStatic val textstart = 2
    @JvmStatic val viewend = 6
    @JvmStatic val viewstart = 5
}
object PropText:Property(){
    @JvmStatic val type = Type
    @JvmStatic val alignment = Alignment

    @JvmStatic fun text(view:View, v:Any){
        if(view !is TextView) return
        view.text = v as String
    }
    @JvmStatic fun textSize(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.textSize = v.PxtoSp.toFloat()
    }
    @JvmStatic fun textScaleX(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.textScaleX = v.toFloat()
    }
    @JvmStatic fun lineSpacing(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.setLineSpacing(v.toFloat(), 1F)
    }
    @JvmStatic fun textColor(view:View, v:Any){
        if(v !is String || view !is TextView) return
        view.setTextColor(ChProperty.color(v))
    }
    @JvmStatic fun textAlignment(view:View, v:Any){
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
    @JvmStatic fun hint(view:View, v:Any){
        if(v !is String || view !is TextView) return
        view.hint = v
    }
    @JvmStatic fun hintColor(view:View, v:Any){
        if(v !is String || view !is TextView) return
        view.setHintTextColor(ChProperty.color(v))
    }
    @JvmStatic fun maxLines(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.maxLines = v.toInt()
    }
    @JvmStatic fun maxLength(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        val filters = view.filters?.toMutableList() ?: mutableListOf()
        filters.find {it is InputFilter.LengthFilter}?.let {filters.remove(it)}
        filters.add(InputFilter.LengthFilter(v.toInt()))
        view.filters = filters.toTypedArray()
    }
    @JvmStatic fun allCaps(view:View, v:Any){
        if(v !is Boolean || view !is TextView) return
        view.isAllCaps = v
    }
    @JvmStatic fun fontFamily(view:View, v:Any) = font(view, v)
    @JvmStatic fun font(view:View, v:Any){
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
    @JvmStatic fun inputType(view:View, v:Any){
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
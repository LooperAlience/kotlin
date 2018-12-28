package chela.kotlin.android

object ChClipBoard {
    @JvmStatic fun copy(v:String){
        ChApp.clip.primaryClip = android.content.ClipData.newPlainText("text label", v)
    }
}

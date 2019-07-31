package chela.kotlin.view.property

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import chela.kotlin.Ch
import java.io.File

object PropWebview:Property(){
    fun client(view: View, v:Any){
        Log.i("chela", "client$v")
        if(view !is WebView) return
        if(v == Ch.WebView.default){
            view.webViewClient = object: WebViewClient(){
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    handler?.proceed()
                    super.onReceivedSslError(view, handler, error)
                }
                @TargetApi(Build.VERSION_CODES.N)
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString()
                    return checkUrl(url)
                }
                @SuppressWarnings("deprecation")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return checkUrl(url)
                }
                private fun checkUrl(url: String?): Boolean {
                    if(url == null) return false
                    if(url.startsWith("tel:")) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                        view.context.startActivity(intent)
                        return true
                    }
                    if(url.startsWith("mailto:")) {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                        view.context.startActivity(intent)
                        return true
                    }
                    return false
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.i("chela", "onPageFinished:$url")
                    super.onPageFinished(view, url)
                }
                @TargetApi(Build.VERSION_CODES.M)
                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    if (error != null) receivedError(error.errorCode)
                    super.onReceivedError(view, request, error)
                }
                private fun receivedError(errorCode: Int) {
                    Toast.makeText(view.context, "weberror: ${webviewError(errorCode)}", Toast.LENGTH_LONG).show()
                }
                private fun webviewError(errorCode: Int): String {
                    var result = ""
                    when (errorCode) {
                        ERROR_AUTHENTICATION -> result = "서버에서 사용자 인증 실패"
                        ERROR_BAD_URL -> result = "잘못된 URL"
                        ERROR_CONNECT -> result = "서버로 연결 실패"
                        ERROR_FAILED_SSL_HANDSHAKE -> result = "SSL handshake 수행 실패"
                        ERROR_FILE -> result = "일반 파일 오류"
                        ERROR_FILE_NOT_FOUND -> result = "파일을 찾을 수 없습니다"
                        ERROR_HOST_LOOKUP -> result = "서버 또는 프록시 호스트 이름 조회 실패"
                        ERROR_IO -> result = "서버에서 읽거나 서버로 쓰기 실패"
                        ERROR_PROXY_AUTHENTICATION -> result = "프록시에서 사용자 인증 실패"
                        ERROR_REDIRECT_LOOP -> result = "너무 많은 리디렉션"
                        ERROR_TIMEOUT -> result = "연결 시간 초과"
                        ERROR_TOO_MANY_REQUESTS -> result = "페이지 로드중 너무 많은 요청 발생"
                        ERROR_UNKNOWN -> result = "일반 오류"
                        ERROR_UNSUPPORTED_AUTH_SCHEME -> result = "지원되지 않는 인증 체계"
                        ERROR_UNSUPPORTED_SCHEME -> result = "ERROR_UNSUPPORTED_SCHEME"
                    }
                    return result
                }
            }
            return
        }
        if(v !is WebViewClient) return
        view.webViewClient = v
    }
    fun chromeClient(view:View, v:Any){
        Log.i("chela", "chromeClient$v")
        if(view !is WebView) return
        if(v == Ch.WebView.default){
            view.webChromeClient = object: WebChromeClient(){
                override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    Log.i("chela", "$message")
                    return super.onJsAlert(view, url, message, result)
                }
                override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
                    Log.d("chela", "$message -- From line $lineNumber of $sourceID")
                }
            }
            return
        }
        if(v !is WebChromeClient) return
        view.webChromeClient = v
    }
    fun setting(view:WebView, v:Any){
        Log.i("chela", "setting$v")
        if(view !is WebView) return
        if(v !is Ch.WebView) return
        when(v){
            Ch.WebView.default -> {
                optimization(view, true)
                debuggingEnabled(view, true)
                javascriptEnabled(view, true)
                localstorageEnabled(view, true)
                openWindowEnabled(view, true)
                cacheEnabled(view, true)
                cacheMode(view, WebSettings.LOAD_DEFAULT)
                fitContent(view, true)
                supportZoom(view, false)
                encoding(view, "UTF-8")
                allowFileAccess(view, true)
                clearCache(view, true)
            }
        }
    }

    fun optimization(view:WebView, v:Any) { //성능 향상(최적화)
        if(view !is WebView) return
        if(v !is Boolean) return
        if(v) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) view.settings.setRenderPriority(WebSettings.RenderPriority.HIGH) //강제적으로 랜더러 우선순위를 높임(deprecated in API level 18)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) view.settings.setEnableSmoothTransition(true) //부드러운 전환 허용 여부(deprecated in API level 17)
            //하드웨어 가속, 4.4이상이면 하드웨어가속이 유리함
            view.setLayerType(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) View.LAYER_TYPE_HARDWARE else View.LAYER_TYPE_SOFTWARE,
                null
            )
        }
    }
    fun debuggingEnabled(view:View, v:Any){
        if(view !is WebView) return
        if(v !is Boolean) return
        //디버깅 허용(킷켓 이상부터 데스크탑 크롬으로 원격 디버깅이 가능)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) WebView.setWebContentsDebuggingEnabled(v)
    }
    fun javascriptEnabled(view:View, v:Any){
        if(view !is WebView) return
        if(v !is Boolean) return
        view.settings.javaScriptEnabled = v  //자바스크립트 사용 여부
    }
    fun localstorageEnabled(view:View, v:Any){
        if(view !is WebView) return
        if(v !is Boolean) return
        view.settings.databaseEnabled = v //데이터베이스 저장 API 사용 여부
        view.settings.domStorageEnabled = v //DOM Storage API 사용 여부
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            view.settings.databasePath = "/data/data/" + view.context.packageName + "/databases/" //database 경로 설정
        }
    }
    fun openWindowEnabled(view:View, v:Any){
        if(view !is WebView) return
        if(v !is Boolean) return
        view.settings.javaScriptCanOpenWindowsAutomatically = v //javascript가 팝업창을 사용할 수 있도록 설정. 해당 속성을 추가해야 window.open()을 사용할 수 있음.
        view.settings.setSupportMultipleWindows(v) //여러개의 윈도우를 사용할 수 있도록 설정(새창 띄우기 허용 여뷰)
    }
    fun cacheEnabled(view:WebView, v:Any){
        if(view !is WebView) return
        if(v !is Boolean) return
        view.settings.setAppCacheEnabled(v) //앱 캐시 사용여부
        //view.settings.setAppCacheMaxSize(v) //앱 캐시 크기설정(deprecated)
        if(v){
            val appCacheDir = File(view.context.cacheDir, "appCache")
            if (!appCacheDir.exists()) appCacheDir.mkdirs()
            view.settings.setAppCachePath(appCacheDir.absolutePath) //캐시 파일 경로 설정
        }
    }
    fun cacheMode(view:WebView, v:Any){
        if(view !is WebView) return
        if(v !is Int) return
        /*  캐시 방식 설정
        WebSettings.LOAD_DEFAULT //기본적인 모드로 캐시를 사용하고 만료된 경우 네트워크를 사용해 로드
        WebSettings.LOAD_NORMAL //기본적인 모드로 캐시를 사용(deprecated in API level 17)
        WebSettings.LOAD_CACHE_ELSE_NETWORK //캐쉬를 사용할수 있는경우 기간이 만료되도 사용. 사용할 수 없으면 네트워크를 사용
        WebSettings.LOAD_NO_CACHE //캐시모드를 사용하지 않고 네트워크를 통해서만 호출
        WebSettings.LOAD_CACHE_ONLY //네트워크를 사용하지 않고 캐시를 불러옴 */
        when(v){
            WebSettings.LOAD_DEFAULT,WebSettings.LOAD_NORMAL,WebSettings.LOAD_CACHE_ELSE_NETWORK,WebSettings.LOAD_NO_CACHE,WebSettings.LOAD_CACHE_ONLY -> view.settings.cacheMode =  v
            else -> {
                Log.i("chela", "cacheMode is WebSettings.LOAD_DEFAULT,WebSettings.LOAD_NORMAL,WebSettings.LOAD_CACHE_ELSE_NETWORK,WebSettings.LOAD_NO_CACHE,WebSettings.LOAD_CACHE_ONLY")
                return
            }
        }
    }
    fun fitContent(view:WebView, v:Any) {
        if(view !is WebView) return
        if(v !is Boolean) return
        /*  랜더링 방식 변경
         *  WebSettings.LayoutAlgorithm.NORMAL : 렌더링이 변경되지 않음을 의미. 이는 다양한 플랫폼 및 Android 버전에서 최대한의 호환성을 위해 권장되는 선택입니다.
         *  WebSettings.LayoutAlgorithm.SINGLE_COLUMN : 모든 내용을 화면에 보이도록 맞춤(Deprecated in API level 12)
         *  WebSettings.LayoutAlgorithm.NARROW_COLUMNS : 모든 열을 화면보다 넓게 만듬.(Deprecated in API level 12)
         *  WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING : 텍스트를 읽을 수 있도록 단락의 글꼴 크기를 향상. 줌 지원이 활성화되어 있어야 함.(KITKAT부터 지원)  */
        view.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        view.settings.loadWithOverviewMode = v //화면에 문서 전체가 보이게 설정, 웹뷰에 맞게 출력(컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정)
        view.settings.useWideViewPort = v //뷰 wide viewport 허용 여부.  html 컨텐츠가 웹뷰에 맞게 나타나도록 함
    }
    fun supportZoom(view:View, v:Any){
        if(view !is WebView) return
        if(v !is Boolean) return
        view.settings.setSupportZoom(v) //확대 축소 기능을 사용할 수 있도록 설정
        view.settings.builtInZoomControls = v //내장 줌 컨트롤 사용
        view.settings.displayZoomControls = false //내장 중 컨트롤 표시 여부
    }
    fun encoding(view:View, v:Any){
        if(view !is WebView) return
        if(v !is String) return
        view.settings.defaultTextEncodingName = v //기본 인코딩 설정("UTF-8")
    }
    fun allowFileAccess(view:View, v:Any){
        if(view !is WebView) return
        if(v !is Boolean) return
        view.settings.allowFileAccess = v //웹 뷰 내에서 파일 액세스 활성화
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) view.settings.allowFileAccessFromFileURLs = v //파일에 접근하는 것을 허용
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) view.settings.allowUniversalAccessFromFileURLs = v
    }
    fun loadsImagesAutomatically(view:View, v:Any) {
        if (view !is WebView) return
        if (v !is Boolean) return
        view.settings.loadsImagesAutomatically = v //앱에 등록되어 있는 이미지 리소스를 자동으로 로드하도록 설정하는 속성
    }
    fun usePlugin(view:View, v:Any) {
        if (view !is WebView) return
        if (v !is Boolean) return
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) view.settings.pluginState = WebSettings.PluginState.ON //플러그인 사용(Deprecated in API level 18)
    }
    fun mediaPlaybackRequiresUserGesture(view:View, v:Any) {
        if (view !is WebView) return
        if (v !is Boolean) return
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) view.settings.mediaPlaybackRequiresUserGesture = v //미디어를 재생할 때 사용자의 조작이 필요한지 여부를 설정
    }
    fun userAgent(view:View, v:Any){
        if(view !is WebView) return
        if(v !is String) return
        view.settings.userAgentString = v
    }


    /* 아래는 실행 함수들 */
    fun loadUrl(view:View, v:Any){
        Log.i("chela", "loadUrl$v")
        if(view !is WebView) return
        if(v !is String) return
        view.loadUrl(v)
    }
    fun loadJavascript(view:View, v:Any){
        if(view !is WebView) return
        if(v !is String) return
        view.loadUrl("javascript:$v")
    }
    fun back(view: View, v:Any){
        if(view !is WebView) return
        if(view.canGoBack()) view.goBack()
    }
    fun reload(view:View, v:Any){
        if(view !is WebView) return
        view.reload()
    }
    fun clearCache(view:View, v:Any){
        if(view !is WebView) return
        if(v !is Boolean) return
        view.clearCache(v)
        if(v){
            view.clearHistory()
            view.clearFormData()
        }
    }


}
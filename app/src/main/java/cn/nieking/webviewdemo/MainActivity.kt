package cn.nieking.webviewdemo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var wvContent: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initWebView()
        initWebViewEventHandler()
        wvContent?.loadUrl("https://www.jd.com")
    }

    private fun initWebView() {
        // 动态添加 WebView
        val params = LinearLayout
                .LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        wvContent = WebView(applicationContext)
        wvContent?.layoutParams = params
        llMain.addView(wvContent)

        // 设置自适应屏幕
        wvContent?.settings?.useWideViewPort = true      // 支持视口
        wvContent?.settings?.loadWithOverviewMode = true // 缩放至屏幕大小

        wvContent?.settings?.setSupportZoom(true)        // 支持缩放
        wvContent?.settings?.builtInZoomControls = true  // 支持内置缩放控件
        wvContent?.settings?.displayZoomControls = false // 隐藏原生缩放控件

        wvContent?.settings?.cacheMode = WebSettings.LOAD_NO_CACHE          // 不使用缓存（根据是否连接网络分别配置
                                                                            // LOAD_DEFAULT 和 LOAD_CACHE_ELSE_NETWORK）
        wvContent?.settings?.javaScriptCanOpenWindowsAutomatically = false  // 支持通过JS打开新窗口
        wvContent?.settings?.allowFileAccess = true                         // 开启文件访问
        wvContent?.settings?.loadsImagesAutomatically = true                // 支持自动加载图片
        wvContent?.settings?.defaultTextEncodingName = "UTF-8"              // 设置编码格式

        // 开启 https 和 http 混用
        wvContent?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        wvContent?.settings?.domStorageEnabled = true    // 开启 DOM storage API
        wvContent?.settings?.databaseEnabled = true      // 开启 Database storage API
        wvContent?.settings?.setAppCacheEnabled(true)    // 开启 Application Cache
    }

    private fun initWebViewEventHandler() {
        // 处理各种通知和请求
        wvContent?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // 开始加载页面
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // 页面加载结束
                super.onPageFinished(view, url)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                // 设置加载资源
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                // 加载页面时出现错误
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                super.onReceivedSslError(view, handler, error)
                // 处理 https 请求
                // 等待证书响应
                handler?.proceed()
            }
        }

        // 辅助 WebView 处理 JS 对话框，网站信息
        wvContent?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // 获取网页加载进度并显示
                super.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                // 获取网页标题
                super.onReceivedTitle(view, title)
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                // 处理网页弹窗警告
                return super.onJsAlert(view, url, message, result)
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                // 处理网页确认对话框
                return super.onJsConfirm(view, url, message, result)
            }

            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                // 处理网页输入对话框
                return super.onJsPrompt(view, url, message, defaultValue, result)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()
        // 开始交互
        wvContent?.onResume()
        // 开始 layout，parsing，JavaScriptTimer
        wvContent?.resumeTimers()
        // 打开JS支持
        wvContent?.settings?.javaScriptEnabled = true
    }

    override fun onPause() {
        // 关闭JS支持
        wvContent?.settings?.javaScriptEnabled = false
        // 暂停 layout，parsing，JavaScriptTimer
        wvContent?.pauseTimers()
        // 暂停交互
        wvContent?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        // Activity 销毁时销毁 WebView
        if (wvContent != null) {
            // 清除网页缓存
            wvContent?.clearCache(true)
            // 清除历史记录
            wvContent?.clearHistory()
            // 清除自动填充表单数据
            wvContent?.clearFormData()
            // 加载空页面
            wvContent?.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            // 根 ViewGroup 移除 ViewGroup
            (wvContent?.parent as ViewGroup).removeView(wvContent)
            // 销毁 WebView
            wvContent?.destroy()
            // 置空
            wvContent = null
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (wvContent?.canGoBack()!!) { // 是否可以后退
            wvContent?.goBack() // 后退网页
        } else {
            super.onBackPressed()
        }
    }
}

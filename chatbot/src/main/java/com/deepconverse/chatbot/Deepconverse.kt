package com.deepconverse.chatbot

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

interface DeepconverseInteractions {
    fun minimize()
    fun close()
}

class AndroidJSInterface(
    val deepconverseInteractions: DeepconverseInteractions
) {
    @JavascriptInterface
    fun actionTapped(str: String) {
        var obj = JSONObject(str)
        val action = obj.optString("action")
        when (action) {
            "minimize" -> {
                deepconverseInteractions.minimize()
            }
            else -> return
        }
    }
}

class Deepconverse : AppCompatActivity(), DeepconverseInteractions {

    private var uri: String? = null
    private lateinit var deepconverseWebView: WebView

    companion object {
        const val URI_PARAM = "URI"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deepconverse)
        deepconverseWebView = findViewById(R.id.deepconverse_webview)
    }

    override fun onResume() {
        super.onResume()
        uri = intent.getStringExtra(URI_PARAM)
        uri?.let {
            initilizeWebview(it)
        } ?:run {
            finish()
        }
    }

    private fun initilizeWebview(uri : String) {
        deepconverseWebView.settings.javaScriptEnabled = true
        deepconverseWebView.settings.domStorageEnabled = true
        deepconverseWebView.settings.builtInZoomControls = false
        deepconverseWebView.settings.setSupportZoom(false)
        deepconverseWebView.settings.allowFileAccess = true
        val androidJSInterface = AndroidJSInterface(this)
        deepconverseWebView.addJavascriptInterface(androidJSInterface, "Android")
        deepconverseWebView.webViewClient = DeepconverseWebViewClient
        deepconverseWebView.loadUrl(uri)
    }

    object DeepconverseWebViewClient: WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            loadJs(view)
            super.onPageFinished(view, url);
        }

        private fun loadJs(view: WebView?) {
            val js = actionButtonJS()
            view?.evaluateJavascript(js) { paRes ->
            }
        }

        private fun actionButtonJS() = """
        document.addEventListener('dc.bot', function(e) {
          let payload = { action: e.detail.action };
          var payloadStr = JSON.stringify(payload);
          Android.actionTapped(payloadStr)
        });
        """
    }

    override fun minimize() {
        finish()
    }

    override fun close() {
    }
}





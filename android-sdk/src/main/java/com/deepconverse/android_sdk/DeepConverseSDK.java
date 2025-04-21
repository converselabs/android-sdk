package com.deepconverse.android_sdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DeepConverseSDK extends LinearLayoutCompat {

    private WebView webView;
    private WebViewCallback webViewCallback;
    private WebViewLoadingCallback webViewLoadingCallback;
    private BotEventListener botEventListener;

    private String domain;
    private String botName;
    private Map<String, String> metadata;

    public interface WebViewCallback {
        void onViewClosed();
    }

    public interface WebViewLoadingCallback {
        void onUrlLoading();
        void onUrlLoaded();
    }

    public interface BotEventListener {
        void onBotEvent(JSONObject eventData);
    }

    public DeepConverseSDK(@NonNull Context context, String domain, String botName,
                           Map<String, String> metadata) {
        super(context);
        this.domain = domain;
        this.botName = botName;
        this.metadata = metadata;
        init();
    }

    public DeepConverseSDK(@NonNull Context context, @Nullable AttributeSet attrs,
                           String domain, String botName, Map<String, String> metadata) {
        super(context, attrs);
        this.domain = domain;
        this.botName = botName;
        this.metadata = metadata;
        init();
    }

    private void init() {
        webView = new WebView(getContext());
        webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.addJavascriptInterface(new WebAppInterface(), "AndroidBridge");

        addView(webView);
    }

    public void setWebViewCallback(WebViewCallback callback) {
        this.webViewCallback = callback;
    }

    public void setWebViewLoadingCallback(WebViewLoadingCallback loadingCallback) {
        this.webViewLoadingCallback = loadingCallback;
    }

    public void setBotEventListener(BotEventListener listener) {
        this.botEventListener = listener;
    }

    public void load() {
        String DEEPCONVERSE_HOST = "cdn.deepconverse.com";
        String url = "https://" + DEEPCONVERSE_HOST + "/v1/assets/widget/embedded-chatbot?"
                + "hostname=" + this.domain + '-' + this.botName + ".deepconverse.com";

        webView.loadUrl(url);
        Log.i("DeepConverseSDK", "URL:" + url);

        Gson gson = new Gson();
        String metadataJSON = gson.toJson(metadata);
        Log.i("DeepConverseSDK", "Metadata:" + metadataJSON);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (webViewLoadingCallback != null) {
                    webViewLoadingCallback.onUrlLoading();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (webViewLoadingCallback != null) {
                    webViewLoadingCallback.onUrlLoaded();
                }

                webView.evaluateJavascript("setTimeout(function () {var evt = new CustomEvent('botWidgetInit', { detail: " + metadataJSON + " });document.dispatchEvent(evt);}, 100)", null);

                webView.evaluateJavascript("document.addEventListener('dc.bot', function(event) { " +
                        "console.log('Event', event.detail); " +
                        "AndroidBridge.onCustomEvent(JSON.stringify(event.detail))" +
                        "});", null);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("DeepConverseSDK_WebView", consoleMessage.message());
                return true;
            }
        });
    }

    public void destroyView() {
        removeView(webView);
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }

    private class WebAppInterface {

        @JavascriptInterface
        public void onCustomEvent(String eventObject) {
            try {
                JSONObject eventData = new JSONObject(eventObject);
                Log.d("DeepConverseSDK", "Received event: " + eventObject);
                String action = eventData.optString("action", "");

                Log.d("DeepConverseSDK", "Action: " + action);

                if ("minimize".equals(action) || "close".equals(action)) {
                    if (webViewCallback != null) {
                        webViewCallback.onViewClosed();
                    }
                }

                if (botEventListener != null) {
                    botEventListener.onBotEvent(eventData);
                }

            } catch (JSONException e) {
                Log.e("DeepConverseSDK", "Error parsing event object: " + e.getMessage());
            }
        }
    }
}

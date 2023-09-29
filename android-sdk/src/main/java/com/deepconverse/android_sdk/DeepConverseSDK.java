package com.deepconverse.android_sdk;

import android.content.Context;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DeepConverseSDK extends LinearLayoutCompat {

    private WebView webView;
    private WebViewCallback webViewCallback; // Callback interface
    private WebViewLoadingCallback webViewLoadingCallback; // Callback interface

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
        webView.setWebViewClient(new WebViewClient());

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

    public void load() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("cdn.converseapps.com")
                .appendPath("v1/assets/widget/embedded-chatbot")
                .appendQueryParameter("hostname", this.domain + '-' + this.botName + ".deepconverse.com");
        for (Map.Entry<String, String> set :metadata.entrySet()) {
            builder.appendQueryParameter(set.getKey(), set.getValue());
        }

        String url = builder.build().toString();
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                if (webViewLoadingCallback != null) {
                    webViewLoadingCallback.onUrlLoading(); // Invoke the callback method
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (webViewLoadingCallback != null) {
                    webViewLoadingCallback.onUrlLoaded(); // Invoke the callback method
                }

                // Execute JavaScript after the page finishes loading
                webView.evaluateJavascript("document.addEventListener('dc.bot', function(event) { " +
                        "console.log('Event', event.detail); " +
                        "AndroidBridge.onCustomEvent(JSON.stringify(event.detail))" +
                        "});", null);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewConsole", consoleMessage.message());
                return true;
            }
        });
    }

    public void destroyView() {
        removeView(webView);
        webView.destroy();
        webView = null;
    }

    private class WebAppInterface {

        @JavascriptInterface
        public void onCustomEvent(String eventObject) {
            // Handle the custom event string received from the web page
            try {
                JSONObject eventData = new JSONObject(eventObject);
                Log.d("WebUrlViewEvent", "Received event: " + eventObject.toString());
                String action = eventData.getString("action");

                Log.d("ACTION", "Action: " + action);

                if (action.equals("minimize") || action.equals("close")) {
                    if (webViewCallback != null) {
                        webViewCallback.onViewClosed(); // Invoke the callback method
                    }
                }
                // Handle the JSON object received from the web page
            } catch (JSONException e) {
                Log.e("WebUrlViewEvent", "Error parsing event object: " + e.getMessage());
            }
        }
    }
}

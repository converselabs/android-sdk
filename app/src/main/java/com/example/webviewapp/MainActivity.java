package com.example.webviewapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_web_view_url_lib.WebUrlView;

public class MainActivity extends AppCompatActivity implements WebUrlView.WebViewCallback {

    private Button openWebViewButton;
    private LinearLayout webUrlContainer;
    private WebUrlView webUrlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openWebViewButton = findViewById(R.id.openWebViewButton);
        webUrlContainer = findViewById(R.id.webUrlContainer);

        openWebViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webUrlView != null) {
                    // Remove the existing WebUrlView if present
                    webUrlContainer.removeView(webUrlView);
                    webUrlView.destroyView();
                }

                // Create a new instance of WebUrlView
                webUrlView = new WebUrlView(MainActivity.this);
                webUrlView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                webUrlView.setWebViewCallback(MainActivity.this);
                String url = "https://cdn.converseapps.com/v1/assets/widget/embedded-chatbot?draft=true&hostname=dcstg5-preshin-19.deepconverse.com";
                webUrlView.loadUrl(url);
                webUrlContainer.addView(webUrlView);

                // Show the webUrlContainer and trigger a layout pass
                webUrlContainer.setVisibility(View.VISIBLE);
                webUrlContainer.requestLayout();
            }
        });
    }

    @Override
    public void onViewClosed() {
        // Remove the WebUrlView from the container
        if (webUrlView != null) {
            webUrlContainer.removeView(webUrlView);
            webUrlView.destroyView();
            webUrlView = null;
            webUrlContainer.setVisibility(View.GONE);
        }
    }
}

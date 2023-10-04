package com.deepconverse.webviewapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.deepconverse.android_sdk.DeepConverseSDK;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements DeepConverseSDK.WebViewCallback {

    private Button openWebViewButton;
    private LinearLayout webUrlContainer;
    private DeepConverseSDK deepConverseSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openWebViewButton = findViewById(R.id.openWebViewButton);
        webUrlContainer = findViewById(R.id.webUrlContainer);

        openWebViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deepConverseSDK != null) {
                    // Remove the existing WebUrlView if present
                    webUrlContainer.removeView(deepConverseSDK);
                    deepConverseSDK.destroyView();
                }

                // Create a new instance of WebUrlView
                Map<String, String> metadata = new HashMap<>();
                Gson gson = new Gson();//
                metadata = gson.fromJson("{\"country\":\"US\", \"email\": \"bob@ose.io\"}", Map.class);
                metadata.put("draft", "true");
                deepConverseSDK = new DeepConverseSDK(MainActivity.this, "dcstg5",
                        "preshin-19", metadata);
                deepConverseSDK.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                deepConverseSDK.setWebViewCallback(MainActivity.this);
                deepConverseSDK.load();
                webUrlContainer.addView(deepConverseSDK);

                // Show the webUrlContainer and trigger a layout pass
                webUrlContainer.setVisibility(View.VISIBLE);
                webUrlContainer.requestLayout();
            }
        });
    }

    @Override
    public void onViewClosed() {
        // Remove the WebUrlView from the container
        if (deepConverseSDK != null) {
            webUrlContainer.removeView(deepConverseSDK);
            deepConverseSDK.destroyView();
            deepConverseSDK = null;
            webUrlContainer.setVisibility(View.GONE);
        }
    }
}

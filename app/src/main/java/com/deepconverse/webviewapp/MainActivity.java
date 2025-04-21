package com.deepconverse.webviewapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.deepconverse.android_sdk.DeepConverseSDK;

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
                    cleanUpDeepConverseSDK();
                }

                // Create metadata
                Map<String, String> metadata = new HashMap<>();
                metadata.put("country", "US");
                metadata.put("email", "bob@ose.io");
                metadata.put("draft", "true");

                // Initialize DeepConverseSDK
                deepConverseSDK = new DeepConverseSDK(MainActivity.this, "dcstg5",
                        "preshin-19", metadata);

                deepConverseSDK.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));

                // Set callbacks
                deepConverseSDK.setWebViewCallback(MainActivity.this);
                deepConverseSDK.setBotEventListener(new DeepConverseSDK.BotEventListener() {
                    @Override
                    public void onBotEvent(org.json.JSONObject eventData) {
                        Log.d("MyApp", "Bot Event: " + eventData.toString());
                        // Handle bot events here
                    }
                });

                // Load the bot and add to container
                deepConverseSDK.load();
                webUrlContainer.addView(deepConverseSDK);
                webUrlContainer.setVisibility(View.VISIBLE);
                webUrlContainer.requestLayout();
            }
        });
    }

    @Override
    public void onViewClosed() {
        cleanUpDeepConverseSDK();
    }

    private void cleanUpDeepConverseSDK() {
        if (deepConverseSDK != null) {
            webUrlContainer.removeView(deepConverseSDK);
            deepConverseSDK.destroyView();
            deepConverseSDK = null;
            webUrlContainer.setVisibility(View.GONE);
        }
    }
}

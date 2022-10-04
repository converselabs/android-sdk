package com.deepconverse.www

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.deepconverse.chatbot.DeepConverseCallbacks
import com.deepconverse.chatbot.DeepConverseErrors
import com.deepconverse.chatbot.DeepConverseSDK
import com.deepconverse.chatbot.DeepConverseSession

class MainActivity : AppCompatActivity(), DeepConverseCallbacks {

    private lateinit var deepConverseSDK: DeepConverseSDK
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val session = DeepConverseSession(
            subdomian = "",
            botname = "",
            metadata = HashMap()
        )
        deepConverseSDK = DeepConverseSDK(
            session = session,
            callbacks = this
        )
        button = findViewById(R.id.open_button)
        button.setOnClickListener {
            open()
        }
    }

    private fun open() {
        deepConverseSDK.open()
    }

    override fun didCloseBot() {
    }

    override fun didOpenBot() {
    }

    override fun didFailToLoadBot(errors: DeepConverseErrors) {
    }

    override fun didReceiveEvent(event: Map<String, String>) {
    }
}
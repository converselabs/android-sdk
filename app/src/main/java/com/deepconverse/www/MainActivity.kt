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

        val items = HashMap<String, String>()
        items["draft"] = "true"

        val session = DeepConverseSession(
            subdomian = "dcshow1",
            botname = "showbot",
            metadata = items
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
        deepConverseSDK.open(context = this)
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
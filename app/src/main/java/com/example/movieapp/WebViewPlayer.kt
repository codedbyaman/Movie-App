
package com.example.movieapp.ui

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            loadUrl(videoUrl)
        }
    }, modifier = modifier)
}

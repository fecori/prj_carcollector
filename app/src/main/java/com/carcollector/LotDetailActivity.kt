package com.carcollector

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.carcollector.databinding.ActivityLotDetailBinding

class LotDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLotDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLotDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.topBar.setNavigationOnClickListener { finish() }

        val url = intent.getStringExtra(EXTRA_URL).orEmpty()
        setupWebView(binding.webView)
        binding.webView.loadUrl(url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(webView: WebView) {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                binding.loading.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_URL = "extra_url"
    }
}

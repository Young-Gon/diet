package com.seedit.diet

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_webview.*
import android.view.KeyEvent.KEYCODE_BACK
import android.webkit.WebView
import android.webkit.WebChromeClient
import com.seedit.diet.R.id.webView








class WebViewActivity : AppCompatActivity()
{
	@SuppressLint("SetJavaScriptEnabled")
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_webview)
		webView.webViewClient = WebViewClient()
		webView.webChromeClient = WebChromeClient()
		webView.settings.javaScriptEnabled = true
		webView.settings.domStorageEnabled = true
		webView.loadUrl("https://cafe.naver.com/dietdiary00")
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean
	{
		if (event.action == KeyEvent.ACTION_DOWN)
		{
			when (keyCode)
			{
				KeyEvent.KEYCODE_BACK ->
				{
					if (webView.canGoBack())
					{
						webView.goBack()
					} else
					{
						finish()
					}
					return true
				}
			}

		}
		return super.onKeyDown(keyCode, event)
	}
}

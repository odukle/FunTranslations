package com.odukle.funtranslations.ui.wakandan

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gargoylesoftware.htmlunit.StringWebResponse
import com.gargoylesoftware.htmlunit.html.HTMLParser
import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput
import com.odukle.funtranslations.ma
import com.odukle.funtranslations.qPage
import com.odukle.funtranslations.qUrl
import com.odukle.funtranslations.webClient
import kotlinx.android.synthetic.main.wakandan_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

private const val TAG = "WakandanViewModel"
class WakandanViewModel : ViewModel() {

//    private var webClient: WebClient = WebClient(BrowserVersion.CHROME)
//    private lateinit var qPage: HtmlPage
//    private lateinit var qUrl: URL
//
//    init {
//        webClient.options.isThrowExceptionOnFailingStatusCode = false
//        webClient.options.isThrowExceptionOnScriptError = false
//        webClient.options.isCssEnabled = false
//        webClient.options.isAppletEnabled = false
//        webClient.options.isJavaScriptEnabled = false
//    }

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text

    fun translate(text: String) {
        val ma = ma
        if (ma != null) {
            ma.count++
            if (ma.count >= 5) {
                ma.mInterstitialAd.show()
                ma.count = 0
            }
            _text.value = text
        }
    }

    fun loadQuotesPage() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                qUrl = URL("https://miniwebtool.com/random-quote-generator/")
                qPage = webClient.getPage(qUrl)
                if (wak?.randomBtnClicked!!) {
                    ma?.runOnUiThread {
                        Toast.makeText(ma, "Text bank is ready", Toast.LENGTH_SHORT).show()
                    }
                    wak?.randomBtnClicked = false
                }
            } catch (e: Exception) {
                Log.d(TAG, e.stackTraceToString())
                ma?.runOnUiThread {
                    Toast.makeText(ma, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun generateRandomQuote() {
        CoroutineScope(Dispatchers.IO).launch {
            ma?.runOnUiThread {
                wak?.original_text_wakanda?.setText("")
                wak?.original_text_wakanda?.hint = "wait, cooking random text..."
            }
            try {
                val generate =
                    qPage.getFirstByXPath<HtmlSubmitInput>("/html/body/div[6]/div[1]/div[7]/div/form/table/tbody/tr[2]/td/input")
                val content = generate.click<HtmlPage>().webResponse.contentAsString
                val stringWebResponse = StringWebResponse(content, qUrl)
                val rPage = HTMLParser.parseHtml(stringWebResponse, webClient.currentWindow)
                val rBody = rPage.body
                val quote =
                    rPage.getFirstByXPath<HtmlDivision>("/html/body/div[6]/div[1]/div[8]/div[4]/div[1]/div/div[1]")
                ma?.runOnUiThread {
                    wak?.original_text_wakanda?.hint = ""
                    wak?.original_text_wakanda?.setText(quote.textContent)
                }
            } catch (e: Exception) {
                Log.d(TAG, e.stackTraceToString())
                ma?.runOnUiThread {
                    Toast.makeText(ma, e.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}
package com.odukle.funtranslations.ui.avatar

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gargoylesoftware.htmlunit.StringWebResponse
import com.gargoylesoftware.htmlunit.html.*
import com.odukle.funtranslations.ma
import com.odukle.funtranslations.qPage
import com.odukle.funtranslations.qUrl
import com.odukle.funtranslations.webClient
import kotlinx.android.synthetic.main.avatar_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

private const val TAG = "AvatarViewModel"
class AvatarViewModel : ViewModel() {

//    private var webClient: WebClient = WebClient(BrowserVersion.CHROME)
//    private lateinit var qPage: HtmlPage
//    private lateinit var qUrl: URL

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

            CoroutineScope(Dispatchers.IO).launch {
                ma.runOnUiThread {
                    ma.progress_bar_avatar.visibility = View.VISIBLE
                }
                try {
                    val url = URL("https://funtranslations.com/navi")
                    val page: HtmlPage = webClient.getPage(url)
                    val forms = page.forms
                    val form = forms[0]
                    val textArea = form.getTextAreaByName("text")
                    textArea.text = text
                    val button = form.getButtonByName("submit")
                    val content = button.click<HtmlPage>().webResponse.contentAsString
                    val stringWebResponse = StringWebResponse(content, url)
                    val rPage = HTMLParser.parseHtml(stringWebResponse, webClient.currentWindow)
                    val body = rPage.body
                    val rText = body.getOneHtmlElementByAttribute<HtmlSpan>("span", "class", "result-text")
                    _text.postValue(rText.textContent)
                } catch (e: Exception) {
                    ma.runOnUiThread {
                        Toast.makeText(ma, e.message, Toast.LENGTH_SHORT).show()
                    }
                    Log.d(TAG, e.stackTraceToString())
                }

                ma.runOnUiThread {
                    ma.progress_bar_avatar?.visibility = View.GONE
                }
            }
        }

    }

//    fun loadQuotesPage() {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                qUrl = URL("https://miniwebtool.com/random-quote-generator/")
//                qPage = webClient.getPage(qUrl)
//                if (av?.randomBtnClicked!!) {
//                    ma?.runOnUiThread {
//                        Toast.makeText(ma, "Text bank is ready", Toast.LENGTH_SHORT).show()
//                    }
//                    av?.randomBtnClicked = false
//                }
//            } catch (e: Exception) {
//                Log.d(TAG, e.stackTraceToString())
//                ma?.runOnUiThread {
//                    Toast.makeText(ma, e.message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    fun generateRandomQuote() {
        CoroutineScope(Dispatchers.IO).launch {
            ma?.runOnUiThread {
                av?.original_text_avatar?.setText("")
                av?.original_text_avatar?.hint = "wait, cooking random text..."
            }
            try {
                val generate =
                    qPage.getFirstByXPath<HtmlSubmitInput>("/html/body/div[6]/div[1]/div[7]/div/form/table/tbody/tr[2]/td/input")
                Log.d(TAG, generate.localName)
                val content = generate.click<HtmlPage>().webResponse.contentAsString
//            Log.d(TAG, content)
                val stringWebResponse = StringWebResponse(content, qUrl)
                val rPage = HTMLParser.parseHtml(stringWebResponse, webClient.currentWindow)
                val rBody = rPage.body
                val quote =
                    rPage.getFirstByXPath<HtmlDivision>("/html/body/div[6]/div[1]/div[8]/div[4]/div[1]/div/div[1]")
                ma?.runOnUiThread {
                    av?.original_text_avatar?.hint = ""
                    av?.original_text_avatar?.setText(quote.textContent)
                }
            } catch (e: Exception) {
                Log.d(TAG, e.stackTraceToString())
                ma?.runOnUiThread {
                    Toast.makeText(ma, e.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

//    fun isInitialized(): Boolean = this::qPage.isInitialized
}
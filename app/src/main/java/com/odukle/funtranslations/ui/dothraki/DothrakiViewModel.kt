package com.odukle.funtranslations.ui.dothraki

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
import kotlinx.android.synthetic.main.fragment_dothraki.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.net.URL

private const val TAG = "DothrakiVM"
class DothrakiViewModel : ViewModel() {

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

    private val _dothraki = MutableLiveData<String>().apply {
        value = ""
    }
    val dothraki: LiveData<String> = _dothraki

    fun translate(text: String) {
        val ma = ma
        if (ma != null) {
            ma.count++
            if (ma.count >= 5) {
                ma.mInterstitialAd.show()
                ma.count = 0
            }

            CoroutineScope(IO).launch {
                ma.runOnUiThread {
                    ma.progress_bar_dothraki.visibility = View.VISIBLE
                }

                try {
                    val page: HtmlPage = webClient.getPage("https://funtranslations.com/dothraki")
                    val forms = page.forms
                    val form = forms[0]
                    val textArea = form.getTextAreaByName("text")
                    textArea.text = text
                    val button = form.getButtonByName("submit")
                    Log.d(TAG, "button: ${button.nameAttribute}")
                    val content = button.click<HtmlPage>().webResponse.contentAsString
                    val url = URL("https://funtranslations.com/dothraki")
                    val stringWebResponse = StringWebResponse(content, url)
                    val rPage = HTMLParser.parseHtml(stringWebResponse, webClient.currentWindow)
                    val body = rPage.body
                    val rText =
                        body.getOneHtmlElementByAttribute<HtmlSpan>("span", "class", "result-text")
                    _dothraki.postValue(rText.textContent)
                } catch (e: Exception) {
                    ma.runOnUiThread {
                        Toast.makeText(ma, e.message, Toast.LENGTH_SHORT).show()
                    }
                    Log.d(TAG, e.stackTraceToString())
                }

                ma.runOnUiThread {
                    ma.progress_bar_dothraki?.visibility = View.GONE
                }
            }
        }
    }

//    fun loadQuotesPage() {
//        CoroutineScope(IO).launch {
//            try {
//                qUrl = URL("https://miniwebtool.com/random-quote-generator/")
//                qPage = webClient.getPage(qUrl)
//                if (dot?.randomBtnClicked!!) {
//                    ma?.runOnUiThread {
//                        Toast.makeText(ma, "Text bank is ready", Toast.LENGTH_SHORT).show()
//                    }
//                    dot?.randomBtnClicked = false
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
        CoroutineScope(IO).launch {
            ma?.runOnUiThread {
                dot?.original_text_dothraki?.setText("")
                dot?.original_text_dothraki?.hint = "wait, cooking random text..."
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
                    dot?.original_text_dothraki?.hint = ""
                    dot?.original_text_dothraki?.setText(quote.textContent)
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

package com.odukle.funtranslations

import android.util.Log
import android.widget.Toast
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.odukle.funtranslations.ui.avatar.av
import com.odukle.funtranslations.ui.dothraki.dot
import com.odukle.funtranslations.ui.hodor.hod
import com.odukle.funtranslations.ui.minion.mnn
import com.odukle.funtranslations.ui.shakespeare.shk
import com.odukle.funtranslations.ui.valyrian.valy
import com.odukle.funtranslations.ui.wakandan.wak
import com.odukle.funtranslations.ui.yoda.yoda
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

private const val TAG = "LoadRandomQuotes"
var webClient: WebClient = WebClient(BrowserVersion.CHROME)
lateinit var qPage: HtmlPage
lateinit var qUrl: URL

class LoadRandomQuotes {

    init {
        webClient.options.isThrowExceptionOnFailingStatusCode = false
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.options.isCssEnabled = false
        webClient.options.isAppletEnabled = false
        webClient.options.isJavaScriptEnabled = false
    }

    fun loadQuotesPage() {
        if (isOnline(ma?.applicationContext!!)) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    qUrl = URL("https://miniwebtool.com/random-quote-generator/")
                    qPage = webClient.getPage(qUrl)
                    ma?.runOnUiThread {
                        if (shk?.randomBtnClicked ?: av?.randomBtnClicked ?: dot?.randomBtnClicked
                            ?: hod?.randomBtnClicked ?: mnn?.randomBtnClicked ?: valy?.randomBtnClicked
                            ?: wak?.randomBtnClicked ?: yoda?.randomBtnClicked!!
                        ) {
                            ma?.runOnUiThread {
                                Toast.makeText(ma, "Text bank is ready", Toast.LENGTH_SHORT).show()
                            }

                            av?.randomBtnClicked = false
                            dot?.randomBtnClicked = false
                            hod?.randomBtnClicked = false
                            mnn?.randomBtnClicked = false
                            shk?.randomBtnClicked = false
                            valy?.randomBtnClicked = false
                            wak?.randomBtnClicked = false
                            yoda?.randomBtnClicked = false
                        }
                    }

                } catch (e: Exception) {
                    Log.d(TAG, e.stackTraceToString())
                    ma?.runOnUiThread {
                        Toast.makeText(ma, e.message, Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
            }
        } else {
            Toast.makeText(ma, "No Internet!", Toast.LENGTH_SHORT).show()
        }
    }

    fun isInitialized(): Boolean = ::qPage.isInitialized
}
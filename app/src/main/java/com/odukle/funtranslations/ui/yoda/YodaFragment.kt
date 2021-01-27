package com.odukle.funtranslations.ui.yoda

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.odukle.funtranslations.R
import com.odukle.funtranslations.isOnline
import com.odukle.funtranslations.ma
import com.odukle.funtranslations.ui.avatar.CLIP_DATA
import kotlinx.android.synthetic.main.fragment_yoda.*

var yoda: YodaFragment? = null
class YodaFragment : Fragment() {

    var randomBtnClicked = false
    private lateinit var yodaViewModel: YodaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        yoda = this

        yodaViewModel =
            ViewModelProvider(this).get(YodaViewModel::class.java)
//        yodaViewModel.loadQuotesPage()
        val root = inflater.inflate(R.layout.fragment_yoda, container, false)
        yodaViewModel.text.observe(viewLifecycleOwner, Observer {
            yoda_text.setText(it)
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        original_text_yoda.hint = "Enter your text here"
        btn_yoda.setOnClickListener {
            if (isOnline(requireContext())) {
                if (original_text_yoda.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Text field is empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    yodaViewModel.translate(original_text_yoda.text.toString())
                }
            } else {
                Toast.makeText(requireContext(), "No Internet!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_copy_yoda.setOnClickListener {
            if (yoda_text.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Nothing to copy 🙄", Toast.LENGTH_SHORT).show()
            } else {
                val clipBoard =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(CLIP_DATA, yoda_text.text.toString())
                clipBoard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Copied to clipboard 🤘", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_share_yoda.setOnClickListener {

            if (yoda_text.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Nothing to share 🙄", Toast.LENGTH_SHORT).show()
            } else {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, yoda_text.text.toString())
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, "Thug Text"))
            }
        }

        btn_random_text_yoda.setOnClickListener {
            if (isOnline(requireContext())) {
                if (ma?.loadQ!!.isInitialized()) {
                    yodaViewModel.generateRandomQuote()
                } else {
                    randomBtnClicked = true
                    Toast.makeText(
                        requireContext(),
                        "Wait, connecting to text bank",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "No Internet!", Toast.LENGTH_SHORT).show()
            }
        }

        adView_yoda.loadAd(ma?.adRequest)
    }

}
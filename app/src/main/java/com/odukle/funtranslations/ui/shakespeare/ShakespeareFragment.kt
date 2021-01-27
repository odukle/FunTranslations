package com.odukle.funtranslations.ui.shakespeare

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import kotlinx.android.synthetic.main.fragment_shakespeare.*

var shk: ShakespeareFragment? = null

class ShakespeareFragment : Fragment() {

    var randomBtnClicked = false
    private lateinit var shakespeareViewModel: ShakespeareViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        shk = this

        shakespeareViewModel =
            ViewModelProvider(this).get(ShakespeareViewModel::class.java)
//        shakespeareViewModel.loadQuotesPage()
        val root = inflater.inflate(R.layout.fragment_shakespeare, container, false)
        shakespeareViewModel.text.observe(viewLifecycleOwner, Observer {
            shakespeare_text.setText(it)
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        original_text_shakespeare.hint = "Enter your text here"
        btn_shakespeare.setOnClickListener {
            if (isOnline(requireContext())) {
                if (original_text_shakespeare.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Text field is empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    shakespeareViewModel.translate(original_text_shakespeare.text.toString())
                }
            } else {
                Toast.makeText(requireContext(), "No Internet!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_copy_shakespeare.setOnClickListener {
            if (shakespeare_text.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Nothing to copy 🙄", Toast.LENGTH_SHORT).show()
            } else {
                val clipBoard =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(CLIP_DATA, shakespeare_text.text.toString())
                clipBoard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Copied to clipboard 🤘", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_share_shakespeare.setOnClickListener {

            if (shakespeare_text.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Nothing to share 🙄", Toast.LENGTH_SHORT).show()
            } else {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shakespeare_text.text.toString())
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, "Thug Text"))
            }
        }

        btn_random_text_shake.setOnClickListener {
            if (isOnline(requireContext())) {
                if (ma?.loadQ!!.isInitialized()) {
                    shakespeareViewModel.generateRandomQuote()
                } else {
                    Looper.myLooper()?.let { it1 ->
                        Handler(it1).postDelayed({
                            ma?.loadQ?.loadQuotesPage()
                        }, 2000)
                    }
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

        adView_shakespeare.loadAd(ma?.adRequest)
    }
}
package com.odukle.funtranslations.ui.hodor

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
import kotlinx.android.synthetic.main.hodor_fragment.*

var hod: HodorFragment? = null
class HodorFragment : Fragment() {

    var randomBtnClicked = false
    private lateinit var hodorViewModel: HodorViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        hod = this
        hodorViewModel =
            ViewModelProvider(this).get(HodorViewModel::class.java)
//        hodorViewModel.loadQuotesPage()
        val root = inflater.inflate(R.layout.hodor_fragment, container, false)
        hodorViewModel.text.observe(viewLifecycleOwner, Observer {
            hodor_text.setText(it)
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        original_text_hodor.hint = "Enter your text here"
        btn_hodor.setOnClickListener {
            if (original_text_hodor.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Text field is empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                hodorViewModel.translate(original_text_hodor.text.toString())
            }
        }

        btn_copy_hodor.setOnClickListener {
            if (hodor_text.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Nothing to copy 🙄", Toast.LENGTH_SHORT).show()
            } else {
                val clipBoard =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(CLIP_DATA, hodor_text.text.toString())
                clipBoard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Copied to clipboard 🤘", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_share_hodor.setOnClickListener {

            if (hodor_text.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Nothing to share 🙄", Toast.LENGTH_SHORT).show()
            } else {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, hodor_text.text.toString())
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, "Thug Text"))
            }
        }

        btn_random_text_hodor.setOnClickListener {
            if (isOnline(requireContext())) {
                if (ma?.loadQ!!.isInitialized()) {
                    hodorViewModel.generateRandomQuote()
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

        adView_hodor.loadAd(ma?.adRequest)
    }

}
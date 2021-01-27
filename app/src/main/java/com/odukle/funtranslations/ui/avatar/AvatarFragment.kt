package com.odukle.funtranslations.ui.avatar

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
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
import kotlinx.android.synthetic.main.avatar_fragment.*

const val CLIP_DATA = "clipData"
var av: AvatarFragment? = null
class AvatarFragment : Fragment() {

    private lateinit var avatarViewModel: AvatarViewModel
    var randomBtnClicked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        av = this
        avatarViewModel =
            ViewModelProvider(this).get(AvatarViewModel::class.java)
//        avatarViewModel.loadQuotesPage()
        val root = inflater.inflate(R.layout.avatar_fragment, container, false)
        avatarViewModel.text.observe(viewLifecycleOwner, Observer {
            avatar_text.setText(it)
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        original_text_avatar.hint = "Enter your text here"
        btn_avatar.setOnClickListener {
            if (isOnline(requireContext())) {
                if (original_text_avatar.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Text field is empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    avatarViewModel.translate(original_text_avatar.text.toString())
                }
            } else {
                Toast.makeText(requireContext(), "No Internet!", Toast.LENGTH_SHORT).show()
            }
        }

        btn_copy_avatar.setOnClickListener {
            if (avatar_text.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Nothing to copy 🙄", Toast.LENGTH_SHORT).show()
            } else {
                val clipBoard =
                    requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(CLIP_DATA, avatar_text.text.toString())
                clipBoard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Copied to clipboard 🤘", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btn_share_avatar.setOnClickListener {

            if (avatar_text.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Nothing to share 🙄", Toast.LENGTH_SHORT).show()
            } else {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, avatar_text.text.toString())
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, "Thug Text"))
            }
        }

        btn_random_text_avatar.setOnClickListener {
            if (isOnline(requireContext())) {
                if (ma?.loadQ!!.isInitialized()) {
                    avatarViewModel.generateRandomQuote()
                } else {
                    randomBtnClicked = true
                    Toast.makeText(requireContext(), "Wait, connecting to text bank", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No Internet!", Toast.LENGTH_SHORT).show()
            }

        }

        adView_avatar.loadAd(ma?.adRequest)
    }

}
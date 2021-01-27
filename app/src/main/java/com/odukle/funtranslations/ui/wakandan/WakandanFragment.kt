package com.odukle.funtranslations.ui.wakandan

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.odukle.funtranslations.R
import com.odukle.funtranslations.isOnline
import com.odukle.funtranslations.ma
import kotlinx.android.synthetic.main.wakandan_fragment.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "WakandanFragment"
var wak: WakandanFragment? = null

class WakandanFragment : Fragment() {

    var randomBtnClicked = false
    private lateinit var wakandanViewModel: WakandanViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        wak = this

        wakandanViewModel =
            ViewModelProvider(this).get(WakandanViewModel::class.java)
//        wakandanViewModel.loadQuotesPage()
        val root = inflater.inflate(R.layout.wakandan_fragment, container, false)
        wakandanViewModel.text.observe(viewLifecycleOwner, Observer {
            wakandan_text.setText(it)
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        original_text_wakanda.hint = "Enter your text here"
        wakandan_text.typeface = ResourcesCompat.getFont(requireContext(), R.font.wakanda)
        btn_wakanda.setOnClickListener {
            if (original_text_wakanda.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Text field is empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                wakandanViewModel.translate(original_text_wakanda.text.toString())
            }
        }

        btn_share_wakanda.setOnClickListener {
            val bitmap = wakandan_text.drawToBitmap(Bitmap.Config.ARGB_8888)
            val share = Intent(Intent.ACTION_SEND)
            share.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            share.type = "image/png"
            try {
                val imagesFolder = File(requireActivity().cacheDir, "images")
                imagesFolder.mkdirs()
                val file = File(imagesFolder, "shared_image.png")

                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                stream.flush()
                stream.close()
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.odukle.funtranslations.fileprovider",
                    file
                )

                share.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(share)
            } catch (e: IOException) {
                Log.d(
                    TAG,
                    "IOException while trying to write file for sharing: " + e.printStackTrace()
                )
            }
        }

        btn_random_text_wakanda.setOnClickListener {
            if (isOnline(requireContext())) {
                if (ma?.loadQ!!.isInitialized()) {
                    wakandanViewModel.generateRandomQuote()
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

        adView_wakanda.loadAd(ma?.adRequest)
    }
}
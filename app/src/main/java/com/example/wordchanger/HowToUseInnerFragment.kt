package com.example.wordchanger

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import java.lang.Exception
import java.lang.NullPointerException

class HowToUseInnerFragment : Fragment() {

    companion object{
        private const val LOG_TAG = "TestApp_HowToUseFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val imgResource = arguments?.getInt("imgResource")

        val layoutView = inflater.inflate(R.layout.fragment_howto, container, false)
        val ivImage = layoutView.findViewById<ImageView>(R.id.ivImage)

        try {
            ivImage.setImageResource(imgResource!!)
        } catch (e: Exception) {
            when (e) {
                is Resources.NotFoundException,
                is NullPointerException -> {
                    e.printStackTrace()
                    Log.e(LOG_TAG, "[Error] IMG_RESOURCE: NULL or NotFound")
                    ivImage.setImageResource(R.drawable.img_howto_error)
                }
                else ->{
                    throw e
                }
            }
        }
        return layoutView
    }
}
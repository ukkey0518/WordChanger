package com.example.wordchanger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_how_to_use.*

class HowToUseActivity : AppCompatActivity() {

    companion object {
        private const val LOG_TAG = "TestApp_HowToUseActivity"
        const val HOW_TO_MODE_OPENING = 1
        const val HOW_TO_MODE_BUTTON = 2
        const val HOW_TO_MODE_NLSSET = 3
        const val HOW_TO_MODE_PRIORITY = 4

    }

    private lateinit var _pagerAdapter: HowToUseAdapter

    private lateinit var _fragmentList: List<Fragment>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_use)

        _fragmentList = when (intent.getIntExtra("howToMode", 0)) {
            HOW_TO_MODE_OPENING -> {
                Log.i(LOG_TAG, "HOW_TO_MODE: OPENING")
                tvHowToUseTitle.text = getString(R.string.htua_title_opening)
                arrayListOf(
                    createNewHowToUseFragment(R.drawable.img_howto_opening_01),
                    createNewHowToUseFragment(R.drawable.img_howto_opening_02),
                    createNewHowToUseFragment(R.drawable.img_howto_opening_03),
                    createNewHowToUseFragment(R.drawable.img_howto_opening_04),
                    createNewHowToUseFragment(R.drawable.img_howto_opening_05),
                    createNewHowToUseFragment(R.drawable.img_howto_opening_06)
                )
            }
            HOW_TO_MODE_BUTTON ->{
                Log.i(LOG_TAG, "HOW_TO_MODE: BUTTON")
                tvHowToUseTitle.text = getString(R.string.htua_title_button)
                arrayListOf(
                    createNewHowToUseFragment(R.drawable.img_howto_button_01),
                    createNewHowToUseFragment(R.drawable.img_howto_button_02)
                )

            }
            HOW_TO_MODE_NLSSET ->{
                Log.i(LOG_TAG, "HOW_TO_MODE: NLSSET")
                tvHowToUseTitle.text = getString(R.string.htua_title_nlsset)
                arrayListOf(
                    createNewHowToUseFragment(R.drawable.img_howto_nlsset_01),
                    createNewHowToUseFragment(R.drawable.img_howto_nlsset_02),
                    createNewHowToUseFragment(R.drawable.img_howto_nlsset_03),
                    createNewHowToUseFragment(R.drawable.img_howto_nlsset_04)
                )
            }
            HOW_TO_MODE_PRIORITY ->{
                Log.i(LOG_TAG, "HOW_TO_MODE: PRIORITY")
                tvHowToUseTitle.text = getString(R.string.htua_title_priority)
                arrayListOf(
                    createNewHowToUseFragment(R.drawable.img_howto_priority_01),
                    createNewHowToUseFragment(R.drawable.img_howto_priority_02),
                    createNewHowToUseFragment(R.drawable.img_howto_priority_03),
                    createNewHowToUseFragment(R.drawable.img_howto_priority_04),
                    createNewHowToUseFragment(R.drawable.img_howto_priority_05)
                )
            }
            else -> {
                Log.e(LOG_TAG, "[Error] HOW_TO_MODE: NULL")
                arrayListOf(
                    createNewHowToUseFragment(R.drawable.img_howto_error)
                )
            }
        }


        _pagerAdapter = HowToUseAdapter(supportFragmentManager, _fragmentList)


    }


    override fun onStart() {
        super.onStart()


        vpHowToUse.adapter = _pagerAdapter


        vpHowToUse.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    Log.i(LOG_TAG, "SELECT_ITEM_INDEX: ${vpHowToUse.currentItem}")
                    when (vpHowToUse.currentItem) {
                        0 -> {
                            ivArrowLeft.visibility = View.INVISIBLE
                            ivArrowRight.visibility = View.VISIBLE
                        }
                        _fragmentList.lastIndex -> {
                            tvDescText.visibility = View.INVISIBLE
                            ivArrowLeft.visibility = View.VISIBLE
                            ivArrowRight.visibility = View.INVISIBLE
                        }
                        else -> {
                            tvDescText.visibility = View.INVISIBLE
                            ivArrowLeft.visibility = View.VISIBLE
                            ivArrowRight.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

            }
        })

    }


    private fun createNewHowToUseFragment(imgResource: Int): HowToUseInnerFragment {
        val fragment = HowToUseInnerFragment()
        val args = Bundle()
        args.putInt("imgResource", imgResource)
        fragment.arguments = args
        return fragment
    }


    fun onCloseButtonClick(@Suppress("UNUSED_PARAMETER") view: View){
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }


}

package com.ukiuki.wordchanger

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class ConfirmDialogFragment private constructor() : DialogFragment() {

    companion object {

        // [インスタンス生成メソッド]
        fun newInstance(title: String, message: String, requestCode: Int, otherData: Bundle = Bundle()): ConfirmDialogFragment {
            val instance = ConfirmDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("message", message)
            args.putInt("requestCode", requestCode)
            args.putBundle("otherData", otherData)
            instance.arguments = args
            return instance
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val title = arguments?.getString("title")
        val message = arguments?.getString("message")
        val requestCode = arguments?.getInt("requestCode", 0)
        val otherData = arguments?.getBundle("otherData")

        val inflater = LayoutInflater.from(activity)
        val layout = inflater.inflate(R.layout.confirm_dialog, null)


        val tvMessage = layout.findViewById<TextView>(R.id.tvMessage)
        tvMessage.text = message

        // タイトル用TextViewの生成
        val tvTitle = TextView(context)
        tvTitle.text = title
        tvTitle.textSize = 24F
        tvTitle.setTextColor(Color.WHITE)
        tvTitle.setBackgroundColor(Color.RED)
        tvTitle.setPadding(20, 20, 20, 20)
        tvTitle.gravity = Gravity.CENTER

        val builder = AlertDialog.Builder(context!!, R.style.AddEditWordDialog)
        builder.setCustomTitle(tvTitle)
        builder.setView(layout)
        builder.setPositiveButton(
            getString(R.string.dialog_btn_positive)) { _, _ ->
            when (activity) {
                is MainActivity -> {
                    val mainActivity = activity as MainActivity
                    mainActivity.onConfirmClickPositive(requestCode, otherData)
                }
                is SettingActivity -> {
                    val settingActivity = activity as SettingActivity
                    settingActivity.onConfirmClickPositive(requestCode, otherData)
                }
            }
        }
        builder.setNeutralButton(getString(R.string.dialog_btn_neutral)){ _, _ ->
            when (activity) {
                is MainActivity -> {
                    val mainActivity = activity as MainActivity
                    mainActivity.onConfirmClickNeutral(requestCode, otherData)
                }
                is SettingActivity -> {
                    val settingActivity = activity as SettingActivity
                    settingActivity.onConfirmClickNeutral(requestCode, otherData)
                }
            }
        }

        // 画面外タップによるキャンセル操作を無効化
        this.isCancelable = false

        return builder.create()
    }


    interface ConfirmDialogListener{
        fun onConfirmClickPositive(requestCode: Int?, otherData: Bundle?)
        fun onConfirmClickNeutral(requestCode: Int?, otherData: Bundle?)
    }
}
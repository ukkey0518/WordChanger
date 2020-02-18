package com.example.wordchanger

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class OpeningDialogFragment: DialogFragment(), DialogInterface.OnClickListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)
        val layout = inflater.inflate(R.layout.dialog_opening, null)

        // タイトル用TextViewの生成
        val tvTitle = TextView(context)
        tvTitle.text = getString(R.string.odf_title)
        tvTitle.textSize = 24F
        tvTitle.setTextColor(Color.WHITE)
        tvTitle.setBackgroundColor(Color.rgb(192, 142, 47))
        tvTitle.setPadding(20, 20, 20, 20)
        tvTitle.gravity = Gravity.CENTER

        val builder = AlertDialog.Builder(context!!, R.style.AddEditWordDialog)
        builder.setCustomTitle(tvTitle)
        builder.setView(layout)
        builder.setPositiveButton("見る", this)
        builder.setNeutralButton("見ない", null)

        // 画面外タップによるキャンセル操作を無効化
        this.isCancelable = false

        return builder.create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when(which){
            DialogInterface.BUTTON_POSITIVE ->{
                val intent = Intent(activity, HowToUseActivity::class.java).putExtra(
                    "howToMode",
                    HowToUseActivity.HOW_TO_MODE_OPENING
                )
                startActivity(intent)
                activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }
}
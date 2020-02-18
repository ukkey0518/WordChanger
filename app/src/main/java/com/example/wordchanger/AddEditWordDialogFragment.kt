package com.example.wordchanger

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_setting.*

class AddEditWordDialogFragment private constructor(): DialogFragment(), DialogInterface.OnClickListener{

    companion object {
        const val ADD_MODE = 0
        const val EDIT_MODE = 1

        fun newInstance(mode: Int, wordData: WordRowData? = null, position: Int? = null, otherData: Bundle = Bundle()): AddEditWordDialogFragment{
            val instance = AddEditWordDialogFragment()
            val args = Bundle()
            args.putInt("mode", mode)
            wordData.let {
                args.putSerializable("wordData", wordData)
            }
            position?.let{
                args.putInt("position", position)
            }
            args.putBundle("otherData", otherData)
            instance.arguments = args
            return instance
        }
    }

    private lateinit var modeTitle: String

    private lateinit var etInput: EditText
    private lateinit var etOutput: EditText
    private lateinit var swRemoveOrChange: Switch
    private lateinit var swAfterNewLine: Switch
    private lateinit var tvRemoveOrChangeDesc: TextView
    private lateinit var tvAfterNewLineDesc: TextView


    private var newInputStr: String = ""
    private var newOutputStr: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val mode = arguments?.getInt("mode")
        val wordData = arguments?.getSerializable("wordData") as WordRowData?
        val position = arguments?.getInt("position")


        val inflater = LayoutInflater.from(activity)
        val layout = inflater.inflate(R.layout.add_edit_word_dialog, null)

        etInput = layout.findViewById(R.id.addDialog_etInput)
        etOutput = layout.findViewById(R.id.addDialog_etOutput)

        tvRemoveOrChangeDesc = layout.findViewById(R.id.tvRemoveOrChangeDesc)
        tvAfterNewLineDesc = layout.findViewById(R.id.tvAfterNewLineDesc)

        swAfterNewLine = layout.findViewById(R.id.swAfterNewLine)
        swRemoveOrChange = layout.findViewById(R.id.swRemoveOrChange)

        modeTitle = if (mode == ADD_MODE) getString(R.string.aewdf_title_add_mode) else getString(R.string.aewdf_title_edit_mode)


        // [リスナ]"ワード後改行"スイッチ切り替え時の処理
        swAfterNewLine.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.text = getString(R.string.aewdf_sw_afternewline_on)
                tvAfterNewLineDesc.text = getString(R.string.aewdf_sw_afternewline_desc_on)
            } else {
                buttonView.text = getString(R.string.aewdf_sw_afternewline_off)
                tvAfterNewLineDesc.text = getString(R.string.aewdf_sw_afternewline_desc_off)
            }
        }

        // [リスナ]"除外・変換"スイッチ切り替え時の処理
        swRemoveOrChange.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                etOutput.hint = ""
                etOutput.setText("")
                etOutput.setBackgroundColor(Color.parseColor("#e5ab47"))
                etOutput.isEnabled = false
                buttonView.text = getString(R.string.aewdf_sw_removeorchange_on)
                tvRemoveOrChangeDesc.text = getString(R.string.aewdf_sw_removeorchange_desc_on)
                //非表示になったところの文字色を変えたい
            } else {
                etOutput.hint = getString(R.string.aewdf_et_hint_under_20)
                etOutput.isEnabled = true
                etOutput.setBackgroundColor(Color.parseColor("#ffffff"))
                buttonView.text = getString(R.string.aewdf_sw_removeorchange_off)
                tvRemoveOrChangeDesc.text = getString(R.string.aewdf_sw_removeorchange_desc_off)
            }
        }


        // アクティビティ変数の取得
        val activity = activity as SettingActivity

        // タイトル用TextViewの生成
        val tvTitle = TextView(context)
        tvTitle.text = modeTitle
        tvTitle.textSize = 24F
        tvTitle.setTextColor(Color.WHITE)
        tvTitle.setBackgroundColor(Color.rgb(192, 142, 47))
        tvTitle.setPadding(20, 20, 20, 20)
        tvTitle.gravity = Gravity.CENTER

        // ダイアログの生成
        val builder = AlertDialog.Builder(context!!, R.style.AddEditWordDialog)
        builder.setCustomTitle(tvTitle)
        builder.setView(layout)
        builder.setPositiveButton(getString(R.string.dialog_btn_positive), null)
        builder.setNeutralButton(getString(R.string.dialog_btn_neutral), this)
        val dialog = builder.create()


        //「編集モード」で開かれた時は、内容を反映する
        var oldInputStr :String? = null
        val oldOutputStr: String?
        if (mode == EDIT_MODE) {

            oldInputStr = wordData?.tvInput
            oldOutputStr = wordData?.tvOutput

            etInput.setText(oldInputStr)
            etOutput.setText(oldOutputStr?.replace("\n", ""))
            if (etOutput.text.toString() == "") {
                swRemoveOrChange.isChecked = true
            }
            if (oldOutputStr != null && oldOutputStr.contains("\n")) {
                swAfterNewLine.isChecked = true
            }
        }




        dialog.setOnShowListener {
            (it as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {

                // ①文字列を取得
                newInputStr = etInput.text.toString()
                newOutputStr = etOutput.text.toString()
                var errorMessage = ""


                // [エラー]除外チェックなし＆＆出力テキスト未入力
                if (!(swRemoveOrChange.isChecked) && newOutputStr == "") {
                    errorMessage = getString(R.string.toast_set_emptyoutput)
                }

                // [エラー]キーが含まれている
                val keySet = mutableSetOf<String>()
                activity.displayDataList.forEach { data ->
                    keySet.add(data.tvInput)
                }

                if (keySet.contains(newInputStr) && !(mode == EDIT_MODE && oldInputStr!= null && oldInputStr == newInputStr)) {
                    errorMessage = getString(R.string.toast_set_alreadyexists)
                }

                // [エラー]入力文字列が未入力
                if (newInputStr == "") {
                    errorMessage = getString(R.string.toast_set_emptyinput)
                }


                if (errorMessage.isEmpty()) {

                    // ②改行文字の有無
                    if (swAfterNewLine.isChecked) {
                        newOutputStr += "\n"
                    } else {
                        newOutputStr = newOutputStr.replace("\n", "")
                    }

                    when (mode) {
                        ADD_MODE -> {
                            activity.displayDataList.add(WordRowData(newInputStr, newOutputStr))
                        }
                        EDIT_MODE -> {
                            activity.displayDataList[position!!] = WordRowData(newInputStr, newOutputStr)
                        }
                    }

                    // [変換リスト] リスト全体の更新
                    activity.rvWordList.adapter?.notifyDataSetChanged()

                    // [ダイアログ] ダイアログを閉じる
                    this@AddEditWordDialogFragment.dismiss()

                } else {
                    val toast =
                        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, -100)
                    toast.show()
                }


            }
        }
        return dialog
    }


    override fun onDismiss(dialog: DialogInterface) {
        val activity = activity as SettingActivity
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        super.onDismiss(dialog)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
    }
}
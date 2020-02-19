package com.example.wordchanger

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random


class MainActivity : AppCompatActivity(), ConfirmDialogFragment.ConfirmDialogListener {

    companion object {
        val escapeWordList = listOf("#", "@", "&", "$", "|", "^")
        const val CONFIRM_REQUEST_CODE_CLEAR = 1
    }

    private lateinit var _app: MainApplication
    private lateinit var _changer: ListSaveDataChanger

    private lateinit var _textWatcher: TextWatcher

    private lateinit var _prefSaveData: SharedPreferences
    private lateinit var _prefFirstBoot: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        _app = application as MainApplication
        _changer = ListSaveDataChanger()

        _prefSaveData = getSharedPreferences("tanukiSaveData", Context.MODE_PRIVATE)
        _prefFirstBoot = getSharedPreferences("tanukiFirstBoot", Context.MODE_PRIVATE)


        // 広告のロード
        MobileAds.initialize(this) {}
        adView.loadAd(AdRequest.Builder().build())


        // [入力クリアボタン]　tvInputに 文字がある時クリアボタン有効　<->　未入力の時無効
        _textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                main_btnClearInput.isEnabled = !(main_btnClearInput.isEnabled) && s.toString().isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }


        // [セーブデータ復元処理]
        val saveData = mutableListOf<String>()
        val count = _prefSaveData.getInt("count", 0)
        for (c in 1..count) {
            val str = _prefSaveData.getString(c.toString(), null)
            if (str != null) saveData.add(str)
        }
        main_etInput.setText(_prefSaveData.getString("text", ""))
        _app.wordList = _changer.saveDataToList(saveData)


        //　初回起動時の処理
        if (_prefFirstBoot.getBoolean("flag", true)) {

        val dialog = OpeningDialogFragment()
        dialog.show(supportFragmentManager, "OpeningDialogFragment")

            _prefFirstBoot.edit().putBoolean("flag", false).apply()
        }


    }


    override fun onStart() {
        super.onStart()

        // [入力欄の監視] TextWatcherの登録
        main_etInput.addTextChangedListener(_textWatcher)

    }


    override fun onResume() {
        super.onResume()

        main_btnClearInput.isEnabled = true
        main_btnSetting.isEnabled = true
        main_btnChange.isEnabled = true
    }


    override fun onPause() {
        super.onPause()
        main_btnClearInput.isEnabled = false
        main_btnSetting.isEnabled = false
        main_btnChange.isEnabled = false

        // [セーブデータ保存処理]
        GlobalScope.launch {
            val saveData = _changer.listToSaveData(_app.wordList)
            val editor = _prefSaveData.edit()
            var count = 0
            for (dataStr in saveData) {
                count++
                editor.putString(count.toString(), dataStr)
            }
            editor.putString("text", main_etInput.text.toString())
            editor.putInt("count", count)
            editor.apply()
        }

    }


    // [変換の設定ボタン]　押下時の処理
    fun onSettingButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {
        // 遷移
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }


    // [入力クリアボタン]　押下時の処理
    fun onClearInputButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {
        val dialog = ConfirmDialogFragment.newInstance(
            "クリアの確認",
            "入力・出力欄をすべてクリアします",
            CONFIRM_REQUEST_CODE_CLEAR
        )
        dialog.show(supportFragmentManager, "ConfirmDialogFragment")
    }


    // [変換ボタン]　押下時の処理
    fun onChangeButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {

        // 両方の EditText が 空欄　→　処理をしない
        if (main_etInput.text.toString() == "" && main_etOutput.text.toString() == "") return


        // 入力テキスト、出力テキスト
        val inputStr = main_etInput.text.toString()
        var outputStr = inputStr


        /* [エスケープ処理]
         *　各文字の変換を順に処理することにより、すでに変換された文字が、さらに別の文字の変換対象となってしまう問題への対策
         * 1, エスケープ文字の選出(文中で使われていない記号)
         * 2, IDの生成(変換文字と紐づけ)
         * 3, 検索文字→エスケープID　変換
         * 4, エスケープID→置換文字　変換
         */
        val escStr = escapeWordList.stream()
            .filter {
                !inputStr.contains(it)
            }
            .findAny()
            .get()
        val inputToEscMap: LinkedHashMap<String, String> = LinkedHashMap()
        val escToOutputMap: LinkedHashMap<String, String> = LinkedHashMap()
        for (e in _app.wordList) {
            val idStr = escStr + Random.nextInt().toString() + escStr
            inputToEscMap[e.tvInput] = idStr
            escToOutputMap[idStr] = e.tvOutput
        }
        for (e in inputToEscMap) {
            outputStr = outputStr.replace(e.key, e.value)
        }
        for (e in escToOutputMap) {
            outputStr = outputStr.replace(e.key, e.value)
        }


        /* [テキスト表示処理]
         * 1, 変換後のテキストを表示
         * 2, 出力欄を有効化(出力欄の背景を白に)
         */
        main_etOutput.setText(outputStr)
        main_etOutput.isEnabled = true
        main_etOutput.setBackgroundColor(Color.WHITE)
    }


    // [クリップボードにコピーボタン]　押下時の処理
    fun onCopyClipBordButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {
        val cbManager =
            applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cbManager.setPrimaryClip(ClipData.newPlainText("", main_etOutput.text.toString()))
        Toast.makeText(this, getString(R.string.toast_main_copy), Toast.LENGTH_SHORT).show()
    }


    private inner class ListSaveDataChanger {
        private var saveData: MutableList<String>? = null
        private var list: ArrayList<WordRowData>? = null


        fun listToSaveData(list: ArrayList<WordRowData>): MutableList<String> {
            saveData = mutableListOf()
            for (data in list) {
                val builder = StringBuilder()
                builder.append(data.tvInput)
                builder.append("},{")
                builder.append(data.tvOutput)
                saveData!!.add(builder.toString())
            }
            return saveData!!
        }


        fun saveDataToList(saveData: MutableList<String>): ArrayList<WordRowData> {
            list = arrayListOf()
            for (data in saveData) {
                val dataAry = data.split("},{")
                val wordData = WordRowData(dataAry[0], dataAry[1])
                list?.add(wordData)
            }
            return list!!
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val howToMode: Int = when (item.itemId) {
            R.id.menuHowToUseOpening -> {
                HowToUseActivity.HOW_TO_MODE_OPENING
            }
            R.id.menuHowToUseButton -> {
                HowToUseActivity.HOW_TO_MODE_BUTTON
            }
            R.id.menuHowToUseNLSSet -> {
                HowToUseActivity.HOW_TO_MODE_NLSSET
            }
            R.id.menuHowToUsePriority -> {
                HowToUseActivity.HOW_TO_MODE_PRIORITY
            }
            else -> {
                return true
            }
        }

        val intent = Intent(this, HowToUseActivity::class.java).putExtra(
            "howToMode", howToMode
        )
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        return true
    }






    override fun onConfirmClickPositive(requestCode: Int?, otherData: Bundle?) {
        when (requestCode) {
            CONFIRM_REQUEST_CODE_CLEAR -> {
                main_etInput.setText("")
                main_etOutput.setText("")
                main_etOutput.isEnabled = false
                main_etOutput.setBackgroundColor(Color.GRAY)
                main_btnClearInput.isEnabled = false
            }
        }
    }

    override fun onConfirmClickNeutral(requestCode: Int?, otherData: Bundle?) {
    }


}



package com.example.wordchanger

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity(), DisplayListAdapter.OnItemClickListener,
    ConfirmDialogFragment.ConfirmDialogListener {

    companion object {
        const val CONFIRM_REQUEST_CODE_SET_DELETE = 2
        const val CONFIRM_REQUEST_CODE_SET_DELETE_ALL = 3
    }

    private var newLineWord: String? = null
    private var spaceWord: String? = null

    var displayDataList = arrayListOf<WordRowData>()

    private lateinit var _app: MainApplication

    private lateinit var _newLineSpinnerAdapter: ArrayAdapter<String>
    private lateinit var _blankSpinnerAdapter: ArrayAdapter<String>

    private lateinit var _layoutManager: LinearLayoutManager
    private lateinit var _adapter: DisplayListAdapter
    private lateinit var _decorator: DividerItemDecoration
    private lateinit var _touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setSupportActionBar(toolbar)

        MobileAds.initialize(this){}
        adView.loadAd(AdRequest.Builder().build())

        // [Application] オブジェクト生成
        _app = application as MainApplication


        // [改行スピナー]アダプター生成
        _newLineSpinnerAdapter = ArrayAdapter(
            this,
            R.layout.set_spinner_item,
            resources.getStringArray(R.array.set_spinner_newLine)
        )

        // [空白スピナー]アダプター生成
        _blankSpinnerAdapter = ArrayAdapter(
            this,
            R.layout.set_spinner_item,
            resources.getStringArray(R.array.set_spinner_space)
        )


        // [変換リスト] レイアウトマネージャー生成
        _layoutManager = LinearLayoutManager(this)

        // [変換リスト] アダプター生成
        _adapter = DisplayListAdapter(displayDataList, this)

        // [変換リスト] デコレーター(区切り線)生成
        _decorator = DividerItemDecoration(this, _layoutManager.orientation)

        // [変換リスト] タッチヘルパー(並び替え・削除)生成
        _touchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos: Int = viewHolder.adapterPosition
                    val toPos: Int = target.adapterPosition
                    displayDataList.add(toPos, displayDataList.removeAt(fromPos))
                    _adapter.notifyItemMoved(fromPos, toPos)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val fromPos = viewHolder.adapterPosition

                    /*
                     *  -- 確認ダイアログ表示 --
                     */
                    val dialog = ConfirmDialogFragment.newInstance(
                        getString(R.string.cdf_set_title_delete),
                        getString(R.string.cdf_set_message_delete),
                        CONFIRM_REQUEST_CODE_SET_DELETE,
                        Bundle().also {
                            it.putInt("fromPos", fromPos)
                        }
                    )

                    dialog.show(supportFragmentManager, "ConfirmDialogFragment")
                }
            }
        )

    }


    override fun onStart() {
        super.onStart()

        // [スピナー]アダプターにドロップダウンビューをセット
        _newLineSpinnerAdapter.setDropDownViewResource(R.layout.set_spinner_dropdown_item)
        _blankSpinnerAdapter.setDropDownViewResource(R.layout.set_spinner_dropdown_item)

        // [スピナー] アダプターのセット
        spNewLineSetting.adapter = _newLineSpinnerAdapter
        spSpaceSetting.adapter = _blankSpinnerAdapter

        // [改行スピナー] 選択リスナを設定
        spNewLineSetting.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> { // 設定しない
                        invisibleSpinnerEditText(etNewLineSetting)
                        newLineWord = null
                    }
                    1 -> { // 指定文字に変換
                        visibleSpinnerEditText(etNewLineSetting)
                        newLineWord = null
                    }
                    2 -> { // すべて削除
                        invisibleSpinnerEditText(etNewLineSetting)
                        newLineWord = ""
                    }
                    else -> {
                        invisibleSpinnerEditText(etNewLineSetting)
                        newLineWord = null
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // [空白スピナー] 選択リスナの設定
        spSpaceSetting.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //Log.i("checkTanuki", position.toString())
                when (position) {
                    0 -> { // 設定しない
                        invisibleSpinnerEditText(etSpaceSetting)
                        spaceWord = null
                    }
                    1 -> { // 半角統一
                        invisibleSpinnerEditText(etSpaceSetting)
                        spaceWord = " "
                    }
                    2 -> { // 全角統一
                        invisibleSpinnerEditText(etSpaceSetting)
                        spaceWord = "　"
                    }
                    3 -> { // 指定文字に変換
                        visibleSpinnerEditText(etSpaceSetting)
                        spaceWord = null
                    }
                    4 -> { // すべて削除
                        invisibleSpinnerEditText(etSpaceSetting)
                        spaceWord = ""
                    }
                    else -> {
                        invisibleSpinnerEditText(etSpaceSetting)
                        spaceWord = null
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        // [変換リスト] レイアウトマネージャの設定
        rvWordList.layoutManager = _layoutManager

        // [変換リスト] アダプタの設定
        rvWordList.adapter = _adapter

        // [変換リスト] 区切り線の指定
        rvWordList.addItemDecoration(_decorator)

        // [変換リスト] タッチヘルパーの設定（並び替え、削除操作）
        _touchHelper.attachToRecyclerView(rvWordList)



        // [改行・空白の設定] 初期化
        displayDataList.clear()
        _app.wordList.forEach {
            when (it.tvInput) {
                "\n" -> {
                    // [一括設定] 改行データ設定値の反映
                    newLineWord = it.tvOutput
                    if (it.tvOutput == "") {
                        spNewLineSetting.setSelection(2)
                        invisibleSpinnerEditText(etNewLineSetting)
                    } else {
                        spNewLineSetting.setSelection(1)
                        visibleSpinnerEditText(etNewLineSetting)
                        etNewLineSetting.setText(it.tvOutput)
                    }
                }
                " ", "　" -> {
                    // [一括設定] 空白データ設定値の反映
                    spaceWord = it.tvOutput
                    when (it.tvOutput) {
                        "" -> {
                            spSpaceSetting.setSelection(4)
                            invisibleSpinnerEditText(etSpaceSetting)
                        }
                        " " -> {
                            spSpaceSetting.setSelection(1)
                            invisibleSpinnerEditText(etSpaceSetting)
                        }
                        "　" -> {
                            spSpaceSetting.setSelection(2)
                            invisibleSpinnerEditText(etSpaceSetting)
                        }
                        else -> {
                            spSpaceSetting.setSelection(3)
                            visibleSpinnerEditText(etSpaceSetting)
                            etSpaceSetting.setText(it.tvOutput)
                        }
                    }
                }
                else -> {
                    val data = WordRowData(it.tvInput, it.tvOutput)
                    displayDataList.add(data)
                }
            }
        }

    }


    override fun onResume() {
        super.onResume()
        freeze(true)
    }


    override fun onPause() {
        super.onPause()
        freeze(false)
    }


    override fun onStop() {
        super.onStop()


        spNewLineSetting.adapter = null
        spSpaceSetting.adapter = null

        // [改行スピナー] 選択リスナを設定
        spNewLineSetting.onItemSelectedListener = null


        // [空白スピナー] 選択リスナの設定
        spSpaceSetting.onItemSelectedListener = null


        // [変換リスト] レイアウトマネージャの設定
        rvWordList.layoutManager = null

        // [変換リスト] アダプタの設定
        rvWordList.adapter = null

        // [変換リスト] 区切り線の指定
        rvWordList.removeItemDecoration(_decorator)

        // [変換リスト] タッチヘルパーの設定（並び替え、削除操作）
        _touchHelper.attachToRecyclerView(null)

    }


    // [追加ボタン] 押下時の処理
    fun onAddListButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {

        freeze(false)

        //ワード新規登録ダイアログを開く（ダイアログ内でリスト登録＆表示処理まで行う）
        val dialogFragment =
            AddEditWordDialogFragment.newInstance(AddEditWordDialogFragment.ADD_MODE)
        dialogFragment.show(supportFragmentManager, "AddWordDialogFragment")

    }


    // [すべて削除ボタン] 押下時の処理
    fun onDeleteAllButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {


        // 変換文字リストが空の時、処理をしない
        if (rvWordList.isEmpty()) {
            freeze(true)
            return
        }

        //確認ダイアログの生成
        val dialog = ConfirmDialogFragment.newInstance(
            getString(R.string.cdf_set_title_reset),
            getString(R.string.cdf_set_message_reset),
            CONFIRM_REQUEST_CODE_SET_DELETE_ALL
        )

        // ダイアログ表示
        dialog.show(supportFragmentManager, "ConfirmDialogFragment")

    }


    // [保存ボタン] 押下時の処理
    fun onSaveButtonClick(@Suppress("UNUSED_PARAMETER") view: View) {

        // 各ボタン無効
        freeze(false)

        // 新規リスト生成
        val newList = arrayListOf<WordRowData>()

        // 変換リストデータを追加
        newList.addAll(displayDataList)

        // 改行データを追加
        if (etNewLineSetting.text.toString() != "") {
            newList.add(WordRowData("\n", etNewLineSetting.text.toString()))
        } else {
            if (newLineWord == null) {
                newList.removeIf { t ->
                    t.tvInput == "\n"
                }
            } else {
                newList.add(WordRowData("\n", newLineWord!!))
            }
        }

        // 空白データを追加
        if (etSpaceSetting.text.toString() != "") {
            newList.add(WordRowData(" ", etSpaceSetting.text.toString()))
            newList.add(WordRowData("　", etSpaceSetting.text.toString()))
        } else {
            if (spaceWord == null) {
                newList.removeIf { t ->
                    t.tvInput == " "
                }

                newList.removeIf { t ->
                    t.tvInput == "　"
                }
            } else {
                newList.add(WordRowData(" ", spaceWord!!))
                newList.add(WordRowData("　", spaceWord!!))
            }
        }

        // [Application] 内部リストを更新
        _app.wordList = newList

        // 各ボタン有効
        freeze(true)

        finish()
    }


    // [スピナー] EditText入力欄非表示
    private fun invisibleSpinnerEditText(et: EditText) {
        et.isEnabled = false
        et.hint = ""
        et.setText("")
        et.setBackgroundColor(Color.parseColor("#e5ab47"))
    }


    // [スピナー] EditText入力欄表示
    private fun visibleSpinnerEditText(et: EditText) {
        et.isEnabled = true
        et.hint = getString(R.string.set_et_hint_spinner)
        et.setBackgroundColor(Color.parseColor("#ffffff"))
    }


    private fun freeze(flag: Boolean) {
        if (flag) {
            // 有効化
            this@SettingActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            // 無効化
            this@SettingActivity.window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }


    // リスト要素選択時の処理
    override fun onClick(position: Int) {
        val data = displayDataList[position]
        val dialogFragment =
            AddEditWordDialogFragment.newInstance(
                AddEditWordDialogFragment.EDIT_MODE,
                data,
                position
            )
        dialogFragment.show(supportFragmentManager, "AddWordDialogFragment")
    }


    // [オプションメニュー]　生成
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true
    }


    // [オプションメニュー]　選択時
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
            CONFIRM_REQUEST_CODE_SET_DELETE -> {
                val fromPos = otherData?.getInt("fromPos")
                // 内部リストとRecyclerView.adapterから指定要素を削除
                fromPos?.let {
                    displayDataList.removeAt(fromPos)
                    _adapter.notifyItemRemoved(fromPos)
                }
            }
            CONFIRM_REQUEST_CODE_SET_DELETE_ALL -> {
                // [変換リスト] 内部リストを初期化してRecyclerView要素をすべて削除
                val listSize = displayDataList.size
                displayDataList.clear()
                rvWordList.adapter?.notifyItemRangeRemoved(0, listSize)
            }
        }
    }


    override fun onConfirmClickNeutral(requestCode: Int?, otherData: Bundle?) {
        when (requestCode) {
            CONFIRM_REQUEST_CODE_SET_DELETE -> {
                // 更新(これをしないとrowが消えたままになる)
                _adapter.notifyDataSetChanged()
            }
            CONFIRM_REQUEST_CODE_SET_DELETE_ALL -> {
            }
        }
    }


}


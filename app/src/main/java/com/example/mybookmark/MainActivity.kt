package com.example.mybookmark

import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mybookmark.db.model.Mark
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.mybookmark.adapter.MarkGridViewAdapter
import com.example.mybookmark.databinding.ActivityMainBinding
import com.example.mybookmark.databinding.DialogAddMarkBinding
import com.example.mybookmark.model.ComicType
import com.example.mybookmark.service.ParseService
import com.example.mybookmark.view_model.MarkViewModel
import com.example.mybookmark.view_model.MarkViewModelFactory
import android.view.Gravity
import android.widget.Toast






class MainActivity : AppCompatActivity() {

    private lateinit var markViewModelFactory: MarkViewModelFactory

    private lateinit var markViewModel: MarkViewModel
    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: MarkGridViewAdapter

    private var mList: List<Mark>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mAdapter = MarkGridViewAdapter()

        mBinding.gridViewMarks.numColumns = 3
        mBinding.gridViewMarks.adapter = mAdapter
        mBinding.gridViewMarks.onItemClickListener = GridOnItemClickListener;
        

        setViewModel()

        val intent = Intent(this, ParseService::class.java)
        startService(intent)



        mBinding.buttonAdd.setOnClickListener {
//
//            markViewModel.addMark("https://m.tohomh123.com/chongshengzhidoushixiuxian/")
            var item = Mark(url="https://ld3-gc-wl.prdaldbwla1.com/mobile?bucode=ldr-demo-t&token=249384444921196544&language=en-gb&table=0000&lobbyUrl=lobbyUrl&logoutUrl=logoutUrl")
            WebActivity.open(this, item)
//            var intent = Intent(this, WebActivity::class.java)
//            startActivity(intent)
//            showAddMark()
        }

        mBinding.buttonDel.setOnClickListener {
            if(mList != null && mList!!.size > 0) {
                val mark = mList!!.get(mList!!.size - 1)
                markViewModel.delMark(mark)
            }
        }
//        test()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add -> {
            showAddMark()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setViewModel() {

        markViewModelFactory = Injection.provideMarkViewModelFactory(this)
        markViewModel = ViewModelProviders.of(this, markViewModelFactory).get(MarkViewModel::class.java)
        markViewModel.loadMadks()



        markViewModel.getMarksLiveData().observe(this,
            Observer<List<Mark>> { marks ->
                run {
                    mAdapter.updateList(marks)
                    mList = marks
                    Log.d("table List", "" + marks.size + "")
                    Log.d("table List", "" + marks.toString() + "")
                }
            })

        markViewModel.getShowMessageLiveData().observe(this, Observer<String> { string ->
            run {
                showToast(string)
            }
        })
    }

    private val GridOnItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> run{
        Log.d("OnItemClickListener", "" + position + "")
        if(mList != null && mList!!.size > 0) {
            var item = mList!!.get(position);

            WebActivity.open(this, item)
        }
    }}

    fun showAddMark() {
//        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_mark, null)

        val dialogBinding= DialogAddMarkBinding.inflate(LayoutInflater.from(this));

        addBuRadioButtons(dialogBinding.radioComicType)

        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(dialogBinding.root)


        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout

        dialogBinding.buttonConfirm.setOnClickListener {
            run {
                val selectTypeId = dialogBinding.radioComicType.checkedRadioButtonId;
                val selected: View = dialogBinding.radioComicType.findViewById(selectTypeId)
                val comicType: ComicType = selected.tag as ComicType

                val urlString = dialogBinding.editUrl.text


                var mark = Mark(url=urlString.toString(), comicType = comicType.value)
                markViewModel.addMark(mark)
                mAlertDialog.dismiss()
            }
        }
    }

    fun addBuRadioButtons(radioGroup: RadioGroup?) {
        if (radioGroup == null) {
            return
        }

        val defType = ComicType.valueOfEnum(0)

        for (c in ComicType.values()) {
            val radioButton = RadioButton(this)
            radioButton.text = c.type
            radioButton.tag = c

            radioGroup.addView(radioButton)
        }
    }

    fun showToast(text: String) {
        val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

}

package com.siang.wei.mybookmark

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.siang.wei.mybookmark.db.model.Mark
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.siang.wei.mybookmark.adapter.MarkGridViewAdapter
import com.siang.wei.mybookmark.databinding.ActivityMainBinding
import com.siang.wei.mybookmark.databinding.DialogAddMarkBinding
import com.siang.wei.mybookmark.model.ComicType
import com.siang.wei.mybookmark.service.ParseService
import com.siang.wei.mybookmark.view_model.MarkViewModel
import com.siang.wei.mybookmark.view_model.ViewModelFactory
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.NonNull
import com.siang.wei.mybookmark.service.GoogleDriveService
import com.siang.wei.mybookmark.util.AlertUtil
import com.siang.wei.mybookmark.util.PermissionUtil


class MainActivity : AppCompatActivity() {

    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var markViewModel: MarkViewModel
    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: MarkGridViewAdapter
    private lateinit var mGoogleDriveService: GoogleDriveService

    private var mProgressDialog: AlertDialog? = null

    private var mList: List<Mark>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mAdapter = MarkGridViewAdapter()

        mBinding.gridViewMarks.numColumns = 3
        mBinding.gridViewMarks.adapter = mAdapter
        mBinding.gridViewMarks.horizontalSpacing = 20
        mBinding.gridViewMarks.onItemClickListener = mGridOnItemClickListener;
        

        setViewModel()
//        mGoogleDriveService = GoogleDriveService()
//        mGoogleDriveService.loginGoogle(this)

//        val intent = Intent(this, ParseService::class.java)
//        startService(intent)

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

        R.id.action_backup -> {
            backupData()
            true
        }

        R.id.action_refresh -> {
            markViewModel.actionRefreshData()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setViewModel() {

        viewModelFactory = Injection.provideMarkViewModelFactory(this)
        markViewModel = ViewModelProviders.of(this, viewModelFactory).get(MarkViewModel::class.java)
        markViewModel.loadMadks()

        markViewModel.getMarksLiveData().observe(this,
            Observer<List<Mark>> { marks ->
                run {
                    mAdapter.updateList(marks)
                    mList = marks
                }
            })

        markViewModel.getShowMessageLiveData().observe(this, Observer<String> { string ->
            run {
                showToast(string)
            }
        })

        markViewModel.getProgressDialogLiveData().observe(this, Observer<Boolean>{ isShow ->
            if(mProgressDialog == null) {
                mProgressDialog = AlertUtil.showProgressBar(this)
            }

            if(isShow)
                mProgressDialog!!.show()
            else
                mProgressDialog!!.dismiss()
        })
    }

    private val mGridOnItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> run{
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
                var comicType: ComicType? = null
                val selectTypeId = dialogBinding.radioComicType.checkedRadioButtonId;

                if(selectTypeId != null && selectTypeId != -1) {
                    val selected: View = dialogBinding.radioComicType.findViewById(selectTypeId)
                    comicType = selected.tag as ComicType
                }

                val urlString = dialogBinding.editUrl.text
                if(comicType != null && !TextUtils.isEmpty(urlString)) {

                    var mark = Mark(url = urlString.toString(), comicType = comicType.value)
                    markViewModel.addMark(mark)
                    mAlertDialog.dismiss()
                }
            }
        }
    }

    fun addBuRadioButtons(radioGroup: RadioGroup?) {
        if (radioGroup == null) {
            return
        }

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


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GoogleDriveService.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            mGoogleDriveService.onActivityResult(requestCode, resultCode, data!!)

        }
    }
    fun backupData() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!PermissionUtil.needGrantRuntimePermission(
                this,
                permissions,
                PermissionUtil.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE
            )
        ) {
            markViewModel.actionBackup()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtil.PERMISSION_REQUEST_CODE_INIT) {
            Log.d("CheckPath", "Permissions Result!")
            if (PermissionUtil.verifyPermissions(grantResults)) {
                backupData()
            } else {
                AlertUtil.showAlertDialog(
                    this, "請同意啟用權限", android.R.string.ok, android.R.string.cancel,
                    DialogInterface.OnClickListener { dialogInterface, i -> backupData() },
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                        System.exit(10)
                    },
                    false
                )
            }
        }
    }
}

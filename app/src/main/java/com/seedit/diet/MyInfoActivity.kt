package com.seedit.diet

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.gondev.clog.CLog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.seedit.diet.database.repository.Repository
import com.seedit.diet.viewmodel.ProfileViewModel
import com.seedit.diet.viewmodel.ViewModelFactory
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_myinfo.*
import java.util.*

class MyInfoActivity : AppCompatActivity() {
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myinfo)

        val factory=ViewModelFactory(application,Repository.provideProfileDataSource(this))
        viewModel=ViewModelProviders.of(this,factory).get(ProfileViewModel::class.java)
        viewModel.observable.observe(this,android.arch.lifecycle.Observer {
            //TODO 화면 갱신
            CLog.i("화면 갱신 profile data size=${it?.size?:0}")
            it?.let {
                if(it.isNotEmpty())
                {
                    CLog.d("profile=${it[0]}")
                    editName.setText(it[0].name, TextView.BufferType.EDITABLE)
                }
            }
        })

        // profile
        profile_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                TedPermission.with(this)
                        .setPermissionListener(object : PermissionListener {
                            override fun onPermissionGranted() {
                                openImagePicker()
                            }

                            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                            }
                        })
                        .setRationaleMessage("프로필 설정을 위해여 갤러리 접근 권한이 필요 합니다")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check()
                return@setOnClickListener
            }

            openImagePicker()
        }

        // spinner
        ArrayAdapter.createFromResource(this,
                R.array.string_array_gender, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = this
        }

        // edittext birthday
        View.OnClickListener{
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                    object :DatePickerDialog.OnDateSetListener{
                        override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                            (it as EditText).text= SpannableStringBuilder(String.format("%d년 %d월 %d일",year,monthOfYear,dayOfMonth))
                        }
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            )
            when(it)
            {
                editBirthday->dpd.maxDate = now
                editDiatTarget->dpd.minDate = now
            }
            dpd.show(fragmentManager, "Datepickerdialog")
        }.let {
            editBirthday.setOnClickListener(it)
            editDiatTarget.setOnClickListener(it)
        }

        // weight, height
        object :TextWatcher
        {
            override fun afterTextChanged(s: Editable?) {
                try {
                    val weight = editWeight.text.toString().toInt()
                    val height = editHeight.text.toString().toInt()

                    val bmi = (weight.toFloat() /(height.toFloat()*height.toFloat()))* 10000
                    txtSeekbarTooltip.text = String.format("%.2f", bmi)
                    var process=0
                    if(bmi<18.5)
                        process= getProgress(bmi,0f,18.5f)
                    else if(bmi<23)
                        process= getProgress(bmi,18.5f,23f)+20
                    else if(bmi<25)
                        process= getProgress(bmi,23f,25f)+40
                    else if(bmi<30)
                        process= getProgress(bmi,25f,30f)+60
                    else
                        process= getProgress(bmi,30f,40f)+80
                    seekBar.progress = process
                }
                catch (e:NumberFormatException)
                {}
            }

            fun getProgress(bmi:Float,min:Float,max:Float): Int {
                return ((bmi-min)*20/(max-min)).toInt()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }.let {
            editHeight.addTextChangedListener(it)
            editWeight.addTextChangedListener(it)
        }

        // seekbar
        seekBar.setOnTouchListener { v, event ->true}
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBar?.getThumb()?.getBounds()?.let {
                    //set the left value to textview x value
                    val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
                    txtSeekbarTooltip.x = (it.left+it.width()/2-txtSeekbarTooltip.width/2+px).toFloat()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    fun openImagePicker()
    {
        val bottomSheetDialogFragment = TedBottomPicker.Builder(this)
                .setOnImageSelectedListener(object : TedBottomPicker.OnImageSelectedListener {
                    override fun onImageSelected(uri: Uri) {
                        Glide.with(this@MyInfoActivity)
                                .load(uri)
                                .into(profile_image)

                    }
                })
                .create()

        bottomSheetDialogFragment.show(supportFragmentManager)
    }

    override fun onBackPressed() {
        val dlg=AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    super.onBackPressed()
                }
                .setNegativeButton(android.R.string.no,null)

        if(editName.text.isEmpty())
        {
            dlg.setMessage("이름을 입력 하지 않았습니다 종료 하시겠습니까?").show()
            return
        }
        /*val profile=viewModel.getProfile()?: ProfileEntity(0)
        profile.name=editName.text.toString()

        viewModel.setPorfile(profile)*/
        viewModel.getProfile()?.apply {
            name=editName.text.toString()
            viewModel.insert(this)
        }
        super.onBackPressed()

    }
}

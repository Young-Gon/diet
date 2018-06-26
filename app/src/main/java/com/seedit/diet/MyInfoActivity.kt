package com.seedit.diet

import android.Manifest
import android.app.Activity
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.gondev.clog.CLog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.seedit.diet.database.entity.ProfileEntity
import com.seedit.diet.database.repository.Repository
import com.seedit.diet.viewmodel.ProfileViewModel
import com.seedit.diet.viewmodel.ViewModelFactory
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_myinfo.*
import java.text.SimpleDateFormat
import java.util.*

class MyInfoActivity : AppCompatActivity() {
    private lateinit var viewModel: ProfileViewModel
    private val sdf= SimpleDateFormat("yyyy년 MM월 dd일")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myinfo)

        val factory=ViewModelFactory(application,Repository.provideProfileDataSource(this))
        viewModel=ViewModelProviders.of(this,factory).get(ProfileViewModel::class.java)
        viewModel.observable.observe(this,android.arch.lifecycle.Observer {
            //TODO 화면 갱신
            CLog.i("화면 갱신 profile data size=${it?.size?:0}")
            it?.let {
                if(it.isEmpty())
                    viewModel.insert(ProfileEntity(0))
                else
                //if(it.isNotEmpty())
                {
                    CLog.d("profile=${it[0]}")

                    if(it[0].profile_image!=null)
                    Glide.with(this@MyInfoActivity)
                            .load(it[0].profile_image)
                            .into(profile_image)

                    editName.setText(it[0].name)
                    spinner.setSelection(it[0].gender,false)
                    if(it[0].birthday.time>0)
                        editBirthday.setText(sdf.format(it[0].birthday))

                    if(it[0].weight>0)
                        editWeight.setText(it[0].weight.toString())

                    if(it[0].height>0)
                        editHeight.setText(it[0].height.toString())

                    if(it[0].targetWeight>0)
                        dietWeight.setText(it[0].targetWeight.toString())

                    if(it[0].targetWorkout>0)
                        editDayWorkout.setText(it[0].targetWorkout.toString())

                    if(it[0].targetDiet>0)
                        editDayDiet.setText(it[0].targetDiet.toString())

                    if(it[0].targetWater>0)
                        editWater.setText(it[0].targetWater.toString())

                    if(it[0].targetDday.time>0)
                        editDiatTarget.setText(sdf.format(it[0].targetDday))
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
            spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.getProfile()?.gender=position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }

        // edittext birthday
        View.OnClickListener{
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                    object :DatePickerDialog.OnDateSetListener{
                        override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                            (it as EditText).text= SpannableStringBuilder(String.format("%d년 %d월 %d일",year,monthOfYear,dayOfMonth))

                            Calendar.getInstance().apply {
                                set(year, monthOfYear, dayOfMonth)
                                when(it) {
                                    editBirthday -> viewModel.getProfile()?.birthday=time
                                    editDiatTarget -> viewModel.getProfile()?.targetDday=time
                                }
                            }
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

                    viewModel.getProfile()?.weight=weight
                    viewModel.getProfile()?.height=height


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
                seekBar?.thumb?.bounds?.let {
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

                        viewModel.getProfile()?.profile_image=uri
                    }
                })
                .create()

        bottomSheetDialogFragment.show(supportFragmentManager)
    }

    override fun onBackPressed() {
        val dlg=AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    setResult(Activity.RESULT_CANCELED)
                    update()
                }
                .setNegativeButton(android.R.string.no,null)

        if(editName.text.isEmpty())
        {
            /*dlg.setMessage("이름을 입력 하지 않았습니다 종료 하시겠습니까?")
                    .setNegativeButton(android.R.string.no){dialog, which ->
                        editName.requestFocus()
                        editName.selectAll()
                    }
                    .show()*/
            showDlg(dlg,"이름을",editName)
            return
        }
        viewModel.getProfile()?.name=editName.text.toString()
        if(editWeight.text.isEmpty())
        {
            showDlg(dlg,"몸무게를",editWeight)
            return
        }
        viewModel.getProfile()?.weight=editWeight.text.toString().toInt()
        if(editHeight.text.isEmpty())
        {
            showDlg(dlg,"키를",editHeight)
            return
        }
        viewModel.getProfile()?.height=editHeight.text.toString().toInt()
        if(editBirthday.text.isEmpty())
        {
            showDlg(dlg,"생일을",editBirthday)
            return
        }
        if(dietWeight.text.isEmpty())
        {
            showDlg(dlg,"목표 몸무게를",dietWeight)
            return
        }
        viewModel.getProfile()?.targetWeight=dietWeight.text.toString().toInt()
        if(editDayWorkout.text.isEmpty())
        {
            showDlg(dlg,"일일 목표 운동량을",editDayWorkout)
            return
        }
        viewModel.getProfile()?.targetWorkout=editDayWorkout.text.toString().toInt()
        if(editDayDiet.text.isEmpty())
        {
            showDlg(dlg,"일일 목표 섭취량을",editDayDiet)
            return
        }
        viewModel.getProfile()?.targetDiet=editDayDiet.text.toString().toInt()
        if(editWater.text.isEmpty())
        {
            showDlg(dlg,"일일 수분 섭취량을",editWater)
            return
        }
        viewModel.getProfile()?.targetWater=editWater.text.toString().toInt()
        if(editDiatTarget.text.isEmpty())
        {
            showDlg(dlg,"다이어트 목표일을",editDiatTarget)
            return
        }

        update()
    }

    fun update()
    {
        viewModel.update()
        viewModel.observable.observe(this,android.arch.lifecycle.Observer {
            super.onBackPressed()
        })
    }

    fun showDlg(dlg: AlertDialog.Builder, subject: String, editText: EditText)
    {
        dlg.setMessage("$subject 입력 하지 않았습니다 종료 하시겠습니까?")
                .setNegativeButton(android.R.string.no){dialog, which ->
                    editText.requestFocus()
                    editText.selectAll()
                }
                .show()
    }
}

package com.seedit.diet.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.seedit.diat.util.SimpleAnimationListener
import com.seedit.diet.R
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.viewmodel.ProfileViewModel
import com.seedit.diet.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.fragment_base.view.*
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseFragment : Fragment(), View.OnClickListener {

    private val calendar=Calendar.getInstance()
    fun getCurrentCalender()=calendar.clone() as Calendar

    private var listener: OnFragmentInteractionListener? = null
    private val sdf=SimpleDateFormat("yyyy년 MM월 dd일")
    protected lateinit var profileViewModel: ProfileViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory= ViewModelFactory(activity?.application!!, AppDatabase.getInstance(context!!))
        profileViewModel= ViewModelProviders.of(this,factory).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
         inflater.inflate(R.layout.fragment_base, container, false).apply {
             val attachView=inflater.inflate(getContentLayoutRes(), fragmentContainer, false)
             fragmentContainer.addView(attachView)
         }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPrev.setOnClickListener(this)
        btnNext.setOnClickListener(this)

        txtDate.text = sdf.format(calendar.time)
        onContentViewCreated(view,calendar)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnPrev->onClickPrev(v)
            R.id.btnNext->onClickNext(v)
        }
    }

    fun onClickPrev(v:View)
    {
        calendar.add(Calendar.DATE, -1)

        txtDate.text = sdf.format(calendar.time)

        val attachView=LayoutInflater.from(context).inflate(getContentLayoutRes(),fragmentContainer,false)
        val detachView=fragmentContainer.getChildAt(0)
        fragmentContainer.addView(attachView,0)
        onContentViewCreated(attachView,calendar)

        val anim= AnimationUtils.loadAnimation(context,R.anim.slide_out_right)
        anim.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationEnd(animation: Animation?) {
                fragmentContainer.removeView(detachView)
            }
        })
        detachView.startAnimation(anim)

        val anim1= AnimationUtils.loadAnimation(context,R.anim.slide_in_left)
        attachView.startAnimation(anim1)
    }

    open fun onClickNext(v:View){
        calendar.add(Calendar.DATE, 1)

        txtDate.text = sdf.format(calendar.time)

        val detachView=fragmentContainer.getChildAt(0)
        val attachView=LayoutInflater.from(context).inflate(getContentLayoutRes(),fragmentContainer,false)
        fragmentContainer.addView(attachView,0)
        onContentViewCreated(attachView, calendar.clone() as Calendar)

        val anim=AnimationUtils.loadAnimation(context,R.anim.slide_out_left)
        anim.setAnimationListener(object : SimpleAnimationListener() {
            override fun onAnimationEnd(animation: Animation?) {
                fragmentContainer.removeView(detachView)
            }
        })
        detachView.startAnimation(anim)

        val anim1=AnimationUtils.loadAnimation(context,R.anim.slide_in_right)
        attachView.startAnimation(anim1)
    }

    fun getAttachView() = fragmentContainer.getChildAt(0)

    @LayoutRes
    abstract fun getContentLayoutRes():Int

    abstract fun onContentViewCreated(view: View, calendar: Calendar)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

}

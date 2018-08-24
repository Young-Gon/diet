package com.seedit.diet

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.gondev.clog.CLog
import com.idescout.sql.SqlScoutServer
import com.seedit.diet.database.AppDatabase
import com.seedit.diet.fragment.BaseFragment
import com.seedit.diet.fragment.DietFragment
import com.seedit.diet.fragment.SummaryFragment
import com.seedit.diet.fragment.WorkoutFragment
import com.seedit.diet.viewmodel.ProfileViewModel
import com.seedit.diet.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, BaseFragment.OnFragmentInteractionListener {

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    protected lateinit var viewModel: ProfileViewModel

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                container.setCurrentItem(0,true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                container.setCurrentItem(1,true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                container.setCurrentItem(2,true)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SqlScoutServer.create(this, getPackageName());
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val factory= ViewModelFactory(application, AppDatabase.getInstance(this))
        viewModel= ViewModelProviders.of(this,factory).get(ProfileViewModel::class.java)
        viewModel.observable.observe(this,android.arch.lifecycle.Observer {
            if(it==null || it.size==0)
                startActivity(Intent(this,MyInfoActivity::class.java))
            else
            {
                // TODO 드로어 메뉴 프로필 초기화
	            with(profileMenu.getHeaderView(0)) {
		            if (it[0].profile_image != null) {
			            Glide.with(this)
					            .load(it[0].profile_image)
					            .thumbnail(0.1f)
					            .into(imgFood)
		            }

		            val sdf=SimpleDateFormat("yyyy년 MM월 dd일")
		            userName.text=it[0].name
		            userInfo.text="${sdf.format(it[0].birthday)}\n${it[0].height}cm ${it[0].weight}Kg\n다이어트 돌입 ${(it[0].startDate.time - Date().time)/8640000+1}일 째"
	            }
            }
        })


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)


        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabbar.selectedItemId = tabbar.menu.getItem(position).itemId

            }
        })
        tabbar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        profileMenu.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this,MyInfoActivity::class.java))
            }
            R.id.nav_Analyze_diet -> {
	            startActivity(Intent(this,AnalyzeDietActivity::class.java))
            }
            R.id.nav_noon_body -> {
                startActivity(Intent(this,NoonBodyActivity::class.java))
            }
	        R.id.nab_community -> {

	        }
	        R.id.nav_help -> {

	        }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            CLog.i("position=$position")
            return when (position) {
                0 -> SummaryFragment()
                1 -> DietFragment()
                else -> WorkoutFragment()
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }
}

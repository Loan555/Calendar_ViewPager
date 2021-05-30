package com.loan555.myviewpager

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.navigation.NavigationView
import com.loan555.myviewpager.fragment.HomeFragment
import com.loan555.myviewpager.fragment.ToDoListFragment
import com.loan555.myviewpager.fragment.mDataNoteList
import com.loan555.myviewpager.model.AppPreferences
import com.loan555.myviewpager.model.CalendarDateModel
import com.loan555.myviewpager.model.DataNoteItem
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val HOME_TAG = "home"
const val LIST_TAG = "list"
const val DETAIL_TAG = "detail"

var lastSelectDateTime = CalendarDateModel(Calendar.getInstance().time)
var startPosition = 500
var currentPosition = startPosition
var lastDoubleTabPosition = CalendarDateModel(Calendar.getInstance().time)
var dayToChose = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        initBody()
        initDrawLayout()
        eventDrawLayout()
    }

    private fun initDrawLayout() {
        val navigationView = findViewById<NavigationView>(R.id.draw_layout_menu)
        val viewHeader = navigationView.getHeaderView(0)
        var image: ImageView = viewHeader.findViewById(R.id.nav_header_imageView)
        image.setImageResource(R.drawable.bg_3)

        var name: TextView = viewHeader.findViewById(R.id.nav_header_textView)
        AppPreferences.init(this)
        name.text = AppPreferences.userName
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_layout)

        val yesBtn = dialog.findViewById(R.id.okBtn) as TextView
        val noBtn = dialog.findViewById(R.id.cancelBtn) as TextView
        val editPass = dialog.findViewById(R.id.editTextTextPassword) as EditText
        val editName = dialog.findViewById(R.id.user_name) as EditText

        yesBtn.setOnClickListener {
            if (editPass.text.toString() != "") {

                AppPreferences.init(this)
                AppPreferences.password = editPass.text.toString()
                AppPreferences.userName = editName.text.toString()
                dialog.dismiss()

                Toast.makeText(
                    this,
                    "Success",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    private fun initBody() {
        goToHome()
    }

    private fun goToHome() {
        // neu chua co thi tao moi, co roi thi back lai
        if (supportFragmentManager.backStackEntryCount <= 0) {
            supportFragmentManager.commit {
                replace<HomeFragment>(R.id.fragment_view_main)
                setReorderingAllowed(true)
                addToBackStack(HOME_TAG)
            }
        } else {
            supportFragmentManager.popBackStack(HOME_TAG, 0)
        }
        draw_layout_menu.menu.findItem(R.id.nav_home).isChecked = true
        printBackStack()
    }

    private fun goToList() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            if (supportFragmentManager.getBackStackEntryAt(i).name == LIST_TAG) {
                supportFragmentManager.popBackStack(LIST_TAG, 0)
                draw_layout_menu.menu.findItem(R.id.nav_list_to_do).isChecked = true
                return
            }
        }
        supportFragmentManager.commit {
            replace<ToDoListFragment>(R.id.fragment_view_main)
            setReorderingAllowed(true)
            addToBackStack(LIST_TAG)
        }
        draw_layout_menu.menu.findItem(R.id.nav_list_to_do).isChecked = true
        printBackStack()
    }

    private fun printBackStack() {
        for (i in 0 until supportFragmentManager.backStackEntryCount) {
            Log.d(
                "backstack",
                "back stac at $i = ${supportFragmentManager.getBackStackEntryAt(i).name}"
            )
        }
    }

    private fun eventDrawLayout() {
        draw_layout_menu.setNavigationItemSelectedListener {
            it.isChecked = true
            draw_layout.closeDrawers()
            when (it.itemId) {
                R.id.nav_home -> {
                    goToHome()
                }
                R.id.nav_list_to_do -> {
                    goToList()
                }
            }
            true
        }
    }

    private fun initToolbar() {
        setSupportActionBar(action_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu , menu)
        Log.d("aaa", "chay den day diiiiiiiiii")

        val search = menu?.findItem(R.id.menu_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
               return when (supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name) {
                    HOME_TAG -> {
                        Log.d("aaa", "search home submit")
                        true
                    }
                    LIST_TAG ->{
                        Log.d("aaa", "search list submit")
                        true
                    }
                    DETAIL_TAG -> {
                        Log.d("aaa", "search detail submit")
                        true
                    }
                   else -> false
                }

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return when (supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name) {
                    HOME_TAG -> {
                        Log.d("aaa", "search home change")
                        true
                    }
                    LIST_TAG ->{
                        Log.d("aaa", "search list change")
                        true
                    }
                    DETAIL_TAG -> {
                        Log.d("aaa", "search detail change")
                        true
                    }
                    else -> false
                }
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                draw_layout.openDrawer(GravityCompat.START)
                true
            }
            R.id.menu_search -> {

                true
            }
            R.id.menu_backup -> {
                // back up
                true
            }
            R.id.menu_reset_pass -> {
                showDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun search(text: String) {

    }

    override fun onBackPressed() {
        if (draw_layout.isDrawerOpen(draw_layout_menu)) {
            draw_layout.closeDrawers()
        } else
            when (supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name) {
                LIST_TAG -> goToHome()
                DETAIL_TAG -> goToList()
                HOME_TAG -> finish()
                else -> super.onBackPressed()
            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initData() {
        val id = mDataNoteList.getNewId()
        val cal1 = Calendar.getInstance()
        for (i in 0..20) {
            var cal = cal1
            cal.add(Calendar.DAY_OF_MONTH, Random().nextInt(20))
            mDataNoteList.addItem(DataNoteItem(id, CalendarDateModel(cal.time), "head", "body"))
        }
        mDataNoteList.addItem(
            DataNoteItem(
                id,
                CalendarDateModel(Calendar.getInstance().time),
                "head",
                "body"
            )
        )
        mDataNoteList.sortByDateDescending()
    }

}
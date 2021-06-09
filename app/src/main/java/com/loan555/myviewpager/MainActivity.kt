package com.loan555.myviewpager

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.commit
import com.google.android.material.navigation.NavigationView
import com.loan555.myviewpager.fragment.HomeFragment
import com.loan555.myviewpager.fragment.ToDoListFragment
import com.loan555.myviewpager.model.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

const val HOME_TAG = "home"
const val LIST_TAG = "list"
const val DETAIL_TAG = "detail"
const val startPosition = 500

var lastSelectDateTime = CalendarDateModel(Calendar.getInstance().time, false)
var lastDoubleTabPosition = lastSelectDateTime

class MainActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job

    private var dataNoteList = DataNoteList(this)
    private val storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val mBackupData = BackupData(this, this, storagePermission, dataNoteList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        setContentView(R.layout.activity_main)

        initToolbar()
        initDrawLayout()
        initBody()
        //doc du lieu
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val data = async(Dispatchers.IO) {
                    dataNoteList.readToList()                // read data to List
                }
                // load lai du lieu ngay khi doc xong
                reload(data.await())
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error read data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkCurrentFragment(): Int {
        return when (supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name) {
            HOME_TAG -> 1
            LIST_TAG -> 2
            DETAIL_TAG -> 3
            else -> 0
        }
    }

    private fun reload(size: Int) {
        when (checkCurrentFragment()) {
            1 -> {
                view_pager2.adapter?.notifyItemChanged(view_pager2.currentItem)
            }
            2 -> {
                recycler_listNote.adapter?.notifyDataSetChanged()
            }
        }
        Toast.makeText(this, "list size = $size", Toast.LENGTH_LONG).show()
    }

    private fun initToolbar() {
        setSupportActionBar(action_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
    }

    private fun initDrawLayout() {
        val navigationView = findViewById<NavigationView>(R.id.draw_layout_menu)
        val viewHeader = navigationView.getHeaderView(0)
        var image: ImageView = viewHeader.findViewById(R.id.nav_header_imageView)
        image.setImageResource(R.drawable.bg_3)

        var name: TextView = viewHeader.findViewById(R.id.nav_header_textView)
        AppPreferences.init(this)
        name.text = AppPreferences.userName

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

    private fun initBody() {
        goToHome() // home is start
    }

    /**
     * Dialog will show to reset password
     */
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

    private fun goToHome() {
        // neu chua co thi tao moi, co roi thi back lai
        if (supportFragmentManager.backStackEntryCount <= 0) {
            supportFragmentManager.commit {
                val homeFragment = HomeFragment(dataNoteList)
                replace(R.id.fragment_view_main, homeFragment)
                setReorderingAllowed(true)
                addToBackStack(HOME_TAG)
            }
        } else {
            supportFragmentManager.popBackStack(HOME_TAG, 0)
        }
        draw_layout_menu.menu.findItem(R.id.nav_home).isChecked = true
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
            val mToDoListFragment = ToDoListFragment(dataNoteList)
            replace(R.id.fragment_view_main, mToDoListFragment)
            setReorderingAllowed(true)
            addToBackStack(LIST_TAG)
        }
        draw_layout_menu.menu.findItem(R.id.nav_list_to_do).isChecked = true
    }

    override fun onDestroy() {
        dataNoteList.closeDatabase() // close database when app close
        job.cancel()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                draw_layout.openDrawer(GravityCompat.START)
                true
            }
            R.id.menu_backup -> {
                // back up / export
                if (mBackupData.checkStoragePermission()) {
                    //permission allowed
                    mBackupData.exportCSV()
                } else {
                    //permission not allowed
                    mBackupData.requestStoragePermissionExport()
                }
                true
            }
            R.id.menu_import -> {
                //import
                if (mBackupData.checkStoragePermission()) {
                    //permission allowed
                    GlobalScope.launch(Dispatchers.Main) {
                        val messageResult = async(Dispatchers.IO) {
                            return@async mBackupData.importCSV()
                        }
                        Toast.makeText(
                            this@MainActivity,
                            "${messageResult.await()}",
                            Toast.LENGTH_LONG
                        ).show()
                        if (messageResult.await()
                                .contains("success")
                        ) {// neu thanh cong thi load lai
                            when (supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name) {
                                HOME_TAG -> view_pager2.adapter?.notifyDataSetChanged()
                                LIST_TAG -> recycler_listNote.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                } else {
                    //permission not allowed
                    mBackupData.requestStoragePermissionImport()
                }
                true
            }
            R.id.menu_reset_pass -> {
                showDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (draw_layout.isDrawerOpen(draw_layout_menu)) {
            draw_layout.closeDrawers()
        } else
            when (supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name) {
                LIST_TAG -> goToHome()
                DETAIL_TAG -> supportFragmentManager.popBackStack()
                HOME_TAG -> finish()
                else -> super.onBackPressed()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_REQUEST_CODE_EXPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    mBackupData.exportCSV()
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            STORAGE_REQUEST_CODE_IMPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    mBackupData.importCSV()
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}
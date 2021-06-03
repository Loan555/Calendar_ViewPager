package com.loan555.myviewpager

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.commit
import com.google.android.material.navigation.NavigationView
import com.loan555.myviewpager.fragment.HomeFragment
import com.loan555.myviewpager.fragment.ToDoListFragment
import com.loan555.myviewpager.model.AppPreferences
import com.loan555.myviewpager.model.CalendarDateModel
import com.loan555.myviewpager.model.DataNoteItem
import com.loan555.myviewpager.model.DataNoteList
import com.opencsv.CSVReader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.Reader
import java.text.SimpleDateFormat
import java.util.*

const val HOME_TAG = "home"
const val LIST_TAG = "list"
const val DETAIL_TAG = "detail"

var lastSelectDateTime = CalendarDateModel(Calendar.getInstance().time, false)
var startPosition = 500
var currentPosition = startPosition
var lastDoubleTabPosition = CalendarDateModel(Calendar.getInstance().time, false)
var dayToChose = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

class MainActivity : AppCompatActivity() {
    private var dataNoteList = DataNoteList(this)

    /*---------For storage permission---------- */
    private val STORAGE_REQUEST_CODE_EXPORT = 1
    private val STORAGE_REQUEST_CODE_IMPORT = 2
    lateinit var storagePermission: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataNoteList.readToList() // read data to List
        initToolbar()
        initDrawLayout()
        initBody()
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun code(str: String): String {
        var codeText = ""
        str.forEach {
            codeText += (it.toInt() + 45).toChar()
        }
        return codeText
    }

    private fun encode(str: String): String {
        var encodeText = ""
        str.forEach {
            encodeText += (it.toInt() - 45).toChar()
        }
        return encodeText
    }

    private fun checkStoragePermission(): Boolean {
        Log.e("permission", "check permission")
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
    }

    private fun requestStoragePermissionImport() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE_IMPORT)
    }

    private fun requestStoragePermissionExport() {
        Log.e("permission", "request export permission")
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE_EXPORT)
    }

    private fun exportCSV() {
        val folder =
            File("${Environment.getExternalStorageDirectory()}/SQLiteBackup") //SQLiteBackup is folder name
        var isFolderCreated = false
        if (!folder.exists())
            isFolderCreated = folder.mkdir()
        Log.e("permission", "exportCSV $isFolderCreated")
        //file name
        val csvFileName = "SQLite_Backup.csv"
        //complete path and name
        val filePathAndName = "$folder/$csvFileName"
        //get records
        try {
            //write csv file
            var fw = FileWriter(filePathAndName)
            dataNoteList.getList().forEach {
                fw.append(it.noteId.toString())
                fw.append(",")
                fw.append(SimpleDateFormat("yyyy/MM/dd").format(it.date.data))
                fw.append(",")
                fw.append(code(it.titleHead))
                fw.append(",")
                fw.append(code(it.titleBody))
                fw.append("\n")
            }
            fw.flush()
            fw.close()
            Toast.makeText(this, "Backup exported to: $filePathAndName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Backup error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importCSV() {
        //use same path and file name to import
        val folderPathAndName =
            "${Environment.getExternalStorageDirectory()}/SQLiteBackup/SQLite_Backup.csv"
        var csvFile = File(folderPathAndName)
        if (csvFile.exists()) {
            try {
                dataNoteList.clear()
                csvFile.forEachLine { it ->
                    var column = 0
                    var ids = ""
                    var date = ""
                    var titleHead = ""
                    var titleBody = ""
                    it.forEach {
                        if (it == ',') {
                            column++
                            return@forEach
                        }
                        when (column) {
                            0 -> ids += it
                            1 -> date += it
                            2 -> titleHead += it
                            3 -> titleBody += it
                        }
                    }
                    val mDataNoteItem = DataNoteItem(
                        ids.toLong(),
                        CalendarDateModel(SimpleDateFormat("yyyy/MM/dd").parse(date), true),
                        encode(titleHead),
                        encode(titleBody)
                    )
                    dataNoteList.addItem(mDataNoteItem)
                }
                Toast.makeText(this, "Backup restore... ", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Import error: ${e.message} ", Toast.LENGTH_SHORT).show()
            }
        }
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
                if (checkStoragePermission()) {
                    //permission allowed
                    exportCSV()
                } else {
                    //permission not allowed
                    requestStoragePermissionExport()
                }
                true
            }
            R.id.menu_import -> {
                //import
                if (checkStoragePermission()) {
                    //permission allowed
                    importCSV()
                    when (supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name) {
                        HOME_TAG -> view_pager2.adapter?.notifyDataSetChanged()
                        LIST_TAG -> recycler_listNote.adapter?.notifyDataSetChanged()
                    }
                } else {
                    //permission not allowed
                    requestStoragePermissionImport()
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
                DETAIL_TAG -> goToList()
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
                    exportCSV()
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            STORAGE_REQUEST_CODE_IMPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    importCSV()
                } else {
                    //permission denied
                    Toast.makeText(this, "Storage permission required...", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}
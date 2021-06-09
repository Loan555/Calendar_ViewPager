package com.loan555.myviewpager.model

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat

const val STORAGE_REQUEST_CODE_EXPORT = 1
const val STORAGE_REQUEST_CODE_IMPORT = 2

class BackupData(
    private val activity: Activity,
    private val context: Context,
    var storagePermission: Array<String>,
    var dataNoteList: DataNoteList
) {
    private val nameFolder = "SQLiteBackup"
    private val csvFileName = "SQLite_Backup.csv"
    private val folderBackup = "${Environment.getExternalStorageDirectory()}/$nameFolder"
    /*---------For storage permission---------- */

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

    fun checkStoragePermission(): Boolean {
        Log.e("permission", "check permission")
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
    }

    fun requestStoragePermissionImport() {
        ActivityCompat.requestPermissions(activity, storagePermission, STORAGE_REQUEST_CODE_IMPORT)
    }

    fun requestStoragePermissionExport() {
        Log.e("permission", "request export permission")
        ActivityCompat.requestPermissions(activity, storagePermission, STORAGE_REQUEST_CODE_EXPORT)
    }

    fun exportCSV() {
        GlobalScope.launch(Dispatchers.Main) {
            val messageResult = async(Dispatchers.IO) {
                var message = ""
                val folder =
                    File(folderBackup)
                var isFolderCreated = false
                if (!folder.exists())
                    isFolderCreated = folder.mkdir()
                Log.e("permission", "exportCSV $isFolderCreated")
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
                    message = "Backup exported to: $filePathAndName"
                } catch (e: Exception) {
                    Log.d("aaa", "Backup error: ${e.message}")
                    message = "Backup error: ${e.message}"
                }
                return@async message
            }
            Toast.makeText(context, "${messageResult.await()}", Toast.LENGTH_LONG).show()
        }
    }

    fun importCSV(): String {

        Thread.sleep(10000)
        // cho nay khong dung coroutin vi con load lai du lieu, dung coroutin de goi ham nay
        var message = ""
        //use same path and file name to import
        val folderPathAndName = "$folderBackup/$csvFileName"
        var csvFile = File(folderPathAndName)
        if (csvFile.exists()) {
            try {
                dataNoteList.clear() // xoa toan bo du lieu cu
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
                message = "Restore success from: $folderPathAndName"
                Log.e("eee", message)
            } catch (e: Exception) {
                message = "Restore error: ${e.message}"
                Log.e("eee", message)
            }
        } else message = "File backup not found!"
        return message
    }
}
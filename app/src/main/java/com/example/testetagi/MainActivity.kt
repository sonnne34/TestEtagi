package com.example.testetagi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.testetagi.fragments.MainFragment
import com.example.testetagi.interfaces.ConnectionType
import com.example.testetagi.interfaces.NetworkMonitorUtil
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private val networkMonitor = NetworkMonitorUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        //проверка подключения к интернету
        networkMonitorResult()
        //инизиализируем бд
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

    }

    //диалоговое окно перед выходом из приложения
    fun openExitDialog(context: Context) {
        val quitDialog = AlertDialog.Builder(
            context
        )
        quitDialog.setTitle("Выход")
        quitDialog.setTitle("Вы уверенны, что хотите закрыть приложение?")
        quitDialog.setPositiveButton(
            "Да"
        ) { _, _ ->
            onDestroy()
        }
        quitDialog.setNegativeButton(
            "Ой, нет!"
        ) { _, _ -> }
        quitDialog.show()
    }

    //проверка подключения к интернету
    private fun  networkMonitorResult(){
        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                Log.i("NETWORK_MONITOR_STATUS", "Wifi Connection")
                            }
                            ConnectionType.Cellular -> {
                                Log.i("NETWORK_MONITOR_STATUS", "Cellular Connection")
                            }
                            else -> { }
                        }
                    }
                    false -> {
                        Log.i("NETWORK_MONITOR_STATUS", "No Connection")
                        Toast.makeText(applicationContext,
                            "Проверьте подключение к интернету",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //проверка подключения к интернету
        networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        //остановка проверки подключения к интернету
        networkMonitor.unregister()
    }

    //переопределения метода для обработки возвращения к фрагментам
    override fun onBackPressed() {
        //проверяем наличие фрагментов в стеке
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            //открывем диалог о выходе
            openExitDialog(this)
        } else {
            //возвращаемся к пред. фрагменту
            supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        moveTaskToBack(true)
        super.onDestroy()
        exitProcess(0)
    }

}
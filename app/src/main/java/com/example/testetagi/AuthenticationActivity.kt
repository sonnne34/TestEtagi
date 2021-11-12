package com.example.testetagi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testetagi.databinding.ActivityAuthenticationBinding
import com.example.testetagi.interfaces.ConnectionType
import com.example.testetagi.interfaces.NetworkMonitorUtil
import com.example.testetagi.singletons.LoginSingleton
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private var mAuth: FirebaseAuth? = null

    private lateinit var pbLoading: ProgressBar
    private lateinit var editUserName: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnEnter: Button

    private val LOGIN = "LOGIN"
    private lateinit var sLogin: SharedPreferences
    private var saveTextLogin: String? = null

    private val networkMonitor = NetworkMonitorUtil(this)

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // инициализируем FirebaseAuth
        mAuth = FirebaseAuth.getInstance()

        pbLoading = binding.loading
        editUserName = binding.editeTextEmail
        editPassword = binding.editeTextPassword
        btnEnter = binding.btnEnter

        networkMonitorResult()
        openSession()
        loadTextPerson()
        loginUserAccount()

    }

    private fun openSession(){

        if (mAuth?.currentUser != null) {
            // Уже вошел в систему
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)

        } else {
            //процедура аутентификации
            loginUserAccount()
        }
    }

    private fun loadTextPerson() {

        //загружаем логин
        sLogin = getPreferences(MODE_PRIVATE)
        saveTextLogin = sLogin.getString(LOGIN, "")
        editUserName.setText(saveTextLogin)
    }

    private fun loginUserAccount() {

        // слушаем кнопочку "вход"
        btnEnter.setOnClickListener {

            // берём значения EditText
            val email = editUserName.text.toString()
            val password = editPassword.text.toString()

            // проверяем правильность ввода адреса электронной почты и пароля
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext,
                    "Введите корректный Email, Пожалуйста",
                    Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext,
                    "Введите корректный пароль, Пожалуйста",
                    Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            // показываем ProgressBar
            pbLoading.visibility = View.VISIBLE

            // вход существующего пользователя
            mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->

                    // если всё успешненько:
                    if (task.isSuccessful) {

                            // сохраняем логин/пароль
                        val login: String = editUserName.text.toString()
                        saveTextLogin(login)
                        LoginSingleton.addLogin(login)

                        // скрываем ProgressBar
                        pbLoading.visibility = View.GONE

                        //приветствуем залогинившегося
                        Toast.makeText(applicationContext,
                            "Добро пожаловать!",
                            Toast.LENGTH_LONG)
                            .show()

                        // переходим к сл. активити
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }

                    else {

                        // если нет, то капец кто-то криворукий
                        Toast.makeText(applicationContext,
                            "Ошибочка вышла",
                            Toast.LENGTH_LONG)
                            .show();

                        // скрываем индикатор ProgressBar
                        pbLoading.visibility = View.GONE;
                    }
                }
        }
    }

    private fun saveTextLogin(login: String) {

        //сохраняем логин
        sLogin = getPreferences(MODE_PRIVATE)
        val sl: SharedPreferences.Editor = sLogin.edit()
        sl.putString(LOGIN, login).toString()
        sl.apply()
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

    fun closeSession(){
        //закрыть сессию
        FirebaseAuth.getInstance().signOut();
    }

    override fun onResume() {
        super.onResume()
        //проверка подключения к интернету
        networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        //проверка подключения к интернету
        networkMonitor.unregister()
    }

    override fun onBackPressed() {
        onDestroy()
    }

    override fun onDestroy() {
        moveTaskToBack(true)
        super.onDestroy()
        exitProcess(0)
    }
}
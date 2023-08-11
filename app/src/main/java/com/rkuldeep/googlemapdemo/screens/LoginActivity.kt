package com.rkuldeep.googlemapdemo.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rkuldeep.googlemapdemo.MainActivity
import com.rkuldeep.googlemapdemo.R
import com.rkuldeep.googlemapdemo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)

        binding.loginButton.setOnClickListener {
            editor.putString("phonenumber",binding.etPhoneNumber.text.toString())
//            editor.putLong("l",100L)
            editor.commit()
            Log.d("PHONE_NUMBER", "phone number: "+binding.etPhoneNumber.text)

            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
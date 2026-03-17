package com.carcollector

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carcollector.databinding.ActivityLoginBinding
import com.carcollector.network.ApiClient
import com.carcollector.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        if (session.getToken() != null) {
            openMain()
            return
        }

        binding.loginButton.setOnClickListener { doLogin() }
        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun doLogin() {
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Completa email y password", Toast.LENGTH_SHORT).show()
            return
        }

        binding.loading.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { ApiClient.login(email, password) }
            binding.loading.visibility = View.GONE

            if (result.first && result.third != null) {
                session.saveToken(result.third!!)
                Toast.makeText(this@LoginActivity, "Bienvenido ${result.second}", Toast.LENGTH_SHORT).show()
                openMain()
            } else {
                val msg = runCatching { JSONObject(result.second).optString("error") }.getOrNull()
                    ?: "Error al iniciar sesión"
                binding.errorText.visibility = View.VISIBLE
                binding.errorText.text = msg
            }
        }
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

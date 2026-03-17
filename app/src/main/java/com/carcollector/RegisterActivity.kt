package com.carcollector

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.carcollector.databinding.ActivityRegisterBinding
import com.carcollector.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.topBar.setNavigationOnClickListener { finish() }

        binding.registerButton.setOnClickListener { doRegister() }
    }

    private fun doRegister() {
        val nombre = binding.nameInput.text?.toString()?.trim().orEmpty()
        val email = binding.emailInput.text?.toString()?.trim().orEmpty()
        val password = binding.passwordInput.text?.toString().orEmpty()

        if (nombre.isBlank() || email.isBlank() || password.length < 6) {
            Toast.makeText(this, "Completa datos (password mínimo 6)", Toast.LENGTH_SHORT).show()
            return
        }

        binding.loading.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) { ApiClient.register(nombre, email, password) }
            binding.loading.visibility = View.GONE

            if (result.first) {
                Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val msg = runCatching { JSONObject(result.second).optString("error") }.getOrNull()
                    ?: "Error en registro"
                binding.errorText.visibility = View.VISIBLE
                binding.errorText.text = msg
            }
        }
    }
}

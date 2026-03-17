package com.carcollector.network

import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {
    // Cambia esta URL por tu servidor real
    private const val BASE_URL = "http://10.0.2.2:8000/backend/api"

    fun register(nombre: String, email: String, password: String): Pair<Boolean, String> {
        val payload = JSONObject()
            .put("nombre", nombre)
            .put("email", email)
            .put("password", password)
        return post("$BASE_URL/register.php", payload)
    }

    fun login(email: String, password: String): Triple<Boolean, String, String?> {
        val payload = JSONObject()
            .put("email", email)
            .put("password", password)
        val (ok, body) = post("$BASE_URL/login.php", payload)
        if (!ok) return Triple(false, body, null)

        val json = JSONObject(body)
        val token = json.optString("token", null)
        val nombre = json.optJSONObject("usuario")?.optString("nombre") ?: ""
        return Triple(token != null, nombre.ifBlank { "Login exitoso" }, token)
    }

    private fun post(url: String, json: JSONObject): Pair<Boolean, String> {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15000
            readTimeout = 15000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        }

        OutputStreamWriter(conn.outputStream).use { it.write(json.toString()) }
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream

        val body = BufferedReader(stream.reader()).use { it.readText() }
        return Pair(code in 200..299, body)
    }
}

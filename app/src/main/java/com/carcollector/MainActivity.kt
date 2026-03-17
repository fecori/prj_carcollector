package com.carcollector

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.carcollector.databinding.ActivityMainBinding
import com.carcollector.model.LotItem
import com.carcollector.network.ApiClient
import com.carcollector.session.SessionManager
import com.carcollector.ui.LotAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: LotAdapter
    private lateinit var session: SessionManager

    private var allLots: List<LotItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        if (session.getToken() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_logout) {
                session.clear()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            } else {
                false
            }
        }

        adapter = LotAdapter { lot ->
            val intent = Intent(this, LotDetailActivity::class.java)
                .putExtra(LotDetailActivity.EXTRA_URL, lot.url)
            startActivity(intent)
        }

        binding.lotsRecycler.layoutManager = LinearLayoutManager(this)
        binding.lotsRecycler.adapter = adapter

        binding.searchInput.doAfterTextChanged { text ->
            filterLots(text?.toString().orEmpty())
        }

        loadLots()
    }

    private fun loadLots() {
        showLoading(true)
        lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    ApiClient.getLotes()
                }
            }.onSuccess { lots ->
                allLots = lots
                binding.statusText.visibility = View.GONE
                filterLots(binding.searchInput.text?.toString().orEmpty())
            }.onFailure { ex ->
                allLots = emptyList()
                adapter.submitList(emptyList())
                binding.statusText.text = getString(R.string.msg_error) + " ${ex.message.orEmpty()}"
                binding.statusText.visibility = View.VISIBLE
            }
            showLoading(false)
        }
    }

    private fun filterLots(query: String) {
        val filtered = allLots.filter { it.title.contains(query, ignoreCase = true) }
        if (allLots.isNotEmpty() && filtered.isEmpty()) {
            binding.statusText.text = getString(R.string.msg_empty)
            binding.statusText.visibility = View.VISIBLE
        } else {
            binding.statusText.visibility = View.GONE
        }
        adapter.submitList(filtered)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

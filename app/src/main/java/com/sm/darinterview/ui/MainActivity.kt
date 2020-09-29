package com.sm.darinterview.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.sm.darinterview.R
import com.sm.darinterview.databinding.ActivityMainBinding
import com.sm.darinterview.ui.adapters.WeatherAdapter
import com.sm.darinterview.ui.base.Empty
import com.sm.darinterview.ui.base.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    @Inject lateinit var preferences: SharedPreferences
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences.getString(QUERY, "")?.let {
            binding.etSearch.setText(it)
        }

        val searchAdapter = WeatherAdapter()
        binding.rvSearch.adapter = searchAdapter


        binding.etSearch.doAfterTextChanged {
            lifecycleScope.launch { viewModel.queryChannel.send(it.toString()) }
        }

        viewModel.results.observe(this, {
            when (it) {
                is Success -> {
                    searchAdapter.submitList(it.data)
                }
                is Empty -> {
                    searchAdapter.submitList(null)
                }
                else -> {
                    Toast.makeText(this, R.string.load_error, Toast.LENGTH_SHORT ).show()
                }
            }
        })
    }

}
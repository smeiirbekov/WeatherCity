package com.sm.darinterview.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sm.darinterview.R
import com.sm.darinterview.data.models.City
import com.sm.darinterview.databinding.ItemCityBinding

class WeatherAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<City>(){
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean = oldItem.city == newItem.city
        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean = oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindTo(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<City>?){
        differ.submitList(list)
    }

    class ViewHolder(private val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(item: City?) {
            binding.tvCity.text = item?.city
            binding.tvTemperature.text = binding.tvTemperature.context.getString(R.string.temperature_x, item?.temperature)
        }
    }
}

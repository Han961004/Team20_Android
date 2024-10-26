package com.example.potatoservice.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.potatoservice.databinding.ServiceItemBinding
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.ui.share.AdapterCallback

class SearchResultAdapter(
	private val callback: AdapterCallback
): ListAdapter<Activity, SearchResultAdapter.ViewHolder>(
	object : DiffUtil.ItemCallback<Activity>(){
		override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
			return oldItem === newItem
		}

		override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
			return oldItem == newItem
		}
	}
) {
	private lateinit var binding: ServiceItemBinding
	inner class ViewHolder(
		itemView: View
	) : RecyclerView.ViewHolder(itemView) {

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		binding = ServiceItemBinding.inflate(inflater, parent, false)
		return ViewHolder(binding.root)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val activity: Activity = getItem(position)
		binding.activity = activity
		holder.itemView.setOnClickListener {
			Log.d("testt", "adapter click ${activity.actId}")
			callback.onClicked(activity.actId)
		}
	}
}
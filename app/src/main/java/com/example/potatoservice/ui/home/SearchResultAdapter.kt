package com.example.potatoservice.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.potatoservice.databinding.ServiceItemBinding
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.ui.share.AdapterCallback

class SearchResultAdapter(
	private val callback: AdapterCallback
) : ListAdapter<Activity, SearchResultAdapter.ViewHolder>(
	object : DiffUtil.ItemCallback<Activity>() {
		override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
			return oldItem.actId == newItem.actId
		}

		override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
			return oldItem == newItem
		}
	}
) {
	inner class ViewHolder(
		private val binding: ServiceItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(activity: Activity) {
			binding.activity = activity
			binding.root.setOnClickListener {
				callback.onClicked(activity.actId)
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val binding = ServiceItemBinding.inflate(inflater, parent, false)
		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val activity = getItem(position)
		holder.bind(activity)
	}
}
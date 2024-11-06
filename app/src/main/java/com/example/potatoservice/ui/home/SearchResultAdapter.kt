package com.example.potatoservice.ui.home

import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.potatoservice.databinding.LoadingItemBinding
import com.example.potatoservice.databinding.ServiceItemBinding
import com.example.potatoservice.model.remote.Activity
import com.example.potatoservice.ui.share.AdapterCallback

class SearchResultAdapter(
	private val callback: AdapterCallback
) : ListAdapter<Activity, RecyclerView.ViewHolder>(
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

	inner class LoadingViewHolder(binding: LoadingItemBinding) : RecyclerView.ViewHolder(binding.root)


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			VIEW_TYPE_ITEM -> {
				val inflater = LayoutInflater.from(parent.context)
				val binding = ServiceItemBinding.inflate(inflater, parent, false)
				ViewHolder(binding) // ViewHolder 반환
			}
			VIEW_TYPE_LOADING -> {
				val inflater = LayoutInflater.from(parent.context)
				val binding = LoadingItemBinding.inflate(inflater, parent, false)
				LoadingViewHolder(binding) // LoadingViewHolder 반환
			}
			else -> throw IllegalArgumentException("Invalid view type")
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (holder is ViewHolder) {
			val activity = getItem(position)
			holder.bind(activity)
		} else {
			// 로딩 표시기 표시
		}
	}

	companion object {
		//뷰가 봉사 활동 아이템인지, 로딩 아이템인지 구분
		private const val VIEW_TYPE_ITEM = 0
		private const val VIEW_TYPE_LOADING = 1
	}

	// 로딩 상태
	private var isLoading = false
	//리사이클러뷰 위치 저장
	private var recyclerViewState: Parcelable? = null

	// 로딩 표시기 추가
	override fun getItemViewType(position: Int): Int {
		val activity = getItem(position)
		return if (position == itemCount - 1 && isLoading && activity.actId == -1) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
	}

	fun setLoading(loading: Boolean) {
		isLoading = loading
		notifyItemChanged(itemCount - 1) // 로딩 표시기 가시성 업데이트
	}

	// 리사이클러뷰에 스크롤 리스너 연결
	fun attachToRecyclerView(recyclerView: RecyclerView) {
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val layoutManager = recyclerView.layoutManager as LinearLayoutManager
				val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
				if (lastVisibleItemPosition == itemCount - 1 && !isLoading) {
					//현재 위치 저장
					recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
					// 더 많은 아이템 로드
					setLoading(true)
					callback.loadMoreActivities(recyclerViewState) // ViewModel에 더 로드하도록 요청

				}
			}
		})
	}
}


package vn.edu.greenwich.cw_1_sample.ui.request.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.databinding.ListItemRequestBinding
import vn.edu.greenwich.cw_1_sample.models.Request
import java.util.*

class RequestAdapter(list: ArrayList<Request>) :
	RecyclerView.Adapter<RequestAdapter.ViewHolder>(),
	Filterable {

	private var _originalList = list
	private var _filteredList = list
	private var _itemFilter = ItemFilter()

	fun updateList(list: ArrayList<Request>) {
		_filteredList = list

		notifyItemRangeChanged(0, list.size)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val view = inflater.inflate(R.layout.list_item_request, parent, false)
		val binding = ListItemRequestBinding.inflate(inflater, view as ViewGroup, false)

		return ViewHolder(view, binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val request = _filteredList[position]

		holder.textDate.text = request.date
		holder.textTime.text = request.time
		holder.textType.text = request.type
		holder.textContent.text = request.content
	}

	override fun getItemCount(): Int = _filteredList.size

	override fun getFilter(): Filter = _itemFilter

	inner class ViewHolder(itemView: View, binding: ListItemRequestBinding) : RecyclerView.ViewHolder(itemView) {
		val textDate: TextView = binding.textRequestDate
		val textTime: TextView = binding.textRequestTime
		val textType: TextView = binding.textRequestType
		val textContent: TextView = binding.textRequestContent
	}

	private inner class ItemFilter : Filter() {
		override fun performFiltering(constraint: CharSequence): FilterResults {
			val list = ArrayList<Request>()
			val filterString = constraint.toString().lowercase(Locale.getDefault())
			val results = FilterResults()

			list.addAll(_originalList)

			for (request in _originalList) {
				val filterableString = request.toString()

				if (filterableString.lowercase(Locale.getDefault()).contains(filterString)) {
					list.add(request)
				}
			}

			results.values = list
			results.count = list.size

			return results
		}

		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			(results.values as? Array<*>)
				?.filterIsInstance<Request>()
				?.toMutableList()
				?.let { _filteredList = ArrayList(it) }

			notifyItemRangeChanged(0, _filteredList.size)
		}
	}
}

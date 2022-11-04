package vn.edu.greenwich.cw_1_sample.ui.request.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.models.Request
import java.util.*

class RequestAdapter(list: ArrayList<Request>) :
	RecyclerView.Adapter<RequestAdapter.ViewHolder>(),
	Filterable {

	private var _originalList: ArrayList<Request>
	private var _filteredList: ArrayList<Request>?
	private var _itemFilter: ItemFilter = ItemFilter()

	init {
		_originalList = list
		_filteredList = list
	}

	fun updateList(list: ArrayList<Request>) {
		_originalList = list
		_filteredList = list

		notifyItemRangeChanged(0, list.size)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val view = inflater.inflate(R.layout.list_item_request, parent, false)

		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val request = _filteredList!![position]

		holder.listItemRequestDate.text = request.date
		holder.listItemRequestTime.text = request.time
		holder.listItemRequestType.text = request.type
		holder.listItemRequestContent.text = request.content
	}

	override fun getItemCount(): Int = if (_filteredList == null) 0 else _filteredList!!.size

	override fun getFilter(): Filter = _itemFilter

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var listItemRequestDate: TextView
		var listItemRequestTime: TextView
		var listItemRequestType: TextView
		var listItemRequestContent: TextView

		init {
			listItemRequestDate = itemView.findViewById(R.id.listItemRequestDate)
			listItemRequestTime = itemView.findViewById(R.id.listItemRequestTime)
			listItemRequestType = itemView.findViewById(R.id.listItemRequestType)
			listItemRequestContent = itemView.findViewById(R.id.listItemRequestContent)
		}
	}

	private inner class ItemFilter : Filter() {
		override fun performFiltering(constraint: CharSequence): FilterResults {
			val list = _originalList
			val nlist = ArrayList<Request>(list.size)
			val filterString = constraint.toString().lowercase(Locale.getDefault())
			val results = FilterResults()

			for (request in list) {
				val filterableString = request.toString()
				if (filterableString.lowercase(Locale.getDefault()).contains(filterString)) nlist.add(request)
			}

			results.values = nlist
			results.count = nlist.size

			return results
		}

		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			(results.values as? Array<*>)
				?.filterIsInstance<Request>()
				?.toMutableList()
				?.let { _filteredList = ArrayList(it) }

			_filteredList?.let { notifyItemRangeChanged(0, it.size) }
		}
	}
}

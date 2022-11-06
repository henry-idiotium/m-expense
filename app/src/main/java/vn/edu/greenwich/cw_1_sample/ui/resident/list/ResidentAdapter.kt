package vn.edu.greenwich.cw_1_sample.ui.resident.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.resident.ResidentDetailFragment
import java.util.*

class ResidentAdapter(var _originalList: ArrayList<Resident>) :
	RecyclerView.Adapter<ResidentAdapter.ViewHolder>(),
	Filterable {

	private var _filteredList: ArrayList<Resident>?
	private var _itemFilter = ItemFilter()

	init {
		_filteredList = _originalList
	}

	fun updateList(list: ArrayList<Resident>) {
		_originalList = list
		_filteredList = list

		notifyItemRangeChanged(0, list.size)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val view = inflater.inflate(R.layout.list_item_resident, parent, false)

		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val resident = _filteredList!![position]
		val owner = holder.itemView.resources.getString(R.string.label_owner)
		val tenant = holder.itemView.resources.getString(R.string.label_tenant)

		holder.listItemName.text = resident.name
		holder.listItemStartDate.text = resident.startDate.toString().replace("\n", "")
		holder.listItemOwner.text = if (resident.owner == 1) owner else tenant
	}

	override fun getItemCount(): Int = if (_filteredList == null) 0 else _filteredList!!.size

	override fun getFilter(): Filter = _itemFilter

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private var _listItemResident: LinearLayout
		var listItemStartDate: TextView
		var listItemName: TextView
		var listItemOwner: TextView

		init {
			listItemName = itemView.findViewById(R.id.listItemResidentName)
			listItemStartDate = itemView.findViewById(R.id.listItemResidentStartDate)
			listItemOwner = itemView.findViewById(R.id.listItemResidentOwner)
			_listItemResident = itemView.findViewById(R.id.listItemResident)
			_listItemResident.setOnClickListener(::showDetail)
		}

		private fun showDetail(view: View?) {
			val resident = _filteredList!![adapterPosition]
			val bundle = Bundle()

			bundle.putSerializable(ResidentDetailFragment.ARG_PARAM_RESIDENT, resident)
			findNavController(view!!).navigate(R.id.fragment_resident_detail, bundle)
		}
	}

	private inner class ItemFilter : Filter() {
		override fun performFiltering(constraint: CharSequence): FilterResults {
			val list = _originalList
			val nlist = ArrayList<Resident>(list.size)
			val filterString = constraint.toString().lowercase(Locale.getDefault())
			val results = FilterResults()

			for (resident in list) {
				val filterableString = resident.toString()
				if (filterableString.lowercase(Locale.getDefault()).contains(filterString)) nlist.add(resident)
			}

			results.values = nlist
			results.count = nlist.size

			return results
		}

		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			(results.values as? Array<*>)
				?.filterIsInstance<Resident>()
				?.toMutableList()
				?.let { _filteredList = ArrayList(it) }

			_filteredList?.let { notifyItemRangeChanged(0, it.size) }
		}
	}
}

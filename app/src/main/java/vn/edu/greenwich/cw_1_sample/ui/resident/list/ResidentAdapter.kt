package vn.edu.greenwich.cw_1_sample.ui.resident.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import vn.edu.greenwich.cw_1_sample.R
import vn.edu.greenwich.cw_1_sample.databinding.ListItemResidentBinding
import vn.edu.greenwich.cw_1_sample.models.Resident
import vn.edu.greenwich.cw_1_sample.ui.resident.ResidentDetailFragment
import java.util.*

class ResidentAdapter(var originalList: ArrayList<Resident>) :
	RecyclerView.Adapter<ResidentAdapter.ViewHolder>(),
	Filterable {

	private var _filteredList: ArrayList<Resident> = originalList
	private var _itemFilter = ItemFilter()

	fun updateList(list: ArrayList<Resident>) {
		originalList = list
		_filteredList = list

		notifyItemRangeChanged(0, list.size)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		val view = inflater.inflate(R.layout.list_item_resident, parent, false)
		val binding = ListItemResidentBinding.inflate(inflater, view as ViewGroup, false)

		return ViewHolder(view, binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val resident = _filteredList[position]
		val owner = holder.itemView.resources.getString(R.string.label_owner)
		val tenant = holder.itemView.resources.getString(R.string.label_tenant)

		holder.textName.text = resident.name
		holder.textStartDate.text = resident.startDate.toString().replace("\n", "")
		holder.textOwner.text = if (resident.owner == 1) owner else tenant
	}

	override fun getItemCount(): Int = _filteredList.size

	override fun getFilter(): Filter = _itemFilter

	inner class ViewHolder(itemView: View, binding: ListItemResidentBinding) : RecyclerView.ViewHolder(itemView) {
		val textStartDate: TextView = binding.textResidentStartDate
		val textName: TextView = binding.textResidentName
		val textOwner: TextView = binding.textResidentOwner

		init {
			binding.root.setOnClickListener(::showDetail)
		}

		private fun showDetail(view: View?) {
			val resident = _filteredList[adapterPosition]
			val bundle = Bundle()

			bundle.putSerializable(ResidentDetailFragment.ARG_PARAM_RESIDENT, resident)
			findNavController(view!!).navigate(R.id.fragment_resident_detail, bundle)
		}
	}

	private inner class ItemFilter : Filter() {
		override fun performFiltering(constraint: CharSequence): FilterResults {
			val list = ArrayList<Resident>()
			val filterString = constraint.toString().lowercase(Locale.getDefault())
			val results = FilterResults()

			list.addAll(originalList)

			for (resident in originalList) {
				val filterableString = resident.toString()

				if (filterableString.lowercase(Locale.getDefault()).contains(filterString)) {
					list.add(resident)
				}
			}

			results.values = list
			results.count = list.size

			return results
		}

		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			(results.values as? Array<*>)
				?.filterIsInstance<Resident>()
				?.toMutableList()
				?.let { _filteredList = ArrayList(it) }

			notifyItemRangeChanged(0, _filteredList.size)
		}
	}
}

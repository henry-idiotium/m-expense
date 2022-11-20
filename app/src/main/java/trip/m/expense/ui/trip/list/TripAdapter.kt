package trip.m.expense.ui.trip.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import trip.m.expense.R
import trip.m.expense.databinding.AdapterItemTripBinding
import trip.m.expense.models.Trip
import trip.m.expense.toSimpleDateFormat
import trip.m.expense.ui.trip.TripDetailFragment
import java.util.*

class TripAdapter(private var _originalList: ArrayList<Trip>) :
	RecyclerView.Adapter<TripAdapter.ViewHolder>(),
	Filterable {

	private val _itemFilter: ItemFilter by lazy { ItemFilter(this, _originalList) }
	private var _filteredList = _originalList

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item_trip, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
		val requiredMessage = "Required assessment"
		val optionalMessage = "Not required assessment"
		val trip = _filteredList[position]

		binding.textAdapterItemName.text = trip.name
		binding.textAdapterItemDateOfTrip.text = trip.dateOfTrip?.toSimpleDateFormat()
		binding.textAdapterItemRiskAssessment.text = if (trip.riskAssessment) requiredMessage else optionalMessage
		binding.textAdapterItemDestination.text = trip.destination
	}

	override fun getItemCount(): Int = _filteredList.size
	override fun getFilter(): Filter = _itemFilter

	fun update(list: ArrayList<Trip>) {
		_originalList = list
		_filteredList = list

		notifyItemRangeChanged(1, list.size)
	}

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val binding = AdapterItemTripBinding.bind(itemView)

		init {
			binding.root.setOnClickListener(::showDetail)
		}

		private fun showDetail(view: View?) = try {
			val id = _filteredList[adapterPosition].id
			val bundle = Bundle().apply { putSerializable(TripDetailFragment.ARG_PARAM_TRIP_ID, id) }

			findNavController(view!!).navigate(R.id.nav_fm_trip_detail, bundle)
		} catch (_: Exception) {
		}
	}

	private inner class ItemFilter(val adapter: TripAdapter, val initialList: ArrayList<Trip>) : Filter() {

		private var filteredList = ArrayList<Trip>()

		override fun performFiltering(constraint: CharSequence): FilterResults {
			filteredList.clear()

			val pattern = constraint.toString().lowercase(Locale.getDefault()).trim()

			initialList.forEach { trip ->
				val matched = trip.toString().lowercase(Locale.getDefault()).contains(pattern)
				if (matched) filteredList.add(trip)
			}

			return FilterResults().apply { values = filteredList; count = filteredList.size }
		}

		@Suppress("UNCHECKED_CAST")
		@SuppressLint("NotifyDataSetChanged")
		override fun publishResults(constraint: CharSequence, results: FilterResults) {
			adapter._filteredList.clear()
			(results.values as? ArrayList<Trip>)?.let { adapter._filteredList = it }

			notifyItemRangeChanged(1, adapter._filteredList.size)
		}
	}
}

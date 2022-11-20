package trip.m.expense.ui.expense.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import trip.m.expense.R
import trip.m.expense.databinding.AdapterItemExpenseBinding
import trip.m.expense.models.Expense
import trip.m.expense.toSimpleDateFormat
import trip.m.expense.ui.expense.ExpenseDetailFragment
import java.util.*

class ExpenseAdapter(
	private var _originalList: ArrayList<Expense>,
	private val _fragmentManager: FragmentManager,
) :
	RecyclerView.Adapter<ExpenseAdapter.ViewHolder>(),
	Filterable {

	private val _itemFilter: ExpenseAdapter.ItemFilter by lazy { ItemFilter(this, _originalList) }
	private var _filteredList = _originalList
	private lateinit var _context: Context

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item_expense, parent, false)
		_context = parent.context
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
		val expense = _filteredList[position]

		binding.listItemExpenseType.text = expense.type.value
		binding.listItemExpenseAmount.text = expense.amount.toString()
		binding.listItemExpenseDateOfExpense.text = expense.dateOfExpense.toSimpleDateFormat()
	}

	override fun getItemCount(): Int = _filteredList.size
	override fun getFilter(): Filter = _itemFilter

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val binding = AdapterItemExpenseBinding.bind(itemView)

		init {
			binding.root.setOnClickListener { showDetail() }
		}

		private fun showDetail() = ExpenseDetailFragment(_filteredList[adapterPosition].id).show(_fragmentManager, null)
	}

	fun update(list: ArrayList<Expense>) {
		_originalList = list
		_filteredList = list
		notifyItemRangeChanged(1, list.size)
	}

	private inner class ItemFilter(val adapter: ExpenseAdapter, val initialList: ArrayList<Expense>) : Filter() {

		private var filteredList = ArrayList<Expense>()

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
			(results.values as? ArrayList<Expense>)?.let { adapter._filteredList = it }

			notifyItemRangeChanged(1, adapter._filteredList.size)
		}
	}
}

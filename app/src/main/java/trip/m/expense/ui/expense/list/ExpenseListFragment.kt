package trip.m.expense.ui.expense.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import trip.m.expense.R
import trip.m.expense.database.DbDAO
import trip.m.expense.databinding.FragmentExpenseListBinding
import trip.m.expense.models.Expense

class ExpenseListFragment : Fragment(R.layout.fragment_expense_list) {

	private var _tripId: Long = -1
	private var _expensesToShow = ArrayList<Expense>()
	private lateinit var _linearLayoutManager: LinearLayoutManager
	private lateinit var _binding: FragmentExpenseListBinding
	private lateinit var _db: DbDAO.ExpenseDbSet

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentExpenseListBinding.inflate(inflater, container, false)
		_db = DbDAO(context).ExpenseDbSet()

		arguments?.run {
			_tripId = getLong(ARG_PARAM_TRIP_ID)
			_expensesToShow = _db.get(Expense(tripId = _tripId))
		}

		_linearLayoutManager = LinearLayoutManager(context)
		val dividerItemDecoration = DividerItemDecoration(context, _linearLayoutManager.orientation)

		_binding.listExpenses.addItemDecoration(dividerItemDecoration)
		_binding.listExpenses.adapter = ExpenseAdapter(_expensesToShow, childFragmentManager)
		_binding.listExpenses.layoutManager = LinearLayoutManager(context)

		reloadList()

		return _binding.root
	}

	fun reloadList() {
		if (_tripId == -1L) return

		_expensesToShow = _db.get(Expense(tripId = _tripId))
		_binding.listExpenses.adapter = ExpenseAdapter(_expensesToShow, childFragmentManager)
		_binding.listExpenses.layoutManager = LinearLayoutManager(context)

		val isEmptyList = _expensesToShow.isEmpty()
		_binding.listExpenses.visibility = if (!isEmptyList) View.VISIBLE else View.GONE
		_binding.imgEmptyNotice.visibility = if (isEmptyList) View.VISIBLE else View.GONE
	}

	companion object {
		const val ARG_PARAM_TRIP_ID = "trip_id"
	}
}

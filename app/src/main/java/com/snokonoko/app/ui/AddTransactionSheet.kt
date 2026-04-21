package com.snokonoko.app.ui

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snokonoko.app.R
import com.snokonoko.app.data.Transaction
import com.snokonoko.app.databinding.SheetAddTransactionBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.time.LocalDate

class AddTransactionSheet : BottomSheetDialogFragment() {

    private var _binding: SheetAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private var selectedType = "expense"

    override fun getTheme(): Int = R.style.BottomSheetStyle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SheetAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set today's date as default
        binding.etDate.setText(LocalDate.now().toString())

        // Category spinner
        val expenseCats = EXPENSE_CATEGORIES.map { categoryLabel(it) }
        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, expenseCats
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Type toggle
        binding.btnExpense.setOnClickListener {
            selectedType = "expense"
            binding.spinnerCategory.visibility = View.VISIBLE
        }
        binding.btnIncome.setOnClickListener {
            selectedType = "income"
            binding.spinnerCategory.visibility = View.GONE
        }

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val desc = binding.etDescription.text.toString().trim()
            val amtStr = binding.etAmount.text.toString()
            val date = binding.etDate.text.toString().trim()
            val amt = amtStr.toDoubleOrNull()

            if (desc.isEmpty() || amt == null || amt <= 0 || date.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Please fill in all fields.", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val catIdx = binding.spinnerCategory.selectedItemPosition
            val cat = if (selectedType == "income") "income" else EXPENSE_CATEGORIES[catIdx]

            viewModel.addTransaction(
                Transaction(type = selectedType, category = cat, description = desc, amount = amt, date = date)
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

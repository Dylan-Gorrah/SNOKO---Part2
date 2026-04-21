package com.snokonoko.app.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.snokonoko.app.databinding.FragmentAccountBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as com.snokonoko.app.MainActivity
        binding.tvUserName.text = activity.userName
        binding.tvUserEmail.text = activity.userEmail

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            val list = transactions ?: emptyList()
            val income = list.filter { it.type == "income" }.sumOf { it.amount }
            val expense = list.filter { it.type == "expense" }.sumOf { it.amount }
            val net = income - expense

            val nf = NumberFormat.getInstance(Locale("en", "ZA"))
            nf.minimumFractionDigits = 0
            nf.maximumFractionDigits = 0
            binding.tvNetBalance.text = "R ${nf.format(abs(net))}"
            binding.tvNetBalance.setTextColor(
                if (net >= 0) Color.parseColor("#30D158") else Color.parseColor("#FF453A")
            )
        }

        binding.btnLogout.setOnClickListener {
            // Clear session
            requireContext().getSharedPreferences("snokonoko_prefs", 0)
                .edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.snokonoko.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.snokonoko.app.databinding.FragmentReportsBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            val list = transactions ?: emptyList()
            val income = list.filter { it.type == "income" }.sumOf { it.amount }
            val expense = list.filter { it.type == "expense" }.sumOf { it.amount }

            binding.tvTotalIncome.text = formatZAR(income)
            binding.tvTotalSpent.text = formatZAR(expense)

            // Group expenses by category
            val byCategory = list.filter { it.type == "expense" }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
                .entries.sortedByDescending { it.value }

            val maxVal = byCategory.firstOrNull()?.value ?: 1.0

            binding.categoryContainer.removeAllViews()
            byCategory.forEach { (cat, amount) ->
                binding.categoryContainer.addView(buildCategoryRow(cat, amount, maxVal))
            }
        }
    }

    private fun buildCategoryRow(cat: String, amount: Double, maxVal: Double): View {
        val ctx = requireContext()
        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 16.dp }
        }

        val catColor = categoryColor(cat)

        // Label row
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 6.dp }
        }
        row.addView(TextView(ctx).apply {
            text = categoryLabel(cat)
            setTextColor(Color.WHITE)
            textSize = 13f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        row.addView(TextView(ctx).apply {
            text = formatZAR(amount)
            setTextColor(Color.WHITE)
            textSize = 13f
        })
        col.addView(row)

        // Progress bar
        val track = FrameLayout(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4.dp)
            setBackgroundColor(Color.parseColor("#222222"))
        }
        val fill = View(ctx).apply {
            val pct = (amount / maxVal).coerceIn(0.0, 1.0)
            layoutParams = FrameLayout.LayoutParams((pct * resources.displayMetrics.widthPixels).toInt(), FrameLayout.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.parseColor(catColor))
        }
        track.addView(fill)
        col.addView(track)

        return col
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    private fun formatZAR(amount: Double): String {
        val nf = NumberFormat.getInstance(Locale("en", "ZA"))
        nf.minimumFractionDigits = 0
        nf.maximumFractionDigits = 0
        return "R ${nf.format(abs(amount))}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

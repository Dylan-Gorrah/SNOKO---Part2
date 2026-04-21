package com.snokonoko.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.snokonoko.app.R
import com.snokonoko.app.data.Transaction
import com.snokonoko.app.databinding.FragmentHomeBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateUI(transactions ?: emptyList())
        }

        binding.fab.setOnClickListener {
            AddTransactionSheet().show(parentFragmentManager, "AddTransaction")
        }
    }

    private fun updateUI(transactions: List<Transaction>) {
        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        val balance = income - expense

        binding.tvBalance.text = formatZAR(balance)
        binding.tvBalance.setTextColor(if (balance >= 0) Color.parseColor("#30D158") else Color.parseColor("#FF453A"))
        binding.tvIncome.text = formatZAR(income)
        binding.tvExpenses.text = formatZAR(expense)
        binding.tvTxCount.text = "${transactions.size} transactions"

        // Show recent 6 transactions
        binding.txListContainer.removeAllViews()
        val recent = transactions.take(6)
        if (recent.isEmpty()) {
            val empty = TextView(requireContext()).apply {
                text = "No transactions yet"
                setTextColor(Color.parseColor("#474747"))
                textSize = 13f
                gravity = android.view.Gravity.CENTER
                setPadding(0, 40, 0, 40)
            }
            binding.txListContainer.addView(empty)
        } else {
            recent.forEachIndexed { index, tx ->
                val row = buildTransactionRow(tx)
                binding.txListContainer.addView(row)
                if (index < recent.size - 1) {
                    val divider = View(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1
                        ).also { it.setMargins(16.dp, 0, 16.dp, 0) }
                        setBackgroundColor(Color.parseColor("#1A1A1A"))
                    }
                    binding.txListContainer.addView(divider)
                }
            }
        }
    }

    private fun buildTransactionRow(tx: Transaction): View {
        val ctx = requireContext()
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16.dp, 14.dp, 16.dp, 14.dp)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val catColor = categoryColor(tx.category)

        // Category icon circle
        val icon = TextView(ctx).apply {
            text = categoryEmoji(tx.category)
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            setTextColor(Color.parseColor(catColor))
            setBackgroundColor(Color.parseColor(catColor + "22"))
            layoutParams = LinearLayout.LayoutParams(40.dp, 40.dp).also {
                it.marginEnd = 12.dp
            }
        }
        row.addView(icon)

        // Description + sub info
        val infoCol = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        infoCol.addView(TextView(ctx).apply {
            text = tx.description
            setTextColor(Color.WHITE)
            textSize = 13f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        })
        infoCol.addView(TextView(ctx).apply {
            text = "${categoryLabel(tx.category)} ${tx.date.substring(5).replace("-", " ")}"
            setTextColor(Color.parseColor("#474747"))
            textSize = 11f
        })
        row.addView(infoCol)

        // Amount
        row.addView(TextView(ctx).apply {
            val sign = if (tx.type == "income") "+" else "-"
            text = "$sign${formatZAR(tx.amount)}"
            setTextColor(if (tx.type == "income") Color.parseColor("#30D158") else Color.WHITE)
            textSize = 14f
        })

        return row
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    private fun formatZAR(amount: Double): String {
        val nf = NumberFormat.getInstance(Locale("en", "ZA"))
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        return "R ${nf.format(abs(amount))}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Shared helpers
fun categoryColor(cat: String): String = when (cat) {
    "food"          -> "#FF6B6B"
    "transport"     -> "#0A84FF"
    "shopping"      -> "#BF5AF2"
    "entertainment" -> "#FF9F0A"
    "fitness"       -> "#FF375F"
    "utilities"     -> "#636366"
    "medical"       -> "#34C759"
    "education"     -> "#5AC8FA"
    "income"        -> "#30D158"
    else            -> "#8E8E93"
}

fun categoryLabel(cat: String): String = when (cat) {
    "food"          -> "Food & Dining"
    "transport"     -> "Transport"
    "shopping"      -> "Shopping"
    "entertainment" -> "Entertainment"
    "fitness"       -> "Fitness"
    "utilities"     -> "Utilities"
    "medical"       -> "Medical"
    "education"     -> "Education"
    "income"        -> "Income"
    else            -> "Other"
}

fun categoryEmoji(cat: String): String = when (cat) {
    "food"          -> "F"
    "transport"     -> "T"
    "shopping"      -> "S"
    "entertainment" -> "E"
    "fitness"       -> "F"
    "utilities"     -> "U"
    "medical"       -> "M"
    "education"     -> "E"
    "income"        -> "I"
    else            -> "O"
}

val EXPENSE_CATEGORIES = listOf(
    "food", "transport", "shopping", "entertainment",
    "fitness", "utilities", "medical", "education", "other"
)

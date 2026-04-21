package com.snokonoko.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.snokonoko.app.data.Budget
import com.snokonoko.app.data.Transaction
import com.snokonoko.app.repository.FinanceRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = FinanceRepository(application)

    // Observe these in your fragments - they update automatically
    val transactions = repo.allTransactions
    val budgets = repo.allBudgets

    fun addTransaction(t: Transaction) = viewModelScope.launch { repo.insertTransaction(t) }
    fun updateTransaction(t: Transaction) = viewModelScope.launch { repo.updateTransaction(t) }
    fun deleteTransaction(t: Transaction) = viewModelScope.launch { repo.deleteTransaction(t) }

    fun addBudget(b: Budget) = viewModelScope.launch { repo.insertBudget(b) }
    fun updateBudget(b: Budget) = viewModelScope.launch { repo.updateBudget(b) }
    fun deleteBudget(b: Budget) = viewModelScope.launch { repo.deleteBudget(b) }
}

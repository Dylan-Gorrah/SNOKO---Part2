package com.snokonoko.app.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.EditText
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.snokonoko.app.R
import com.snokonoko.app.data.Category
import com.snokonoko.app.data.Transaction
import com.snokonoko.app.databinding.SheetAddTransactionBinding
import com.snokonoko.app.viewmodel.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

class AddTransactionSheet : BottomSheetDialogFragment() {

    private var _binding: SheetAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    private var selectedType = "expense"
    private var userCategories: List<Category> = emptyList()

    // Selected values
    private var selectedDate: LocalDate = LocalDate.now()
    private var selectedTime: LocalTime? = null
    private var currentPhotoPath: String? = null

    // Edit mode
    private var editTransaction: Transaction? = null
    private var isEditMode = false

    companion object {
        fun newInstance(transaction: Transaction): AddTransactionSheet {
            val sheet = AddTransactionSheet()
            val args = Bundle().apply {
                putInt("id", transaction.id)
                putString("type", transaction.type)
                putString("category", transaction.category)
                putString("description", transaction.description)
                putDouble("amount", transaction.amount)
                putString("date", transaction.date)
                putString("startTime", transaction.startTime)
                putString("photoPath", transaction.photoPath)
            }
            sheet.arguments = args
            return sheet
        }
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private var tempPhotoUri: Uri? = null

    // Activity result launchers
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                savePhotoToInternalStorage(uri)?.let { path ->
                    currentPhotoPath = path
                    updatePhotoDisplay()
                }
            }
        }
    }

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            savePhotoToInternalStorage(it)?.let { path ->
                currentPhotoPath = path
                updatePhotoDisplay()
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetStyle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SheetAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check for edit mode
        arguments?.let { args ->
            if (args.containsKey("id")) {
                isEditMode = true
                val prefs = requireContext().getSharedPreferences("snokonoko_prefs", 0)
                val userId = prefs.getInt("user_id", -1)
                editTransaction = Transaction(
                    id = args.getInt("id"),
                    userId = userId,
                    type = args.getString("type") ?: "expense",
                    category = args.getString("category") ?: "",
                    description = args.getString("description") ?: "",
                    amount = args.getDouble("amount"),
                    date = args.getString("date") ?: "",
                    startTime = args.getString("startTime"),
                    endTime = null,
                    photoPath = args.getString("photoPath")
                )
                loadTransactionData()
            }
        }

        // Set initial date display
        updateDateDisplay()

        // Observe categories and update spinner
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            userCategories = categories ?: emptyList()
            updateCategorySpinner()
        }

        // Type toggle with clear visual indicator
        binding.btnExpense.setOnClickListener {
            selectedType = "expense"
            updateToggleUI()
        }
        binding.btnIncome.setOnClickListener {
            selectedType = "income"
            updateToggleUI()
        }
        updateToggleUI() // Set initial state

        // Date & Time picker - opens date then time
        binding.btnDatePicker.setOnClickListener { showDateTimePicker() }

        // Time picker (optional)
        binding.btnStartTime.setOnClickListener { showTimePicker() }

        // Clear time button
        binding.btnClearStartTime.setOnClickListener {
            selectedTime = null
            updateTimeDisplay()
        }

        // Photo button with popup menu
        binding.btnAddPhoto.setOnClickListener { showPhotoOptionsPopup() }
        binding.btnRemovePhoto.setOnClickListener {
            currentPhotoPath = null
            updatePhotoDisplay()
        }

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener { saveTransaction() }

        // Update UI for edit mode
        if (isEditMode) {
            binding.tvTitle.text = "Edit Transaction"
            binding.btnSave.text = "Update"
        }
    }

    private fun loadTransactionData() {
        editTransaction?.let { tx ->
            selectedType = tx.type
            binding.etDescription.setText(tx.description)
            binding.etAmount.setText(tx.amount.toString())
            selectedDate = LocalDate.parse(tx.date, dateFormatter)
            selectedTime = tx.startTime?.let { LocalTime.parse(it, timeFormatter) }
            currentPhotoPath = tx.photoPath
            updateDateDisplay()
            updateTimeDisplay()
            updatePhotoDisplay()
            updateToggleUI()
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        calendar.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate = LocalDate.of(year, month + 1, day)
                updateDateDisplay()
                // After date is selected, show time picker
                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        selectedTime?.let {
            calendar.set(Calendar.HOUR_OF_DAY, it.hour)
            calendar.set(Calendar.MINUTE, it.minute)
        }

        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                selectedTime = LocalTime.of(hour, minute)
                updateTimeDisplay()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        ).show()
    }

    private fun updateDateDisplay() {
        binding.btnDatePicker.text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
    }

    private fun updateTimeDisplay() {
        binding.btnStartTime.text = selectedTime?.format(timeFormatter) ?: "--:--"
    }

    private fun showPhotoOptionsPopup() {
        val popup = PopupMenu(requireContext(), binding.btnAddPhoto)
        popup.menu.add("Take Photo")
        popup.menu.add("Choose from Gallery")
        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Take Photo" -> checkCameraPermissionAndLaunch()
                "Choose from Gallery" -> pickPhotoLauncher.launch("image/*")
            }
            true
        }
        popup.show()
    }

    private fun saveTransaction() {
        val desc = binding.etDescription.text.toString().trim()
        val amtStr = binding.etAmount.text.toString()
        val amt = amtStr.toDoubleOrNull()

        if (desc.isEmpty() || amt == null || amt <= 0) {
            android.widget.Toast.makeText(requireContext(), "Please fill in description and amount.", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val cat = if (selectedType == "income") {
            "income"
        } else {
            val catIdx = binding.spinnerCategory.selectedItemPosition
            if (userCategories.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Please create a category first.", android.widget.Toast.LENGTH_SHORT).show()
                return
            }
            userCategories[catIdx].name
        }

        val prefs = requireContext().getSharedPreferences("snokonoko_prefs", 0)
        val userId = prefs.getInt("user_id", -1)
        val transaction = Transaction(
            id = if (isEditMode) editTransaction?.id ?: 0 else 0,
            userId = userId,
            type = selectedType,
            category = cat,
            description = desc,
            amount = amt,
            date = selectedDate.format(dateFormatter),
            startTime = selectedTime?.format(timeFormatter),
            endTime = null,
            photoPath = currentPhotoPath
        )

        if (isEditMode) {
            viewModel.updateTransaction(transaction)
        } else {
            viewModel.addTransaction(transaction)
        }
        dismiss()
    }

    private fun updateToggleUI() {
        if (selectedType == "expense") {
            binding.btnExpense.setBackgroundColor(android.graphics.Color.parseColor("#F49AC2"))
            binding.btnExpense.setTextColor(android.graphics.Color.WHITE)
            binding.btnIncome.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.btnIncome.setTextColor(android.graphics.Color.parseColor("#8C8C8C"))
            binding.spinnerCategory.visibility = View.VISIBLE
            binding.tvTypeBadge.text = "EXPENSE"
        } else {
            binding.btnIncome.setBackgroundColor(android.graphics.Color.parseColor("#F49AC2"))
            binding.btnIncome.setTextColor(android.graphics.Color.WHITE)
            binding.btnExpense.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            binding.btnExpense.setTextColor(android.graphics.Color.parseColor("#8C8C8C"))
            binding.spinnerCategory.visibility = View.GONE
            binding.tvTypeBadge.text = "INCOME"
        }
    }

    private fun updateCategorySpinner() {
        val categoryNames = mutableListOf<String>()
        categoryNames.addAll(userCategories.map { it.name })
        categoryNames.add("+ Create New Category")

        if (categoryNames.size == 1) {
            binding.spinnerCategory.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                listOf("No categories - tap to create")
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        } else {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            binding.spinnerCategory.adapter = adapter
            binding.spinnerCategory.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == categoryNames.size - 1) {
                        showCreateCategoryDialog()
                    }
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
        }
    }

    private fun showCreateCategoryDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "Category name"
            setTextColor(android.graphics.Color.WHITE)
            setHintTextColor(android.graphics.Color.parseColor("#8C8C8C"))
            setBackgroundResource(android.R.drawable.edit_text)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Create New Category")
            .setView(editText)
            .setPositiveButton("Create") { dialog: android.content.DialogInterface, which: Int ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    val prefs = requireContext().getSharedPreferences("snokonoko_prefs", 0)
                    val userId = prefs.getInt("user_id", -1)
                    viewModel.addCategory(Category(userId = userId, name = name, colour = "#8E8E93"))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Photo handling functions
    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                Toast.makeText(requireContext(), "Camera permission is needed to take photos", Toast.LENGTH_SHORT).show()
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
            else -> {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val photoFile = createImageFile()
        photoFile?.let { file ->
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            tempPhotoUri = photoUri
            takePhotoLauncher.launch(photoUri)
        }
    }

    private fun createImageFile(): File? {
        return try {
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("temp_", ".jpg", storageDir)
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun savePhotoToInternalStorage(sourceUri: Uri): String? {
        return try {
            val bitmap = requireContext().contentResolver.openInputStream(sourceUri)?.use { input ->
                BitmapFactory.decodeStream(input)
            } ?: return null

            // Resize bitmap to reasonable size
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800 * bitmap.height / bitmap.width, true)

            val filename = "receipt_${UUID.randomUUID()}.jpg"
            val file = File(requireContext().filesDir, filename)

            FileOutputStream(file).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }

            file.absolutePath
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error saving photo", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun updatePhotoDisplay() {
        if (currentPhotoPath != null) {
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            binding.ivPhotoThumbnail.setImageBitmap(bitmap)
            binding.ivPhotoThumbnail.visibility = View.VISIBLE
            binding.btnRemovePhoto.visibility = View.VISIBLE
        } else {
            binding.ivPhotoThumbnail.setImageDrawable(null)
            binding.ivPhotoThumbnail.visibility = View.GONE
            binding.btnRemovePhoto.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

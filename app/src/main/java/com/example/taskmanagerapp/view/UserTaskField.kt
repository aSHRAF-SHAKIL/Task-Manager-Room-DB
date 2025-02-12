package com.example.taskmanagerapp.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.taskmanagerapp.databinding.ActivityUserTaskFieldBinding
import com.example.taskmanagerapp.db.Database
import com.example.taskmanagerapp.db.TaskDao
import com.example.taskmanagerapp.db.TaskEntity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class UserTaskField : AppCompatActivity() {

    private lateinit var binding: ActivityUserTaskFieldBinding
    private lateinit var dao: TaskDao
    private var dueDate: String = ""  // Declaring dueDate globally

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserTaskFieldBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //db instance
        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "tasks"
        ).allowMainThreadQueries().build()

        dao = db.taskDao()

        binding.wordCount2.text =
                "Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())}"
        // Date Picker Functionality
        binding.userDueDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build()

            datePicker.show(supportFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val selectedDate = Date(selection)
                dueDate = sdf.format(selectedDate) //Store selected date

                val currentDate = Date()
                val diffInMillis = selectedDate.time - currentDate.time
                val remainingDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                // Update UI
                binding.dueDateText.text =
                    "Selected Date: $dueDate\n Remaining Days: $remainingDays"

            }
        }


        //EditText Character Count Functionality
        binding.descriptionInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.wordCount.text = "\t Character Count: ${s?.length ?: 0}"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Add Task Button Click
        binding.addTaskInsertBtn.setOnClickListener {
            val title = binding.titleInput.text.toString().trim()
            val description = binding.descriptionInput.text.toString().trim()
            val isCompleted = binding.taskCheckboxBInput.isChecked  // Keep it as Boolean


            // Prevent Empty Inputs
            if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insert Task into DB
            insertTask(title, description, dueDate, isCompleted)

            Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show()

            // Clear Inputs
            binding.titleInput.text.clear()
            binding.descriptionInput.text.clear()
            binding.dueDateText.text = ""
            binding.taskCheckboxBInput.isChecked = false

            finish()
        }


        binding.titleInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.titleTv.text = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Optional: Handle before text changes if needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Optional: Handle text changes if needed
            }
        })


    }

    // Insert Task Function
    private fun insertTask(
        title: String,
        description: String,
        dueDate: String,
        isCompleted: Boolean
    ) {
        val task =
            TaskEntity(0, title, description, dueDate, isCompleted) // Ensure correct data types
        dao.insertTask(task)
    }
}

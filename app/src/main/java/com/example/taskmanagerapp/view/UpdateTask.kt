package com.example.taskmanagerapp.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.databinding.ActivityUpdateTaskBinding
import com.example.taskmanagerapp.db.Database
import com.example.taskmanagerapp.db.TaskDao
import com.example.taskmanagerapp.db.TaskEntity
import com.google.android.gms.gcm.Task

class UpdateTask : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateTaskBinding

    private lateinit var dao:TaskDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //db instance
        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "tasks"
        ).allowMainThreadQueries().build()

        dao = db.taskDao()

        val taskId = intent.getIntExtra("TASK_ID", -1)

        val taskList = dao.getTaskById(taskId)


        if (taskId != -1) {
            // Fetch and display the task details using the ID
            Log.d("UpdateTask", "Selected Task ID: $taskId")
        }

        // Retrieve data from intents
        val idOfTask = intent.getIntExtra("TASK_ID",-1)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: ""
        val taskDesc = intent.getStringExtra("TASK_DESC") ?: ""
        val taskDueDate = intent.getStringExtra("TASK_DUE_DATE") ?: ""
        binding.updateCheckBox.isChecked = intent.getBooleanExtra("check",false)

        // Set data in EditText

        binding.titleInputUpdate.setText(taskTitle)
        binding.descriptionInputUpdate.setText(taskDesc)
        binding.dateInputUpdate.setText(taskDueDate)
        val updateCheckState = binding.updateCheckBox.isChecked

        binding.TaskUpdateBtn.setOnClickListener {
            val updatedTitle = binding.titleInputUpdate.text.toString()
            val updatedDesc = binding.descriptionInputUpdate.text.toString()
            val updatedDueDate = binding.dateInputUpdate.text.toString()
            val updatedCheckState = binding.updateCheckBox.isChecked // Boolean value directly

            // Basic validation (optional but recommended)
            if (updatedTitle.isEmpty() || updatedDesc.isEmpty() || updatedDueDate.isEmpty()) {
                // Show error message or notify the user about missing fields
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the updated task object
            val updateTask = TaskEntity(
                id = taskId, // Assume taskId is passed from the previous activity
                title = updatedTitle,
                description = updatedDesc,
                dueDate = updatedDueDate,
                isCompleted = updatedCheckState
            )

            // Perform database update on a background thread
            Thread {
                // Update the task in the database
                dao.updateTask(updateTask)
//                Toast.makeText(this, "Update done!", Toast.LENGTH_SHORT).show()

                // After the database update, return to the previous screen
                runOnUiThread {

                    finish() // Close the activity to return to the previous one
                }
            }.start()
        }
        binding.TaskDeleteBtn.setOnClickListener{
            dao.deleteById(idOfTask)

            finish()
        }


    }
}
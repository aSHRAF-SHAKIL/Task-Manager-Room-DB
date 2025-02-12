package com.example.taskmanagerapp.view

import TaskAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.databinding.ActivityMainBinding
import com.example.taskmanagerapp.db.Database
import com.example.taskmanagerapp.db.TaskDao
import com.example.taskmanagerapp.db.TaskEntity

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var dao: TaskDao
    private lateinit var taskAdapter: TaskAdapter
    private var taskList: List<TaskEntity> = listOf() // Initialize as empty list


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Database instance
        val db = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "tasks"
        ).allowMainThreadQueries().build()

        dao = db.taskDao()

        // Fetch tasks data from database

        taskList = dao.getAllTasks()

        //for update


        // Log.d("RecyclerViewDebug", "Task List Size: ${taskList.size}")

        // Set up RecyclerView this method is added because of the error (No layout manager attached; skipping layout) also added taskAdapter and task list as global

        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this) //LayoutManager added
        taskAdapter = TaskAdapter(this,
            taskList.toMutableList()
        ){ taskId ->
            val intent = Intent(this, UpdateTask::class.java)
            intent.putExtra("TASK_ID", taskId) // Passing task ID
            startActivity(intent)

        }
        binding.taskRecyclerView.adapter = taskAdapter







        //enabling a second activity for insert data
        binding.taskInputView.setOnClickListener {
            val intent = Intent(this@MainActivity, UserTaskField::class.java)
            startActivity(intent)
        }
    }


}



import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database
import com.example.taskmanagerapp.databinding.TaskDesignBinding
import com.example.taskmanagerapp.db.TaskEntity
import com.example.taskmanagerapp.view.UpdateTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class TaskAdapter(
    private val context: Context,
    private val taskList: MutableList<TaskEntity>, // Mutable list to modify the list
    private val onItemClick: (Int) -> Unit // Passing the clicked item's ID
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount() = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.binding.apply {
            RVtaskTitleTV.text = task.title

            // Limit description to 33 characters
            val truncatedDescription = if (task.description.length > 33) {
                task.description.substring(0, 33) + "..."
            } else {
                task.description
            }

            RVtaskDescriptionTV.text = truncatedDescription
            RVtaskCheckboxB.isChecked = task.isCompleted

            // Handle the due date
            val sdf = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
            try {
                val dueDate = sdf.parse(task.dueDate)
                val currentDate = Date()
                val diffInMillis = dueDate!!.time - currentDate.time
                val remainingDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                RVtaskDueDateTV.text = if (remainingDays > 0) {
                    if (task.isCompleted) "Done" else "$remainingDays days to go"
                } else {
                    if (task.isCompleted) "Done" else "Time Exceeded Selected date: ${task.dueDate}"
                }
            } catch (e: Exception) {
                RVtaskDueDateTV.text = "Invalid Date"
                e.printStackTrace()
            }

            // Set up long-click listener to show the delete confirmation dialog
            root.setOnLongClickListener {
                showDeleteConfirmationDialog(task, position, context) // Pass context from itemView
                true // Return true to indicate the event is handled
            }
        }
        holder.bind(task) // Pass TaskEntity, not position
        
    }

    private fun showDeleteConfirmationDialog(task: TaskEntity, position: Int, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to delete this task?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                deleteTask(task, position, context)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog if "No" is clicked
            }
        val alert = builder.create()
        alert.show()
    }

    private fun deleteTask(task: TaskEntity, position: Int, context: Context) {
        // Remove the item from the list (RecyclerView)
        taskList.removeAt(position)
        notifyItemRemoved(position) // Notify the RecyclerView to update


    }

    inner class TaskViewHolder(val binding: TaskDesignBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskEntity) { // Pass TaskEntity instead of Int
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, UpdateTask::class.java)
                intent.putExtra("TASK_ID", task.id) // Passing Task ID
                intent.putExtra("TASK_TITLE", task.title) // Passing title
                intent.putExtra("TASK_DESC", task.description) // Passing description
                intent.putExtra("TASK_DUE_DATE", task.dueDate) // Passing due date
                intent.putExtra("check", task.isCompleted)
                itemView.context.startActivity(intent)
            }
        }
    }
}

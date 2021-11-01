package com.lunat.todolist

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lunat.todolist.data.Task
import com.lunat.todolist.databinding.FragmentTaskDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TaskDetailFragment : Fragment() {

    lateinit var task: Task

    private val navigationArgs: TaskDetailFragmentArgs by navArgs()

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory(
            (activity?.application as TaskApplication).database.taskDao()
        )
    }

    private fun bind(task: Task) {
        binding.apply {
            titulo.text = task.title
            detalhes.text = task.detalhes
            detalhes.movementMethod = ScrollingMovementMethod()
            data.text = task.data
            hora.text = task.hora
            more.setOnClickListener { showPopup() }
            editTask.setOnClickListener { editTask() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.taskId
        viewModel.retrieveTask(id).observe(this.viewLifecycleOwner) { selectedTask ->
            task = selectedTask
            bind(task)
        }
    }

    private fun showPopup() {
        val more = binding.more
        val popupMenu = PopupMenu(more.context, more)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.completed -> showCompletedConfirmationDialog()
                R.id.edit -> editTask()
                R.id.duplicate -> duplicateTask()
                R.id.delete -> showDeleteConfirmationDialog()
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteTask()
            }
            .show()
    }

    private fun deleteTask() {
        viewModel.deleteTask(task)
        findNavController().navigateUp()
    }

    private fun editTask() {
        val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToAddTaskFragment(
            getString(R.string.edit_fragment_title),
            task.id
        )
        this.findNavController().navigate(action)
    }

    private fun showCompletedConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_alert_title))
            .setMessage(getString(R.string.complete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                completedTask()
            }
            .show()
    }

    private fun completedTask() {
        if (isEntryValid()) {
            viewModel.updateTask(
                this.navigationArgs.taskId,
                this.binding.titulo.text.toString(),
                this.binding.detalhes.text.toString(),
                this.binding.data.text.toString(),
                this.binding.hora.text.toString(),
                true
            )
            findNavController().navigateUp()
        }
    }

    private fun duplicateTask() {
        if (isEntryValid()) {
            viewModel.addNewTask(
                binding.titulo.text.toString(),
                binding.detalhes.text.toString(),
                binding.data.text.toString(),
                binding.hora.text.toString(),
                task.completed
            )
            findNavController().navigateUp()
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.titulo.text.toString(),
            binding.data.text.toString(),
            binding.hora.text.toString(),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
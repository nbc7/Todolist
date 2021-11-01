package com.lunat.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunat.todolist.databinding.TaskListFragmentBinding

class TaskListFragment : Fragment() {

    private var _binding: TaskListFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory(
            (activity?.application as TaskApplication).database.taskDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TaskListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.getStringArray("listarray")

        val adapter = TaskListAdapter {
            val action =
                TaskListFragmentDirections.actionTaskListFragmentToTaskDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        if(binding.itemNameMenu.text.isBlank()){
            viewModel.allTasks.observe(this.viewLifecycleOwner) { tasks ->
                tasks.let {
                    adapter.submitList(it)
                }
                binding.includeEmpty.emptyState.visibility = if (tasks.isEmpty()) View.VISIBLE
                else View.GONE
                binding.itemNameMenu.setText(R.string.label_Tarefas)
                binding.itemNameMenu.updateLayoutParams { binding.itemNameMenu }
            }
        }

        val popupMenu = PopupMenu(binding.itemNameMenu.context, binding.itemNameMenu)
        popupMenu.menuInflater.inflate(R.menu.task_list, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.tarefas -> {
                    viewModel.allTasks.observe(this.viewLifecycleOwner) { tasks ->
                        tasks.let {
                            adapter.submitList(it)
                        }
                        binding.includeEmpty.emptyState.visibility = if (tasks.isEmpty()) View.VISIBLE
                        else View.GONE
                        binding.itemNameMenu.setText(R.string.label_Tarefas)
                        binding.itemNameMenu.updateLayoutParams { binding.itemNameMenu }
                    }
                }
                R.id.concluido ->{
                    viewModel.completedTasks.observe(this.viewLifecycleOwner) { tasks ->
                        tasks.let {
                            adapter.submitList(it)
                        }
                        binding.includeEmpty.emptyState.visibility = View.GONE
                        binding.itemNameMenu.setText(R.string.label_Concluido)
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
        binding.itemNameMenu.setText(R.string.label_Tarefas)
        binding.itemNameMenu.setOnClickListener{ popupMenu.show() }

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.btnAdicionar.setOnClickListener {
            val action = TaskListFragmentDirections.actionTaskListFragmentToAddTaskFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }
    }
}
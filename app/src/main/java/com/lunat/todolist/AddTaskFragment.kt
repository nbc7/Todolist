package com.lunat.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lunat.todolist.data.Task
import com.lunat.todolist.databinding.FragmentAddTaskBinding
import com.lunat.todolist.extensions.text
import java.text.SimpleDateFormat
import java.util.*

class AddTaskFragment : Fragment() {

    private val navigationArgs: TaskDetailFragmentArgs by navArgs()
    lateinit var task: Task

    private val viewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory(
            (activity?.application as TaskApplication).database.taskDao()
        )
    }

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!

    private fun bind(task: Task) {
        binding.apply {
            taskTitle.setText(task.title, TextView.BufferType.SPANNABLE)
            taskDetalhes.setText(task.detalhes, TextView.BufferType.SPANNABLE)
            taskData.setText(task.data, TextView.BufferType.SPANNABLE)
            taskHora.setText(task.hora, TextView.BufferType.SPANNABLE)
            taskData.setOnClickListener { getDate(requireContext()) }
            taskHora.setOnClickListener { getTime(requireContext()) }
            cancelar.setOnClickListener {
                val action = AddTaskFragmentDirections.actionAddTaskFragmentToTaskListFragment()
                findNavController().navigate(action)
            }
            concluir.setOnClickListener { updateTask() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.titulo.text,
            binding.data.text,
            binding.hora.text,
        )
    }

    private fun addNewTask() {
        if (isEntryValid()) {
            viewModel.addNewTask(
                binding.titulo.text,
                binding.detalhes.text,
                binding.data.text,
                binding.hora.text,
                false
            )
            val action = AddTaskFragmentDirections.actionAddTaskFragmentToTaskListFragment()
            findNavController().navigate(action)
        }
    }

    private fun updateTask() {
        if (isEntryValid()) {
            viewModel.updateTask(
                this.navigationArgs.taskId,
                this.binding.titulo.text,
                this.binding.detalhes.text,
                this.binding.data.text,
                this.binding.hora.text,
                this.task.completed
            )
            val action = AddTaskFragmentDirections.actionAddTaskFragmentToTaskListFragment()
            findNavController().navigate(action)
        }
    }

    private fun getDate(context: Context) {
        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                binding.taskData.setText(sdf.format(myCalendar.time))
            }

        DatePickerDialog(
            context, datePickerOnDataSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getTime(context: Context) {
        val myCalendar = Calendar.getInstance()
        val timePickerOnTimeSetListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hour)
                myCalendar.set(Calendar.MINUTE, minute)
                val stf = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
                binding.taskHora.setText(stf.format(myCalendar.time))
            }

        TimePickerDialog(
            context,
            timePickerOnTimeSetListener,
            myCalendar.get(Calendar.HOUR),
            myCalendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.taskId
        if (id > 0) {
            viewModel.retrieveTask(id).observe(this.viewLifecycleOwner) { selectedTask ->
                task = selectedTask
                bind(task)
            }
        } else {
            binding.taskData.setOnClickListener { getDate(requireContext()) }
            binding.taskHora.setOnClickListener { getTime(requireContext()) }
            binding.cancelar.setOnClickListener {
                val action = AddTaskFragmentDirections.actionAddTaskFragmentToTaskListFragment()
                findNavController().navigate(action)
            }
            binding.concluir.setOnClickListener {
                addNewTask()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
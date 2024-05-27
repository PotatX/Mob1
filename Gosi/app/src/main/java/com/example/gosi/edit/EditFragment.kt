package com.example.gosi.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.navigation.findNavController
import com.example.gosi.R
import com.example.gosi.data.MyDatabase
import com.example.gosi.models.CarState
import com.example.gosi.models.Item

class EditFragment : Fragment() {

    private val viewModel = EditViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var wasItem = false
        val db = MyDatabase.getDatabase(requireContext())

        val id = requireArguments().getInt("itemId")

        updateSpinner(view)

        if (id > 0) {
            val item = db.itemDao().get(id)
            setItem(item)
            wasItem = true;
        }

        view.apply {
            findViewById<EditText>(R.id.editTextName)?.doOnTextChanged { text, start, before, count ->
                viewModel.name = text.toString()
            }

            findViewById<EditText>(R.id.editTextClient)?.doOnTextChanged { text, start, before, count ->
                viewModel.client = text.toString()
            }

            findViewById<Spinner>(R.id.spinnerForState)?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val stateString = parent.getItemAtPosition(position).toString()
                    try {
                        val carState = CarState.valueOf(stateString)
                        viewModel.state = carState.toString()

                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    if (!wasItem){
                        viewModel.state = CarState.None.toString()
                    }
                }
            }

            findViewById<Button>(R.id.buttonSave)?.setOnClickListener {

                val clientFio = if (CarState.valueOf(viewModel.state) == CarState.InUse) viewModel.client else ""
                if (id > 0) {
                    val newItem = Item(viewModel.name, viewModel.state, clientFio, id)
                    db.itemDao().update(newItem)

                } else {
                    val newItem = Item(viewModel.name, viewModel.state, clientFio)
                    db.itemDao().insert(newItem)
                }

                findNavController().popBackStack()
            }

            findViewById<Button>(R.id.buttonDelete)?.setOnClickListener {
                if (wasItem){
                    val item = db.itemDao().get(id)
                    db.itemDao().delete(item)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateSpinner(view: View){
        val items = CarState.entries.map { it.name }
        val adapter = ArrayAdapter(requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items)
        view.findViewById<Spinner>(R.id.spinnerForState).adapter = adapter
    }

    private fun setItem(item: Item){
        view?.apply {
            findViewById<EditText>(R.id.editTextName).setText(item.name)
            findViewById<Spinner>(R.id.spinnerForState).setSelection(CarState.valueOf(item.state).ordinal)
            findViewById<EditText>(R.id.editTextClient).setText(item.client)
        }

        viewModel.name = item.name
        viewModel.state = item.state
        viewModel.client = item.client
    }
}
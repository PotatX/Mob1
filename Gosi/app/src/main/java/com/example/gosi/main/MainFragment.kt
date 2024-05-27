package com.example.gosi.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.gosi.R
import com.example.gosi.data.MyDatabase
import com.example.gosi.models.Item
import com.example.gosi.report.ReportActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray

class MainFragment : Fragment() {

    private lateinit var arrayAdapter: ArrayAdapter<Item>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = MyDatabase.getDatabase(requireContext())
        val dataList = db.itemDao().getAll()

        arrayAdapter = ArrayAdapter<Item>(requireContext(), android.R.layout.simple_list_item_1)

        view.apply {
            findViewById<Button>(R.id.buttonAdd).setOnClickListener{
                findNavController().navigate(
                    R.id.action_mainFragment_to_editFragment,
                    Bundle().apply {
                        putInt("itemId", -1)
                    }
                )
            }

            findViewById<ListView>(R.id.list_items).apply{
                adapter = arrayAdapter
                setOnItemClickListener{ adapterView, view, position, id ->
                    val item = dataList[position]

                    findNavController().navigate(R.id.action_mainFragment_to_editFragment,
                        Bundle().apply {
                        putInt("itemId", item.id!!)
                    })
                }
            }

            findViewById<Button>(R.id.buttonExport).setOnClickListener {
                writeJson(dataList)
            }

            findViewById<Button>(R.id.buttonImport).setOnClickListener {
                readJson(db)

                arrayAdapter.clear()
                arrayAdapter.addAll(db.itemDao().getAll())
                updateCount(view, arrayAdapter.count)
            }

            findViewById<Button>(R.id.buttonReport).setOnClickListener {
                val i = Intent(activity, ReportActivity::class.java)
                startActivity(i)
            }
        }

        arrayAdapter.addAll(db.itemDao().getAll())
        updateCount(view, arrayAdapter.count)
    }

    private fun updateCount(view: View, count: Int){
        view.findViewById<TextView>(R.id.textViewCount)?.text = "Count: = $count"
    }

    private fun readJson(db: MyDatabase) {
        val strJson = requireContext().openFileInput("data.json")
            .bufferedReader().use { it.readText() }

        val items = Json.decodeFromString<List<Item>>(strJson)

        db.itemDao().deleteAll()
        db.itemDao().insertAll(items)
    }

    private fun writeJson(item: List<Item>) {
        val itemsStr = Json.encodeToString(value = item)

        requireContext().openFileOutput("data.json", Context.MODE_PRIVATE).use {
                it.write(itemsStr.toByteArray())
            }
    }
}
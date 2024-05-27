package com.example.gosi.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.gosi.R
import com.example.gosi.data.MyDatabase
import com.example.gosi.models.Item

class ReportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = MyDatabase.getDatabase(requireContext())
        val isNewItems = db.itemDao().loadNew()
        val inUseItems = db.itemDao().loadInUse()
        val isDeletedItems = db.itemDao().loadWasDeleted()

        val arrayAdapterIsNew = ArrayAdapter<Item>(requireContext(), android.R.layout.simple_list_item_1)
        arrayAdapterIsNew.addAll(isNewItems)

        val arrayAdapterInUse = ArrayAdapter<Item>(requireContext(), android.R.layout.simple_list_item_1)
        arrayAdapterInUse.addAll(inUseItems)

        val arrayAdapterWasDeleted = ArrayAdapter<Item>(requireContext(), android.R.layout.simple_list_item_1)
        arrayAdapterWasDeleted.addAll(isDeletedItems)

        view.findViewById<ListView>(R.id.report_list_isNew)?.adapter = arrayAdapterIsNew
        view.findViewById<ListView>(R.id.report_list_inUse)?.adapter = arrayAdapterInUse
        view.findViewById<ListView>(R.id.report_list_isDeleted)?.adapter = arrayAdapterWasDeleted
    }
}
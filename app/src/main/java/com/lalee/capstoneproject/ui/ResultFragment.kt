package com.lalee.capstoneproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.model.TrashResult
import com.lalee.capstoneproject.viewmodel.TrashResultViewModel
import kotlinx.android.synthetic.main.item_result.*

class ResultFragment: Fragment() {

    private val trashResultViewModel: TrashResultViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(activity, "DIT IS DE RESULTS", Toast.LENGTH_SHORT).show()
        //observeTrashResult()
    }

    private fun observeTrashResult() {
        trashResultViewModel.trashImage.observe(viewLifecycleOwner, {

        })
    }
}
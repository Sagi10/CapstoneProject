package com.lalee.capstoneproject.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.adapters.ResultAdapter
import com.lalee.capstoneproject.model.TrashType
import com.lalee.capstoneproject.viewmodel.MyFirebaseViewModel
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment: Fragment() {

    private val posts = arrayListOf<TrashType>()
    private val resultAdapter = ResultAdapter(posts, ::onClickMoreInfo)

    private val myFirebaseViewModel: MyFirebaseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myFirebaseViewModel.getAllPosts()
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

        checkPosts()
        observePostsHistoryResult()
        rv_posts_history.adapter = resultAdapter

        fab_camera_result.setOnClickListener {
            findNavController().navigate(R.id.action_ResultFragment_to_CameraFragment)
        }

    }

    private fun observePostsHistoryResult() {
        myFirebaseViewModel.posts.observe(viewLifecycleOwner, {
            tv_result_items.isVisible = false
            pb_loading_posts.isVisible = false
            fab_camera_result.isVisible = true
            this@ResultFragment.posts.clear()
            this@ResultFragment.posts.addAll(it)
            resultAdapter.notifyDataSetChanged()
        })

    }

    private fun onClickMoreInfo(trashType: TrashType) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tip")
            .setMessage(trashType.tip)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        checkPosts()
    }

    private fun checkPosts(){
        if (posts.isNullOrEmpty()){
            tv_result_items.isVisible = true
            pb_loading_posts.isVisible = false
            fab_camera_result.isVisible = true
        }
    }

}
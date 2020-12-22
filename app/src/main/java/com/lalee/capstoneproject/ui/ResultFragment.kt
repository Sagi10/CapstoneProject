package com.lalee.capstoneproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.adapters.ResultAdapter
import com.lalee.capstoneproject.model.TrashType
import com.lalee.capstoneproject.viewmodel.MyFirebaseViewModel
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment: Fragment() {

    private val posts = arrayListOf<TrashType>()
    private val resultAdapter = ResultAdapter(posts)

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

        fab_camera_result.setOnClickListener {
            findNavController().navigate(R.id.action_ResultFragment_to_CameraFragment)
        }
        rv_posts_history.adapter = resultAdapter

        if (posts.isNullOrEmpty()){
            tv_result_items.isVisible = true
            pb_loading_posts.isVisible = false
            fab_camera_result.isVisible = true
        }

        observePostsHistoryResult()
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

//    override fun onStop() {
//
//        Toast.makeText(activity, "ON STOP CALLED", Toast.LENGTH_SHORT).show()
//        pb_loading_posts.isVisible = true
//        fab_camera_result.isVisible = false
//        tv_result_items.isVisible = false
//        super.onStop()
//    }

}
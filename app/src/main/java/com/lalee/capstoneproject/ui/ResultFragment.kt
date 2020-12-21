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
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.adapters.ResultAdapter
import com.lalee.capstoneproject.model.TrashType
import com.lalee.capstoneproject.viewmodel.CustomVisionViewModel
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

        observePostsHistoryResult()

        rv_posts_history.adapter = resultAdapter
    }

    private fun observePostsHistoryResult() {
        myFirebaseViewModel.posts.observe(viewLifecycleOwner, {
            this@ResultFragment.posts.clear()
            this@ResultFragment.posts.addAll(it)
            pb_loading_posts.isVisible = false
            resultAdapter.notifyDataSetChanged()
        })
    }

}
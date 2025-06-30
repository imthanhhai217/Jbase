package com.juhalion.base.mvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.callscreen.caller.basemvvm.R
import com.callscreen.caller.basemvvm.databinding.FragmentHomeBinding
import com.juhalion.base.mvvm.adapters.CommentAdapter
import com.juhalion.base.mvvm.networking.ApiResponse
import com.juhalion.base.mvvm.ui.comments.MainActivity
import com.juhalion.base.mvvm.base.BaseFragment
import com.juhalion.base.mvvm.ui.comments.CommentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val commentViewModel: CommentViewModel by activityViewModels()
    private lateinit var commentAdapter: CommentAdapter
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentHomeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setTitle(getString(R.string.tool_bar_title))
        getData()
    }

    private fun getData() {
        commentViewModel.getComment()
        commentViewModel.commentData.observe(viewLifecycleOwner) {response->
            when (response) {
                is ApiResponse.Success -> {
                    dismissLoadingDialog()
                    commentAdapter = CommentAdapter()
                    response.data?.let {responseData->
                        commentAdapter.updateData(responseData.comments)
                        binding.rvDemo.layoutManager = LinearLayoutManager(requireActivity())
                        binding.rvDemo.adapter = commentAdapter

                        commentAdapter.listener = { view, item, position ->
                            when (view.id) {
                                R.id.tvComment -> showToast()
                            }
                        }
                    }
                }

                is ApiResponse.Failed -> {
                    dismissLoadingDialog()
                }

                is ApiResponse.Loading -> {
                    showLoadingDialog()
                }
            }
        }
    }

    private fun showToast() {

    }
}
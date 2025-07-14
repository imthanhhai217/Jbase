package com.juhalion.base.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.juhalion.bae.base.BaseFragment
import com.juhalion.bae.networking.ApiResponse
import com.juhalion.base.R
import com.juhalion.base.adapters.CommentAdapterSingleType
import com.juhalion.base.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var commentAdapterSingleType: CommentAdapterSingleType

    override fun inflateBinding(
            inflater: LayoutInflater,
            container: ViewGroup?,
    ) = FragmentHomeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        throw Exception("Test crashlytics")
        getCommentData()
        observeViewModel()
    }

    private fun observeViewModel() {
        homeViewModel.commentData.observe(viewLifecycleOwner) { commentDataEvent ->
            commentDataEvent.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        commentAdapterSingleType = CommentAdapterSingleType()
                        response.data?.let { responseData ->
                            commentAdapterSingleType.updateData(responseData.comments)
                            binding.rvDemo.layoutManager = LinearLayoutManager(requireActivity())
                            binding.rvDemo.adapter = commentAdapterSingleType

                            commentAdapterSingleType.listener = { view, item, position ->
                                when (view.id) {
                                    R.id.tvComment -> {
                                        //Todo do something
                                    }
                                }
                            }
                        }
                    }

                    is ApiResponse.Failed  -> {
//                        dismissLoadingDialog()
                    }

                    is ApiResponse.Loading -> {
//                        showLoadingDialog()
                    }
                }
            }
        }
    }

    private fun getCommentData() {
        homeViewModel.getComment()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
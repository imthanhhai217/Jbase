package com.juhalion.base.ui.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.juhalion.bae.base.BaseFragment
import com.juhalion.base.databinding.FragmentProfileBinding
import com.juhalion.base.models.product.Product
import com.juhalion.base.models.user.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentProfileBinding.inflate(layoutInflater, container, false)

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.getListUser()
        profileViewModel.getListProduct()

        profileViewModel.upsertUser(User(1, "Ju nè"))
        profileViewModel.upsertUser(User(2, "Hải nè"))


//        profileViewModel.upsertUser(User(1, "Ju nè update"))
//        profileViewModel.deleteUser(2)

        profileViewModel.upsertProduct(
            Product(
                name = "Sample Product", price = 99.99, inStock = true, image = listOf(
                    "https://fastly.picsum.photos/id/5/5000/3334.jpg?hmac=R_jZuyT1jbcfBlpKFxAb0Q3lof9oJ0kREaxsYV3MgCc",
                    "https://fastly.picsum.photos/id/8/5000/3333.jpg?hmac=OeG5ufhPYQBd6Rx1TAldAuF92lhCzAhKQKttGfawWuA"
                )

            )
        )
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                profileViewModel.userData.collectLatest { data ->
                    Log.d(TAG, "observeViewModel: userData ${data}")
                    // Show lên view, etc,...
                }
            }
            launch {
                profileViewModel.productData.collectLatest { data ->
                    Log.d(TAG, "observeViewModel: productData ${data}")
                }
            }
        }

        profileViewModel.demoLiveData.observe(viewLifecycleOwner) {
            //Tính toán logic
        }

        profileViewModel.singleLiveData.observe(viewLifecycleOwner) { singleEvent ->
            singleEvent.getContentIfNotHandled()?.let { data ->
                //Đọc được dữ liệu
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
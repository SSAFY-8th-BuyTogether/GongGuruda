package com.buy.together.ui.view.address

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.buy.together.R
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentAddressSearchBinding
import com.buy.together.ui.base.BaseBottomSheetDialogFragment
import com.buy.together.ui.viewmodel.AddressViewModel
import com.buy.together.util.AddressUtils
import com.buy.together.util.CommonUtils.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressSearchFragment() : BaseBottomSheetDialogFragment<FragmentAddressSearchBinding>(FragmentAddressSearchBinding::inflate) {
    private val viewModel: AddressViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation;
    }

    override fun initView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(BridgeInterface(), "Android")
            webViewClient = object : WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.webView.loadUrl(getString(R.string.url_address_search_execute))
                }
            }
            loadUrl(getString(R.string.url_address_search))
        }
    }

    override fun setEvent() { }


    private inner class BridgeInterface(){
        @JavascriptInterface
        fun processDATA(data : String){
            val latLng = AddressUtils.getPointsFromGeo(this@AddressSearchFragment.requireContext(), data)
            val addressDto = AddressDto(
                address = AddressUtils.getRepresentAddress(data),
                addressDetail = data
            ).apply {
                latLng?.let {
                    lat = it.latitude
                    lng = it.longitude
                }
            }
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    viewModel.addAddress(addressDto).observe(viewLifecycleOwner){ response ->
                        when(response){
                            is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                            is FireStoreResponse.Success -> {
                                this@AddressSearchFragment.dismiss()
                                dismissLoadingDialog()
                            }
                            is FireStoreResponse.Failure -> {
                                makeToast(requireContext(), response.errorMessage)
                                dismissLoadingDialog()
                            }
                        }
                    }
                }
            }
        }
    }

}
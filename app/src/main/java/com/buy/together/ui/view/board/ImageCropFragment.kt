package com.buy.together.ui.view.board

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.buy.together.R
import com.buy.together.databinding.FragmentImageCropBinding
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel
import com.buy.together.util.GalleryUtils

class ImageCropFragment : BaseFragment<FragmentImageCropBinding>(
    FragmentImageCropBinding::bind, R.layout.fragment_image_crop
) {
    private val viewModel : BoardViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val img = viewModel.BitmapImage
        if(img == null){
            showToast("이미지를 가져올 수 없습니다.", ToastType.ERROR)
            findNavController().popBackStack()
        }
        viewModel.BitmapImage = null
        binding.cropImageView.setImageBitmap(img)

        binding.btnSave.setOnClickListener {
            val getImg = binding.cropImageView.getCroppedImage(500,500)
            val uri = getImg?.let {
                GalleryUtils.getImageUri(requireContext(),it)
            }
            viewModel.imageUri = uri

            findNavController().popBackStack()
        }
    }
}
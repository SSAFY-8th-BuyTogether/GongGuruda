package com.buy.together.ui.view.board

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.dto.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentBoardWritingBinding
import com.buy.together.ui.adapter.ImageAdpater
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.util.*

private const val TAG = "BoardWritingFragment_싸피"

class BoardWritingFragment : BaseFragment<FragmentBoardWritingBinding>(
    FragmentBoardWritingBinding::bind, R.layout.fragment_board_writing
) {
    private val viewModel : BoardViewModel by activityViewModels()
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var imageAdapter: ImageAdpater
    private var selectedTime: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setListener()
    }

    fun setAdapter() {
        val items = resources.getStringArray(R.array.categories)
        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        binding.spinnerCategory.adapter = spinnerAdapter

        //imageAdapter
        imageAdapter = ImageAdpater(requireContext(),arrayListOf())
        imageAdapter.itemClickListener = object : ImageAdpater.ItemClickListener {
            override fun onClick(view: View, size: Int) {
                if (size == 0) {
                    binding.rvImages.visibility = View.GONE
                } else {
                    binding.rvImages.visibility = View.VISIBLE
                }
            }
        }
        binding.rvImages.adapter = imageAdapter
    }

    fun setListener() {
        binding.apply {
            llOptionButton.setOnClickListener {
                if (includeWritingOption.clOptionBody.visibility == View.VISIBLE) {
                    includeWritingOption.clOptionBody.visibility = View.GONE
                    ivDownImg.animate().setDuration(200).rotation(0f)
                } else {
                    includeWritingOption.clOptionBody.visibility = View.VISIBLE
                    ivDownImg.animate().setDuration(200).rotation(180f)
                }
            }
            ibDeadlineButton.setOnClickListener {
                makeDateDialog()
            }
            ibBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
            ibGallery.setOnClickListener {
                getGallery()
            }
            btnOkay.setOnClickListener {
                if(checkAllWritten()){
                    sendBoardData()
                }
            }
        }
    }

    fun checkAllWritten() : Boolean{
        binding.apply {
            if (etTitle.editText?.text?.isEmpty() == true) {
                Toast.makeText(requireContext(),"제목을 입력해주세요",Toast.LENGTH_SHORT).show()
                etTitle.requestFocus()
                return false
            }
            if(selectedTime == null){
                Toast.makeText(requireContext(),"날짜를 선택해주세요",Toast.LENGTH_SHORT).show()
                ibDeadlineButton.requestFocus()
                return false
            }
            if(etPrice.editText?.text?.isEmpty() == true){
                Toast.makeText(requireContext(),"가격을 입력해주세요",Toast.LENGTH_SHORT).show()
                etPrice.requestFocus()
                return false
            }
            if(etContent.editText?.text?.isEmpty() == true){
                Toast.makeText(requireContext(),"내용을 입력해주세요",Toast.LENGTH_SHORT).show()
                etContent.requestFocus()
                return false
            }
        }
        return true
    }

    fun sendBoardData(){ //TODO : boardid = userid + 현재시각
        val calendar = Calendar.getInstance()
        val board = BoardDto(
            "${calendar.timeInMillis}_${"test"}",
            binding.etTitle.editText?.text.toString(),
            binding.spinnerCategory.selectedItem.toString(),
            selectedTime?:0,
            binding.etPrice.editText?.text.toString().toInt(),
            binding.etContent.editText?.text.toString(),
            calendar.timeInMillis,
            "test",
        )
        showLoadingDialog(requireContext())
        viewModel.saveImage(board,imageAdapter.ImageList)
        viewModel.ImgLiveData.observe(viewLifecycleOwner){
            board.images = it
            dismissLoadingDialog()
            viewModel.saveBoard(board).observe(viewLifecycleOwner){ response ->
                when(response){
                    is FireStoreResponse.Loading ->{ showLoadingDialog(requireContext())}
                    is FireStoreResponse.Success -> {
                        showCustomDialogBasicOneButton("성공적으로 저장되었습니다.")
                        dismissLoadingDialog()
                        findNavController().popBackStack()
                    }
                    is FireStoreResponse.Failure -> {
                        showCustomDialogBasicOneButton(response.errorMessage)
                        dismissLoadingDialog()
                    }
                }
            }
        }
    }

    fun getGallery() {
        getPermission(object : PermissionListener {
            override fun onPermissionGranted() {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                imageLauncher.launch(intent)
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.setType("image/*")
//                imageLauncher.launch(intent)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(requireContext(), "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getPermission(listener: PermissionListener){
        TedPermission.create()
            .setPermissionListener(listener)
            .setDeniedMessage("권한을 허용해주세요")
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

    private val imageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            Log.d(TAG, "uri : $imageUri")
            if (imageUri != null) {
                imageAdapter.apply {
                    ImageList.add(imageUri)
                    notifyItemInserted(ImageList.size - 1)
                    Log.d(TAG, "images : ${ImageList.size}")
                    binding.rvImages.visibility = View.VISIBLE
                }
            }
        }
    }

    fun makeDateDialog() {
        val cal = Calendar.getInstance()
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, date -> //datepickerdialog 띄우기
                binding.tvDeadline.text =
                    "${String.format("%04d", year)}.${
                        String.format(
                            "%02d",
                            month + 1
                        )
                    }.${String.format("%02d", date)}" //버튼 text 변경
                val calendar = Calendar.getInstance()
                calendar.set(year, month, date)
                selectedTime = calendar.timeInMillis
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)
        )
        dialog.datePicker.minDate = cal.timeInMillis
        dialog.show()
    }
}
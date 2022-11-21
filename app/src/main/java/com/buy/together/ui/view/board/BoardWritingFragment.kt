package com.buy.together.ui.view.board

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.Application.Companion.sharedPreferences
import com.buy.together.R
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentBoardWritingBinding
import com.buy.together.ui.adapter.ImageAdpater
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel
import com.buy.together.util.GalleryUtils.getGallery
import java.util.*

private const val TAG = "BoardWritingFragment_싸피"

class BoardWritingFragment : BaseFragment<FragmentBoardWritingBinding>(
    FragmentBoardWritingBinding::bind, R.layout.fragment_board_writing
) {
    private val viewModel : BoardViewModel by activityViewModels()
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var imageAdapter: ImageAdpater
    private var selectedTime: Long? = null

    private var clickedAddress = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setListener()
        requireActivity().supportFragmentManager.setFragmentResultListener("getAddress",viewLifecycleOwner){ requestKey, result ->
            if(requestKey == "getAddress" && result["address"] != null){
                val addressDto : AddressDto = result["address"] as AddressDto
                if(clickedAddress == 1){
                    binding.includeWritingOption.etBuyPoint.editText?.setText(addressDto.addressDetail)
                }else if(clickedAddress == 2){
                    binding.includeWritingOption.etMeetPoint.editText?.setText(addressDto.addressDetail)
                }else{
                    Toast.makeText(requireContext(), "오류가 발생했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                getGallery(requireContext(), imageLauncher)
            }
            btnOkay.setOnClickListener {
                if(checkAllWritten()){
                    sendBoardData()
                }
            }
            includeWritingOption.ibBuyPointButton.setOnClickListener {
                clickedAddress = 1
                showAddressFragment()
            }

            includeWritingOption.ibMeetPoint.setOnClickListener {
                clickedAddress = 2
                showAddressFragment()
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

    fun sendBoardData(){
        val userId = sharedPreferences.getAuthToken()
        if(userId == null){
            Toast.makeText(requireContext(),"알수없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
            Log.e(TAG, "userId is null====================")
            return
        }
        val board = setBoardDto(userId)
        showLoadingDialog(requireContext())
        viewModel.saveImage(board,imageAdapter.ImageList)
        viewModel.ImgLiveData.observe(viewLifecycleOwner){
            board.images = it
            dismissLoadingDialog()
            viewModel.saveBoard(board).observe(viewLifecycleOwner){ response ->
                when(response){
                    is FireStoreResponse.Loading ->{ showLoadingDialog(requireContext())}
                    is FireStoreResponse.Success -> {
                        sendUser(userId,board)
                    }
                    is FireStoreResponse.Failure -> {
                        Toast.makeText(requireContext(),"데이터를 저장하는 중에 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                        dismissLoadingDialog()
                    }
                }
            }
        }
    }

    fun sendUser(userId: String,boardDto: BoardDto){
        viewModel.saveBoardToUser(userId,boardDto).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { }
                is FireStoreResponse.Success -> {
                    sendParticipator(userId,boardDto)
                }
                is FireStoreResponse.Failure -> {
                    Toast.makeText(requireContext(),"데이터를 저장하는 중에 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                    dismissLoadingDialog()
                }
            }
        }
    }

    fun sendParticipator(userId: String, boardDto : BoardDto){
        viewModel.insertUserParticipate(userId,boardDto,true)
            .observe(viewLifecycleOwner) { response_ ->
                when (response_) {
                    is FireStoreResponse.Loading -> {
                        showLoadingDialog(requireContext())
                    }
                    is FireStoreResponse.Success -> {
                        Toast.makeText(requireContext(),"성공적으로 저장되었습니다.",Toast.LENGTH_SHORT).show()
                        dismissLoadingDialog()
                        findNavController().popBackStack()
                    }
                    is FireStoreResponse.Failure -> {
                        Toast.makeText(requireContext(),"데이터를 저장하는 중에 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                        dismissLoadingDialog()
                    }
                }
            }
    }

    fun setBoardDto(userId : String) : BoardDto{
        val calendar = Calendar.getInstance()
        val maxPeople = binding.includeWritingOption.etMaxPeople.editText?.text.toString()
        val meetPoint = binding.includeWritingOption.etMeetPoint.editText?.text.toString()
        val buyPoint = binding.includeWritingOption.etBuyPoint.editText?.text.toString()
        val board = BoardDto(
            id = "Board_${calendar.timeInMillis}_${userId}",
            title= binding.etTitle.editText?.text.toString(),
            category= binding.spinnerCategory.selectedItem.toString(),
            deadLine =  selectedTime?:0,
            price= binding.etPrice.editText?.text.toString().toInt(),
            content= binding.etContent.editText?.text.toString(),
            writeTime= calendar.timeInMillis,
            writer= userId,
            participator = arrayListOf(userId),
            images= listOf(),
            maxPeople = if(maxPeople.isEmpty()) null else maxPeople.toInt(),
            meetPoint= if(meetPoint.isEmpty()) null else meetPoint,
            buyPoint = if(buyPoint.isEmpty()) null else buyPoint
        )
        return board
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

    private fun showAddressFragment(){ findNavController().navigate(R.id.action_boardWritingFragment_to_addressGraph) }
}
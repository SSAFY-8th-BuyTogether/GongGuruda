package com.buy.together.ui.view.board

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.buy.together.R
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.dto.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentBoardBinding
import com.buy.together.restartActivity
import com.buy.together.ui.adapter.PagerImageAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel
import com.buy.together.util.CommonUtils
import java.lang.Math.abs

private const val TAG = "BoardFragment_싸피"
class BoardFragment : BaseFragment<FragmentBoardBinding>(
    FragmentBoardBinding::bind, R.layout.fragment_board
) {
    private val viewModel: BoardViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.collapseToolbar.inflateMenu()
        initAdapter()
        initListener()
        initData()
    }

    private fun initAdapter(){
        Log.d(TAG, "initAdapter: dto : ${viewModel.dto?.images?.isEmpty()}")
        if (viewModel.dto == null || viewModel.dto?.images == null ||viewModel.dto?.images?.isEmpty() == true){
            Log.d(TAG, "null=======")
        }else{
            binding.vpImages.adapter = PagerImageAdapter(viewModel.dto?.images ?: listOf())
        }
        binding.vpImages.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    private fun initListener(){
        // 이미지
        binding.ablBoardAppbarlayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { //접혔을 때
                binding.vpImages.apply{
                    visibility = View.INVISIBLE
                }
            } else { //펴졌을 때
                binding.vpImages.apply{
                    visibility = View.VISIBLE
                }
            }
        }
        binding.ibBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun initData(){
        val dto = viewModel.dto
        if(dto == null){
            Toast.makeText(requireContext(),"알수없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
            restartActivity()
        }else{
            viewModel.getEachBoard(dto.category,dto.id).observe(viewLifecycleOwner){ response ->
                when(response){
                    is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                    is FireStoreResponse.Success -> {
                        Log.d(TAG, "initData: success")
                        Log.d(TAG, "title : ${response.data["title"]}")
                        val dto_ = viewModel.makeBoard(response.data)
                        makeView(dto_)
                    }
                    is FireStoreResponse.Failure -> {
                        Toast.makeText(requireContext(), "존재하지 않는 게시글입니다.",Toast.LENGTH_SHORT).show()
                        dismissLoadingDialog()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    fun makeView(dto : BoardDto){
        binding.apply {
            tvLeftPerson.visibility = View.GONE //TODO : 남은 인원 어쩌지
            tvWriterName.text = dto.writer
            tvDday.text = CommonUtils.getDday(dto.deadLine)
            tvWritenTitle.text = dto.title
            tvWritenCategory.text = dto.category
            tvWritenDate.text = CommonUtils.getDateString(dto.writeTime)
            tvWritenContent.text = dto.content
            if(dto.meetPoint != null){
                llMeetPlace.visibility = View.VISIBLE
                tvMeetAddress.text = "임시주소" //TODO : 변환 코드 넣기
            }else{
                llMeetPlace.visibility = View.GONE
            }
            if(dto.buyPoint != null){
                llBuyPlace.visibility = View.VISIBLE
                tvBuyAddress.text = "임시주소"
            }else{
                llBuyPlace.visibility = View.GONE
            }
            tvPrice.text = CommonUtils.makeComma(dto.price)
        }
        dismissLoadingDialog()
    }
}
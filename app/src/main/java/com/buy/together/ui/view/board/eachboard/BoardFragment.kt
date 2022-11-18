package com.buy.together.ui.view.board.eachboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.buy.together.Application
import com.buy.together.R
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
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
        Log.d(TAG, "initAdapter: dto : ${viewModel.boardDto?.images?.isEmpty()}")
        if(viewModel.boardDto == null){
            backPress()
            return
        }
        binding.vpImages.adapter = PagerImageAdapter(viewModel.boardDto?.images ?: listOf())
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
        //뒤로 가기 버튼
        binding.ibBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        //참여하기 버튼 클릭
        binding.btnParticipate.setOnClickListener {
            makeDialog()
        }
        //댓글 버튼 클릭
        binding.ibComment.setOnClickListener {
            showCommentFragment()
        }
    }

    private fun makeDialog(){
        val btnText = binding.btnParticipate.text.toString()
        Log.d(TAG, "makeDialog: btnText : $btnText")
        showCustomDialogBasicTwoButton(
            if(btnText == requireContext().getString(R.string.btn_participate)) "해당 공구에 참여하시겠습니까?"
            else "해당 공구에서 빠지시겠습니까?",
            "취소",
            "확인"
        ) {
            val userID = Application.sharedPreferences.getAuthToken()
            val boardDto = viewModel.boardDto
            if(userID == null){
                Log.d(TAG, "makeDialog: userId가 없음")
                Toast.makeText(requireContext(),"알수없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                restartActivity()
            }else if (boardDto == null) {
                Log.d(TAG, "makeDialog: boardId가 없음")
                backPress()
            }else{
                saveData(userID,btnText,boardDto)
            }
        }
    }

    private fun saveData(userId:String,btnText: String, boardDto:BoardDto){
        val flag: Boolean = (btnText == requireContext().getString(R.string.btn_participate))
        viewModel.insertParticipator(boardDto,userId, flag).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    viewModel.insertUserParticipate(userId,boardDto,flag)
                        .observe(viewLifecycleOwner) { response_ ->
                            when (response_) {
                                is FireStoreResponse.Loading -> {
                                    showLoadingDialog(requireContext())
                                }
                                is FireStoreResponse.Success -> {
                                    dismissLoadingDialog()
                                    initData()
                                }
                                is FireStoreResponse.Failure -> {
                                    Toast.makeText(requireContext(),"데이터를 저장하는 중에 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                                    dismissLoadingDialog()
                                }
                            }
                        }
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    dismissLoadingDialog()
                    backPress()
                }
            }
        }
    }

    private fun initData(){
        val dto = viewModel.boardDto
        if(dto == null){
            backPress()
            return
        }
        viewModel.getEachBoard(dto.category,dto.id).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    val dto_ = viewModel.makeBoard(response.data)
                    makeView(dto_)
                }
                is FireStoreResponse.Failure -> {
                    dismissLoadingDialog()
                    backPress()
                }
            }
        }
    }

    fun makeView(dto : BoardDto){
        binding.apply {
            tvLeftPerson.visibility = View.GONE
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

            //button
            val userId : String? = Application.sharedPreferences.getAuthToken()
            if(dto.participator.contains(userId)){
                binding.btnParticipate.text = requireContext().getString(R.string.btn_cancel)
                binding.btnParticipate.backgroundTintList = requireContext().getColorStateList(R.color.black_50)
            }else{
                binding.btnParticipate.text = requireContext().getString(R.string.btn_participate)
                binding.btnParticipate.backgroundTintList = requireContext().getColorStateList(R.color.colorAccent)
            }

            //participator
            var text = "참여자 : "
            dto.participator.forEach{ text += "$it " }
            tvParticipator.text = text
            tvLeftPerson.visibility = View.GONE
            Log.d(TAG, "makeView: maxPeople : ${dto.maxPeople}")
            if(dto.maxPeople != null){
                val leftPerson = dto.maxPeople!! - dto.participator.size
                if(leftPerson < 10){
                    tvLeftPerson.visibility = View.VISIBLE
                    tvLeftPerson.text = "${leftPerson}명 남았어요!!"
                }
            }
        }
        dismissLoadingDialog()
    }

    private fun backPress(){
        Toast.makeText(requireContext(), "존재하지 않는 게시글입니다.",Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun showCommentFragment(){
        findNavController().navigate(R.id.action_boardFragment_to_commentFragment)
    }
}
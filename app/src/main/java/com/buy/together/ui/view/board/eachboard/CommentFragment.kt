package com.buy.together.ui.view.board.eachboard

import android.R
import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.buy.together.Application
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.dto.CommentDto
import com.buy.together.data.dto.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentCommentBinding
import com.buy.together.ui.adapter.CommentAdapter
import com.buy.together.ui.adapter.PagerImageAdapter
import com.buy.together.ui.base.BaseBottomSheetDialogFragment
import com.buy.together.ui.viewmodel.BoardViewModel
import com.buy.together.util.CustomDialog
import com.google.firebase.Timestamp
import org.w3c.dom.Comment
import java.util.*

class CommentFragment : BaseBottomSheetDialogFragment<FragmentCommentBinding>(
    FragmentCommentBinding::inflate) {
    private val viewModel: BoardViewModel by activityViewModels()
    private lateinit var commentAdapter : CommentAdapter
    lateinit var mLoadingDialog: CustomDialog

    override fun initView() {
        initAdapter()
        initData()
    }

    override fun setEvent() {
        binding.apply {
            btnSend.setOnClickListener{
                val userId : String? = Application.sharedPreferences.getAuthToken()
                if(viewModel.boardDto != null && userId != null){ //TODO : profileImage
                    val calendar = Calendar.getInstance()
                    val comment = CommentDto(
                        id= "${calendar.timeInMillis}_${userId}",
                        boardId= viewModel.boardDto!!.id,
                        writer=  userId,
                        content= etComment.text.toString(),
                        time= Timestamp.now()
                    )
                    viewModel.insertComment(viewModel.boardDto!!.category, comment).observe(viewLifecycleOwner){ response->
                        when(response){
                            is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                            is FireStoreResponse.Success -> {
                                dismissLoadingDialog()
                                etComment.setText("")
                                initData()
                            }
                            is FireStoreResponse.Failure -> {
                                dismissLoadingDialog()
                                backPress()
                            }
                        }
                    }
                }else{
                    Toast.makeText(requireContext(),"알수없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initAdapter(){
        if(viewModel.boardDto == null){
            backPress()
            return
        }
        commentAdapter = CommentAdapter()
        val decoration = DividerItemDecoration(requireContext(),RecyclerView.VERTICAL)
        binding.rvComment.adapter = commentAdapter
        binding.rvComment.addItemDecoration(decoration)
    }

    private fun initData(){
        if(viewModel.boardDto == null){
            return
        }
        viewModel.getComments(viewModel.boardDto!!.category,viewModel.boardDto!!.id).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    val list = arrayListOf<CommentDto>()
                    response.data.forEach{
                        list.add(CommentDto(it))
                    }
                    commentAdapter.commentList = list
                    commentAdapter.notifyDataSetChanged()
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    Toast.makeText(requireContext(), "댓글을 받아올 수 없습니다", Toast.LENGTH_SHORT).show()
                    dismissLoadingDialog()
                }
            }
        }
    }

    private fun backPress(){
        Toast.makeText(requireContext(), "존재하지 않는 게시글입니다.", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    fun showLoadingDialog(context: Context) {
        mLoadingDialog = CustomDialog(context)
        mLoadingDialog.show()
    }

    fun dismissLoadingDialog() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
    }
}
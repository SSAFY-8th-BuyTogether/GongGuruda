package com.buy.together.ui.view.board.eachboard

import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.Application
import com.buy.together.R
import com.buy.together.data.dto.CommentDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentCommentBinding
import com.buy.together.ui.adapter.CommentAdapter
import com.buy.together.ui.base.BaseBottomSheetDialogFragment
import com.buy.together.ui.viewmodel.BoardViewModel
import com.google.firebase.Timestamp
import java.util.*

private const val TAG = "CommentFragment_싸피"
class CommentFragment : BaseBottomSheetDialogFragment<FragmentCommentBinding>(
    FragmentCommentBinding::inflate) {
    private val viewModel: BoardViewModel by activityViewModels()
    private lateinit var commentAdapter : CommentAdapter
    private var mention : String? = null

    override fun initView() {
        binding.layoutEmpty.layoutAddressEmptyView.visibility = View.GONE
        initAdapter()
        initData()
    }

    override fun setEvent() {
        binding.apply {
            btnSend.setOnClickListener{
                val userId : String? = Application.sharedPreferences.getAuthToken()
                if(checkNull(userId)){
                    saveComment(userId!!)
                }
            }
            ibMentionRemove.setOnClickListener {
                mention = null
                tvMention.text = ""
                llMentionLayout.visibility = View.GONE
            }
        }
    }

    fun saveComment(userId : String){
        val calendar = Calendar.getInstance()
        val comment = CommentDto(
            id= "Comment_${calendar.timeInMillis}_${userId}",
            boardId= viewModel.boardDto!!.id,
            boardTitle= viewModel.boardDto!!.title,
            writer=  userId,
            content= binding.etComment.text.toString(),
            time= Timestamp.now(),
        )
        if(mention != null){
            comment.mention = mention
        }
        val category = viewModel.boardDto!!.category
        viewModel.insertComment(category, comment).observe(viewLifecycleOwner){ response->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    viewModel.saveCommentToUser(userId, category, comment)
                        .observe(viewLifecycleOwner) { _response ->
                            when (_response) {
                                is FireStoreResponse.Loading -> {
                                    showLoadingDialog(requireContext())
                                }
                                is FireStoreResponse.Success -> {
                                    dismissLoadingDialog()
                                    binding.etComment.setText("")
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

    private fun initAdapter(){
        if(viewModel.boardDto == null){
            backPress()
            return
        }
        commentAdapter = CommentAdapter()
        val decoration = DividerItemDecoration(requireContext(),RecyclerView.VERTICAL)
        commentAdapter.itemClickListener = object : CommentAdapter.ItemClickListener {
            override fun onClick(view: View, dto : CommentDto) {
                val userId : String? = Application.sharedPreferences.getAuthToken()
                if(userId != dto.writer) {
                    binding.llMentionLayout.visibility = View.VISIBLE
                    mention = dto.writer
                    binding.tvMention.text = "${getString(R.string.at_sign)}${mention}"
                }
            }

            override fun onItemOptionClick(view: View, dto: CommentDto) {
                popUpMenu(view, dto)
            }
        }
        binding.rvComment.adapter = commentAdapter
        binding.rvComment.addItemDecoration(decoration)
    }

    private fun initData(){
        binding.llMentionLayout.visibility = View.GONE
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
                    if(list.isEmpty()){
                        binding.layoutEmpty.layoutAddressEmptyView.visibility = View.VISIBLE
                        binding.layoutEmpty.tvEmptyView.text = "댓글이"
                    }
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    Toast.makeText(requireContext(), "댓글을 받아올 수 없습니다", Toast.LENGTH_SHORT).show()
                    dismissLoadingDialog()
                }
            }
        }
    }

    private fun popUpMenu(view : View, dto : CommentDto ){
        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.menuInflater.inflate(R.menu.menu_option_comment,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener{ item ->
            when(item.itemId) {
                R.id.comment_delete -> deleteComment(dto)
            }
            true
        }
        popupMenu.show()
    }

    private fun deleteComment(dto : CommentDto){
        val userId : String? = Application.sharedPreferences.getAuthToken()
        if(!checkNull(userId)){
            return
        }
        viewModel.removeCommentFromUser(userId!!,viewModel.boardDto!!.category,dto).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    viewModel.removeComment(viewModel.boardDto!!.category,dto).observe(viewLifecycleOwner){ _response ->
                        when(_response){
                            is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                            is FireStoreResponse.Success -> {
                                dismissLoadingDialog()
                                initData()
                                Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                            is FireStoreResponse.Failure -> {
                                Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                dismissLoadingDialog()
                            }
                        }
                    }
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    dismissLoadingDialog()
                }
            }
        }
    }

    private fun checkNull(userId: String?) : Boolean{
        if(viewModel.boardDto == null || userId == null) { //TODO : profileImage
            Toast.makeText(requireContext(),"알수없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun backPress(){
        Toast.makeText(requireContext(), "존재하지 않는 게시글입니다.", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }
}
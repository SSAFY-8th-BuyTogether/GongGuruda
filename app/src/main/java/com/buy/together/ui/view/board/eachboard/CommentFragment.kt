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
import com.buy.together.data.model.domain.CommentDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentCommentBinding
import com.buy.together.ui.adapter.CommentAdapter
import com.buy.together.ui.base.BaseBottomSheetDialogFragment
import com.buy.together.ui.viewmodel.BoardViewModel
import com.buy.together.ui.viewmodel.MyPageViewModel
import com.buy.together.util.showKeyboard
import com.google.firebase.Timestamp

private const val TAG = "CommentFragment_싸피"
class CommentFragment : BaseBottomSheetDialogFragment<FragmentCommentBinding>(
    FragmentCommentBinding::inflate) {
    private val viewModel: BoardViewModel by activityViewModels()
    private val myPageViewModel: MyPageViewModel by activityViewModels()
    private lateinit var commentAdapter : CommentAdapter
    private var mention : String? = null
    private var mentionComment : String? = null

    override fun initView() {
//        showKeyboard(binding.etComment)
        initAdapter()
        initData()
    }

    override fun setEvent() {
        binding.apply {
            btnSend.setOnClickListener{
                val userId : String? = Application.sharedPreferences.getAuthToken()
                if(binding.etComment.text.isEmpty()){
                    Toast.makeText(requireContext(),"댓글을 입력해주세요",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
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
        val comment = CommentDto(
            id= "Comment_${System.currentTimeMillis()}_${userId}",
            boardId= viewModel.boardDto!!.id,
            boardTitle= viewModel.boardDto!!.title,
            writer=  userId,
            content= binding.etComment.text.toString(),
            time= Timestamp.now(),
        )
        if(mention != null && mentionComment != null){
            comment.mention = mention
        }
        val category = viewModel.boardDto!!.category
        myPageViewModel.getUserInfo().observe(viewLifecycleOwner) {
            it?.let { userDto ->
                comment.writerProfile = userDto.profile
                viewModel.insertComment(viewModel.boardDto!!.writer, mentionComment,category, comment).observe(viewLifecycleOwner){ response->
                    when(response){
                        is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                        is FireStoreResponse.Success -> {
                            dismissLoadingDialog()
                            binding.etComment.setText("")
                            Toast.makeText(requireContext(),"저장되었습니다.",Toast.LENGTH_SHORT).show()
                            initData()
                        }
                        is FireStoreResponse.Failure -> {
                            dismissLoadingDialog()
                            backPress()
                        }
                    }
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
                    mentionComment = dto.content
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
        binding.layoutEmpty.layoutEmptyView.visibility = View.GONE
        binding.rvComment.visibility = View.VISIBLE
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
                        binding.layoutEmpty.layoutEmptyView.visibility = View.VISIBLE
                        binding.rvComment.visibility = View.GONE
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

    private fun popUpMenu(view : View, dto : CommentDto){
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
        viewModel.removeComment(viewModel.boardDto!!.category,dto).observe(viewLifecycleOwner){ _response ->
            when(_response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    dismissLoadingDialog()
                    Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    initData()
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
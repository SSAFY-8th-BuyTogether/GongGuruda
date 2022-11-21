package com.buy.together.ui.view.board

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.Application
import com.buy.together.R
import com.buy.together.data.model.domain.BoardDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentBoardCategoryBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel

class BoardCategoryFragment : BaseFragment<FragmentBoardCategoryBinding>(
    FragmentBoardCategoryBinding::bind, R.layout.fragment_board_category
){
    private val viewModel : BoardViewModel by activityViewModels()
    private lateinit var boardAdapter : BoardAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCategory.text = viewModel.category
        binding.layoutEmpty.layoutEmptyView.visibility = View.GONE
        initAdapter()
        initListener()

    }

    fun initAdapter(){
        boardAdapter = BoardAdapter()
        val decoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rvBoard.addItemDecoration(decoration)
        binding.rvBoard.adapter = boardAdapter

        if(viewModel.category == "전체"){
            getDataAll()
        }else{
            getData()
        }

        boardAdapter.itemClickListener = object : BoardAdapter.ItemClickListener {
            override fun onClick(view: View, dto : BoardDto) {
                viewModel.boardDto = dto
                showBoardFragment()
            }

            override fun onItemOptionClick(view: View, dto: BoardDto) {
                popUpMenu(view,dto)
            }
        }
    }

    private fun popUpMenu(view : View, dto : BoardDto){
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

    private fun deleteComment(dto : BoardDto){
        val userId : String? = Application.sharedPreferences.getAuthToken()
        if(userId == null) {
            Toast.makeText(requireContext(),"알수없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.removeBoardFromUser(userId,dto).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    viewModel.removeBoard(dto).observe(viewLifecycleOwner){ _response ->
                        when(_response){
                            is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                            is FireStoreResponse.Success -> {
                                dismissLoadingDialog()
                                if(viewModel.category == "전체"){
                                    getDataAll()
                                }else{
                                    getData()
                                }
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

    private fun getData(){
        viewModel.getBoardList(viewModel.category).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    val list = mutableListOf<BoardDto>()
                    response.data.forEach{
                        list.add(viewModel.makeBoard(it))
                    }
                    boardAdapter.boardDtoList = list
                    boardAdapter.notifyDataSetChanged()
                    if(list.isEmpty()){
                        setEmptyLayout()
                    }
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    Toast.makeText(requireContext(), "게시글을 받아올 수 없습니다", Toast.LENGTH_SHORT).show()
                    dismissLoadingDialog()
                }
            }
        }
    }

    private fun setEmptyLayout(){
        binding.apply {
            layoutEmpty.layoutEmptyView.visibility = View.VISIBLE
            layoutEmpty.tvEmptyView.text = "게시글이"
        }
    }

    private fun getDataAll(){
        val list = mutableListOf<BoardDto>()
        var count = 0
        viewModel.getBoardList(viewModel.category).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    response.data.forEach{
                        list.add(viewModel.makeBoard(it))
                    }
                    count++
                    if(count == viewModel.categoryListKr.size -1){
                        list.sortBy { it.deadLine }
                        boardAdapter.boardDtoList = mutableListOf()
                        boardAdapter.boardDtoList = list
                        boardAdapter.notifyDataSetChanged()
                        if(list.isEmpty()){
                            setEmptyLayout()
                        }
                    }
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    Toast.makeText(requireContext(), "게시글을 받아올 수 없습니다", Toast.LENGTH_SHORT).show()
                    dismissLoadingDialog()
                }
            }
        }
    }

    private fun initListener(){
        binding.ibBackButton.setOnClickListener{
            findNavController().popBackStack()
        }
    }
    private fun showBoardFragment() {findNavController().navigate(R.id.action_boardCategoryFragment_to_boardFragment)}
}
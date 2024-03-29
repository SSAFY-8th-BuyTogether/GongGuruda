package com.buy.together.ui.view.board

import android.os.Bundle
import android.util.Log
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
import com.buy.together.ui.base.BaseBottomSheetDialogFragment
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
        initAdapter()
        initListener()
        initData()
    }

    fun initAdapter(){
        boardAdapter = BoardAdapter()
        val decoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rvBoard.addItemDecoration(decoration)
        binding.rvBoard.adapter = boardAdapter

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

    private fun initListener(){
        binding.apply {
            ibBackButton.setOnClickListener{
                findNavController().popBackStack()
            }
            fabWriteBoard.setOnClickListener{
                showBoardWriteFragment()
            }
        }
    }

    private fun popUpMenu(view : View, dto : BoardDto){
        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.menuInflater.inflate(R.menu.menu_option_comment,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener{ item ->
            when(item.itemId) {
                R.id.comment_delete -> deleteBoard(dto)
            }
            true
        }
        popupMenu.show()
    }

    private fun deleteBoard(dto : BoardDto){
        val userId : String? = Application.sharedPreferences.getAuthToken()
        if(userId == null) {
            Toast.makeText(requireContext(),"알수없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.removeBoard(userId,dto).observe(viewLifecycleOwner){ response ->
            when(response){
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


    private fun setEmptyLayout(){
        binding.apply {
            layoutEmpty.layoutEmptyView.visibility = View.VISIBLE
            layoutEmpty.tvEmptyView.text = "게시글이"
        }
    }

    private fun initData(){
        binding.layoutEmpty.layoutEmptyView.visibility = View.GONE
        if(viewModel.category == "전체"){
            getDataAll()
        }else{
            getData()
        }
    }

    private fun getData(){
        viewModel.getBoardList(viewModel.category).observe(viewLifecycleOwner){ response ->
            try{
                val resList = response as ArrayList<BoardDto>?
                if(response == null || resList?.isEmpty() == true){
                    setEmptyLayout()
                    return@observe
                }else{
                    val itemList = arrayListOf<BoardDto>()
                    resList?.forEach{
                        val meetPoint = it.meetPoint
                        if(meetPoint == null || meetPoint.contains(viewModel.selectedAddress)) {
                            itemList.add(it)
                        }
                    }
                    boardAdapter.setList(itemList)
                }
            }catch (e : Exception){
                showToast("게시글을 받아올 수 없습니다", ToastType.ERROR)
                Log.d("BoardCategory", "getData: ${e.message}")
                setEmptyLayout()
            }
        }
    }

    private fun getDataAll(){
        val list = mutableListOf<BoardDto>()
        viewModel.getBoardListAll().observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    response.data.forEach{
                        val meetPoint = it["meetPoint"] as String?
                        if(meetPoint == null || meetPoint.contains(viewModel.selectedAddress)) {
                            list.add(viewModel.makeBoard(it))
                        }
                    }
                    list.sortBy { it.deadLine }
                    boardAdapter.setList(list as ArrayList<BoardDto>)
                    if(list.isEmpty()){
                        setEmptyLayout()
                    }
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    showToast("게시글을 받아올 수 없습니다", ToastType.ERROR)
                    dismissLoadingDialog()
                }
            }
        }
    }
    private fun showBoardFragment() {findNavController().navigate(R.id.action_boardCategoryFragment_to_boardFragment)}
    private fun showBoardWriteFragment() {findNavController().navigate(R.id.action_boardCategoryFragment_to_boardWritingFragment)}
}
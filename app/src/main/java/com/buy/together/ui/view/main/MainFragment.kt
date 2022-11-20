package com.buy.together.ui.view.main

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
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentMainBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.viewmodel.BoardViewModel

// TODO : 인터넷 연결 여부 체크 필요.
class MainFragment : BaseFragment<FragmentMainBinding>(
    FragmentMainBinding::bind, R.layout.fragment_main
) {
    private val viewModel : BoardViewModel by activityViewModels()
    private lateinit var boardAdapter : BoardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initListener()
        initData()
        requireActivity().supportFragmentManager.setFragmentResultListener("getAddress",viewLifecycleOwner){ requestKey, result ->
            if(requestKey == "getAddress" && result["address"] != null){
                val addressDto : AddressDto = result["address"] as AddressDto
                binding.tvAddress.text = "${addressDto.address} ▾"
                initData()
            }
        }
    }

    fun initAdapter(){
        boardAdapter = BoardAdapter()
        val decoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rvMainBoard.addItemDecoration(decoration)
        binding.rvMainBoard.adapter = boardAdapter
    }

    fun initListener(){
        binding.apply {
            fabWriteBoard.setOnClickListener{
                showBoardWritingFragment()
            }
            llCategoryLayout.apply{
                ibImgAll.setOnClickListener{
                    onclickCategory("전체")
                }
                ibImgFood.setOnClickListener{
                    onclickCategory("식품")
                }
                ibImgStationery.setOnClickListener{
                    onclickCategory("문구")
                }
                ibImgDailyNeccessity.setOnClickListener{
                    onclickCategory("생활용품")
                }
                ibImgEtc.setOnClickListener{
                    onclickCategory("기타")
                }
            }

            tvAddress.setOnClickListener { showAddressFragment() }
            
            boardAdapter.itemClickListener = object : BoardAdapter.ItemClickListener {
                override fun onClick(view: View, dto : BoardDto) {
                    viewModel.boardDto = dto
                    showBoardFragment()
                }

                override fun onItemOptionClick(view: View, dto: BoardDto) {
                    popUpMenu(view, dto)
                }
            }
        }
    }

    private fun initData(){
        viewModel.getBoardList(viewModel.categoryListKr[1]).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    val list = mutableListOf<BoardDto>()
                    response.data.forEach{
                        if((it["meetPoint"] as String).contains("구미시")){ //TODO : 현재 위치 받아오기
                            list.add(viewModel.makeBoard(it))
                        }
                    }
                    boardAdapter.boardDtoList = list
                    boardAdapter.notifyDataSetChanged()
                    dismissLoadingDialog()
                }
                is FireStoreResponse.Failure -> {
                    Toast.makeText(requireContext(), "게시글을 받아올 수 없습니다", Toast.LENGTH_SHORT).show()
                    dismissLoadingDialog()
                }
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

    fun onclickCategory(type : String){
        viewModel.category = type
        showBoardCategoryFragment()
    }

    private fun showAddressFragment(){ findNavController().navigate(R.id.action_mainFragment_to_addressGraph) }
    private fun showBoardWritingFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardWritingFragment) }
    private fun showBoardCategoryFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardCategoryFragment)}
    private fun showBoardFragment() {findNavController().navigate(R.id.action_mainFragment_to_boardFragment)}
}
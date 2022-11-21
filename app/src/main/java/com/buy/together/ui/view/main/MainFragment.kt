package com.buy.together.ui.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.buy.together.Application.Companion.sharedPreferences
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.R
import com.buy.together.data.model.domain.BoardDto
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentMainBinding
import com.buy.together.ui.adapter.BoardAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.view.MainActivity
import com.buy.together.ui.viewmodel.AddressViewModel
import com.buy.together.ui.viewmodel.BoardViewModel
import com.buy.together.ui.viewmodel.MyPageViewModel
import com.buy.together.util.AddressUtils
import java.io.IOException

// TODO : 인터넷 연결 여부 체크 필요.
private const val TAG = "MainFragment_싸피"
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::bind, R.layout.fragment_main) {
    private val addressViewModel : AddressViewModel by viewModels()
    private val myPageViewModel : MyPageViewModel by viewModels()
    private val viewModel : BoardViewModel by activityViewModels()
    private lateinit var boardAdapter : BoardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addressViewModel.getAddress().observe(viewLifecycleOwner){
            if (!(it == null || it.isEmpty())) setAddressView((it as ArrayList<AddressDto>)[0])
        }
        viewModel.onBackPressed.observe(viewLifecycleOwner){
            if (it==true) requireActivity().finish()
            else showToast(it.toString())
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.layoutDrawer.isDrawerOpen(GravityCompat.END)) binding.layoutDrawer.closeDrawer(GravityCompat.END)
            else viewModel.onBackPressed()
        }
        initAlarmNavi()
        initMyPageNavi()
        initAdapter()
        initListener()
        if(binding.tvAddress.text == getString(R.string.tv_address_unselected)){
            setEmpty()
        }else{
            initData()
        }
        requireActivity().supportFragmentManager.setFragmentResultListener("getAddress",viewLifecycleOwner){ requestKey, result ->
            if(requestKey == "getAddress" && result["address"] != null){
                val addressDto : AddressDto = result["address"] as AddressDto
                binding.tvAddress.text = "${addressDto.address} ▾"
                initData()
            }
        }
    }

    private fun initAdapter(){
        boardAdapter = BoardAdapter()
        val decoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rvMainBoard.addItemDecoration(decoration)
        binding.rvMainBoard.adapter = boardAdapter
    }

    private fun initListener(){
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
        binding.layoutEmpty.layoutEmptyView.visibility = View.GONE
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
                    if(list.isEmpty()){
                        setEmpty()
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

    private fun setEmpty(){
        binding.layoutEmpty.apply {
            tvEmptyView.text = "게시글이"
            layoutEmptyView.visibility = View.VISIBLE
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
        val userId : String? = sharedPreferences.getAuthToken()
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
    private fun initAlarmNavi(){
        binding.apply {
            ibNotification.setOnClickListener{
                layoutDrawer.openDrawer(GravityCompat.END)
                setAlarmView(true)
                setMyPageView(false)
            }
            layoutNaviAlarm.btnBack.setOnClickListener { layoutDrawer.closeDrawer(GravityCompat.END) }
        }
    }

    private fun initMyPageNavi(){
        binding.apply {
            ibMyPage.setOnClickListener {
                layoutDrawer.openDrawer(GravityCompat.END)
                setAlarmView(false)
                setMyPageView(true)
            }
            layoutNaviMyPage.apply{
                btnBack.setOnClickListener { layoutDrawer.closeDrawer(GravityCompat.END) }
                myPageViewModel.getUserInfo().observe(viewLifecycleOwner){
                    it?.let { userDto ->
                        userDto.makeProfileSrc(requireContext())?.let { src ->
                            Glide.with(imgUserProfile)
                                .load(src)
                                .into(imgUserProfile)
                        }
                        tvUserNickname.text = userDto.nickName
                        tvUserName.text = userDto.name
                        tvUserBirth.text = userDto.makeFormattedBirth()
                        tvUserSms.text = userDto.makeFormattedPhone()
                    }
                }
                btnMyInfoModify.setOnClickListener { showMyInfoModifyFragment() }
                btnMyPwdModify.setOnClickListener { showMyPwdModifyFragment() }
                btnMyWriteComment.setOnClickListener { showMyWriteCommentFragment() }
                btnMyParticipate.setOnClickListener { showMyParticipateFragment() }
                btnLogout.setOnClickListener {
                    showCustomDialogBasicTwoButton("로그아웃하시겠습니까?", "취소", "로그아웃"){
                        myPageViewModel.logOut().observe(viewLifecycleOwner){ response ->
                            when(response){
                                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                                is FireStoreResponse.Success -> {
                                    logout()
                                    dismissLoadingDialog()
                                }
                                is FireStoreResponse.Failure -> {
                                    showCustomDialogBasicOneButton("로그인에 실패했습니다.\n아이디 혹은 비밀번호를 확인해주세요.")
                                    dismissLoadingDialog()
                                }
                            }
                        }
                    }
                }
                btnWithDraw.setOnClickListener {
                    showCustomDialogBasicTwoButton("모든 정보가 삭제되며, 복구할 수 없습니다.\n" + "정말 계정을 탈퇴하시겠습니까?", "취소", "탈퇴"){
                        myPageViewModel.withDraw().observe(viewLifecycleOwner){ response ->
                            when(response){
                                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                                is FireStoreResponse.Success -> {
                                    logout()
                                    dismissLoadingDialog()
                                }
                                is FireStoreResponse.Failure -> {
                                    showCustomDialogBasicOneButton("로그인에 실패했습니다.\n아이디 혹은 비밀번호를 확인해주세요.")
                                    dismissLoadingDialog()
                                }
                            }

                        }
                    }
                }
            }
        }
    }
    private fun setAddressView(addressDto: AddressDto){
        binding.tvAddress.text = String.format(getString(R.string.tv_address_selected), AddressUtils.getSelectedAddress(addressDto.addressDetail))
        // TODO : 아름 -> 주소 등록 여부에 따른 글 출력 뷰 조절.
    }
    private fun setAlarmView(isSet : Boolean, itemList : ArrayList<AddressDto> = arrayListOf()){
        if (isSet){
            binding.layoutNaviAlarm.layoutNaviAlarm.visibility = View.VISIBLE
            // TODO : rv값 세팅 or EmptyView 세팅
        }else binding.layoutNaviAlarm.layoutNaviAlarm.visibility = View.GONE
    }
    private fun setMyPageView(isSet : Boolean){
        binding.layoutNaviMyPage.apply {
            if (isSet)  layoutNaviMyPage.visibility = View.VISIBLE
            else layoutNaviMyPage.visibility = View.GONE
        }
    }
    private fun onclickCategory(type : String){
//        if(binding.tvAddress.text == getString(R.string.tv_address_unselected)){
//            showCustomDialogBasicOneButton("먼저 주소를 등록해주세요")
//            return
//        }
        viewModel.category = type
        showBoardCategoryFragment()
    }

    private fun showAddressFragment(){ findNavController().navigate(MainFragmentDirections.actionMainFragmentToAddressGraph(true)) }
    private fun showBoardWritingFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardWritingFragment) }
    private fun showBoardCategoryFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardCategoryFragment) }
    private fun showBoardFragment() { findNavController().navigate(R.id.action_mainFragment_to_boardFragment) }
    private fun showMyInfoModifyFragment() { findNavController().navigate(R.id.action_mainFragment_to_myInfoModifyFragment) }
    private fun showMyPwdModifyFragment() { findNavController().navigate(R.id.action_mainFragment_to_myPwdModifyFragment) }
    private fun showMyWriteCommentFragment(){ findNavController().navigate(R.id.action_mainFragment_to_myWriteCommentFragment) }
    private fun showMyParticipateFragment(){ findNavController().navigate(R.id.action_mainFragment_to_myParticipateFragment) }

    private fun removeUserInfo() {
        sharedPreferences.removeAuthToken()
        sharedPreferences.removeFCMToken()
    }

    private fun logout() {
        try {
            removeUserInfo()
            Intent(context, MainActivity::class.java).apply {
                requireActivity().finish()
                startActivity(this)
            }
            requireActivity().finish()
        } catch (e: IOException) { findNavController().navigate(R.id.action_global_loginFragment) }
    }
}
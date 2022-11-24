package com.buy.together.ui.view.board.eachboard

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.buy.together.Application
import com.buy.together.R
import com.buy.together.data.model.domain.BoardDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentBoardBinding
import com.buy.together.ui.adapter.PagerImageAdapter
import com.buy.together.ui.base.BaseFragment
import com.buy.together.ui.view.restartActivity
import com.buy.together.ui.viewmodel.BoardViewModel
import com.buy.together.util.AddressUtils
import com.buy.together.util.CommonUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import java.lang.Math.abs

private const val TAG = "BoardFragment_싸피"
class BoardFragment : BaseFragment<FragmentBoardBinding>(FragmentBoardBinding::bind, R.layout.fragment_board){
    private val navArgs : BoardFragmentArgs by navArgs()
    private val viewModel: BoardViewModel by activityViewModels()

    private lateinit var mapMeetFragment : MapFragment
    private lateinit var googleMeetMap: NaverMap
    private lateinit var mapBuyFragment : MapFragment
    private lateinit var googleBuyMap: NaverMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initListener()
        initMap()
        initData()
        viewModel.writingBoardDto = null
        viewModel.imageList = arrayListOf()
    }

    private fun initMap(){
        mapMeetFragment = childFragmentManager.findFragmentById(R.id.fcv_meet_map) as MapFragment
        mapBuyFragment = childFragmentManager.findFragmentById(R.id.fcv_buy_map) as MapFragment
    }

    private fun setMap(){
        mapMeetFragment.getMapAsync { setMeetMap(it) }
        mapBuyFragment.getMapAsync{ setBuyMap(it) }
    }

    private fun initAdapter(){
        Log.d(TAG, "initAdapter: dto : ${viewModel.boardDto?.images?.isEmpty()}")
        binding.vpImages.adapter = PagerImageAdapter(viewModel.boardDto?.images ?: listOf())
        binding.vpImages.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(binding.intoTabLayout,binding.vpImages)
        {tab, position ->}.attach()
    }

    private fun initListener(){
        binding.apply {
            // 이미지
            ablBoardAppbarlayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { //접혔을 때
                    vpImages.visibility = View.INVISIBLE
                    intoTabLayout.visibility = View.INVISIBLE
                } else { //펴졌을 때
                    vpImages.visibility = View.VISIBLE
                    intoTabLayout.visibility = View.VISIBLE
                }
            }
            //뒤로 가기 버튼
            ibBackButton.setOnClickListener {
                findNavController().popBackStack()
            }
            //참여하기 버튼 클릭
            btnParticipate.setOnClickListener {
                makeDialog()
            }
            //댓글 버튼 클릭
            ibComment.setOnClickListener {
                showCommentFragment()
            }
            //메뉴 버튼
            val userId = Application.sharedPreferences.getAuthToken()
            if(viewModel.boardDto != null && viewModel.boardDto?.writer == userId){
                ibOptionButton.visibility = View.VISIBLE
            }else{
                ibOptionButton.visibility = View.GONE
            }
            ibOptionButton.setOnClickListener {
                popUpMenu(it,viewModel.boardDto!!)
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
            showToast("알수없는 오류가 발생했습니다.",ToastType.ERROR)
            return
        }
        viewModel.removeBoard(userId,dto).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext())}
                is FireStoreResponse.Success -> {
                    dismissLoadingDialog()
                    showToast("삭제되었습니다.",ToastType.SUCCESS)
                    backPress()
                }
                is FireStoreResponse.Failure -> {
                    showToast("삭제에 실패했습니다.",ToastType.ERROR)
                    dismissLoadingDialog()
                }
            }
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
                showToast("알수없는 오류가 발생했습니다.",ToastType.ERROR)
                restartActivity()
            }else{
                boardDto?.let { saveData(userID,btnText,it) }
            }
        }
    }

    private fun saveData(userId:String,btnText: String, boardDto: BoardDto){
        val flag: Boolean = (btnText == requireContext().getString(R.string.btn_participate))
        viewModel.insertParticipator(boardDto,userId, flag).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    dismissLoadingDialog()
                    showToast("적용되었습니다.",ToastType.SUCCESS)
                    initData()
                }
                is FireStoreResponse.Failure -> {
                    dismissLoadingDialog()
                    backPress()
                }
            }
        }
    }

    private fun initData(){
        navArgs.boardDto?.let { viewModel.boardDto = it }
        if(viewModel.boardDto == null){
            backPress()
            return
        }
        val dto = viewModel.boardDto!!
        viewModel.getEachBoard(dto.category,dto.id).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> {
                    showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    viewModel.boardDto = viewModel.makeBoard(response.data)
                    dismissLoadingDialog()
                    makeView(viewModel.boardDto!!)
                    setMap()
                }
                is FireStoreResponse.Failure -> {
                    dismissLoadingDialog()
                    backPress()
                }
            }
        }
    }

    private fun makeView(dto : BoardDto){
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
                tvMeetAddress.text = dto.meetPoint
            }else{
                llMeetPlace.visibility = View.GONE
            }
            if(dto.buyPoint != null){
                llBuyPlace.visibility = View.VISIBLE
                tvBuyAddress.text = dto.buyPoint
            }else{
                llBuyPlace.visibility = View.GONE
            }
            tvPrice.text = CommonUtils.makeComma(dto.price)

            //button
            val userId : String? = Application.sharedPreferences.getAuthToken()
            if(dto.participator.contains(userId)){
                btnParticipate.text = requireContext().getString(R.string.btn_cancel)
                btnParticipate.backgroundTintList = requireContext().getColorStateList(R.color.gray)
            }else{
                btnParticipate.text = requireContext().getString(R.string.btn_participate)
                btnParticipate.backgroundTintList = requireContext().getColorStateList(R.color.colorAccent)
            }
            if(userId == dto.writer){
                btnParticipate.visibility = View.GONE
            }else{
                btnParticipate.visibility = View.VISIBLE
            }
            //profile
            if(dto.writerProfile!= null){
                Glide.with(requireContext())
                    .load(dto.writerProfile)
                    .into(ivWriterProfile)
            }

            //participator
            var text = "참여자 : "
            dto.participator.forEach{ text += "$it " }
            tvParticipator.text = text
            tvLeftPerson.visibility = View.GONE
            Log.d(TAG, "makeView: maxPeople : ${dto.maxPeople}")
            if(dto.maxPeople != null){
                val leftPerson = dto.maxPeople!! - dto.participator.size
                if(leftPerson in 1..9){
                    tvLeftPerson.visibility = View.VISIBLE
                    tvLeftPerson.text = "${leftPerson}명 남았어요!!"
                }
            }
        }
    }

    //Map
    private fun setMeetMap(map : NaverMap){
        val dto = viewModel.boardDto
        if(dto?.meetPoint == null) return
        val place : LatLng = AddressUtils.getPointsFromGeo(requireContext(), dto.meetPoint!!) ?: return
        val marker = Marker().apply {
            position = place
            iconTintColor = Color.GREEN
            setCaptionAligns(Align.Top)
        }
        marker.map = map
        map.moveCamera(CameraUpdate.scrollAndZoomTo(place, 15.0))
        map.uiSettings.apply {
            isScrollGesturesEnabled = false
            isZoomControlEnabled = true
            isZoomGesturesEnabled = false
        }
        googleMeetMap = map
    }

    private fun setBuyMap(map : NaverMap){
        val dto = viewModel.boardDto
        if(dto?.buyPoint == null) return
        val place : LatLng = AddressUtils.getPointsFromGeo(requireContext(), dto.buyPoint!!) ?: return
        val marker = Marker().apply {
            position = place
            iconTintColor = Color.GREEN
            setCaptionAligns(Align.Top)
        }
        marker.map = map
        map.moveCamera(CameraUpdate.scrollAndZoomTo(place, 15.0))
        map.uiSettings.apply {
            isScrollGesturesEnabled = false
            isZoomControlEnabled = true
            isZoomGesturesEnabled = false
        }
        googleMeetMap = map
    }

    override fun onDestroy() {
        super.onDestroy()
        mapMeetFragment.onDestroy()
        mapBuyFragment.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        mapMeetFragment.onStart()
        mapBuyFragment.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapMeetFragment.onStop()
        mapBuyFragment.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapMeetFragment.onResume()
        mapBuyFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapMeetFragment.onPause()
        mapBuyFragment.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapMeetFragment.onLowMemory()
        mapBuyFragment.onLowMemory()
    }

    private fun backPress(){
        showToast("존재하지 않는 게시글입니다.",ToastType.ERROR)
        findNavController().popBackStack()
    }

    private fun showCommentFragment(){
        findNavController().navigate(R.id.action_boardFragment_to_commentFragment)
    }
}
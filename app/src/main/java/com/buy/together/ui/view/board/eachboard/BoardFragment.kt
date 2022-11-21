package com.buy.together.ui.view.board.eachboard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Math.abs

private const val TAG = "BoardFragment_싸피"
class BoardFragment : BaseFragment<FragmentBoardBinding>(FragmentBoardBinding::bind, R.layout.fragment_board){
    private val navArgs : BoardFragmentArgs by navArgs()
    private val viewModel: BoardViewModel by activityViewModels()

    private lateinit var mapMeetFragment : SupportMapFragment
    private lateinit var googleMeetMap: GoogleMap
    private lateinit var mapBuyFragment : SupportMapFragment
    private lateinit var googleBuyMap: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.collapseToolbar.inflateMenu()
        initAdapter()
        initListener()
        initData()
        initMap()
    }

    private fun initMap(){
        mapMeetFragment = childFragmentManager.findFragmentById(R.id.fcv_meet_map) as SupportMapFragment
        mapMeetFragment.getMapAsync { setMeetMap(it)}
        mapBuyFragment = childFragmentManager.findFragmentById(R.id.fcv_buy_map) as SupportMapFragment
        mapBuyFragment.getMapAsync{ setBuyMap(it) }
    }

    private fun initAdapter(){
        Log.d(TAG, "initAdapter: dto : ${viewModel.boardDto?.images?.isEmpty()}")
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
                    Toast.makeText(requireContext(),"적용되었습니다.",Toast.LENGTH_SHORT).show()
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
        val dto = viewModel.boardDto
        if(dto == null){
            backPress()
            return
        }
        viewModel.getEachBoard(dto.category,dto.id).observe(viewLifecycleOwner){ response ->
            when(response){
                is FireStoreResponse.Loading -> { showLoadingDialog(requireContext()) }
                is FireStoreResponse.Success -> {
                    viewModel.boardDto = viewModel.makeBoard(response.data)
                    makeView(viewModel.boardDto!!)
                    dismissLoadingDialog()
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
                btnParticipate.backgroundTintList = requireContext().getColorStateList(R.color.black_50)
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
                if(leftPerson < 10){
                    tvLeftPerson.visibility = View.VISIBLE
                    tvLeftPerson.text = "${leftPerson}명 남았어요!!"
                }
            }
        }
    }

    //Map
    private fun setMeetMap(map : GoogleMap){
        val dto = viewModel.boardDto
        if(dto?.meetPoint == null) return
        val place : LatLng = AddressUtils.getPointsFromGeo(requireContext(), dto.meetPoint!!) ?: return
        val marker = MarkerOptions().position(place)
        map.addMarker(marker)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(place, 15f)
        map.moveCamera(cameraUpdate)
        map.uiSettings.apply {
            isScrollGesturesEnabled = false
            isZoomControlsEnabled = false
            isZoomGesturesEnabled = false
        }
        googleMeetMap = map
    }

    private fun setBuyMap(map : GoogleMap){
        val dto = viewModel.boardDto
        if(dto?.buyPoint == null) return
        val place : LatLng = AddressUtils.getPointsFromGeo(requireContext(), dto.buyPoint!!) ?: return
        val marker = MarkerOptions().position(place)
        map.addMarker(marker)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(place, 15f)
        map.moveCamera(cameraUpdate)
        map.uiSettings.apply {
            isScrollGesturesEnabled = false
            isZoomControlsEnabled = false
            isZoomGesturesEnabled = false
        }
        googleBuyMap = map
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
        Toast.makeText(requireContext(), "존재하지 않는 게시글입니다.",Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun showCommentFragment(){
        findNavController().navigate(R.id.action_boardFragment_to_commentFragment)
    }
}
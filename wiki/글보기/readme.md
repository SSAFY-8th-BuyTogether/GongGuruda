# 글보기
## 구현 방법
✔️ boardFragment  
#### Main
해당 게시글에 참여의사나 참여취소 의사를 표시할 수 있음

#### Detail
참여하지 않은 사람에게는 참여하기 버튼이 참여한 사람에게는 취소하기 버튼을 보임


✔️ ImageViewPager 
#### Main
이미지를 보여주기 위해 가로 ViewPager2와 Glide, indicator를 사용


---

## 구현 코드
✔️ boardFragment  

📔 BoardFragment.kt
```
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
           
```
<br>

✔️ ImageViewPager  

📔 PageImageAdapter.kt
```
inner class  PagerViewHolder(val binding: ItemViewpagerImageBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position : Int,image : String){
            Glide.with(itemView)
                .load(image)
                .into(binding.ivImageItem)
            if(position <= imageList.size){
                val endPosition = if(position + 2 > imageList.size){
                    imageList.size
                }else{
                    position + 2
                }
                imageList.subList(position,endPosition).forEach{
                    Glide.with(itemView)
                        .load(it).preload()
                }
            }
        }
    }
```
📔 fragment_board.xml
```
       
                <androidx.viewpager2.widget.ViewPager2
                    android:id="@+id/vp_images"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    app:layout_collapseMode="parallax" />

```
📔 item_viewpager_image.xml
```
 <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:id="@+id/iv_image_item"
        android:layout_width="0dp"
        android:layout_height="250dp"
        android:scaleType="centerCrop"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:srcCompat="@drawable/img_category_carrot" />
</androidx.constraintlayout.widget.ConstraintLayout>
```
---


## 실행 화면
https://user-images.githubusercontent.com/72062916/204453287-1ee8a93e-05a2-4dcf-a2e2-f93427b09052.mp4

# 댓글 보기
## 구현 방법
✔️ Comment
#### Main
댓글을 보여주기 위해 recyclerview 사용
본인이 작성한 댓글은 삭제 가능

<br>
✔️ Mention
#### Main
mention 기능 사용

#### Detail
본인 댓글이 아닌 댓글을 클릭시 멘션 레이아웃을 활성화함

---

## 구현 코드
✔️ Comment  

📔 CommentAdapter.kt
```
inner class CommentHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int, comment : CommentDto){
            binding.apply{
                val userId = Application.sharedPreferences.getAuthToken()
                if(comment.writer == userId){
                    ibOptionButton.visibility = View.VISIBLE
                }else{
                    ibOptionButton.visibility = View.GONE
                }
                tvCommentWriter.text = comment.writer
                tvCommentContent.text = comment.content
                if(comment.mention == null){
                    tvMention.visibility = View.GONE
                }else{
                    tvMention.visibility = View.VISIBLE
                    tvMention.text = "\u0040${comment.mention}"
                }
                tvCommentTime.text = CommonUtils.getDateString(comment.time.seconds * 1000)
                ibOptionButton.setOnClickListener {
                    itemClickListener.onItemOptionClick(it,comment)
                }
                clCommentLayout.setOnClickListener{
                    itemClickListener.onClick(it,comment)
                }
                if(comment.writerProfile != null){
                    Glide.with(itemView)
                        .load(comment.writerProfile)
                        .into(ivWriterProfile)
                }
            }
        }
    }  
           
```
📔 item_comment.xml
```
     <androidx.cardview.widget.CardView
        android:id="@+id/cv_comment_profile"
        android:layout_width="30dp"
        android:layout_height="30dp"
        app:cardCornerRadius="160dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <ImageView
            android:id="@+id/iv_writer_profile"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:scaleType="centerCrop"
            android:src="@drawable/img_profile_default" />

    </androidx.cardview.widget.CardView>

    <TextView
        android:id="@+id/tv_comment_writer"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        tools:text="누군가"
        android:textSize="16sp"
        android:textStyle="bold"
        android:layout_marginStart="16dp"
        app:layout_constraintStart_toEndOf="@id/cv_comment_profile"
        app:layout_constraintTop_toTopOf="@id/cv_comment_profile"
        app:layout_constraintBottom_toBottomOf="@id/cv_comment_profile"
        />

    <ImageButton
        android:id="@+id/ib_option_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/ic_option"
        android:backgroundTint="@color/black_60"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

```
<br>
✔️ Mention  

📔 CommentFragment.kt
```
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
```
📔 fragment_comment.xml
```
    <LinearLayout
        android:id="@+id/ll_mention_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:background="@color/colorPrimary"
        android:weightSum="12"
        app:layout_constraintBottom_toTopOf="@+id/cl_bottom_layout">

        <TextView
            android:id="@+id/tv_mention"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:textColor="@color/blue"
            android:layout_weight="2"
            tools:text="\@areum"/>

        <ImageButton
            android:id="@+id/ib_mention_remove"
            android:layout_width="match_parent"
            android:layout_height="16dp"
            android:layout_weight="10"
            android:background="@color/transparent"
            android:layout_marginTop="4dp"
            android:layout_gravity="center"
            android:src="@drawable/ic_remove_gray"/>

    </LinearLayout>
```
---

## 실행 화면

https://user-images.githubusercontent.com/72062916/204457673-87f7614e-386d-4219-a0fa-33a422ea1725.mp4

https://user-images.githubusercontent.com/72062916/204457700-6ce08c88-6ffa-46b8-9767-6d726254f687.mp4

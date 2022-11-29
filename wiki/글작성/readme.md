# 글작성
## 구현 방법
✔️ boardWritingFragment  
#### Main
입력할 때 다양한 효과를 보여주기 위해 TextInputLayout과 TextInputEditText를 사용

#### Detail
확인 버튼을 클릭했을 때 입력하지 않은 칸이 있으면 focus를 주고 error를 표시함
만약 이미지가 있다면 이미지 먼저 저장하고 그 위치의 url를 DB에 저장한다.


✔️ ImageRecyclerview  
#### Main
이미지를 보여주기 위해 가로 recyclerview를 사용

#### Detail
갤러리에서 이미지를 가져오면 URI로 저장함
카메라로 사진을 촬영하면 파일로 변환하여 해당 파일의 URI를 저장함
이렇게 저장한 URI는 저장시에 url로 변환되어 DB에 저장됨

✔️ ImageCrop  
#### Main
카메라로 사진 촬영시 가져온 이미지를 crop하기 위해 라이브러리를 사용
"com.github.CanHub:Android-Image-Cropper:4.3.0"

#### Detail
촬영한 이미지 bitmap을 crop하여 uri로 변환한다.

---

## 구현 코드
✔️ boardWritingFragment  

📔 BoardWritingFragment.kt
```
btnOkay.setOnClickListener {
                if(checkAllWritten()){
                    sendBoardData()
                }
            }
            
     fun checkAllWritten() : Boolean{
        binding.apply {
            if (etTitle.editText?.text?.isEmpty() == true) {
                showToast("제목을 입력해주세요", ToastType.WARNING)
                etTitle.requestFocus()
                return false
            }
            if(selectedTime == null){
                showToast("날짜를 선택해주세요", ToastType.WARNING)
                ibDeadlineButton.requestFocus()
                return false
            }
            if(etPrice.editText?.text?.isEmpty() == true){
                showToast("가격을 입력해주세요", ToastType.WARNING)
                etPrice.requestFocus()
                return false
            }
            if(etPrice.editText?.text?.toString()?.length!! > 10){
                showToast("가격은 10자리 이하여야 합니다.", ToastType.WARNING)
                etPrice.requestFocus()
                return false
            }
            etPrice.error = null
            if(etContent.editText?.text?.isEmpty() == true){
                showToast("내용을 입력해주세요", ToastType.WARNING)
                etContent.requestFocus()
                return false
            }
        }
        return true
    }       
           
```
📔 fragment_board_writing.xml
```
        <com.google.android.material.textfield.TextInputLayout
                android:id="@+id/et_title"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="@string/label_title"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent">

                <com.google.android.material.textfield.TextInputEditText
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@color/white"
                    android:imeOptions="actionNext"
                    android:singleLine="true"
                    android:textSize="16sp" />

            </com.google.android.material.textfield.TextInputLayout>
```
<br>
✔️ ImageRecyclerview  

📔 ImageAdapter.kt
```
 inner class ImageHolder(private val binding : ItemImageBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int,dto: Uri){
            Glide.with(content)
                .load(dto)
                .placeholder(R.drawable.img_category_carrot)
                .error({
                    Log.d(TAG, "bindInfo: error===========")
                })
                .into(binding.ivImageImg)

            binding.ibImageClose.setOnClickListener{
                Log.d(TAG, "clcked: ${ImageList[position]}")
                val index = ImageList.indexOf(dto)
                ImageList.removeAt(index)
                Log.d(TAG, "bindInfo: ${index}")
                Log.d(TAG, "삭제 한 후 결과: ${ImageList}")
                notifyDataSetChanged()
                itemClickListener.onClick(it,ImageList.size)
            }
        }
    }
```
📔 fragment_board_writing.xml
```
       <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rv_images"
            android:paddingTop="8dp"
            android:background="@color/black_5"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            app:layout_constraintBottom_toTopOf="@id/btn_okay"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />
```
📔 item_image.xml
```
  <androidx.cardview.widget.CardView
        android:layout_marginStart="8dp"
        android:layout_width="100dp"
        android:layout_height="100dp"
        app:cardCornerRadius="10dp"
        app:cardElevation="0dp"
        android:backgroundTint="@color/grayForBackground"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_marginBottom="10dp">
    <ImageView
        android:id="@+id/iv_image_img"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:scaleType="centerCrop"
        android:src="@drawable/img_category_carrot"
        app:layout_constraintDimensionRatio="1"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    </androidx.cardview.widget.CardView>
    <ImageButton
        android:id="@+id/ib_image_close"
        android:layout_width="16dp"
        android:layout_height="16dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="8dp"
        android:background="@color/black_10"
        android:src="@android:drawable/ic_menu_close_clear_cancel"
        app:layout_constraintDimensionRatio="1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:tint="@color/red" />
```
<br>
✔️ ImageCrop   

📔 BoardWritingFragment.kt
```
private val imageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "hi why so serious)")
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "data: ${result} ")
            val imageUri: Uri? = result.data?.data
            Log.d(TAG, "uri : $imageUri")
            if (imageUri != null) {
                imageAdapter.apply {
                    ImageList.add(imageUri)
                    notifyItemInserted(ImageList.size - 1)
                    Log.d(TAG, "images : ${ImageList.size}")
                    binding.rvImages.visibility = View.VISIBLE
                }
            }
        }
    }

    private val cameraListener = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if(result.data?.extras?.get("data") != null){
                val img = result.data?.extras?.get("data") as Bitmap
                viewModel.BitmapImage = img
                tempSaveData()
                showCropImageFragment()
            }
        }
    }
```

📔 ImageCropFragment.kt
```
val img = viewModel.BitmapImage
        if(img == null){
            showToast("이미지를 가져올 수 없습니다.", ToastType.ERROR)
            findNavController().popBackStack()
        }
        viewModel.BitmapImage = null
        binding.cropImageView.setImageBitmap(img)

        binding.btnSave.setOnClickListener {
            val getImg = binding.cropImageView.getCroppedImage(500,500)
            val uri = getImg?.let {
                GalleryUtils.getImageUri(requireContext(),it)
            }
            viewModel.imageUri = uri

            findNavController().popBackStack()
        }
```

📔 fragment_image_crop.xml
```
   <com.canhub.cropper.CropImageView
        android:id="@+id/cropImageView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <Button
        android:id="@+id/btn_save"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="저장"
        app:layout_constraintBottom_toBottomOf="parent" />
```
---

## 실행 화면

https://user-images.githubusercontent.com/72062916/204451808-28a73d92-3945-42a4-981b-54743ac1bd46.mp4

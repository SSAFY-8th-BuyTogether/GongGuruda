package com.buy.together.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object GalleryUtils {
    private val fbStore = FirebaseStorage.getInstance().reference
    val baseProfile = "@drawable/img_profile_default.png"
    
    fun getGallery(context : Context, imageLauncher : ActivityResultLauncher<Intent>) {
        getPermission(object : PermissionListener {
            override fun onPermissionGranted() {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                imageLauncher.launch(intent)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(context, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getPermission(listener: PermissionListener){
        TedPermission.create()
            .setPermissionListener(listener)
            .setDeniedMessage("권한을 허용해주세요")
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

    suspend fun insertImage(url : ArrayList<String>, imgUri : ArrayList<Uri>) : ArrayList<String>{ //TODO : 실패했을 경우 처리
        return withContext(Dispatchers.IO){
            val list = arrayListOf<String>()
            for(i in 0..url.size-1){
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("싸피 util", "이미지 저장 성공~~~~~~~~~~$i\n image : ${url[i]}")
                    val imageRef = fbStore.child("images/${url[i]}")
                    list.add(imageRef.putFile(imgUri[i]).await().storage.downloadUrl.await().toString())
                }.join()
            }
            list
        }
    }

    fun changeProfileImg(userId : String, imgUri : Uri) : LiveData<String>{
        if(imgUri.toString() == baseProfile){
            return MutableLiveData(baseProfile)
        }
        val profileImg : MutableLiveData<String> = MutableLiveData()
        CoroutineScope(Dispatchers.IO).launch {
            val img = insertImage(arrayListOf("IMG_${userId}_PROFILE.png"), arrayListOf(imgUri))
            profileImg.postValue(img[0])
        }
        return profileImg
    }
}
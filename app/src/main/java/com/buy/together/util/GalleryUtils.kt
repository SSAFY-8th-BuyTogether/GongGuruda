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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

object GalleryUtils {
    private val fbStore = FirebaseStorage.getInstance().reference.child("images")
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

    fun insertImage(url : String, imgUri : Uri, listener : OnSuccessListener<Uri>){ //TODO : 실패했을 경우 처리
        fbStore.child(url).putFile(imgUri)
            .addOnSuccessListener {
                fbStore.child(url).downloadUrl
                    .addOnSuccessListener(listener)
                    .addOnFailureListener{
                        Log.d("싸피", "getImage: Fail ${it.message}")
                    }
            }
            .addOnFailureListener{
                Log.d("싸피", "getImage: Fail ${it.message}")
            }
    }

    fun changeProfileImg(userId : String, imgUri : Uri) : LiveData<String>{
        if(imgUri.toString() == baseProfile){
            return MutableLiveData(baseProfile)
        }
        val profileImg : MutableLiveData<String> = MutableLiveData()
        insertImage("IMG_${userId}_PROFILE.png", imgUri){
            profileImg.postValue(it.toString())
        }
        return profileImg
    }
}
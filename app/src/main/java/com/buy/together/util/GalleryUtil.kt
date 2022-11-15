package com.buy.together.util

import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

class GalleryUtil {
//    val REVIEW_MIN_LENGTH = 10
//    val READ_GALLERY = 1
//
//    val PARAM_KEY_IMAGE = "image"
//    val PARAM_KEY_PRODUCT_ID = "product_id"
//    val PARAM_REVIEW = "review_content"
//    val PARAM_KEY_RATING = "rating"

    companion object{
//        fun getImageFromGallery(){
//
//        }

        fun getPermission(listener: PermissionListener){
            TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedMessage("권한을 허용해주세요")
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
        }
    }
}
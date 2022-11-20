package com.buy.together.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.buy.together.util.CustomDialog
import com.buy.together.util.CustomDialogBasicOneButton
import com.buy.together.util.CustomDialogBasicTwoButton

// Fragment의 기본을 작성, 뷰 바인딩 활용
abstract class BaseFragment<B : ViewBinding>(private val bind: (View) -> B, @LayoutRes layoutResId: Int) : Fragment(layoutResId) {
    private var _binding: B? = null
    val binding get() = _binding?: throw IllegalStateException("binding fail")

    lateinit var mLoadingDialog: CustomDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bind(super.onCreateView(inflater, container, savedInstanceState)!!)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showCustomDialogBasicOneButton(msg : String){
        val dialog = CustomDialogBasicOneButton(requireContext(),  msg).apply {
            clickListener = object : CustomDialogBasicOneButton.DialogButtonClickListener {
                override fun dialogClickListener() { dismiss() }
            }
        }
        showDialog(dialog, viewLifecycleOwner)
    }

    fun showCustomDialogBasicTwoButton(msg : String, cancelBtn : String, customBtn : String, customAction : () -> Unit){
        val dialog = CustomDialogBasicTwoButton(requireContext(),  msg, cancelBtn, customBtn).apply {
            clickListener = object : CustomDialogBasicTwoButton.DialogButtonClickListener {
                override fun dialogCloseClickListener() { dismiss() }
                override fun dialogCustomClickListener() { customAction() }
            }
        }
        showDialog(dialog, viewLifecycleOwner)
    }

    private fun showDialog(dialog: Dialog, lifecycleOwner: LifecycleOwner?, cancelable: Boolean = true, dismissHandler: (() -> Unit)? = null) {
        val targetEvent = if (cancelable) Lifecycle.Event.ON_STOP else Lifecycle.Event.ON_DESTROY
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == targetEvent && dialog.isShowing) {
                dialog.dismiss()
                dismissHandler?.invoke()
            }
        }
        dialog.show()
        lifecycleOwner?.lifecycle?.addObserver(observer)
        dialog.setOnDismissListener { lifecycleOwner?.lifecycle?.removeObserver(observer) }
    }

    fun showLoadingDialog(context: Context) {
        mLoadingDialog = CustomDialog(context)
        mLoadingDialog.show()
    }

    fun dismissLoadingDialog() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
    }
}
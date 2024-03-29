package com.buy.together.ui.base


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.buy.together.util.CustomDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.dmoral.toasty.Toasty


abstract class BaseBottomSheetDialogFragment<B : ViewBinding>(private val bindingInflater: (layoutInflater:LayoutInflater) -> B) : BottomSheetDialogFragment() {
    private var _binding: B? = null
    val binding get() = _binding?: throw IllegalStateException("binding fail")

    lateinit var mLoadingDialog: CustomDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater.invoke(inflater)
        return _binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            setupRatio(bottomSheet)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setEvent()
    }

    abstract fun initView()
    abstract fun setEvent()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun showToast(message: String, type : ToastType?=null, iconEnable : Boolean=true) {
        when (type){
            ToastType.CUSTOM -> Toasty.warning(requireContext(), message, Toast.LENGTH_SHORT).show()
            ToastType.INFO -> Toasty.info(requireContext(), message, Toast.LENGTH_SHORT, iconEnable).show()
            ToastType.ERROR -> Toasty.error(requireContext(), message, Toast.LENGTH_SHORT, iconEnable).show()
            ToastType.SUCCESS -> Toasty.success(requireContext(), message, Toast.LENGTH_SHORT, iconEnable).show()
            ToastType.WARNING -> Toasty.warning(requireContext(), message, Toast.LENGTH_SHORT, iconEnable).show()
            else -> Toasty.normal(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
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

    private fun setupRatio(view : View){
        val layoutParams = view.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        view.layoutParams = layoutParams
    }

    private fun getBottomSheetDialogDefaultHeight(): Int { return getWindowHeight() * 90 / 100 }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    enum class ToastType { ERROR, SUCCESS, INFO, WARNING, BASIC, CUSTOM}
}
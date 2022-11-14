package com.buy.together.ui.view.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.databinding.FragmentBoardBinding
import com.buy.together.databinding.FragmentBoardWritingBinding
import com.buy.together.ui.adapter.ImageAdpater
import com.buy.together.ui.base.BaseFragment

class BoardWritingFragment : BaseFragment<FragmentBoardWritingBinding>(
    FragmentBoardWritingBinding::bind, R.layout.fragment_board_writing
){
    lateinit var spinnerAdapter : ArrayAdapter<String>
    lateinit var imageAdapter : ImageAdpater
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setListener()
    }

    fun setAdapter(){
        val items = resources.getStringArray(R.array.categories)
        spinnerAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,items)
        binding.spinnerCategory.adapter = spinnerAdapter

        //imageAdapter
        imageAdapter = ImageAdpater(ArrayList())
        imageAdapter.itemClickListener = object : ImageAdpater.ItemClickListener{
            override fun onClick(view: View, size: Int) {
                if(size == 0){
                    binding.rvImages.visibility = View.GONE
                }else{
                    binding.rvImages.visibility = View.VISIBLE
                }
            }
        }
        binding.rvImages.adapter = imageAdapter
    }

    fun setListener(){
        binding.apply{
            llOptionButton.setOnClickListener{
                if(includeWritingOption.clOptionBody.visibility == View.VISIBLE){
                    includeWritingOption.clOptionBody.visibility = View.GONE
                    ivDownImg.animate().setDuration(200).rotation(0f)
                }else{
                    includeWritingOption.clOptionBody.visibility = View.VISIBLE
                    ivDownImg.animate().setDuration(200).rotation(180f)
                }
            }
            ibBackButton.setOnClickListener{
                findNavController().popBackStack()
            }
        }
    }
}
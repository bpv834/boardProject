package com.lion.boardproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion.boardproject.fragment.BottomSheetBoardReadChatFragment

class RowChatViewModel(val bottomSheetBoardReadChatFragment: BottomSheetBoardReadChatFragment):ViewModel() {

    // textViewDateRowChat - text
    val textViewDateRowChat = MutableLiveData("")
}
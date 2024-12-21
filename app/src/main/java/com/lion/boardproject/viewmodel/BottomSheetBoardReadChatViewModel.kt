package com.lion.boardproject.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lion.boardproject.fragment.BottomSheetBoardReadChatFragment
class BottomSheetBoardReadChatViewModel(val bottomSheetBoardReadChatFragment: BottomSheetBoardReadChatFragment) : ViewModel() {
    // editTextCommentBottomSheetBoardReadChat - text
    val editTextCommentBottomSheetBoardReadChat = MutableLiveData("")

    // buttonSubmitCommentBottomSheetBoardReadChat - onClick
    fun onclickInputComment() {
        /*Log.d("test200","onClick")*/
        bottomSheetBoardReadChatFragment.proBoardWriteSubmit()
        editTextCommentBottomSheetBoardReadChat.value=""
    }
}


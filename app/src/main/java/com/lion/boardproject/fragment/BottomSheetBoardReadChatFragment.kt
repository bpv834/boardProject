package com.lion.boardproject.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.firebase.Timestamp
import com.lion.boardproject.BoardActivity
import com.lion.boardproject.R
import com.lion.boardproject.databinding.FragmentBottomSheetBoardReadChatBinding
import com.lion.boardproject.databinding.RowChatBinding
import com.lion.boardproject.model.ReplyModel
import com.lion.boardproject.service.ReplyService
import com.lion.boardproject.util.ReplyState
import com.lion.boardproject.viewmodel.BottomSheetBoardReadChatViewModel
import com.lion.boardproject.viewmodel.RowChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.text.SimpleDateFormat
import java.util.Locale


class BottomSheetBoardReadChatFragment(val boardReadFragment: BoardReadFragment) :
    BottomSheetDialogFragment() {
    // 메인 RecyclerView를 구성하기 위해 사용할 리스트
    var recyclerViewList = mutableListOf<ReplyModel>()


    override fun onStart() {
        super.onStart()
        // dialog가 null이 아니고 BottomSheetDialog 타입인지 확인
        val bottomSheetDialog = dialog as? BottomSheetDialog
        if (bottomSheetDialog != null) {
            customSheetSize(bottomSheetDialog)
            whenDownKeyBoard()
        } else {
            Log.e("BottomSheetError", "Dialog is null or not a BottomSheetDialog")
        }
    }

    lateinit var boardActivity: BoardActivity
    lateinit var fragmentBottomSheetBoardReadChatBinding: FragmentBottomSheetBoardReadChatBinding
    lateinit var fragmentRowChatBinding: RowChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBottomSheetBoardReadChatBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_board_read_chat,
            container,
            false
        )
        fragmentBottomSheetBoardReadChatBinding.bottomSheetBoardReadChatViewModel =
            BottomSheetBoardReadChatViewModel(this@BottomSheetBoardReadChatFragment)
        fragmentBottomSheetBoardReadChatBinding.lifecycleOwner =
            this@BottomSheetBoardReadChatFragment

        fragmentRowChatBinding =
            DataBindingUtil.inflate(inflater, R.layout.row_chat, container, false)
        fragmentRowChatBinding.rowChatViewModel =
            RowChatViewModel(this@BottomSheetBoardReadChatFragment)
        fragmentRowChatBinding.lifecycleOwner = this@BottomSheetBoardReadChatFragment

        boardActivity = activity as BoardActivity
        // 바텀시트 크기 조절, 포커스에 따라 조절하기
        settingEditTextCommentBottomSheetBoardReadChat()
        // 리사이클러뷰 세팅
        settingRecyclerView()
        // 리스트 세팅
        refreshBottomSheetRecyclerView()
        // 엔터키 리스너
        // ㅂㅈsettingEditTextDoneAction()
        return fragmentBottomSheetBoardReadChatBinding.root

    }

    // 데이터를 가져와 MainRecyclerView를 갱신하는 메서드
    fun refreshBottomSheetRecyclerView() {
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                ReplyService.gettingReplyListData(boardReadFragment.boardDocumentId)
            }
            recyclerViewList = work1.await()

            recyclerViewList.forEach {
                Log.d("test300", "${it.toString()}")

            }

            fragmentBottomSheetBoardReadChatBinding.recyclerViewBoardChat.adapter?.notifyDataSetChanged()
        }
    }


    // 에딧텍스트 포커스에 따라 바텀시트 사이즈를 결정
    fun settingEditTextCommentBottomSheetBoardReadChat() {
        fragmentBottomSheetBoardReadChatBinding.apply {
            editTextCommentBottomSheetBoardReadChat.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    /*Log.d("test200","hasFocus")*/
                    // 사이즈를 확장
                 /*   expandBottomSheetSize()*/
                } else {
                    // 포커스를 풀면 사이즈를 축소
                    customSheetSize(dialog as BottomSheetDialog)
                }
            }
        }
    }

    // 포커스 풀기
    fun clearEditTextFocus() {
        if (dialog?.isShowing == true){
            clearEditTextFocus()
        }

    }

    // 댓글 작성 완료 처리 메서드
    fun proBoardWriteSubmit() {
        fragmentBottomSheetBoardReadChatBinding.apply {

            val comment =
                bottomSheetBoardReadChatViewModel?.editTextCommentBottomSheetBoardReadChat?.value!!

            if (comment.isEmpty()) {
                boardActivity.showMessageDialog("오류","댓글을 입력해주세요","확인"){
                }
                return
            }

            CoroutineScope(Dispatchers.Main).launch {

                // 서버에 저장할 댓글 데이터
                val replyModel = ReplyModel()
                replyModel.replyNickName = boardActivity.loginUserNickName
                replyModel.replyText = comment
                replyModel.replyBoardId = boardReadFragment.boardDocumentId
                replyModel.replyTimeStamp = Timestamp.now()
                replyModel.replyState = ReplyState.REPLY_STATE_NORMAL
                // 저장한다.

                val work1 = async(Dispatchers.IO) {
                    ReplyService.addBoardReplyData(replyModel)
                }
                val documentId = work1.await()


                // 키보드를 내리고, 포커스를 푼다, 포커스를 풀면 바텀시트 사이즈를 줄이는 메서드 실행되기 때문
                hideKeyboardAndClearFocus()

            }
            refreshBottomSheetRecyclerView()
        }
    }

/*
    // 키보드 확인 버튼
    // 문자열을 비우는게 애매해서 구현 하지 않음
    fun settingEditTextDoneAction() {
        val editText = fragmentBottomSheetBoardReadChatBinding.editTextCommentBottomSheetBoardReadChat

        editText.setOnEditorActionListener { v, actionId, event ->
            // 'Done' 버튼이 눌렸을 때 actionId가 IME_ACTION_DONE인지를 확인
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                proBoardWriteSubmit()
                fragmentBottomSheetBoardReadChatBinding.editTextCommentBottomSheetBoardReadChat

                true  // 리스너가 이벤트를 처리했음을 반환
            }
            false
        }
    }
*/



    // 댓글 삭제 메서드
    fun deleteReplyData(boardId: String, replyDocId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("test300", "coroution")
            val work1 = async(Dispatchers.IO) {
                ReplyService.deleteBoardReplyData(boardId, replyDocId)
            }
            work1.join()
            refreshBottomSheetRecyclerView()

        }
    }


    // RecyclerView를 구성하는 메서드
    fun settingRecyclerView() {
        fragmentBottomSheetBoardReadChatBinding.apply {
            // 어뎁터
            recyclerViewBoardChat.adapter = RecyclerViewMainAdapter()
            // LayoutManager
            recyclerViewBoardChat.layoutManager = LinearLayoutManager(boardActivity)
            // 구분선
            val deco =
                MaterialDividerItemDecoration(boardActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewBoardChat.addItemDecoration(deco)
        }
    }

    // 바텀시트 사이즈를 2/3 크기로 조절하여, 입력텍스트와 리사이클러뷰를 볼수있게 레이아웃을 짠다
    fun customSheetSize(dialog: BottomSheetDialog) {
        // BottomSheetDialog 내부에서 실제 바텀시트 뷰를 ID를 통해 찾아냅니다.
        val bottomSheet =
            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        // bottomSheet가 null이 아닌 경우에만 실행되는 블록
        bottomSheet?.apply {
/*
            // bottomSheet와 연결된 BottomSheetBehavior를 가져옵니다.
            val behavior = BottomSheetBehavior.from(bottomSheet)*/

            // 화면의 전체 높이를 가져옵니다.
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels // 화면 높이를 픽셀 단위로 가져옵니다.
            val twoThirdsHeight = (screenHeight * 2) / 3 // 화면 높이의 2/3 크기를 계산합니다.

            // 바텀시트의 높이를 계산한 2/3 크기로 고정합니다.
            bottomSheet.layoutParams.height = twoThirdsHeight // 바텀시트의 레이아웃 높이를 설정합니다.
            bottomSheet.requestLayout() // 레이아웃 변경 사항을 적용합니다.

            /*   // 바텀시트의 드래그 기능을 비활성화합니다.
               behavior.isDraggable = false // 사용자가 바텀시트를 드래그로 움직일 수 없도록 설정합니다.
               behavior.state = BottomSheetBehavior.STATE_COLLAPSED // 바텀시트를 접힌 상태로 고정합니다.*/
        }
    }

    // 바텀시트 펼치기
    private fun expandBottomSheetSize() {
        Log.d("test200", "increase")
        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            // 화면의 전체 높이를 가져옵니다.
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels // 화면 높이를 픽셀 단위로 가져옵니다.

            // 바텀시트의 높이를 전체 화면 크기로 고정합니다.
            bottomSheet.layoutParams.height = screenHeight // 바텀시트의 레이아웃 높이를 설정합니다.
            bottomSheet.requestLayout() // 레이아웃 변경 사항을 적용합니다.

            // BottomSheetBehavior와 연결
            val behavior = BottomSheetBehavior.from(bottomSheet)
            // BottomSheetBehavior 상태를 확장으로 설정
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // 상태 변경 시 충돌 방지: 확장된 상태에서만 유지
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // 확장 상태 유지
                    if (newState != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // 슬라이드 중 추가 동작이 필요하면 구현 가능
                }
            })
        }
    }


    // 바텀시트에서 boardActivity의 hideKeyboard메서드가 안먹어 직접 만들어 실행
    fun hideKeyboardAndClearFocus() {
        fragmentBottomSheetBoardReadChatBinding.apply {
            // 키보드 숨기기
            val inputMethodManager = activity?.getSystemService(InputMethodManager::class.java)
            inputMethodManager?.hideSoftInputFromWindow(
                editTextCommentBottomSheetBoardReadChat.windowToken, 0
            )
            // 포커스 해제
            editTextCommentBottomSheetBoardReadChat.clearFocus()
        }
    }

    // 키보드가 내려갔을 때 반응 하는 리스너,
    // implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:3.0.0-RC2") 키보드 내렸을때 반응하는 리스너 의존성
    // import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
    // import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
    fun whenDownKeyBoard() {
        // 키보드 가시성 이벤트 리스너 설정
        KeyboardVisibilityEvent.setEventListener(boardActivity, object :
            KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (!isOpen) {
                    // 키보드가 닫힐 때 동작
                    // 에딧 텍스트 포커스 clear하기,
                    // 포커스가 풀리면, 바텀시트를 조절하기 때문,ㅂㅈ
                    clearEditTextFocus()
                }
            }
        })
    }



    // RecyclerView의 어뎁터
    inner class RecyclerViewMainAdapter :
        RecyclerView.Adapter<RecyclerViewMainAdapter.ViewHolderMain>() {
        // ViewHolder
        inner class ViewHolderMain(val rowBoardChatBinding: RowChatBinding) :
            RecyclerView.ViewHolder(rowBoardChatBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMain {

            val rowBoardChatBinding = DataBindingUtil.inflate<RowChatBinding>(
                layoutInflater,
                R.layout.row_chat,
                parent,
                false
            )

            val viewHolderMain = ViewHolderMain(rowBoardChatBinding)

            // 답글 기능 넣기
            viewHolderMain.rowBoardChatBinding.root.setOnClickListener { }

            viewHolderMain.rowBoardChatBinding.root.setOnLongClickListener {
                if (recyclerViewList[viewHolderMain.adapterPosition].replyNickName == boardActivity.loginUserNickName) {
                    boardActivity.showMessageDialog("삭제 하시겠습니까?", "삭제 시 복구가 불가능합니다.", "삭제") {
                        deleteReplyData(
                            recyclerViewList[viewHolderMain.adapterPosition].replyBoardId,
                            recyclerViewList[viewHolderMain.adapterPosition].replyDocumentId
                        )
                    }
                }
                true
            }

            return viewHolderMain
        }

        override fun getItemCount(): Int {
            return recyclerViewList.size
        }

        override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
            holder.rowBoardChatBinding.textViewNickRowChat.text =
                recyclerViewList[position].replyNickName
            holder.rowBoardChatBinding.textViewContentRowChat.text =
                recyclerViewList[position].replyText
            val timeStamp = recyclerViewList[position].replyTimeStamp

            // 원하는 날짜 형식으로 포맷팅
            val dateFormat = SimpleDateFormat("yy.MM.dd HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(timeStamp!!.toDate())

            holder.rowBoardChatBinding.textViewDateRowChat.text = formattedDate
        }
    }


}
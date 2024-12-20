package com.lion.boardproject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.boardproject.BoardActivity
import com.lion.boardproject.R
import com.lion.boardproject.databinding.FragmentBottomSheetBoardReadChatBinding
import com.lion.boardproject.databinding.RowChatBinding
import com.lion.boardproject.viewmodel.BottomSheetBoardReadChatViewModel

class BottomSheetBoardReadChatFragment(boardReadFragment: BoardReadFragment) : BottomSheetDialogFragment() {


    // 바텀시트 사이즈 조절
    override fun onStart() {
        super.onStart() // 부모 클래스의 onStart()를 호출하여 정상적인 라이프사이클 동작을 보장합니다.

        // 현재 다이얼로그를 BottomSheetDialog로 캐스팅합니다.
        val dialog = dialog as BottomSheetDialog

        // BottomSheetDialog 내부에서 실제 바텀시트 뷰를 ID를 통해 찾아냅니다.
        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)


        // bottomSheet가 null이 아닌 경우에만 실행되는 블록
        bottomSheet?.apply {

/*            // bottomSheet와 연결된 BottomSheetBehavior를 가져옵니다.
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



    // ReyclerView 구성을 위한 임시 데이터
    val tempList1 = Array(50) {
        "닉네임 ${it + 1}"
    }
    val tempList2 = Array(50){
        "내용 ${it + 1}"
    }
    lateinit var boardActivity: BoardActivity
    lateinit var fragmentBottomSheetBoardReadChatBinding: FragmentBottomSheetBoardReadChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBottomSheetBoardReadChatBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_bottom_sheet_board_read_chat,container,false)
        fragmentBottomSheetBoardReadChatBinding.bottomSheetBoardReadChatViewModel = BottomSheetBoardReadChatViewModel(this@BottomSheetBoardReadChatFragment)
        fragmentBottomSheetBoardReadChatBinding.lifecycleOwner = this@BottomSheetBoardReadChatFragment

        boardActivity = activity as BoardActivity

        settingRecyclerView()
        return fragmentBottomSheetBoardReadChatBinding.root

    }

    // 글 작성 완료 처리 메서드
    fun proBoardWriteSubmit(){
        fragmentBottomSheetBoardReadChatBinding.apply {
            val comment = bottomSheetBoardReadChatViewModel?.editTextCommentBottomSheetBoardReadChat?.value!!
        }

    }


    // RecyclerView를 구성하는 메서드
    fun settingRecyclerView(){
        fragmentBottomSheetBoardReadChatBinding.apply {
            // 어뎁터
            recyclerViewBoardChat.adapter = RecyclerViewMainAdapter()
            // LayoutManager
            recyclerViewBoardChat.layoutManager = LinearLayoutManager(boardActivity)
            // 구분선
            val deco = MaterialDividerItemDecoration(boardActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewBoardChat.addItemDecoration(deco)
        }
    }




    // RecyclerView의 어뎁터
    inner class RecyclerViewMainAdapter : RecyclerView.Adapter<RecyclerViewMainAdapter.ViewHolderMain>(){
        // ViewHolder
        inner class ViewHolderMain(val rowBoardChatBinding: RowChatBinding) : RecyclerView.ViewHolder(rowBoardChatBinding.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMain {

            val rowBoardChatBinding = DataBindingUtil.inflate<RowChatBinding>(layoutInflater,R.layout.row_chat,parent,false)

            val viewHolderMain = ViewHolderMain(rowBoardChatBinding)

            viewHolderMain.rowBoardChatBinding.root.setOnClickListener {  }

            return viewHolderMain
        }

        override fun getItemCount(): Int {
            return tempList1.size
        }

        override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
            holder.rowBoardChatBinding.textViewNickRowChat.text = tempList1[position]
            holder.rowBoardChatBinding.textViewContentRowChat.text = tempList2[position]
        }
    }

}
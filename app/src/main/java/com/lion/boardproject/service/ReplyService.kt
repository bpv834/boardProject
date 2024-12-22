package com.lion.boardproject.service

import android.util.Log
import com.lion.boardproject.model.BoardModel
import com.lion.boardproject.model.ReplyModel
import com.lion.boardproject.model.UserModel
import com.lion.boardproject.repository.BoardRepository
import com.lion.boardproject.repository.ReplyRepository
import com.lion.boardproject.repository.UserRepository
import com.lion.boardproject.util.BoardType
import com.lion.boardproject.vo.BoardVO
import com.lion.boardproject.vo.ReplyVO
import com.lion.boardproject.vo.UserVO

class ReplyService {
    companion object {

        // 댓글 데이터를 저장하는 메서드
        // 새롭게 추가된 문서의 id를 반환한다.
        suspend fun addBoardReplyData(replyModel: ReplyModel): String {
            // VO 객체를 생성한다.
            val replyVO = replyModel.toReplyVO()
            // 저장한다.
            val documentId = ReplyRepository.addReplyData(replyVO)
            return documentId
        }

        // 댓글 데이터 전체를 불러오는 메서드
        suspend fun gettingReplyListData(boardDocId: String) : MutableList<ReplyModel>{

            val replyModelList = mutableListOf<ReplyModel>()
            // Doc Id와 Reply Vo를 담은 mapList 리턴
            val resultMap = ReplyRepository.gettingReplyListData(boardDocId)

            resultMap.forEach {
                val replyVo = it["replyVO"] as ReplyVO
                val replyModel = replyVo.toReplyModel(it["documentId"] as String)

                Log.d("test300",replyModel.toString())
                replyModelList.add(replyModel)
            }
            return replyModelList

        }

    }
}
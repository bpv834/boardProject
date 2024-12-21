package com.lion.boardproject.service

import com.lion.boardproject.model.BoardModel
import com.lion.boardproject.model.ReplyModel
import com.lion.boardproject.model.UserModel
import com.lion.boardproject.repository.BoardRepository
import com.lion.boardproject.repository.ReplyRepository

class ReplyService {
companion object{

    // 댓글 데이터를 저장하는 메서드
    // 새롭게 추가된 문서의 id를 반환한다.
    suspend fun addBoardReplyData(replyModel: ReplyModel) : String{
        // VO 객체를 생성한다.
        val replyVO = replyModel.toReplyVO()
        // 저장한다.
        val documentId = ReplyRepository.addReplyData(replyVO)
        return documentId
    }
}


}
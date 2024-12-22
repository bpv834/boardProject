package com.lion.boardproject.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lion.boardproject.vo.ReplyVO
import kotlinx.coroutines.tasks.await

class ReplyRepository {
    companion object {

        // 특정 boardId의 댓글을 불러오는 메서드
        suspend fun gettingReplyListData(boardId: String): MutableList<Map<String, *>> {
            val firestore = FirebaseFirestore.getInstance()
            val collectionReference = firestore.collection("BoardData")
                .document(boardId)
                .collection("reply") // 특정 보드의 reply 서브컬렉션 접근

            return try {
                // 데이터를 가져오기
                val result =
                    collectionReference.orderBy("replyTimeStamp", Query.Direction.DESCENDING).get()
                        .await()

                // 반환할 리스트
                val resultMapList = mutableListOf<Map<String, *>>()

                // 데이터 변환 및 추가
                result.forEach { document ->
                    val map = mapOf(
                        // 문서의 id
                        "documentId" to document.id,
                        // 데이터를 가지고 있는 객체
                        "replyVO" to document.toObject(ReplyVO::class.java)
                    )
                    resultMapList.add(map)
                }
                resultMapList
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("test100","error남")
                mutableListOf() // 예외 발생 시 빈 리스트 반환
            }
        }

        // 댓글 입력 메서드
        suspend fun addReplyData(replyVO: ReplyVO): String {
            val firestore = FirebaseFirestore.getInstance()
            val replyCollection = firestore.collection("BoardData").document(replyVO.replyBoardId)
                .collection("reply") // 특정 보드의 reply 서브컬렉션

            val docRef = replyCollection.add(replyVO).await()

            // 댓글 문서 아이디 리턴
            return docRef.id
        }

        // 댓글 삭제 메서드
        suspend fun deleteReplyData(boardId: String, replyId: String) {
            val firestore = FirebaseFirestore.getInstance()
            val replyDocument =
                firestore.collection("BoardData").document(boardId).collection("reply")
                    .document(replyId) // 특정 댓글 문서에 접근
            replyDocument.delete().await()

        }


    }
}
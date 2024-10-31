package shop.samgak.mini_board.comment.services;

import java.util.List;

import shop.samgak.mini_board.comment.dto.CommentDTO;

public interface CommentService {

        /**
         * 주어진 게시물 ID에 해당하는 모든 댓글을 조회
         * 
         * @param postId 댓글을 가져올 게시물의 ID
         * @return 해당 게시물에 대한 댓글 목록
         */
        List<CommentDTO> get(Long postId);

        /**
         * 주어진 게시물에 새로운 댓글을 작성
         * 
         * @param content 댓글 내용
         * @param postId  댓글이 속한 게시물의 ID
         * @param userId  댓글 작성자의 ID
         * @return 작성된 댓글의 정보
         */
        CommentDTO create(String content, Long postId, Long userId);

        /**
         * 특정 댓글을 수정합
         * 
         * @param commentId 수정할 댓글의 ID
         * @param content   수정된 댓글 내용
         * @param userId    댓글을 수정하는 사용자의 ID
         */
        void update(Long commentId, String content, Long userId);

        /**
         * 특정 댓글을 삭제
         * 
         * @param commentId 삭제할 댓글의 ID
         * @param userId    댓글을 삭제하는 사용자의 ID
         */
        void delete(Long commentId, Long userId);
}

package com.numble.team3.comment.infra;

import com.numble.team3.comment.domain.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCommentRepository
    extends JpaRepository<Comment, Long>, JpaSearchCommentRepository {
  @Query("SELECT c FROM Comment c WHERE c.account.id = :accountId and c.id = :commentId")
  Optional<Comment> findCommentByAccountIdWithId(
      @Param("accountId") Long accountId, @Param("commentId") Long commentId);

  @Query(
      "SELECT c FROM Comment c LEFT JOIN FETCH c.accountLikeCommentList"
        + " JOIN FETCH c.account JOIN FETCH c.video WHERE c.id = :commentId AND c.account.id = :accountId")
  Optional<Comment> findCommentFetchJoinByIdAndAccountId(
      @Param("commentId") Long commentId, @Param("accountId") Long accountId);
}

package com.campthreads.CampusThreads.repository;

import com.campthreads.CampusThreads.model.Post;
import com.campthreads.CampusThreads.model.User;
import com.campthreads.CampusThreads.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findAllByUser(User user);
    Optional<Comment> findCommentByUserAndPostAndId(User user, Post post, Long id);


    void deleteAllByPost(Post post);
}
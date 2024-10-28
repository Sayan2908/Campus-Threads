package com.campthreads.CampusThreads.repository;

import com.campthreads.CampusThreads.model.Post;
import com.campthreads.CampusThreads.model.Subthreads;
import com.campthreads.CampusThreads.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubthreads(Subthreads subthreads);

    List<Post> findAllByUser(User user);

}

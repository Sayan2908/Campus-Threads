package com.campthreads.CampusThreads.repository;

import com.campthreads.CampusThreads.model.Post;
import com.campthreads.CampusThreads.model.User;
import com.campthreads.CampusThreads.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IVoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

    void deleteVoteByPostAndUser(Post post, User currentUser);


}

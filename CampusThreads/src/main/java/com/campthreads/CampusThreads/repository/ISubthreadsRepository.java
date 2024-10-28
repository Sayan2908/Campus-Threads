package com.campthreads.CampusThreads.repository;

import com.campthreads.CampusThreads.model.Subthreads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISubthreadsRepository extends JpaRepository<Subthreads, Long> {

    Optional<Subthreads> findByName(String subredditName);
}

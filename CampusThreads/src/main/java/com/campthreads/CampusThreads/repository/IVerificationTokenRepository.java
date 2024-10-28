package com.campthreads.CampusThreads.repository;

import com.campthreads.CampusThreads.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IVerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> deleteVerificationTokenByToken(String token);
}

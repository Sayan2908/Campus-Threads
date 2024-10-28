package com.campthreads.CampusThreads.service;

import com.campthreads.CampusThreads.Exceptions.CustomException;
import com.campthreads.CampusThreads.model.RefreshToken;
import com.campthreads.CampusThreads.repository.IRefreshTokenRepository;
import com.campthreads.CampusThreads.repository.IUserRepository;
import com.campthreads.CampusThreads.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import com.campthreads.CampusThreads.model.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private final IRefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final IUserRepository userRepository;

    @Transactional
    public RefreshToken generateRefreshToken(String username) {
        String token = jwtService.generateRefreshToken(username);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        Instant expiryDate = Instant.now().plus(Duration.ofDays(1));
        refreshToken.setExpirationDate(expiryDate);
        Instant createdDate = Instant.now();
        refreshToken.setCreatedDate(createdDate);

        User user = userRepository.findByUsername(username).orElseThrow(()-> new CustomException("No user found with username to generate refresh token"));
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
    @Transactional
    public void validateRefreshToken(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()-> new CustomException("Invalid refresgh token : " + refreshToken));
    }
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}

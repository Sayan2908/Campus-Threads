package com.campthreads.CampusThreads.service;

import com.campthreads.CampusThreads.Exceptions.CustomException;
import com.campthreads.CampusThreads.dto.VoteDto;
import com.campthreads.CampusThreads.model.Post;
import com.campthreads.CampusThreads.model.Vote;
import com.campthreads.CampusThreads.model.VoteType;
import com.campthreads.CampusThreads.repository.IPostRepository;
import com.campthreads.CampusThreads.repository.IVoteRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.campthreads.CampusThreads.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
@Slf4j
public class VoteService {

    private final IVoteRepository voteRepository;
    private final IPostRepository postRepository;
    private final AuthService authService;
    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(()->new CustomException("No posts found with id: "+ voteDto.getPostId()));
        Optional<Vote> voteByPostandUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(
                post,authService.getCurrentUser());

        if (voteByPostandUser.isPresent() && voteByPostandUser.get().getVoteType().toString().equals(voteDto.getVoteType().toString())){
//            throw new CustomException("You have already "+ voteDto.getVoteType().toString().toLowerCase()+"d for this post");throw new CustomException("You have already "+ voteDto.getVoteType().toString().toLowerCase()+"d for this post");
            post.setVoteCount(post.getVoteCount()+(UPVOTE.equals(voteDto.getVoteType())?-1:1));
            voteRepository.deleteVoteByPostAndUser(post,authService.getCurrentUser());
        } else if (voteByPostandUser.isPresent() && !voteByPostandUser.get().getVoteType().toString().equals(voteDto.getVoteType().toString())){
            post.setVoteCount(post.getVoteCount()+(UPVOTE.equals(voteDto.getVoteType())?2:-2));
            voteRepository.deleteVoteByPostAndUser(post,authService.getCurrentUser());
            voteRepository.save(mapVote(voteDto,post));
        } else if (voteByPostandUser.isEmpty() && UPVOTE.equals(voteDto.getVoteType())){
            post.setVoteCount(post.getVoteCount()+1);
            voteRepository.save(mapVote(voteDto,post));
        } else {
            post.setVoteCount(post.getVoteCount()-1);
            voteRepository.save(mapVote(voteDto,post));
        }

        postRepository.save(post);

    }

    private Vote mapVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
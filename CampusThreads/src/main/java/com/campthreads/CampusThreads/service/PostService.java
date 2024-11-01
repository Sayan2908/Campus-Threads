package com.campthreads.CampusThreads.service;

import com.campthreads.CampusThreads.Exceptions.CustomException;
import com.campthreads.CampusThreads.dto.PostRequest;
import com.campthreads.CampusThreads.dto.PostResponse;
import com.campthreads.CampusThreads.mapper.PostMapper;
import com.campthreads.CampusThreads.model.*;
import com.campthreads.CampusThreads.repository.ICommentRepository;
import com.campthreads.CampusThreads.repository.IPostRepository;
import com.campthreads.CampusThreads.repository.ISubthreadsRepository;
import com.campthreads.CampusThreads.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private final ISubthreadsRepository subredditRepository;
    private final AuthService authService;
    private final PostMapper postMapper;
    private final IPostRepository postRepository;
    private final IUserRepository userRepository;
    private final ICommentRepository commentRepository;
    @Transactional
    public PostResponse save(PostRequest postRequest) {
        Subthreads subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(()->new CustomException("Subthreads not found: "+postRequest.getSubredditName()));
        User user = authService.getCurrentUser();
        Post savedPost = postRepository.save(postMapper.map(postRequest,subreddit,user));
        String POST_URL = ServletUriComponentsBuilder.fromCurrentContextPath().path(
                "/api/v1/posts/"+savedPost.getPostId()).toUriString();
        savedPost.setUrl(POST_URL);
        postRepository.save(savedPost);
        return postMapper.mapToDto(savedPost);
    }

    @Transactional
    public List<PostResponse> getAllPosts() {


        List<PostResponse> allPosts = postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());

        return allPosts;

    }

    @Transactional
    public PostResponse getPost(Long id) {

        Post post = postRepository.findById(id).orElseThrow(
                ()-> new CustomException("Post with the following id not found " + id)
        );

        return postMapper.mapToDto(post);
    }

    @Transactional
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {

        Subthreads subreddit = subredditRepository.findById(subredditId).orElseThrow(
                ()->new CustomException("Subthreads with the following id was not found: "+ subredditId));

        List<PostResponse> postsbySubreddit = postRepository.findAllBySubthreads(subreddit)
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
        return postsbySubreddit;

    }

    @Transactional
    public List<PostResponse> getPostsByUsername(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(
                ()->new CustomException("User with the following username was not found: "+ username));

        List<PostResponse> postsbySubreddit = postRepository.findAllByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());
        return postsbySubreddit;
    }

    @Transactional
    public boolean toggleNotificationStatus(Long id, boolean newStatus) {
        Post post = postRepository.findById(id).orElseThrow(
                ()-> new CustomException("Post with the following id not found " + id)
        );
        post.setNotificationStatus(newStatus);
        postRepository.save(post);

        return newStatus;
    }
    @Transactional
    public PostResponse update(PostRequest postRequest) {
        User user = authService.getCurrentUser();

        if (user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.MODERATOR)) {
            Post post = postRepository.findById(postRequest.getPostId()).orElseThrow(
                    ()->new CustomException("Post doesn't exist!"));
            post.setPostName(postRequest.getPostName());
            post.setDescription(postRequest.getDescription());
            postRepository.save(post);
            return postMapper.mapToDto(postRepository.save(post));
        }

        Post post = postRepository.findById(postRequest.getPostId())
                .orElseThrow(()-> new CustomException("No posts found with post id: "+ postRequest.getPostId()));

        if (!post.getUser().equals(user)){
            throw new CustomException("Post doesn't exist or insufficient privileges!");
        }

        post.setPostName(postRequest.getPostName());
        post.setDescription(postRequest.getDescription());
        return postMapper.mapToDto(postRepository.save(post));
    }
    @Transactional
    public void delete(PostRequest postRequest) {
        User user = authService.getCurrentUser();

        if (user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.MODERATOR)) {
            Post post = postRepository.findById(postRequest.getPostId()).orElseThrow(
                    ()->new CustomException("Post doesn't exist!"));

            commentRepository.deleteAllByPost(post);
            postRepository.delete(post);
        }

        Post post = postRepository.findById(postRequest.getPostId())
                .orElseThrow(()-> new CustomException("No posts found with post id: "+ postRequest.getPostId()));

        if (!post.getUser().equals(user)){
            throw new CustomException("Post doesn't exist or insufficient privileges!");
        }

        if (commentRepository.findByPost(post).size()>0){
            post.setPostName("Deleted by owner");
            post.setDescription("");
        } else {
            postRepository.delete(post);
        }

    }
}
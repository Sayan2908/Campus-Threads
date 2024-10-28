package com.campthreads.CampusThreads.service;

import com.campthreads.CampusThreads.Exceptions.CustomException;
import com.campthreads.CampusThreads.dto.SubthreadsDto;
import com.campthreads.CampusThreads.mapper.SubthreadsMapper;
import com.campthreads.CampusThreads.model.Post;
import com.campthreads.CampusThreads.model.Role;
import com.campthreads.CampusThreads.model.Subthreads;
import com.campthreads.CampusThreads.model.User;
import com.campthreads.CampusThreads.repository.ICommentRepository;
import com.campthreads.CampusThreads.repository.IPostRepository;
import com.campthreads.CampusThreads.repository.ISubthreadsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubthreadsService {

    private final ISubthreadsRepository subthreadRepository;
    private final SubthreadsMapper subredditMapper;
    private final AuthService authService;
    private final IPostRepository postRepository;
    private final ICommentRepository commentRepository;
    @Transactional
    public SubthreadsDto save(SubthreadsDto subthreadDto) {
        Subthreads savedSubreddit = subthreadRepository.save(subredditMapper.mapDtoToSubreddit(subthreadDto));
        subthreadDto.setId(savedSubreddit.getId());
        return subthreadDto;
    }


    @Transactional
    public List<SubthreadsDto> getAll() {
        List<SubthreadsDto> allSubthreads = subthreadRepository.findAll()
                .stream()
                .map(subredditMapper::mapSubredditToDto)
                .collect(toList());
        return allSubthreads;
    }


    @Transactional
    public SubthreadsDto getSubthread(Long id) {
        System.out.print("get subreddit called "+ id);
        Subthreads subthread= subthreadRepository.findById(id).orElseThrow(
                ()->new CustomException("No subreddit found with given ID."));
        return subredditMapper.mapSubredditToDto(subthread);
    }


    @Transactional
    public SubthreadsDto update(SubthreadsDto subthreadDto) {

        User user = authService.getCurrentUser();

        if (user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.MODERATOR)) {
            Subthreads subthread = subthreadRepository.findById(subthreadDto.getId()).orElseThrow(
                    ()->new CustomException("Subreddit doesn't exist!"));
            subthread.setDescription(subthreadDto.getDescription());
            subthread.setName(subthreadDto.getName());
            return subredditMapper.mapSubredditToDto(subthreadRepository.save(subthread));
        } else {
            throw new CustomException("Can not update subreddit: insufficient privileges");
        }
    }

    @Transactional
    public void delete(SubthreadsDto subthreadDto) {

        User user = authService.getCurrentUser();

        if (user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.MODERATOR)) {
            Subthreads subthread = subthreadRepository.findById(subthreadDto.getId()).orElseThrow(
                    ()->new CustomException("Subreddit doesn't exist!"));

            List<Post> posts = postRepository.findAllBySubthreads(subthread);

            // Delete all comments belonging to the posts in the subthread
            for (Post post : posts) {
                commentRepository.deleteAllByPost(post);
            }
            // Delete all posts in the subthread
            postRepository.deleteAll(posts);

            subthreadRepository.delete(subthread);

        } else {
            throw new CustomException("Can not delete subthread: insufficient privileges");
        }
    }
}
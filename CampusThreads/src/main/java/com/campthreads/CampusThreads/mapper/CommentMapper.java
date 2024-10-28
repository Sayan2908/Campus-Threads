package com.campthreads.CampusThreads.mapper;

import com.campthreads.CampusThreads.dto.CommentsDto;
import com.campthreads.CampusThreads.model.Comment;
import com.campthreads.CampusThreads.model.Post;
import com.campthreads.CampusThreads.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentsDto.text")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "user", source = "user")
    Comment map(CommentsDto commentsDto, Post post, User user);

    @Mapping(target = "postId", expression = "java(comment.getPost().getPostId())")
    @Mapping(target = "userName", expression = "java(comment.getUser().getUsername())")
    @Mapping(target = "duration", expression = "java(com.github.marlonlom.utilities.timeago.TimeAgo.using(comment.getCreatedDate().toEpochMilli()))")
    CommentsDto mapToDto(Comment comment);
}

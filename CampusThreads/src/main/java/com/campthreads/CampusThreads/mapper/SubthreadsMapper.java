package com.campthreads.CampusThreads.mapper;

import com.campthreads.CampusThreads.dto.SubthreadsDto;
import com.campthreads.CampusThreads.model.Post;
import com.campthreads.CampusThreads.model.Subthreads;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubthreadsMapper {

    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    @Mapping(target = "duration", expression = "java(com.github.marlonlom.utilities.timeago.TimeAgo.using(subreddit.getCreatedDate().toEpochMilli()))")
    SubthreadsDto mapSubredditToDto(Subthreads subreddit);

    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    Subthreads mapDtoToSubreddit(SubthreadsDto subredditDto);
}

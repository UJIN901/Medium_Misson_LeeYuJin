package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.repository.PostRepository;
import com.ll.medium.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public RsData<Post> write(Member author, String title, String body, boolean isPublished) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .body(body)
                .isPublished(isPublished)
                .build();

        postRepository.save(post);
        return RsData.of("200", "%d번째 글이 등록되었습니다.".formatted(post.getId()), post);
    }

    public Object findTop30ByIsPublishedOrderByIdDesc(boolean isPublished) {
        return postRepository.findTop30ByIsPublishedOrderByIdDesc(isPublished);
    }

    public Object findByIsPublishedOrderByIdDesc(boolean isPublished) {
        return postRepository.findByIsPublishedOrderByIdDesc(isPublished);
    }


    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    public Object findByAuthorIdOrderByIdDesc(Long id) {
        return postRepository.findByAuthorIdOrderByIdDesc(id);
    }

    public boolean canModify(Member member, Post post) {
        if (member == null) return false;

        return post.getAuthor().equals(member);
    }

    @Transactional
    public RsData<Post> modify(Post post, String title, String body, Boolean isPublished) {
        post.setTitle(title);
        post.setBody(body);
        post.setIsPublished(isPublished);

        return RsData.of("200", "%d번째 글이 수정되었습니다.".formatted(post.getId()), post);
    }
}

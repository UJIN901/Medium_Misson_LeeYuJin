package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    // 글 작성 서비스
    @Transactional
    public RsData<Post> write(Member author, String title, String body, boolean isPublished, boolean isPaid) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .body(body)
                .isPublished(isPublished)
                .isPaid(isPaid)
                .build();

        postRepository.save(post);
        return RsData.of("200", "%d번째 글이 등록되었습니다.".formatted(post.getId()), post);
    }

    // 최근 30개의 공개된 글 조회
    public Object findTop30ByIsPublishedOrderByIdDesc(boolean isPublished) {
        return postRepository.findTop30ByIsPublishedOrderByIdDesc(isPublished);
    }


    // 공개된 글 전체 조회
    public Object findByIsPublishedOrderByIdDesc(boolean isPublished) {
        return postRepository.findByIsPublishedOrderByIdDesc(isPublished);
    }



    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    public Object findByAuthorIdOrderByIdDesc(Long id) {
        return postRepository.findByAuthorIdOrderByIdDesc(id);
    }

    // 글 수정 권한 확인
    public boolean canModify(Member member, Post post) {
        if (member == null) return false;

        return post.getAuthor().equals(member);
    }

    // 글 수정 서비스
    @Transactional
    public RsData<Post> modify(Post post, String title, String body, Boolean isPublished, Boolean isPaid) {
        post.setTitle(title);
        post.setBody(body);
        post.setIsPublished(isPublished);
        post.setIsPaid(isPaid);


        return RsData.of("200", "%d번째 글이 수정되었습니다.".formatted(post.getId()), post);
    }

    // 글 삭제 권한 확인
    public boolean canDelete(Member member, Post post) {
        if (member == null) return false;

        if (member.isAdmin()) return true;

        return post.getAuthor().equals(member);
    }

    // 글 삭제 서비스
    @Transactional
    public RsData<Post> delete(Post post) {
        postRepository.delete(post);

        return RsData.of("200", "%d번째 글이 삭제되었습니다.".formatted(post.getId()), post);
    }


    public Object findByAuthorUsernameAndIsPublishedOrderByIdDesc(String username, boolean isPublished) {
        return postRepository.findByAuthorUsernameAndIsPublishedOrderByIdDesc(username, isPublished);
    }

    // 글 접근 권한 확인
    public boolean canAccessPost(Post post, Member member, long id) {
        if(post == null){return false;}
        if(member == null){return false;}
        if(!post.getIsPublished()){return false;}

        return post.getAuthor().equals(member);
    }
}

package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/b")
@RequiredArgsConstructor
public class BController {
    private final PostService postService;
    private final MemberService memberService;
    private final Rq rq;

    // 사용자가 작성한 글 목록 조회
    @GetMapping("/{username}")
    public String showUserPostList(@PathVariable String username){
        // 해당하는 회원이 없는 경우 예외 처리
        if(!memberService.findByUsername(username).isPresent()) throw new RuntimeException("해당하는 회원이 없습니다.");
        rq.setAttribute("posts", postService.findByAuthorUsernameAndIsPublishedOrderByIdDesc(username, true));
        return "domain/post/post/userPostList";
    }

    // 사용자가 작성한 특정 글 상세 페이지 조회
    @GetMapping("/{username}/{id}")
    public String showUserPostDetail(@PathVariable String username, @PathVariable long id){
        Post post = postService.findById(id).get();
        // 글 접근 권한이 없는 경우 예외 처리
        Member member = memberService.findByUsername(username).get();
        if(!postService.canAccessPost(post, member, id)) throw new RuntimeException("해당하는 글이 없거나 비공개 된 글입니다..");
        rq.setAttribute("post", post);

        return "domain/post/post/detail";
    }
}

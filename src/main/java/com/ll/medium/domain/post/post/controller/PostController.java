package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import com.ll.medium.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/post/{id}")
    public String showPost(@PathVariable long id){
        rq.setAttribute("post", postService.findById(id).get());
        return "domain/post/post/detail";
    }

    @GetMapping("/b/{username}")
    public String showUserPostList(@PathVariable String username){
        if(!memberService.findByUsername(username).isPresent()) throw new RuntimeException("해당하는 회원이 없습니다.");
        rq.setAttribute("posts", postService.findByAuthorUsernameAndIsPublishedOrderByIdDesc(username, true));
        return "domain/post/post/userPostList";
    }

    @GetMapping("/b/{username}/{id}")
    public String showUserPostDetail(@PathVariable String username, @PathVariable long id){
        Post post = postService.findById(id).get();
        Member member = memberService.findByUsername(username).get();
        if(!postService.canAccessPost(post, member, id)) throw new RuntimeException("해당하는 글이 없거나 비공개 된 글입니다..");
        rq.setAttribute("post", post);

        return "domain/post/post/detail";
    }

    @GetMapping("/post/list")
    public String showList(){
        rq.setAttribute("posts", postService.findByIsPublishedOrderByIdDesc(true));
        return "domain/post/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/myList")
    public String showMyList(){
        rq.setAttribute("posts", postService.findByAuthorIdOrderByIdDesc(rq.getMember().getId()));
        return "domain/post/post/myList";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/write")
    public String showWrite(){
        return "domain/post/post/write";
    }

    @Getter
    @Setter
    public static class WriteForm{
        @NotBlank
        private String title;
        @NotBlank
        private String body;
        private Boolean isPublished = false;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/post/write")
    public String write(@Valid WriteForm writeForm){
        RsData<Post> writeRs = postService.write(rq.getMember(), writeForm.getTitle(), writeForm.getBody(), writeForm.getIsPublished());

        return rq.redirect("/post/myList", writeRs.getMsg());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/{id}/modify")
    public String showModify(@PathVariable long id){
        Post post = postService.findById(id).get();
        if (!postService.canModify(rq.getMember(), post)) throw new RuntimeException("수정권한이 없습니다.");

        rq.setAttribute("post", post);

        return "domain/post/post/modify";
    }

    @Getter
    @Setter
    public static class ModifyForm{
        @NotBlank
        private String title;
        @NotBlank
        private String body;
        private Boolean isPublished;
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/post/{id}/modify")
    public String modify(@PathVariable long id, @Valid ModifyForm modifyForm){
        Post post = postService.findById(id).get();
        if (!postService.canModify(rq.getMember(), post)) throw new RuntimeException("수정권한이 없습니다.");

        RsData<Post> modifyRs = postService.modify(post, modifyForm.title, modifyForm.body, modifyForm.getIsPublished());

        return rq.redirect("/post/" + id, modifyRs.getMsg());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/post/{id}/delete")
    public String delete(@PathVariable long id) {
        Post post = postService.findById(id).get();

        if (!postService.canDelete(rq.getMember(), post)) throw new RuntimeException("삭제권한이 없습니다.");

        RsData<Post> deleteRs = postService.delete(post);

        return rq.redirect("/post/list", deleteRs.getMsg());
    }

}

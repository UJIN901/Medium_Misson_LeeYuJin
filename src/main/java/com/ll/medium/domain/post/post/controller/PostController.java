package com.ll.medium.domain.post.post.controller;

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
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final MemberService memberService;
    private final Rq rq;

    // 글 상세 페이지 조회
    @GetMapping("/{id}")
    public String showPost(@PathVariable long id){
        rq.setAttribute("post", postService.findById(id).get());
        return "domain/post/post/detail";
    }

    // 전체 공개된 글 목록 조회
    @GetMapping("/list")
    public String showList(){
        rq.setAttribute("posts", postService.findByIsPublishedOrderByIdDesc(true));
        return "domain/post/post/list";
    }

    // 현재 사용자가 작성한 글 목록 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myList")
    public String showMyList(){
        rq.setAttribute("posts", postService.findByAuthorIdOrderByIdDesc(rq.getMember().getId()));
        return "domain/post/post/myList";
    }

    // 글 작성 페이지 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite(){
        return "domain/post/post/write";
    }

    // 글 작성 처리
    @Getter
    @Setter
    public static class WriteForm{
        @NotBlank
        private String title;
        @NotBlank
        private String body;
        private Boolean isPublished = false;
        private Boolean isPaid;
    }

    //글 수정 페이지 조회
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@Valid WriteForm writeForm){
        RsData<Post> writeRs = postService.write(rq.getMember(), writeForm.getTitle(), writeForm.getBody(), writeForm.getIsPublished(), writeForm.getIsPaid());

        return rq.redirect("/post/myList", writeRs.getMsg());
    }

    // 굴 수정 처리
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@PathVariable long id){
        Post post = postService.findById(id).get();
        // 글 수정 권한이 없는 경우 예외 처리
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
        private Boolean isPaid;
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/modify")
    public String modify(@PathVariable long id, @Valid ModifyForm modifyForm){
        Post post = postService.findById(id).get();
        // 글 수정 권한이 없는 경우 예외 처리
        if (!postService.canModify(rq.getMember(), post)) throw new RuntimeException("수정권한이 없습니다.");

        RsData<Post> modifyRs = postService.modify(post, modifyForm.title, modifyForm.body, modifyForm.getIsPublished(), modifyForm.getIsPaid());

        return rq.redirect("/post/" + id, modifyRs.getMsg());
    }

    // 글 삭제 처리
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable long id) {
        Post post = postService.findById(id).get();

        if (!postService.canDelete(rq.getMember(), post)) throw new RuntimeException("삭제권한이 없습니다.");

        RsData<Post> deleteRs = postService.delete(post);

        return rq.redirect("/post/list", deleteRs.getMsg());
    }

}

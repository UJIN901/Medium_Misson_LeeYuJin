package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping("/{id}")
    public String showPost(@PathVariable long id){
        rq.setAttribute("post", postService.findById(id).get());
        return "domain/post/post/detail";
    }

    @GetMapping("/list")
    public String showList(){
        rq.setAttribute("posts", postService.findByIsPublishedOrderByIdDesc(true));
        return "domain/post/post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myList")
    public String showMyList(){
        rq.setAttribute("posts", postService.findByAuthorIdOrderByIdDesc(rq.getMember().getId()));
        return "domain/post/post/myList";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
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
        private Boolean isPublished;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public void write(@Valid WriteForm writeForm){
        return;
    }


}

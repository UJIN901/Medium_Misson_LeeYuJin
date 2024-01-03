package com.ll.medium.domain.member.member.controller;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.rq.Rq;
import com.ll.medium.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final Rq rq;

    // 회원 가입 페이지로 이동
    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String join() {
        return "domain/member/member/join";
    }

    // 회원 가입 처리
    @Getter
    @Setter
    public static class JoinForm{
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @NotBlank
        private String passwordConfirm;
        private Boolean isPaid;
    }

    // 회원 가입 요청 처리
    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String signup(@Valid JoinForm joinForm) {
        // 회원 가입 서비스 호출
        RsData<Member> joinRs = memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getIsPaid());

        // 회원 가입 결과에 따라 리다이렉션 수행
        return rq.redirectOrBack(joinRs, "/member/login");
    }

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public String showLogin(){
        return "domain/member/member/login";
    }
}

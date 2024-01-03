package com.ll.medium.global.init;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.util.stream.IntStream;

@Configuration
@Profile("!prod")
@Slf4j
@RequiredArgsConstructor
public class NotProd {
    private final MemberService memberService;
    private final PostService postService;

    @Bean
    @Order(3)
    public ApplicationRunner initNotProd(){
        return args -> {
            if (memberService.findByUsername("user1").isPresent()) return;

            // 유료멤버십x 회원 96명 생성
            Member[] members = new Member[96];
            IntStream.range(0, 95).forEach(i -> {
                members[i] = memberService.join("user" + (i+1), "1234", false).getData();
            });

            // 유료멤버십 회원 4명 생성
            Member memberUser97 = memberService.join("user97", "1234", true).getData();
            Member memberUser98 = memberService.join("user98", "1234", true).getData();
            Member memberUser99 = memberService.join("user99", "1234", true).getData();
            Member memberUser100 = memberService.join("user100", "1234", true).getData();

            IntStream.rangeClosed(1, 25).forEach(i -> {
                postService.write(members[0], "제목 " + i, "내용 " + i, false, false);
            });

            IntStream.rangeClosed(26, 50).forEach(i -> {
                postService.write(members[1], "제목 " + i, "내용 " + i, true, false);
            });

            IntStream.rangeClosed(51, 70).forEach(i -> {
                postService.write(memberUser97, "제목 " + i, "내용 " + i, true, true);
            });

            IntStream.rangeClosed(71, 80).forEach(i -> {
                postService.write(memberUser98, "제목 " + i, "내용 " + i, false, true);
            });

            IntStream.rangeClosed(81, 90).forEach(i -> {
                postService.write(memberUser99, "제목 " + i, "내용 " + i, false, false);
            });

            IntStream.rangeClosed(91, 100).forEach(i -> {
                postService.write(memberUser100, "제목 " + i, "내용 " + i, false, false);
            });
        };
    }
}

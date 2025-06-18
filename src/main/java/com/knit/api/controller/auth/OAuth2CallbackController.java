package com.knit.api.controller.auth;


import com.knit.api.dto.auth.KakaoUserInfo;
import com.knit.api.service.auth.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/oauth2/callback")
@RequiredArgsConstructor
public class OAuth2CallbackController {
    private final OAuth2Service oAuth2Service;

    @GetMapping("/kakao")
    public String kakaoCallback(@RequestParam("code") String code) {
        log.info("kakao code {}", code);
        KakaoUserInfo kakaoUser = oAuth2Service.handleKakaoLogin(code);
        // JWT 발급 등 추가 처리 가능
        return "로그인 성공: " + kakaoUser.nickname();
    }
}
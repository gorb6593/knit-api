package com.knit.api.controller.auth;

import com.knit.api.domain.user.User;
import com.knit.api.dto.auth.KakaoUserInfo;
import com.knit.api.dto.auth.LoginResponse;
import com.knit.api.service.auth.OAuth2Service;
import com.knit.api.service.user.UserService;
import com.knit.api.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.knit.api.domain.user.User.AuthProvider.KAKAO;

@Slf4j
@RestController
@RequestMapping("/oauth2/callback")
@RequiredArgsConstructor
public class OAuth2CallbackController {
    private final OAuth2Service oAuth2Service;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @GetMapping("/kakao")
    public LoginResponse kakaoCallback(@RequestParam("code") String code) {

        // 카카오 유저 정보 조회
        log.info("kakao code {}", code);
        KakaoUserInfo kakaoUser = oAuth2Service.handleKakaoLogin(code);

        // 회원가입 or 로그인 처리
        log.info("kakao user {}", kakaoUser);
        User user = userService.saveOrLoginSocialUser(
                kakaoUser.nickname(),
                kakaoUser.email(),
                kakaoUser.profileImage(),
                KAKAO,
                kakaoUser.providerId(),
                User.Role.USER
        );

        // JWT Token 발급
        String token = jwtProvider.createToken(user);
        log.info("kakao token {}", token);

        return new LoginResponse(token);
    }
}

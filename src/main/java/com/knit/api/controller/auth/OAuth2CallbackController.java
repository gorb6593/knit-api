package com.knit.api.controller.auth;

import com.knit.api.domain.user.User;
import com.knit.api.dto.auth.KakaoUserInfo;
import com.knit.api.dto.auth.LoginResponse;
import com.knit.api.dto.user.UserDto;
import com.knit.api.service.auth.OAuth2Service;
import com.knit.api.service.user.UserService;
import com.knit.api.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.knit.api.domain.user.User.AuthProvider.KAKAO;

@Slf4j
@RestController
@RequestMapping("/oauth2/callback")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
public class OAuth2CallbackController {
    private final OAuth2Service oAuth2Service;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @GetMapping("/kakao")
    public LoginResponse kakaoCallback(@RequestParam("code") String code) {

        // 카카오 유저 정보 조회
        log.info("kakao code :: {}", code);
        KakaoUserInfo kakaoUser = oAuth2Service.handleKakaoLogin(code);

        // 회원가입 or 로그인 처리
        log.info("kakao user :: {}", kakaoUser);
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
        log.info("kakao token :: {}", token);

        // UserDto 변환
        UserDto userDto = UserDto.from(user);
        log.info("UserDto :: {}", userDto);

        return new LoginResponse(token, userDto);
    }

    // 신규: 모바일/웹용 카카오 로그인 (사용자 정보 직접 전송)
    @PostMapping("/kakao/mobile")
    public LoginResponse kakaoMobileLogin(@RequestBody Map<String, Object> request) {
        
        // 프론트엔드에서 받은 카카오 사용자 정보
        String nickname = (String) request.get("nickname");
        String email = (String) request.get("email");
        String profileImage = (String) request.get("profileImage");
        String providerId = (String) request.get("providerId");

        log.info("카카오 모바일 로그인 - providerId: {}, nickname: {}", providerId, nickname);

        // 기존 로직과 동일하게 회원가입 or 로그인 처리
        User user = userService.saveOrLoginSocialUser(
                nickname,
                email,
                profileImage,
                KAKAO,
                providerId,
                User.Role.USER
        );

        // JWT Token 발급
        String token = jwtProvider.createToken(user);
        log.info("카카오 모바일 토큰 :: {}", token);

        // UserDto 변환
        UserDto userDto = UserDto.from(user);
        log.info("UserDto :: {}", userDto);

        return new LoginResponse(token, userDto);
    }

    @GetMapping("/kakao/mobile/callback")
    public String kakaoMobileCallback(@RequestParam String code) {
        log.info("Kakao mobile callback received with code: {}", code);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>카카오 로그인 처리중</title>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        text-align: center;
                        margin-top: 50px;
                        background-color: #f8f9fa;
                    }
                    .container {
                        max-width: 400px;
                        margin: 0 auto;
                        padding: 20px;
                        background: white;
                        border-radius: 8px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    h2 {
                        color: #333;
                        margin-bottom: 10px;
                    }
                    p {
                        color: #666;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>🔄 로그인 처리 중입니다...</h2>
                    <p>잠시만 기다려주세요.</p>
                </div>
                <script>
                    // React Native WebView로 인증 코드 전달
                    if (window.ReactNativeWebView) {
                        window.ReactNativeWebView.postMessage(JSON.stringify({
                            type: 'KAKAO_AUTH_CODE',
                            code: '%s'
                        }));
                    }

                    // 웹에서도 동작하도록 fallback
                    setTimeout(() => {
                        try {
                            window.close();
                        } catch (e) {
                            document.querySelector('.container').innerHTML = `
                                <h2>✅ 인증이 완료되었습니다!</h2>
                                <p>앱으로 돌아가세요.</p>
                            `;
                        }
                    }, 1000);
                </script>
            </body>
            </html>
            """.formatted(code);
    }
}

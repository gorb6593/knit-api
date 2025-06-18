package com.knit.api.service.auth;

import com.knit.api.dto.auth.KakaoUserInfo;
import org.springframework.stereotype.Service;

@Service
public class OAuth2Service {
    public KakaoUserInfo handleKakaoLogin(String code) {
        // 1. code로 access token 요청
        // 2. access token으로 사용자 정보 요청
        // 3. KakaoUserInfo 객체로 반환
        // (구현 예시는 아래에서 바로 드릴 수 있습니다)
        return new KakaoUserInfo("임시닉네임", "이메일", "카카오id", "프로필이미지");
    }
}

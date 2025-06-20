package com.knit.api.service.auth;

import com.knit.api.dto.auth.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    public KakaoUserInfo handleKakaoLogin(String code) {
        String accessToken = getKakaoAccessToken(code);
        KakaoUserInfo kakaoUser = getKakaoUserInfo(accessToken);
        return kakaoUser;
    }

    public String getKakaoAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        Map body = response.getBody();
        return (String) body.get("access_token");
    }

    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);
        Map<String, Object> body = response.getBody();

        Long id = ((Number) body.get("id")).longValue();
        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
        String nickname = profile != null ? (String) profile.get("nickname") : null;
        String profileImage = profile != null ? (String) profile.get("profile_image_url") : null;

        return new KakaoUserInfo(nickname, email, id.toString(), profileImage);
    }
}

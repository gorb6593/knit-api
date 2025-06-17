package com.knit.api.domain.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = PROTECTED) // JPA/Hibernate 프록시 생성을 위한 기본생성자(외부 호출 X)
@AllArgsConstructor(access = PRIVATE)  // 객체 생성은 빌더/팩토리 메소드로만 허용
@Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String nickname; // 사용자 표시명

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 200)
    private String profileImage; // 프로필 이미지 URL

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private AuthProvider provider; // GOOGLE, APPLE, KAKAO 등

    @Column(length = 100, unique = true)
    private String providerId; // 소셜 플랫폼의 유저 고유ID

    @Column(length = 200)
    private String password; // email 회원가입 등(소셜 로그인은 null 가능)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private LocalDateTime createdAt;

    // == 정적 생성자 패턴 ==
    public static User social(String nickname, String email, String profileImage,
                              AuthProvider provider, String providerId, Role role) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .profileImage(profileImage)
                .provider(provider)
                .providerId(providerId)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User local(String nickname, String email, String password, Role role) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .provider(AuthProvider.LOCAL)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public enum Role {
        USER, ADMIN
    }

    public enum AuthProvider {
        LOCAL, GOOGLE, KAKAO, APPLE
    }
}

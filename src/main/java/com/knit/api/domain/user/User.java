package com.knit.api.domain.user;

import com.knit.api.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = PROTECTED) // JPA/Hibernate 프록시 생성을 위한 기본생성자(외부 호출 X)
@AllArgsConstructor(access = PRIVATE)  // 객체 생성은 빌더/팩토리 메소드로만 허용
@Builder
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String nickname;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String profileImage;

    @Column(length = 20)
    @Enumerated(STRING)
    private AuthProvider provider;

    @Column(length = 100, unique = true)
    private String providerId; // 소셜 플랫폼 유저 고유ID

    @Column(length = 200)
    private String password;

    @Enumerated(STRING)
    @Column
    private Role role;


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
                .build();
    }

    public static User local(String nickname, String email, String password, Role role) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .provider(AuthProvider.LOCAL)
                .role(role)
                .build();
    }

    public enum Role {
        USER, ADMIN
    }

    public enum AuthProvider {
        LOCAL, GOOGLE, KAKAO, APPLE
    }

    // == 비즈니스 로직 ==
    public void updateProfile(String nickname, String profileImage) {
        if (nickname != null && !nickname.trim().isEmpty()) {
            this.nickname = nickname;
        }
        if (profileImage != null && !profileImage.trim().isEmpty()) {
            this.profileImage = profileImage;
        }
    }
}

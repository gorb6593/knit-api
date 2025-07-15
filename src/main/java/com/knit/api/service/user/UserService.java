package com.knit.api.service.user;

import com.knit.api.domain.user.User;
import com.knit.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User saveOrLoginSocialUser(
            String nickname, String email, String profileImage,
            User.AuthProvider provider, String providerId, User.Role role
    ) {
        // 가입된 유저 찾기
        Optional<User> optional = userRepository.findByProviderAndProviderId(provider, providerId);
        if (optional.isPresent()) {
            return optional.get();
        }

        // 신규 회원 가입
        User user = User.social(nickname, email, profileImage, provider, providerId, role);
        return userRepository.save(user);
    }

}
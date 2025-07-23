package com.knit.api.service.user;

import com.knit.api.config.aws.S3Service;
import com.knit.api.domain.user.User;
import com.knit.api.dto.user.UserProfileUpdateRequest;
import com.knit.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;


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

    @Transactional
    public User updateProfile(Long userId, UserProfileUpdateRequest request, MultipartFile profileImage) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = s3Service.uploadProfileImage(profileImage);
        }

        user.updateProfile(request.nickname(), profileImageUrl);
        return user;
    }

}
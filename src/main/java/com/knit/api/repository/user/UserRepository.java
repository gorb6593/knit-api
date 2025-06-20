package com.knit.api.repository.user;

import com.knit.api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderId(User.AuthProvider provider, String providerId);
    Optional<User> findByEmail(String email);
}

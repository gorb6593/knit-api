package com.knit.api.repository.user;

import com.knit.api.domain.user.UserAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccessLogRepository extends JpaRepository<UserAccessLog, Long> {
}
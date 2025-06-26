package com.knit.api.domain.user;

import com.knit.api.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Setter;

@Setter
@Entity
public class UserAccessLog extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Long userId;
    private String method;
    private String endpoint;
    @Column(length = 2000)
    private String queryParams;
    @Column(length = 5000)
    private String requestBody;
    private String ip;
    @Column(length = 1000)
    private String userAgent;
    @Column(length = 1000)
    private String referer;

}

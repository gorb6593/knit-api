package com.knit.api.controller.tempTodo;

import com.knit.api.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter @Getter
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    private String auth;
}

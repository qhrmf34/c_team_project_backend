package com.hotel_project.review_jpa.choice.dto;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "choice_tbl")
public class ChoiceEntity implements IChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false, length = 30, unique = true)
    private String name;
}

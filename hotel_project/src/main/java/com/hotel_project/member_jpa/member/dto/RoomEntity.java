package com.hotel_project.member_jpa.member.dto;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room")
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String roomNumber;

    @Column(nullable = false, length = 50)
    private String roomType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
//룸 관련 추가 필요
}
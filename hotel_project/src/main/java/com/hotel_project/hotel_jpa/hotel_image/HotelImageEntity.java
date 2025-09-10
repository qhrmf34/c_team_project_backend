package com.hotel_project.hotel_jpa.hotel_image;

import com.hotel_project.hotel_jpa.hotel.dto.HotelEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "HotelImageEntity")
@Table (name = "hotel_image_tbl")
public class HotelImageEntity implements IHotelImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    @Column(nullable = false, length = 255)
    private String hotelImageName;

    @Column(nullable = false, length = 500)
    private String hotelImagePath;

    private Long hotelImageSize;

    @Column(nullable = false)
    private Integer hotelImageIndex;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
    }

    @Override
    public Long getHotelId() {
        if (this.hotel == null) {
            return 0L;
        }
        return this.hotel.getId();
    }

    @Override
    public void setHotelId(Long hotelId) {
        if (this.hotel == null) {
            return;
        }
        this.hotel.setId(hotelId);
    }
}

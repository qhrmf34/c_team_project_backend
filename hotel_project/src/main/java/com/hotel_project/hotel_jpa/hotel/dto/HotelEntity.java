package com.hotel_project.hotel_jpa.hotel.dto;

import com.hotel_project.hotel_jpa.city.dto.CityEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel_tbl")
public class HotelEntity implements IHotel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityEntity city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HotelType hotelType;

    @Column(nullable = false, length = 255)
    private String hotelName;

    @Column(precision = 9, scale = 6,nullable = false)
    private BigDecimal hotelLatitude;

    @Column(precision = 11, scale = 8,nullable = false)
    private BigDecimal hotelLongitude;

    private String hotelContent;

    private Integer hotelStar;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer freebiesNumber = 0;

    @Column(length = 30)
    private String hotelNumber;

    @Column(nullable = false)
    private LocalTime checkinTime;

    @Column(nullable = false)
    private LocalTime checkoutTime;

    @Column(columnDefinition = "DECIMAL(2,1) DEFAULT 0.0")
    private BigDecimal hotelRating = new BigDecimal("0.0");

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public Long getCityId() {
        if (this.city == null) {
            return 0L;
        }
        return this.city.getId();
    }

    @Override
    public void setCityId(Long cityId) {
        if (this.city == null) {
            return;
        }
        this.city.setId(cityId);
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

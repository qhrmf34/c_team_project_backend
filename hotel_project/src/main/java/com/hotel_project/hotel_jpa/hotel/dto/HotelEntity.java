package com.hotel_project.hotel_jpa.hotel.dto;

import com.hotel_project.common_jpa.dto.IId;
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
@Builder
@Entity
@Table(name = "hotel_tbl")
public class HotelEntity implements IHotel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity cityEntity;

    @Transient
    private Long cityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HotelType hotelType;

    @Column(nullable = false, length = 255)
    private String hotelName;

    @Column(precision = 9, scale = 6,nullable = false)
    private BigDecimal hotelLatitude;

    @Column(precision = 11, scale = 8,nullable = false)
    private BigDecimal hotelLongitude;

    @Lob
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

    @Column(columnDefinition = "DECIMAL(2,1)")
    private BigDecimal hotelRating;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public IId getCity(){
        return this.cityEntity;
    }

    @Override
    public void setCity(IId iId) {
        if (iId == null){
            return;
        }
        if (this.cityEntity == null){
            this.cityEntity = new CityEntity();
        }
        this.cityEntity.copyMembersId(iId);
    }

    @Override
    public Long getCityId() {
        return this.cityEntity != null ? this.cityEntity.getId() : null;
    }

    @Override
    public void setCityId(Long cityId) {
        if (cityId == null) {
            throw new IllegalArgumentException("cityId cannot be null");
        }
        if (this.cityEntity == null){
            this.cityEntity = new CityEntity();
        }
        this.cityEntity.setId(cityId);
        this.cityId = cityId;
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
        if (hotelRating == null) {
            hotelRating = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

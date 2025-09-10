package com.hotel_project.hotel_jpa.city_image.dto;

import com.hotel_project.hotel_jpa.city.dto.CityEntity;
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
@Entity(name = "CityImageEntity")
@Table (name = "city_image_tbl")
public class CityImageEntity implements ICityImage{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityEntity city;

    @Column(nullable = false, length = 255)
    private String cityImageName;

    @Column(nullable = false, length = 500)
    private String cityImagePath;

    @Column(nullable = false)
    private Integer cityImageIndex;

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
}

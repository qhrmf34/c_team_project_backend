package com.hotel_project.hotel_jpa.city_image.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.city.dto.CityEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table (name = "city_image_tbl")
public class CityImageEntity implements ICityImage{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity cityEntity;

    @Transient
    private Long cityId;

    @Column(nullable = false, length = 255)
    private String cityImageName;

    @Column(nullable = false, length = 500)
    private String cityImagePath;

    private Long cityImageSize;

    @Column(nullable = false)
    private Integer cityImageIndex;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Override
    public IId getCity() {
        return this.cityEntity;
    }

    @Override
    public void setCity(IId iId) {
        if(iId == null){
            return;
        }
        if(this.cityEntity == null){
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
            throw new IllegalArgumentException("City Id cannot be null");
        }
        if (this.cityEntity == null) {
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
    }
}

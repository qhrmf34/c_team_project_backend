package com.hotel_project.hotel_jpa.hotel_amenities.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesEntity;
import com.hotel_project.hotel_jpa.hotel.dto.HotelEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table (name = "hotel_amenities_tbl")
public class HotelAmenitiesEntity implements IHotelAmenities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotelEntity;

    @Transient
    private Long hotelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amenities_id", nullable = false)
    private AmenitiesEntity amenitiesEntity;

    @Transient
    private Long amenitiesId;


    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public IId getHotel(){
        return this.hotelEntity;
    }

    @Override
    public void setHotel(IId iId) {
        if(iId == null){
            return;
        }
        if (this.hotelEntity == null){
            this.hotelEntity = new HotelEntity();
        }
        this.hotelEntity.copyMembersId(iId);
    }

    @Override
    public Long getHotelId() {
        return this.hotelEntity != null ? this.hotelEntity.getId() : null;
    }

    @Override
    public void setHotelId(Long hotelId) {
        if(hotelId == null){
            throw new IllegalArgumentException("hotelId cannot be null");
        }
        if (this.hotelEntity == null){
            this.hotelEntity = new HotelEntity();
        }
        this.hotelEntity.setId(hotelId);
        this.hotelId = hotelId;
    }

    @Override
    public IId getAmenities(){
        return this.amenitiesEntity;
    }

    @Override
    public void setAmenities(IId iId) {
        if(iId == null){
            return;
        }
        if (this.amenitiesEntity == null){
            this.amenitiesEntity = new AmenitiesEntity();
        }
        this.amenitiesEntity.copyMembersId(iId);
    }

    @Override
    public Long getAmenitiesId() {
        return this.amenitiesEntity != null ? this.amenitiesEntity.getId() : null;
    }

    @Override
    public void setAmenitiesId(Long amenitiesId) {
        if (amenitiesId == null){
            throw new IllegalArgumentException("amenitiesId cannot be null");
        }
        if (this.amenitiesEntity == null){
            this.amenitiesEntity = new AmenitiesEntity();
        }
        this.amenitiesEntity.setId(amenitiesId);
        this.amenitiesId = amenitiesId;
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

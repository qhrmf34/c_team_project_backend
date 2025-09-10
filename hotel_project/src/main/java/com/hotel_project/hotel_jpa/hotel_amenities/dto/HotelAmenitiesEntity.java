package com.hotel_project.hotel_jpa.hotel_amenities.dto;

import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesEntity;
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
@Entity
@Table (name = "hotel_amenities_tbl")
public class HotelAmenitiesEntity implements IHotelAmenities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    @ManyToOne
    @JoinColumn(name = "amenities_id", nullable = false)
    private AmenitiesEntity amenities;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isAvailable = false;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public Long getHotelId() {
        if (this.hotel == null){
            return 0L;
        }
        return this.hotel.getId();
    }

    @Override
    public void setHotelId(Long hotelId) {
        if(this.hotel == null){
            return;
        }
        this.hotel.setId(hotelId);
    }

    @Override
    public Long getAmenitiesId() {
        if (this.amenities == null){
            return 0L;
        }
        return amenities.getId();
    }

    @Override
    public void setAmenitiesId(Long amenitiesId) {
        if(this.amenities == null){
            return;
        }
        this.amenities.setId(amenitiesId);
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

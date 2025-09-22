package com.hotel_project.hotel_jpa.hotel_image;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.hotel.dto.HotelEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "HotelImageEntity")
@Table (name = "hotel_image_tbl")
public class HotelImageEntity implements IHotelImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotelEntity;

    @Transient
    private Long hotelId;

    @Column(nullable = false, length = 255)
    private String hotelImageName;

    @Column(nullable = false, length = 500)
    private String hotelImagePath;

    private Long hotelImageSize;

    @Column(nullable = false)
    private Integer hotelImageIndex;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

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

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
    }
}

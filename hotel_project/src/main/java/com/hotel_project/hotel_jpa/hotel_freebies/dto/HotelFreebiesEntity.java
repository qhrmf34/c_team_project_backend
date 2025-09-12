package com.hotel_project.hotel_jpa.hotel_freebies.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
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
@Table(name = "hotel_freebies_tbl")
public class HotelFreebiesEntity implements IHotelFreebies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotelEntity;

    @Transient
    private Long hotelId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "freebies_id", nullable = false)
    private FreebiesEntity freebiesEntity;

    @Transient
    private Long freebiesId;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isAvailable = false;

    @Column(nullable = false, updatable = false
            ,columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true
            ,columnDefinition = "TIMESTAMP")
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
        if(this.hotelEntity == null){
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
        if(hotelId == null) {
            throw new IllegalArgumentException("hotelId cannot be null");
        }
        if(this.hotelEntity == null){
            this.hotelEntity = new HotelEntity();
        }
        this.hotelEntity.setId(hotelId);
        this.hotelId = hotelId;
    }

    @Override
    public IId getFreebies(){
        return this.freebiesEntity;
    }

    @Override
    public void setFreebies(IId iId) {
        if(iId == null){
            return;
        }
        if(this.freebiesEntity == null){
            this.freebiesEntity = new FreebiesEntity();
        }
        this.freebiesEntity.copyMembersId(iId);
    }

    @Override
    public Long getFreebiesId() {
        return this.freebiesEntity != null ? this.freebiesEntity.getId() : null;
    }

    @Override
    public void setFreebiesId(Long freebiesId) {
        if(freebiesId == null) {
            throw new IllegalArgumentException("freebiesId cannot be null");
        }
        if(this.freebiesEntity == null){
            this.freebiesEntity = new FreebiesEntity();
        }
        this.freebiesEntity.setId(freebiesId);
        this.freebiesId = freebiesId;
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

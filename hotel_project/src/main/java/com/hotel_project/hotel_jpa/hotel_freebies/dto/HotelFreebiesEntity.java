package com.hotel_project.hotel_jpa.hotel_freebies.dto;

import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
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
@Table(name = "hotel_freebies_tbl")
public class HotelFreebiesEntity implements IHotelFreebies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    @ManyToOne
    @JoinColumn(name = "freebies_id")
    private FreebiesEntity freebies;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isAvailable = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Override
    public Long getHotelId() {
        if (this.hotel == null) {
            return 0L;
        }
        return this.hotel.getId();
    }

    @Override
    public void setHotelId(Long hotelId) {
        if(this.hotel == null) {
            return;
        }
        this.hotel.setId(hotelId);
    }

    @Override
    public Long getFreebiesId() {
        if (this.freebies == null) {
            return 0L;
        }
        return this.freebies.getId();
    }

    @Override
    public void setFreebiesId(Long freebiesId) {
        if(this.freebies == null) {
            return;
        }
        this.freebies.setId(freebiesId);
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

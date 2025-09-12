package com.hotel_project.hotel_jpa.city.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.country.dto.CountryEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "CityEntity")
@Table(name = "city_tbl")
public class CityEntity implements ICity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity countryEntity;

    @Transient
    private Long countryId;


    @Column(nullable = false, length = 100, unique = true)
    private String cityName;

    @Lob
    @Column(nullable = true)
    private String cityContent;

    @Override
    public IId getCountry(){
        return this.countryEntity;
    }

    @Override
    public void setCountry(IId iId) {
        if (iId == null){
            return;
        }
        if (this.countryEntity == null){
            this.countryEntity = new CountryEntity();
        }
        this.countryEntity.copyMembersId(iId);
    }

    @Override
    public Long getCountryId() {
        return this.countryEntity != null ? this.countryEntity.getId() : null;
    }

    @Override
    public void setCountryId(Long countryId) {
        if (countryId == null){
            throw new IllegalArgumentException("countryId cannot be null");
        }
        if (this.countryEntity == null){
            this.countryEntity = new CountryEntity();
        }
        this.countryEntity.setId(countryId);
        this.countryId = countryId;
    }
}

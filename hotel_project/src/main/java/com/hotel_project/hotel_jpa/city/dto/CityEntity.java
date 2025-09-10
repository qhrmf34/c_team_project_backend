package com.hotel_project.hotel_jpa.city.dto;

import com.hotel_project.hotel_jpa.country.dto.CountryEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "CityEntity")
@Table(name = "city_tbl")
public class CityEntity implements ICity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private CountryEntity country;

    @Column(nullable = false, length = 100, unique = true)
    private String cityName;

    @Lob
    private String cityContent;

    @Override
    public Long getCountryId() {
        if (this.country == null) {
            return 0L;
        }
        return this.country.getId();
    }

        @Override
        public void setCountryId(Long countryId) {
            if (this.country == null) {
                return;
            }
            this.country.setId(countryId);
        }
}

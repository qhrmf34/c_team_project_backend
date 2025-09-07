package com.hotel_project.hotel_jpa.country.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "country_tbl")
public class CountryEntity implements ICountry{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100, unique = true)
    private String countryName;


    @Column(nullable = false,length = 100)
    private String idd;
}

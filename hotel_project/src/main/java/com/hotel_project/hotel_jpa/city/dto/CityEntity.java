package com.hotel_project.hotel_jpa.city.dto;

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
@Table(name = "city_tbl")
public class CityEntity implements ICity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100, unique = true)
    private String cityName;

    @Lob
    @Column(nullable = true)
    private String cityContent;

    @Column(nullable = false,length = 100)
    private String idd;
}

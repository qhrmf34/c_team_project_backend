package com.hotel_project.common_jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicSearchDto {
    private int page = 0;
    private int size = 10;
    private String search;
}

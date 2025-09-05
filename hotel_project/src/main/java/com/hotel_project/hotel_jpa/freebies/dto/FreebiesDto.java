package com.hotel_project.hotel_jpa.freebies.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreebiesDto implements IFreebies{

    private Long id;

    @NotBlank(message = "무료시설 이름은 필수 입력입니다.")
    @Size(min = 1, max = 100, message = "무료시설 이름은 1자 이상 100자 이하로 입력해야 합니다.")
    private String freebiesName;
}

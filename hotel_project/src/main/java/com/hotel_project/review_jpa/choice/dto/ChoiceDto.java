package com.hotel_project.review_jpa.choice.dto;


import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceDto implements IChoice{

    private Long id;

    @Size(min = 1, max = 30, message = "이름은 1자 이상 30자 이하로 입력해야 합니다.")
    private String name;
}

package com.hotel_project.payment_jpa.payment_method.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardRegistrationRequestDto {

    @NotBlank(message = "카드번호는 필수입니다")
    @Pattern(regexp = "\\d{16}", message = "카드번호는 16자리 숫자여야 합니다")
    private String cardNumber;

    @NotBlank(message = "만료년도는 필수입니다")
    @Pattern(regexp = "\\d{2}", message = "만료년도는 2자리 숫자여야 합니다 (예: 25)")
    private String cardExpirationYear;

    @NotBlank(message = "만료월은 필수입니다")
    @Pattern(regexp = "0[1-9]|1[0-2]", message = "만료월은 01-12 사이여야 합니다")
    private String cardExpirationMonth;

    @NotBlank(message = "카드 비밀번호는 필수입니다")
    @Pattern(regexp = "\\d{2}", message = "카드 비밀번호는 2자리 숫자여야 합니다")
    private String cardPassword;

    @NotBlank(message = "카드 소유자명은 필수입니다")
    private String customerName;

    private String customerEmail;
}
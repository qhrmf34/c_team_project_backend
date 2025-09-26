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

    // 프론트엔드에서 받은 데이터를 토스 API 형식으로 변환
    public static CardRegistrationRequestDto fromFrontendData(
            String cardNumber,
            String expiry,
            String cardPassword,
            String name) {

        CardRegistrationRequestDto dto = new CardRegistrationRequestDto();

        // 카드번호에서 공백 제거
        dto.setCardNumber(cardNumber.replaceAll("\\s", ""));

        // 만료일 MM/YY를 분리
        if (expiry != null && expiry.contains("/")) {
            String[] parts = expiry.split("/");
            dto.setCardExpirationMonth(parts[0]);
            dto.setCardExpirationYear(parts[1]);
        }

        // 카드 비밀번호 설정
        dto.setCardPassword(cardPassword);
        dto.setCustomerName(name);

        return dto;
    }

    // 유효성 검사용 메서드
    public boolean isValidCardNumber() {
        return cardNumber != null && cardNumber.matches("\\d{16}");
    }

    public boolean isValidExpiry() {
        if (cardExpirationMonth == null || cardExpirationYear == null) {
            return false;
        }

        try {
            int month = Integer.parseInt(cardExpirationMonth);
            int year = Integer.parseInt("20" + cardExpirationYear);
            int currentYear = java.time.Year.now().getValue();
            int currentMonth = java.time.MonthDay.now().getMonthValue();

            return month >= 1 && month <= 12 &&
                    (year > currentYear || (year == currentYear && month >= currentMonth));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isValidCvc() {
        return cardPassword != null && cardPassword.matches("\\d{2}");
    }

    public boolean isValidCustomerName() {
        return customerName != null && customerName.trim().length() >= 2;
    }

    // 전체 유효성 검사
    public boolean isValid() {
        return isValidCardNumber() &&
                isValidExpiry() &&
                isValidCvc() &&
                isValidCustomerName();
    }

    // 유효성 검사 오류 메시지 반환
    public java.util.List<String> getValidationErrors() {
        java.util.List<String> errors = new java.util.ArrayList<>();

        if (!isValidCardNumber()) {
            errors.add("카드번호는 16자리 숫자여야 합니다");
        }
        if (!isValidExpiry()) {
            errors.add("올바른 만료일을 입력해주세요 (MM/YY 형식)");
        }
        if (!isValidCvc()) {
            errors.add("카드 비밀번호는 2자리 숫자여야 합니다");
        }
        if (!isValidCustomerName()) {
            errors.add("카드 소유자명을 2글자 이상 입력해주세요");
        }
//        VISA 카드
//
//        4330123456781234 (가장 일반적으로 사용)
//        4370123456781234
//
//        MasterCard
//
//        5311123456781234
//        5570123456781234
//
//        JCB
//
//        3530123456781234
//
//        다이너스
//
//        30123456781234 (14자리) 이렇게만 카드번호 사용가능

//        Visa: 시작 숫자 4. (모든 Visa 카드가 4로 시작)
//        Kount | An Equifax company
//
//        MasterCard: 51–55 또는 2221–2720.
//        sycurio.com
//
//        American Express(Amex): 34 또는 37 (15자리).
//        bincodes.com
//
//        JCB: 3528–3589.
//        bincodes.com
//
//        Discover: 6011, 622126–622925, 644–649, 65.
//        bincodes.com
//
//        Diners Club: 300–305, 36, 38–39 (일부는 14자리).
//        sycurio.com
        return errors;
    }
}
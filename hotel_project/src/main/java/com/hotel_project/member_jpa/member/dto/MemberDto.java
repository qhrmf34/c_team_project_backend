package hotel_project.src.main.java.com.hotel_project.member_jpa.member.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto implements IMember {

    private Long id;

    @NotBlank(message = "성을 입력해주세요.")
    @Size(max = 50, message = "성은 50자 이하로 입력해야 합니다.")
    private String firstName;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 50, message = "이름은 50자 이하로 입력해야 합니다.")
    private String lastName;

    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해야 합니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력입니다.")
    @Size(max = 30, message = "전화번호는 30자 이하로 입력해야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(max = 255, message = "비밀번호는 255자 이하로 입력해야 합니다.")
    private String password;

    @NotNull(message = "로그인 제공자는 필수 입력입니다.")
    private Provider provider = Provider.local;

    private String providerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
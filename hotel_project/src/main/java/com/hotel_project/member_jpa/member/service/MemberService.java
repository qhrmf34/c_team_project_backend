package com.hotel_project.member_jpa.member.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.member_jpa.member.dto.*;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    // ========== ✅ 공통 유효성 검사 메서드 ==========

    /**
     * ✅ 비밀번호 유효성 검사 (공통 메서드)
     */
    private void validatePassword(String password) throws CommonExceptionTemplate {
        if (password == null || password.isEmpty()) {
            throw new CommonExceptionTemplate(400, "비밀번호는 필수 입력입니다.");
        }

        if (password.length() < 8) {
            throw new CommonExceptionTemplate(400, "비밀번호는 최소 8자 이상이어야 합니다.");
        }

        if (password.length() > 255) {
            throw new CommonExceptionTemplate(400, "비밀번호는 255자 이하로 입력해야 합니다.");
        }

        // 영문 체크
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        // 숫자 체크
        boolean hasDigit = password.matches(".*\\d.*");
        // 특수문자 체크
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        if (!hasLetter || !hasDigit || !hasSpecial) {
            throw new CommonExceptionTemplate(400,
                    "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.");
        }
    }

    /**
     * ✅ 전화번호 유효성 검사 (공통 메서드)
     */
    private void validatePhoneNumber(String phoneNumber) throws CommonExceptionTemplate {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new CommonExceptionTemplate(400, "전화번호는 필수 입력입니다.");
        }

        // 하이픈 제거
        String cleanPhone = phoneNumber.replace("-", "");

        // 한국 휴대폰 번호 형식 체크 (010, 011, 016, 017, 018, 019로 시작하는 10~11자리)
        if (!cleanPhone.matches("^01[016789]\\d{7,8}$")) {
            throw new CommonExceptionTemplate(400,
                    "올바른 휴대폰 번호 형식이 아닙니다. (예: 01012345678)");
        }
    }
    /**
     * ✅ 이메일 유효성 검사 (공통 메서드)
     */
    private void validateEmail(String email) throws CommonExceptionTemplate {
        if (email == null || email.isEmpty()) {
            throw new CommonExceptionTemplate(400, "이메일은 필수 입력입니다.");
        }

        // 기본 이메일 형식 체크
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new CommonExceptionTemplate(400, "올바른 이메일 형식이 아닙니다.");
        }

        // .social 도메인 사용 금지 (내부 자동 생성용)
        if (email.toLowerCase().endsWith(".social")) {
            throw new CommonExceptionTemplate(400, "사용할 수 없는 이메일 도메인입니다.");
        }

        // 이메일 길이 체크
        if (email.length() > 100) {
            throw new CommonExceptionTemplate(400, "이메일은 100자 이하로 입력해야 합니다.");
        }
    }
    // ========== 회원가입 / 로그인 ==========

    /**
     * ✅ 일반 회원가입
     */
    public LoginResponse signup(SignupRequest signupRequest) throws CommonExceptionTemplate {
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            throw new CommonExceptionTemplate(400, "비밀번호가 일치하지 않습니다.");
        }

        validateEmail(signupRequest.getEmail());
        validatePassword(signupRequest.getPassword());
        validatePhoneNumber(signupRequest.getPhoneNumber());

        String cleanPhone = signupRequest.getPhoneNumber().replace("-", "");
        if (memberRepository.existsByPhoneNumber(cleanPhone)) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }

        // ✅ 이메일 중복 체크 (email만으로 검사)
        Optional<MemberEntity> existingMember = memberRepository.findByEmail(signupRequest.getEmail());

        if (existingMember.isPresent()) {
            MemberEntity member = existingMember.get();

            // provider가 'leave'인 경우
            if (member.getProvider() == Provider.leave) {
                // 1시간 이내 탈퇴한 경우 재가입 불가
                if (member.getDeletedAt() != null) {
                    LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
                    if (member.getDeletedAt().isAfter(oneHourAgo)) {
                        throw new CommonExceptionTemplate(403,
                                "탈퇴 후 1시간 동안은 재가입이 불가능합니다. 잠시 후 다시 시도해주세요.");
                    }
                }
                // 1시간 경과했으면 재가입 허용 (통과)
            } else {
                // provider가 'leave'가 아닌 경우 중복 에러
                throw new CommonExceptionTemplate(409, "이미 사용 중인 이메일입니다.");
            }
        }

        MemberEntity newMember = new MemberEntity();
        newMember.setFirstName(signupRequest.getFirstName());
        newMember.setLastName(signupRequest.getLastName());
        newMember.setEmail(signupRequest.getEmail());
        newMember.setPhoneNumber(cleanPhone);
        newMember.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newMember.setProvider(Provider.local);
        newMember.setRoadAddress(signupRequest.getRoadAddress());
        newMember.setDetailAddress(signupRequest.getDetailAddress());
        newMember.setProviderId(null);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        MemberEntity savedMember = memberRepository.save(newMember);

        String token = jwtUtil.generateToken(savedMember.getId());

        return LoginResponse.builder()
                .token(token)
                .memberId(savedMember.getId())
                .firstName(savedMember.getFirstName())
                .lastName(savedMember.getLastName())
                .email(savedMember.getEmail())
                .provider(savedMember.getProvider().toString())
                .build();
    }

    /**
     * 일반 로그인
     */
    public LoginResponse login(LoginRequest loginRequest) throws CommonExceptionTemplate {
        // 먼저 이메일로만 조회 (provider 무관)
        Optional<MemberEntity> memberEntity = memberRepository.findByEmail(loginRequest.getEmail());

        if (memberEntity.isEmpty()) {
            throw new CommonExceptionTemplate(401, "이메일 또는 비밀번호가 잘못되었습니다.");
        }

        MemberEntity member = memberEntity.get();

        //  2. 탈퇴 회원 체크
        if (member.getProvider() == Provider.leave) {
            throw new CommonExceptionTemplate(403, "탈퇴한 회원입니다. 재가입 후 이용해주세요.");
        }

        //  3. provider가 'local'인지 확인
        if (member.getProvider() != Provider.local) {
            String providerName = getProviderDisplayName(member.getProvider());
            throw new CommonExceptionTemplate(401,
                    String.format("해당 이메일은 %s(으)로 가입된 계정입니다.", providerName));
        }

        //  4. 비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new CommonExceptionTemplate(401, "이메일 또는 비밀번호가 잘못되었습니다.");
        }

        //  JWT 토큰 생성 (memberId만)
        String token = jwtUtil.generateToken(member.getId());

        return LoginResponse.builder()
                .token(token)
                .memberId(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .email(member.getEmail())
                .provider(member.getProvider().toString())
                .build();
    }

    /**
     * Provider 표시명 반환 (헬퍼 메서드 추가)
     */
    private String getProviderDisplayName(Provider provider) {
        switch (provider) {
            case google:
                return "Google";
            case kakao:
                return "Kakao";
            case naver:
                return "Naver";
            case local:
                return "일반 로그인";
            case leave:
                return "탈퇴";
            default:
                return "소셜 로그인";
        }
    }

    // ========== 비밀번호 재설정 ==========

    public boolean existsByEmail(String email) {
        MemberDto member = memberMapper.findByEmailAndProvider(email, Provider.local);
        return member != null;
    }

    public String resetPassword(String email, String newPassword) throws CommonExceptionTemplate {
        MemberDto memberDto = memberMapper.findByEmailAndProvider(email, Provider.local);

        if (memberDto == null) {
            throw new CommonExceptionTemplate(404, "해당 이메일로 가입된 계정이 없습니다.");
        }

        if (memberDto.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(400, "소셜 로그인 계정은 비밀번호를 재설정할 수 없습니다.");
        }

        // ✅ 비밀번호 유효성 검사
        validatePassword(newPassword);

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.copyMembers(memberDto);
        memberEntity.setPassword(passwordEncoder.encode(newPassword));
        memberEntity.setUpdatedAt(LocalDateTime.now());

        memberRepository.save(memberEntity);

        return "비밀번호가 성공적으로 재설정되었습니다.";
    }

    @Transactional
    public String resetPasswordDirect(String email, String newPassword) throws CommonExceptionTemplate {
        MemberDto memberDto = memberMapper.findByEmailAndProvider(email, Provider.local);

        if (memberDto == null) {
            throw new CommonExceptionTemplate(404, "해당 이메일로 가입된 계정이 없습니다.");
        }

        if (memberDto.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(400, "소셜 로그인 계정은 비밀번호를 재설정할 수 없습니다.");
        }

        // ✅ 비밀번호 유효성 검사
        validatePassword(newPassword);

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.copyMembers(memberDto);
        memberEntity.setPassword(passwordEncoder.encode(newPassword));
        memberEntity.setUpdatedAt(LocalDateTime.now());

        memberRepository.save(memberEntity);

        return "비밀번호가 성공적으로 재설정되었습니다.";
    }

    // ========== 기존 메서드들 ==========

    public MemberDto getMemberDtoByToken(String token) throws CommonExceptionTemplate {
        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        MemberDto member = memberMapper.findById(memberId);

        if (member == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        // ✅ 탈퇴 회원 체크
        if (member.getProvider() == Provider.leave) {
            throw new CommonExceptionTemplate(403, "탈퇴한 회원입니다.");
        }

        member.setPassword(null);
        return member;
    }

    public MemberDto updateMemberAndReturnDto(Long memberId, MemberDto memberDto) throws CommonExceptionTemplate {
        updateMember(memberId, memberDto);

        MemberDto responseDto = memberMapper.findById(memberId);
        responseDto.setPassword(null);

        return responseDto;
    }

    public String updateMember(Long memberId, MemberDto memberDto) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        MemberDto existingMemberDto = memberMapper.findById(memberId);
        if (existingMemberDto == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        if (existingMemberDto.getProvider() == Provider.leave) {
            throw new CommonExceptionTemplate(403, "탈퇴한 회원입니다.");
        }

        MemberEntity existingMember = new MemberEntity();
        existingMember.copyMembers(existingMemberDto);

        // ✅ 이름 변경 (로컬 계정만 가능)
        if (memberDto.getFirstName() != null && existingMember.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(403, "소셜 로그인 계정은 이름을 변경할 수 없습니다.");
        }

        // ✅ 이메일 변경 (카카오, 네이버만 가능)
        if (memberDto.getEmail() != null &&
                !memberDto.getEmail().equals(existingMember.getEmail())) {

            if (existingMember.getProvider() == Provider.google) {
                throw new CommonExceptionTemplate(403, "Google 계정은 이메일을 변경할 수 없습니다.");
            }

            if (existingMember.getProvider() == Provider.local) {
                throw new CommonExceptionTemplate(403, "일반 계정은 이메일을 변경할 수 없습니다.");
            }

            if (memberRepository.existsByEmailAndIdNot(memberDto.getEmail(), memberId)) {
                throw new CommonExceptionTemplate(409, "이미 사용 중인 이메일입니다.");
            }
        }

        // ✅ 전화번호 변경 (모든 계정 가능)
        if (memberDto.getPhoneNumber() != null &&
                !memberDto.getPhoneNumber().equals(existingMember.getPhoneNumber())) {
            validatePhoneNumber(memberDto.getPhoneNumber());

            String cleanPhone = memberDto.getPhoneNumber().replace("-", "");
            if (memberRepository.existsByPhoneNumberAndIdNot(cleanPhone, memberId)) {
                throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
            }
            memberDto.setPhoneNumber(cleanPhone);
        }

        // ✅ 비밀번호 변경 (로컬 계정만 가능)
        if (memberDto.getPassword() != null && existingMember.getProvider() == Provider.local) {
            validatePassword(memberDto.getPassword());
            memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        memberDto.setUpdatedAt(LocalDateTime.now());

        // ✅ CRITICAL: provider, providerId는 절대 변경하면 안됨!
        memberDto.setProvider(null);
        memberDto.setProviderId(null);

        existingMember.copyNotNullMembers(memberDto);

        memberRepository.save(existingMember);
        return "update ok";
    }


    /**
     * ✅ 회원 탈퇴 (즉시 삭제가 아닌 소프트 삭제)
     */
    @Transactional
    public String withdrawMember(Long memberId, String password) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        MemberDto memberDto = memberMapper.findById(memberId);
        if (memberDto == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        // ✅ 이미 탈퇴한 회원 체크
        if (memberDto.getProvider() == Provider.leave) {
            throw new CommonExceptionTemplate(400, "이미 탈퇴한 회원입니다.");
        }

        // ✅ 로컬 계정만 비밀번호 확인
        if (memberDto.getProvider() == Provider.local) {
            if (password == null || !passwordEncoder.matches(password, memberDto.getPassword())) {
                throw new CommonExceptionTemplate(401, "비밀번호가 일치하지 않습니다.");
            }
        }

        // ✅ Provider를 leave로 변경 & deletedAt 설정
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.copyMembers(memberDto);
        memberEntity.setProvider(Provider.leave);
        memberEntity.setDeletedAt(LocalDateTime.now());
        memberEntity.setUpdatedAt(LocalDateTime.now());

        memberRepository.save(memberEntity);

        return "회원 탈퇴가 완료되었습니다. 1시간 후 모든 정보가 삭제됩니다.";
    }
    /**
     * ✅ 스케줄러: 탈퇴 후 1시간 경과 회원 정보 삭제 (매 30분마다 실행)
     */
    @Scheduled(cron = "0 0/30 * * * ?")  // 매 30분마다 실행
    @Transactional
    public void cleanupWithdrawnMembers() {
        System.out.println("========================================");
        System.out.println("✅ 탈퇴 회원 정리 스케줄러 시작: " + LocalDateTime.now());
        System.out.println("========================================");

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        List<MemberEntity> withdrawnMembers = memberRepository.findByProviderAndDeletedAtBefore(
                Provider.leave, oneHourAgo
        );

        System.out.println("✅ 정리 대상 회원 수: " + withdrawnMembers.size());

        for (MemberEntity member : withdrawnMembers) {
            System.out.println("✅ 회원 정보 삭제 중 - ID: " + member.getId() + ", deletedAt: " + member.getDeletedAt());

            // ✅ 모든 정보를 null로 변경 (이름은 "탈퇴회원"으로)
            member.setFirstName("탈퇴회원");
            member.setLastName(null);
            member.setEmail(null);
            member.setPhoneNumber(null);
            member.setPassword(null);
            member.setProviderId(null);
            member.setRoadAddress(null);
            member.setDetailAddress(null);
            member.setUpdatedAt(LocalDateTime.now());

            memberRepository.save(member);

            System.out.println("✅ 탈퇴 회원 정보 삭제 완료: ID=" + member.getId());
        }

        if (!withdrawnMembers.isEmpty()) {
            System.out.println("========================================");
            System.out.println("✅ 총 " + withdrawnMembers.size() + "명의 탈퇴 회원 정보 삭제 완료");
            System.out.println("========================================");
        } else {
            System.out.println("========================================");
            System.out.println("✅ 정리할 탈퇴 회원이 없습니다.");
            System.out.println("========================================");
        }
    }

    public String deleteMember(Long memberId) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        if (!memberRepository.existsById(memberId)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        memberRepository.deleteById(memberId);
        return "delete ok";
    }

    /**
     * ✅ 소셜 로그인 후 JWT 토큰 생성 (memberId만)
     */
    public String generateTokenForSocialLogin(MemberEntity member) {
        return jwtUtil.generateToken(member.getId());
    }

    public boolean verifyCurrentPassword(Long memberId, String currentPassword) {
        try {
            MemberDto member = memberMapper.findById(memberId);

            if (member == null) {
                return false;
            }

            if (member.getProvider() != Provider.local) {
                return false;
            }

            return passwordEncoder.matches(currentPassword, member.getPassword());

        } catch (Exception e) {
            System.out.println("비밀번호 확인 실패: " + e.getMessage());
            return false;
        }
    }

    public String changePassword(Long memberId, String newPassword) throws CommonExceptionTemplate {
        try {
            MemberDto member = memberMapper.findById(memberId);

            if (member == null) {
                throw new CommonExceptionTemplate(404, "회원을 찾을 수 없습니다.");
            }

            if (member.getProvider() != Provider.local) {
                throw new CommonExceptionTemplate(403, "소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
            }

            // ✅ 비밀번호 유효성 검사
            validatePassword(newPassword);

            String encodedPassword = passwordEncoder.encode(newPassword);

            member.setPassword(encodedPassword);
            member.setUpdatedAt(LocalDateTime.now());
            MemberEntity updatedMember = new MemberEntity();
            updatedMember.copyNotNullMembers(member);

            memberRepository.save(updatedMember);

            return "비밀번호가 성공적으로 변경되었습니다.";

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            throw new CommonExceptionTemplate(500, "비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * ✅ 소셜 로그인 후 회원가입 완료
     */
    @Transactional
    public LoginResponse completeSocialSignup(String tempToken, CompleteSignupRequest request)
            throws CommonExceptionTemplate {

        if (!jwtUtil.validateToken(tempToken)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        if (!jwtUtil.isSocialSignupToken(tempToken)) {
            throw new CommonExceptionTemplate(400, "소셜 회원가입 토큰이 아닙니다.");
        }

        String providerId = jwtUtil.getProviderIdFromToken(tempToken);
        String providerStr = jwtUtil.getProviderFromToken(tempToken);
        String emailFromToken = jwtUtil.getEmailFromToken(tempToken);
        String firstNameFromToken = jwtUtil.getFirstNameFromToken(tempToken);
        String lastNameFromToken = jwtUtil.getLastNameFromToken(tempToken);

        Provider provider = Provider.valueOf(providerStr);

        System.out.println("=== 소셜 회원가입 처리 시작 ===");
        System.out.println("Provider: " + provider);
        System.out.println("ProviderId: " + providerId);

        // ✅ 1. 탈퇴 회원 체크 (providerId로만 검사)
        List<MemberEntity> withdrawnMembers = memberRepository.findByProviderId(providerId);
        for (MemberEntity withdrawnMember : withdrawnMembers) {
            if (withdrawnMember.getProvider() == Provider.leave &&
                    withdrawnMember.getDeletedAt() != null) {
                LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
                if (withdrawnMember.getDeletedAt().isAfter(oneHourAgo)) {
                    throw new CommonExceptionTemplate(403,
                            "탈퇴 후 1시간 동안은 재가입이 불가능합니다. 잠시 후 다시 시도해주세요.");
                }
            }
        }

        // ✅ 2. 기존 정상 회원 확인 (provider + providerId)
        MemberDto existingMember = memberMapper.findByProviderAndProviderId(provider, providerId);
        if (existingMember != null) {
            throw new CommonExceptionTemplate(409, "이미 가입된 회원입니다.");
        }

        validatePhoneNumber(request.getPhoneNumber());

        String cleanPhone = request.getPhoneNumber().replace("-", "");
        if (memberRepository.existsByPhoneNumber(cleanPhone)) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }

        // ✅ 이메일 처리
        String finalEmail;

        if (provider == Provider.google) {
            finalEmail = emailFromToken;
            if (finalEmail == null || finalEmail.isEmpty()) {
                throw new CommonExceptionTemplate(400, "Google 로그인 시 이메일은 필수입니다.");
            }
        } else {
            // ✅ Kakao, Naver: 사용자가 입력한 이메일 사용
            finalEmail = request.getEmail();
            if (finalEmail == null || finalEmail.isEmpty()) {
                throw new CommonExceptionTemplate(400, "이메일을 입력해주세요.");
            }
            validateEmail(finalEmail);
        }

        if (memberRepository.existsByEmail(finalEmail)) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 이메일입니다.");
        }

        MemberEntity newMember = new MemberEntity();
        newMember.setProvider(provider);
        newMember.setProviderId(providerId);
        newMember.setEmail(finalEmail);
        newMember.setPhoneNumber(cleanPhone);
        newMember.setFirstName(firstNameFromToken);
        newMember.setLastName(lastNameFromToken);
        newMember.setRoadAddress(request.getRoadAddress());
        newMember.setDetailAddress(request.getDetailAddress());
        newMember.setPassword(null);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        MemberEntity savedMember = memberRepository.save(newMember);

        String token = jwtUtil.generateToken(savedMember.getId());

        return LoginResponse.builder()
                .token(token)
                .memberId(savedMember.getId())
                .firstName(savedMember.getFirstName())
                .lastName(savedMember.getLastName())
                .email(savedMember.getEmail())
                .provider(provider.toString())
                .build();
    }

    @PostConstruct
    public void initCleanup() {
        System.out.println("========================================");
        System.out.println("✅ 서버 시작 - 탈퇴 회원 정리 시작");
        System.out.println("========================================");
        cleanupWithdrawnMembers();
    }

//    /**
//     * ✅ 랜덤 이메일 생성 (Kakao, Naver용)
//     */
//    private String generateUniqueRandomEmail(Provider provider, String providerId) {
//        String hash = Integer.toHexString(providerId.hashCode());
//        if (hash.startsWith("-")) {
//            hash = hash.substring(1);
//        }
//        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
//
//        String email = String.format("%s_%s_%s@%s.social",
//                provider.toString().toLowerCase(),
//                hash,
//                timestamp,
//                provider.toString().toLowerCase()
//        );
//
//        int attempt = 0;
//        while (memberRepository.existsByEmail(email) && attempt < 10) {
//            timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
//            email = String.format("%s_%s_%s_%d@%s.social",
//                    provider.toString().toLowerCase(),
//                    hash,
//                    timestamp,
//                    attempt,
//                    provider.toString().toLowerCase()
//            );
//            attempt++;
//        }
//
//        return email;
//    }
}
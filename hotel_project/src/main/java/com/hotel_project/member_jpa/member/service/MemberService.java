package com.hotel_project.member_jpa.member.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.member_jpa.member.dto.*;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    // ========== 회원가입 / 로그인 ==========

    /**
     * ✅ 일반 회원가입
     */
    public LoginResponse signup(SignupRequest signupRequest) throws CommonExceptionTemplate {
        // 비밀번호 확인
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            throw new CommonExceptionTemplate(400, "비밀번호가 일치하지 않습니다.");
        }

        // ✅ 비밀번호 유효성 검사
        validatePassword(signupRequest.getPassword());

        // ✅ 전화번호 유효성 검사
        validatePhoneNumber(signupRequest.getPhoneNumber());

        // 전화번호 중복 체크 (하이픈 제거 후)
        String cleanPhone = signupRequest.getPhoneNumber().replace("-", "");
        if (memberRepository.existsByPhoneNumber(cleanPhone)) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }

        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        MemberEntity newMember = new MemberEntity();
        newMember.setFirstName(signupRequest.getFirstName());
        newMember.setLastName(signupRequest.getLastName());
        newMember.setEmail(signupRequest.getEmail());
        newMember.setPhoneNumber(cleanPhone); // ✅ 하이픈 제거된 전화번호 저장
        newMember.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newMember.setProvider(Provider.local);
        newMember.setRoadAddress(signupRequest.getRoadAddress());
        newMember.setDetailAddress(signupRequest.getDetailAddress());
        newMember.setProviderId(null);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        MemberEntity savedMember = memberRepository.save(newMember);

        // ✅ JWT 토큰 생성 (memberId만)
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
     * ✅ 일반 로그인
     */
    public LoginResponse login(LoginRequest loginRequest) throws CommonExceptionTemplate {
        MemberDto member = memberMapper.findByEmailAndProvider(loginRequest.getEmail(), Provider.local);

        if (member == null) {
            throw new CommonExceptionTemplate(401, "이메일 또는 비밀번호가 잘못되었습니다.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new CommonExceptionTemplate(401, "이메일 또는 비밀번호가 잘못되었습니다.");
        }

        // ✅ JWT 토큰 생성 (memberId만)
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

        MemberEntity existingMember = new MemberEntity();
        existingMember.copyMembers(existingMemberDto);

        if (memberDto.getEmail() != null &&
                !memberDto.getEmail().equals(existingMember.getEmail()) &&
                memberRepository.existsByEmailAndIdNot(memberDto.getEmail(), memberId)) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        if (memberDto.getPhoneNumber() != null &&
                !memberDto.getPhoneNumber().equals(existingMember.getPhoneNumber())) {
            // ✅ 전화번호 변경 시 유효성 검사
            validatePhoneNumber(memberDto.getPhoneNumber());

            String cleanPhone = memberDto.getPhoneNumber().replace("-", "");
            if (memberRepository.existsByPhoneNumberAndIdNot(cleanPhone, memberId)) {
                throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
            }
            memberDto.setPhoneNumber(cleanPhone);
        }

        if (memberDto.getPassword() != null && existingMember.getProvider() == Provider.local) {
            // ✅ 비밀번호 변경 시 유효성 검사
            validatePassword(memberDto.getPassword());
            memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        memberDto.setUpdatedAt(LocalDateTime.now());
        existingMember.copyNotNullMembers(memberDto);

        memberRepository.save(existingMember);
        return "update ok";
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
     * - 임시 토큰에서 사용자 정보 추출 (아직 DB에 없어서)
     * - CompleteSignupRequest에서 전화번호, 주소 정보 받음
     */
    @Transactional
    public LoginResponse completeSocialSignup(String tempToken, CompleteSignupRequest request)
            throws CommonExceptionTemplate {

        // 1. JWT 토큰 검증
        if (!jwtUtil.validateToken(tempToken)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        // 2. 토큰 타입 확인
        if (!jwtUtil.isSocialSignupToken(tempToken)) {
            throw new CommonExceptionTemplate(400, "소셜 회원가입 토큰이 아닙니다.");
        }

        // 3. ✅ 토큰에서 OAuth 정보 추출
        String providerId = jwtUtil.getProviderIdFromToken(tempToken);
        String providerStr = jwtUtil.getProviderFromToken(tempToken);
        String emailFromToken = jwtUtil.getEmailFromToken(tempToken);
        String firstNameFromToken = jwtUtil.getFirstNameFromToken(tempToken);
        String lastNameFromToken = jwtUtil.getLastNameFromToken(tempToken);

        Provider provider = Provider.valueOf(providerStr);

        System.out.println("=== 소셜 회원가입 처리 시작 ===");
        System.out.println("Provider: " + provider);
        System.out.println("ProviderId: " + providerId);
        System.out.println("FirstName: " + firstNameFromToken);
        System.out.println("LastName: " + lastNameFromToken);
        System.out.println("Email: " + emailFromToken);

        // 4. 이미 가입된 회원인지 확인
        MemberDto existingMember = memberMapper.findByProviderAndProviderId(provider, providerId);
        if (existingMember != null) {
            throw new CommonExceptionTemplate(409, "이미 가입된 회원입니다.");
        }

        // 5. ✅ 전화번호 유효성 검사
        validatePhoneNumber(request.getPhoneNumber());

        // 전화번호 중복 체크 (하이픈 제거 후)
        String cleanPhone = request.getPhoneNumber().replace("-", "");
        if (memberRepository.existsByPhoneNumber(cleanPhone)) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }

        // 6. ✅ 이메일 처리
        String finalEmail;
        if (provider == Provider.google) {
            // Google: 토큰의 이메일 사용
            finalEmail = emailFromToken;
            if (finalEmail == null || finalEmail.isEmpty()) {
                throw new CommonExceptionTemplate(400, "Google 로그인 시 이메일은 필수입니다.");
            }
            if (memberRepository.existsByEmail(finalEmail)) {
                throw new CommonExceptionTemplate(409, "이미 사용 중인 이메일입니다.");
            }
        } else {
            // Kakao, Naver: 랜덤 이메일 생성
            finalEmail = generateUniqueRandomEmail(provider, providerId);
        }

        // 7. ✅ 신규 회원 생성 (토큰에서 가져온 정보 + request 정보 결합)
        MemberEntity newMember = new MemberEntity();
        newMember.setProvider(provider);
        newMember.setProviderId(providerId);
        newMember.setEmail(finalEmail);
        newMember.setPhoneNumber(cleanPhone); // ✅ 하이픈 제거된 전화번호 저장
        newMember.setFirstName(firstNameFromToken);  // ✅ 토큰에서
        newMember.setLastName(lastNameFromToken);    // ✅ 토큰에서
        newMember.setRoadAddress(request.getRoadAddress());
        newMember.setDetailAddress(request.getDetailAddress());
        newMember.setPassword(null);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        MemberEntity savedMember = memberRepository.save(newMember);

        // 8. ✅ 정식 JWT 생성 (memberId만)
        String token = jwtUtil.generateToken(savedMember.getId());

        // 9. LoginResponse 반환
        return LoginResponse.builder()
                .token(token)
                .memberId(savedMember.getId())
                .firstName(savedMember.getFirstName())
                .lastName(savedMember.getLastName())
                .email(savedMember.getEmail())
                .provider(provider.toString())
                .build();
    }

    /**
     * ✅ 랜덤 이메일 생성 (Kakao, Naver용)
     */
    private String generateUniqueRandomEmail(Provider provider, String providerId) {
        String hash = Integer.toHexString(providerId.hashCode());
        if (hash.startsWith("-")) {
            hash = hash.substring(1);
        }
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);

        String email = String.format("%s_%s_%s@%s.social",
                provider.toString().toLowerCase(),
                hash,
                timestamp,
                provider.toString().toLowerCase()
        );

        int attempt = 0;
        while (memberRepository.existsByEmail(email) && attempt < 10) {
            timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
            email = String.format("%s_%s_%s_%d@%s.social",
                    provider.toString().toLowerCase(),
                    hash,
                    timestamp,
                    attempt,
                    provider.toString().toLowerCase()
            );
            attempt++;
        }

        return email;
    }
}
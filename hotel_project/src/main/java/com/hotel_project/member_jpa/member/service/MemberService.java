package com.hotel_project.member_jpa.member.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.member_jpa.member.dto.*;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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

    /**
     * 일반 회원가입 - JPA 사용
     */
    public LoginResponse signup(SignupRequest signupRequest) throws CommonExceptionTemplate {
// 비밀번호 확인
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            throw new CommonExceptionTemplate(400, "비밀번호가 일치하지 않습니다.");
        }

        if (memberRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }
// 중복 체크- JPA 사용
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }



// 새 회원 생성
        MemberEntity newMember = new MemberEntity();
        newMember.setFirstName(signupRequest.getFirstName());
        newMember.setLastName(signupRequest.getLastName());
        newMember.setEmail(signupRequest.getEmail());
        newMember.setPhoneNumber(signupRequest.getPhoneNumber());
        newMember.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newMember.setProvider(Provider.local);

        newMember.setRoadAddress(signupRequest.getRoadAddress());
        newMember.setDetailAddress(signupRequest.getDetailAddress());

        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        newMember.setProviderId(null);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        MemberEntity savedMember = memberRepository.save(newMember);

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(
                savedMember.getId(),
                savedMember.getProvider().toString(),
                savedMember.getFirstName(),
                savedMember.getLastName(),
                savedMember.getEmail()
        );

        return new LoginResponse(
                token,
                savedMember.getId(),
                savedMember.getFirstName(),
                savedMember.getLastName(),
                savedMember.getEmail(),
                savedMember.getProvider().toString()
        );
    }

    /**
     * 일반 로그인 - MyBatis 사용
     */
    public LoginResponse login(LoginRequest loginRequest) throws CommonExceptionTemplate {
// MyBatis로 회원 조회
        MemberDto member = memberMapper.findByEmailAndProvider(loginRequest.getEmail(), Provider.local);

        if (member == null) {
            throw new CommonExceptionTemplate(401, "이메일 또는 비밀번호가 잘못되었습니다.");
        }

// 비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new CommonExceptionTemplate(401, "이메일 또는 비밀번호가 잘못되었습니다.");
        }

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(
                member.getId(),
                member.getProvider().toString(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail()
        );

        return new LoginResponse(
                token,
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getProvider().toString()
        );
    }

// ========== 비밀번호 재설정 관련 메서드(MyBatis 조회 방식) ==========

    /**
     * 이메일로 회원 존재 여부 확인 - MyBatis 사용
     */
    public boolean existsByEmail(String email) {
        MemberDto member = memberMapper.findByEmailAndProvider(email, Provider.local);
        return member != null;
    }

    /**
     * 비밀번호 재설정 - MyBatis 조회 + JPA 저장
     */
    public String resetPassword(String email, String newPassword) throws CommonExceptionTemplate {
// MyBatis로 회원 조회
        MemberDto memberDto = memberMapper.findByEmailAndProvider(email, Provider.local);

        if (memberDto == null) {
            throw new CommonExceptionTemplate(404, "해당 이메일로 가입된 계정이 없습니다.");
        }

// 소셜 로그인 계정은 비밀번호 재설정 불가
        if (memberDto.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(400, "소셜 로그인 계정은 비밀번호를 재설정할 수 없습니다.");
        }

// DTO를Entity로 변환
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.copyMembers(memberDto);

// 새 비밀번호 암호화 및 설정
        memberEntity.setPassword(passwordEncoder.encode(newPassword));
        memberEntity.setUpdatedAt(LocalDateTime.now());

// JPA로 저장
        memberRepository.save(memberEntity);

        return "비밀번호가 성공적으로 재설정되었습니다.";
    }
    @Transactional
    public String resetPasswordDirect(String email, String newPassword) throws CommonExceptionTemplate {
// MyBatis로 회원 조회
        MemberDto memberDto = memberMapper.findByEmailAndProvider(email, Provider.local);

        if (memberDto == null) {
            throw new CommonExceptionTemplate(404, "해당 이메일로 가입된 계정이 없습니다.");
        }

// 소셜 로그인 계정은 비밀번호 재설정 불가
        if (memberDto.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(400, "소셜 로그인 계정은 비밀번호를 재설정할 수 없습니다.");
        }

// DTO를Entity로 변환
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.copyMembers(memberDto);

// 새 비밀번호 암호화 및 설정
        memberEntity.setPassword(passwordEncoder.encode(newPassword));
        memberEntity.setUpdatedAt(LocalDateTime.now());

// JPA로 저장
        memberRepository.save(memberEntity);

        return "비밀번호가 성공적으로 재설정되었습니다.";
    }

// ========== 기존 메서드들==========

    /**
     * JWT 토큰으로 회원 정보 조회 - MyBatis 사용
     */
    public MemberDto getMemberDtoByToken(String token) throws CommonExceptionTemplate {
        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        MemberDto member = memberMapper.findById(memberId);

        if (member == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

// 비밀번호 제외
        member.setPassword(null);
        return member;
    }

    /**
     * 회원 정보 수정 후 DTO 반환
     */
    public MemberDto updateMemberAndReturnDto(Long memberId, MemberDto memberDto) throws CommonExceptionTemplate {
        updateMember(memberId, memberDto);

// MyBatis로 최신 정보 조회
        MemberDto responseDto = memberMapper.findById(memberId);
        responseDto.setPassword(null); // 비밀번호 제외

        return responseDto;
    }

    /**
     * 회원 정보 수정 - MyBatis + JPA 사용
     */
    public String updateMember(Long memberId, MemberDto memberDto) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

// MyBatis로 기존 회원 조회
        MemberDto existingMemberDto = memberMapper.findById(memberId);
        if (existingMemberDto == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

// DTO를Entity로 변환
        MemberEntity existingMember = new MemberEntity();
        existingMember.copyMembers(existingMemberDto);

// 중복 체크- JPA 사용
        if (memberDto.getEmail() != null &&
                !memberDto.getEmail().equals(existingMember.getEmail()) &&
                memberRepository.existsByEmailAndIdNot(memberDto.getEmail(), memberId)) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        if (memberDto.getPhoneNumber() != null &&
                !memberDto.getPhoneNumber().equals(existingMember.getPhoneNumber()) &&
                memberRepository.existsByPhoneNumberAndIdNot(memberDto.getPhoneNumber(), memberId)) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }

// 비밀번호 암호화(일반 회원인 경우만)
        if (memberDto.getPassword() != null && existingMember.getProvider() == Provider.local) {
            memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        memberDto.setUpdatedAt(LocalDateTime.now());

// null이 아닌 값만 복사
        existingMember.copyNotNullMembers(memberDto);

        memberRepository.save(existingMember);
        return "update ok";
    }

    /**
     * 회원 탈퇴 - JPA 사용
     */
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
     * 소셜 로그인 후 JWT 토큰 생성
     */
    public String generateTokenForSocialLogin(MemberEntity member) {
        return jwtUtil.generateToken(
                member.getId(),
                member.getProvider().toString(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail()
        );
    }

// MemberService.java에 추가할 메서드들

    /**
     * 현재 비밀번호 확인
     */
    public boolean verifyCurrentPassword(Long memberId, String currentPassword) {
        try {
            MemberDto member = memberMapper.findById(memberId);

            if (member == null) {
                return false;
            }

// 소셜 계정은 비밀번호가 없음
            if (member.getProvider() != Provider.local) {
                return false;
            }

// 암호화된 비밀번호와 비교(BCrypt 사용 가정)
            return passwordEncoder.matches(currentPassword, member.getPassword());

        } catch (Exception e) {
            System.out.println("비밀번호 확인 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 비밀번호 변경
     */
    public String changePassword(Long memberId, String newPassword) throws CommonExceptionTemplate {
        try {
            MemberDto member = memberMapper.findById(memberId);

            if (member == null) {
                throw new CommonExceptionTemplate(404, "회원을 찾을 수 없습니다.");
            }

// 소셜 계정은 비밀번호 변경 불가
            if (member.getProvider() != Provider.local) {
                throw new CommonExceptionTemplate(403, "소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
            }

// 새 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(newPassword);

// 비밀번호 업데이트
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

// 소셜 로그인 후 추가 정보 입력 완료
// ========== 새로운completeSocialSignup 메서드(JWT 기반) ==========
    /**
     * ⭐ 소셜 로그인 후 회원가입 완료 (JWT 기반)
     */
    @Transactional
    public LoginResponse completeSocialSignup(String tempToken, CompleteSignupRequest request) throws CommonExceptionTemplate {

// 1. JWT 토큰 검증
        if (!jwtUtil.validateToken(tempToken)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

// 2. 토큰 타입 확인
        if (!jwtUtil.isSocialSignupToken(tempToken)) {
            throw new CommonExceptionTemplate(400, "소셜 회원가입 토큰이 아닙니다.");
        }

// 3. 토큰에서 정보 추출
        String providerId = jwtUtil.getProviderIdFromToken(tempToken);
        String providerStr = jwtUtil.getProviderFromToken(tempToken);
        String emailFromToken = jwtUtil.getEmailFromToken(tempToken);
        String firstNameFromToken = jwtUtil.getFirstNameFromToken(tempToken);
        String lastNameFromToken = jwtUtil.getLastNameFromToken(tempToken);

        Provider provider = Provider.valueOf(providerStr);

        System.out.println("=== 소셜 회원가입 처리 시작===");
        System.out.println("Provider: " + provider);

// 4. 이미 가입된 회원인지 확인
        MemberDto existingMember = memberMapper.findByProviderAndProviderId(provider, providerId);
        if (existingMember != null) {
            throw new CommonExceptionTemplate(409, "이미 가입된 회원입니다.");
        }

// 5. 전화번호 중복 체크
        if (memberRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }

// 6. ⭐ 이메일 처리
        String finalEmail;
        if (provider == Provider.google) {
// Google: JWT의 이메일 사용
            finalEmail = emailFromToken;
            if (memberRepository.existsByEmail(finalEmail)) {
                throw new CommonExceptionTemplate(409, "이미 사용 중인 이메일입니다.");
            }
        } else {
// Kakao, Naver: 랜덤 이메일 생성
            finalEmail = generateUniqueRandomEmail(provider, providerId);
        }

// 7. 신규 회원 생성
        MemberEntity newMember = new MemberEntity();
        newMember.setProvider(provider);
        newMember.setProviderId(providerId);
        newMember.setEmail(finalEmail);
        newMember.setPhoneNumber(request.getPhoneNumber());
        newMember.setFirstName(firstNameFromToken);
        newMember.setLastName(lastNameFromToken);
        newMember.setRoadAddress(request.getRoadAddress());
        newMember.setDetailAddress(request.getDetailAddress());
        newMember.setPassword(null);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        MemberEntity savedMember = memberRepository.save(newMember);

        //정식JWT 생성
        String token = jwtUtil.generateToken(
                savedMember.getId(),
                provider.toString(),
                savedMember.getFirstName(),
                savedMember.getLastName(),
                savedMember.getEmail()
        );

        // 9. LoginResponse 반환
        return new LoginResponse(
                token,
                savedMember.getId(),
                savedMember.getFirstName(),
                savedMember.getLastName(),
                savedMember.getEmail(),
                provider.toString()
        );
    }

    /**
     * ⭐ 랜덤 이메일 생성 (Kakao, Naver용)
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

// 중복 체크
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
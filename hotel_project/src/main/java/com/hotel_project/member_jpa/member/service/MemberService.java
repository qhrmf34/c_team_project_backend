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

        // 중복 체크 - JPA 사용
        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        if (memberRepository.existsByPhoneNumber(signupRequest.getPhoneNumber())) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }

        // 새 회원 생성
        MemberEntity newMember = new MemberEntity();
        newMember.setFirstName(signupRequest.getFirstName());
        newMember.setLastName(signupRequest.getLastName());
        newMember.setEmail(signupRequest.getEmail());
        newMember.setPhoneNumber(signupRequest.getPhoneNumber());
        newMember.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newMember.setProvider(Provider.local);
        newMember.setProviderId(null);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        MemberEntity savedMember = memberRepository.save(newMember);

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(savedMember.getId(), savedMember.getProvider().toString());

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
        String token = jwtUtil.generateToken(member.getId(), member.getProvider().toString());

        return new LoginResponse(
                token,
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getProvider().toString()
        );
    }

    // ========== 비밀번호 재설정 관련 메서드 (MyBatis 조회 방식) ==========

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

        // DTO를 Entity로 변환
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

        // DTO를 Entity로 변환
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.copyMembers(memberDto);

        // 새 비밀번호 암호화 및 설정
        memberEntity.setPassword(passwordEncoder.encode(newPassword));
        memberEntity.setUpdatedAt(LocalDateTime.now());

        // JPA로 저장
        memberRepository.save(memberEntity);

        return "비밀번호가 성공적으로 재설정되었습니다.";
    }

    // ========== 기존 메서드들 ==========

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

        // DTO를 Entity로 변환
        MemberEntity existingMember = new MemberEntity();
        existingMember.copyMembers(existingMemberDto);

        // 중복 체크 - JPA 사용
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

        // 비밀번호 암호화 (일반 회원인 경우만)
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
        return jwtUtil.generateToken(member.getId(), member.getProvider().toString());
    }
}